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

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

/**
 * Base class for DAO (master) related testing.
 * 
 * @author Markus
 * 
 * @param <T>
 *            Type of a concrete DAO master
 */
public abstract class AbstractDaoSessionTest<T extends AbstractDaoMaster, S extends AbstractDaoSession>
        extends DbTest {

    private final Class<T> daoMasterClass;
    protected T daoMaster;
    protected S daoSession;

    public AbstractDaoSessionTest(Class<T> daoMasterClass) {
        this(daoMasterClass, true);
    }

    public AbstractDaoSessionTest(Class<T> daoMasterClass, boolean inMemory) {
        super(inMemory);
        this.daoMasterClass = daoMasterClass;
    }

	@SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            Constructor<T> constructor = daoMasterClass.getConstructor(SQLiteDatabase.class);
            daoMaster = constructor.newInstance(db);

            Method createTableMethod = daoMasterClass.getMethod("createAllTables", SQLiteDatabase.class, boolean.class);
            createTableMethod.invoke(null, db, false);
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO session test", e);
        }
        daoSession = (S) daoMaster.newSession();
    }

}