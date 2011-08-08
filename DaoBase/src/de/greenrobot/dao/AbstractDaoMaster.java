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

package de.greenrobot.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.database.sqlite.SQLiteDatabase;

/**
 * Incomplete. Check back later.
 * 
 * @author Markus
 */
public class AbstractDaoMaster {
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;
    private final int schemaVersion;

    public AbstractDaoMaster(SQLiteDatabase db, int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;
        entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public <T> long insert(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        return dao.insert(entity);
    }

    public <T, K> T load(Class<T> entityClass, K key) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
        return dao.load(key);
    }

    public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
        AbstractDao<?, ?> dao = entityToDao.get(entityClass);
        if (dao == null) {
            throw new RuntimeException("No DAO registered for " + entityClass);
        }
        return dao;
    }

    protected void registerDao(Class<?> entityClass, AbstractDao<?, ?> dao) {
        entityToDao.put(entityClass, dao);
    }

    /**
     * Run the given Runnable inside a database transaction. If you except a result, consider callInTx.
     */
    public void runInTx(Runnable runnable) {
        db.beginTransaction();
        try {
            runnable.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Calls the given Callable inside a database transaction and returns the result of the Callable. If you don't
     * except a result, consider runInTx.
     */
    public <V> V callInTx(Callable<V> callable) throws Exception {
        db.beginTransaction();
        try {
            V result = callable.call();
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

}
