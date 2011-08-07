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

import android.database.sqlite.SQLiteDatabase;

/**
 * Incomplete. Check back later.
 *  
 * @author Markus
 */
public class AbstractDaoMaster {
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

    public AbstractDaoMaster(SQLiteDatabase db) {
        this.db = db;
        entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();

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

}
