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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.database.sqlite.SQLiteDatabase;

/**
 * Incomplete. Check back later.
 *  
 * @author Markus
 */
public class AbstractDaoMaster {
    private static final Map<Class<?>, Class<? extends AbstractDao<?, ?>>> entityToDaoClass = new HashMap<Class<?>, Class<? extends AbstractDao<?, ?>>>();

    public static <T> void registerDao(Class<T> entityClass, Class<? extends AbstractDao<T, ?>> daoClass) {
        entityToDaoClass.put(entityClass, daoClass);
    }
    
    public static void unregisterAllDaos() {
        entityToDaoClass.clear();
    }

    public static void createEntityTables(SQLiteDatabase db) {
        Collection<Class<? extends AbstractDao<?, ?>>> daoClasses = entityToDaoClass.values();
        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {
            try {
                Method createTableMethod = daoClass.getMethod("createTable", SQLiteDatabase.class, boolean.class);
                createTableMethod.invoke(null, db, false);
            } catch (Exception e) {
                throw new RuntimeException("Could not create table for " + daoClass, e);
            }
        }
    }

    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

    public AbstractDaoMaster(SQLiteDatabase db) {
        this.db = db;
        entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();

        Collection<Entry<Class<?>, Class<? extends AbstractDao<?, ?>>>> entries = entityToDaoClass.entrySet();
        for (Entry<Class<?>, Class<? extends AbstractDao<?, ?>>> entry : entries) {
            Class<? extends AbstractDao<?, ?>> daoClass = entry.getValue();
            try {
                Constructor<? extends AbstractDao<?, ?>> constructor = daoClass.getConstructor(SQLiteDatabase.class);
                AbstractDao<?, ?> dao = constructor.newInstance(db);
                entityToDao.put(entry.getKey(), dao);
            } catch (Exception e) {
                throw new RuntimeException("Could create DAO: " + daoClass, e);
            }
        }
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

}
