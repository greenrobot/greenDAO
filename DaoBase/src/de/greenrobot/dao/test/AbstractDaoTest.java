/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.greenrobot.dao.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.UnitTestDaoAccess;

/**
 * Base class for DAO related testing. Prepares an in-memory DB and DAO.
 * 
 * @author Markus
 * 
 * @param <D>
 *            DAO class
 * @param <T>
 *            Entity type of the DAO
 * @param <K>
 *            Key type of the DAO
 */
public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends ApplicationTestCase<Application> {

    protected D dao;
    protected SQLiteDatabase db;
    protected Random random;
    protected final Class<D> daoClass;
    protected UnitTestDaoAccess<T, K> daoAccess;
    protected Property pkColumn;
    private final boolean inMemory;

    public AbstractDaoTest(Class<D> daoClass) {
        this(daoClass, true);
    }

    public AbstractDaoTest(Class<D> daoClass, boolean inMemory) {
        super(Application.class);
        this.inMemory = inMemory;
        random = new Random();
        this.daoClass = daoClass;
    }

    @Override
    protected void setUp() {
        createApplication();
        if (inMemory) {
            db = SQLiteDatabase.create(null);
        } else {
            getApplication().deleteDatabase("test-db");
            db = getApplication().openOrCreateDatabase("test-db", Context.MODE_PRIVATE, null);
        }
        try {
            Constructor<D> constructor = daoClass.getConstructor(SQLiteDatabase.class);
            dao = constructor.newInstance(db);

            Method createTableMethod = daoClass.getMethod("createTable", SQLiteDatabase.class, boolean.class);
            createTableMethod.invoke(null, db, false);
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO Test", e);
        }
        daoAccess = new UnitTestDaoAccess<T, K>(dao);
    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }
    }

}