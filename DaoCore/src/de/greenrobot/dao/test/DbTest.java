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

import java.util.Random;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import de.greenrobot.dao.DbUtils;

/**
 * Base class for database related testing. Prepares an in-memory or an file-based DB.
 * 
 * @author Markus
 * 
 */
public abstract class DbTest<T extends Application> extends ApplicationTestCase<T> {

    protected SQLiteDatabase db;
    protected Random random;
    protected final boolean inMemory;
    private boolean dontCreateApp;

    public DbTest() {
        this(true);
    }

    @SuppressWarnings("unchecked")
    public DbTest(boolean inMemory) {
        this((Class<T>) Application.class, inMemory);
    }

    public DbTest(Class<T> appClass, boolean inMemory) {
        super(appClass);
        this.inMemory = inMemory;
        random = new Random();
    }

    protected void dontCreateApplicationDuringSetUp() {
        dontCreateApp = true;
    }

    @Override
    protected void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!dontCreateApp) {
            createApplication();
        }
        setUpDb();
    }

    /** Override if you create your own DB */
    protected void setUpDb() {
        if (inMemory) {
            db = SQLiteDatabase.create(null);
        } else {
            getApplication().deleteDatabase("test-db");
            db = getApplication().openOrCreateDatabase("test-db", Context.MODE_PRIVATE, null);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }
        super.tearDown();
    }

    protected void logTableDump(String tablename) {
        DbUtils.logTableDump(db, tablename);
    }

}