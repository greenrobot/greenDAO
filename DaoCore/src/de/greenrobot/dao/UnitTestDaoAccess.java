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

import de.greenrobot.dao.wrapper.SQLiteDatabaseWrapper;

import android.database.Cursor;

/** Reserved for internal unit tests that want to access some non-public methods. Don't use for anything else. */
public class UnitTestDaoAccess<T, K> {
    private final AbstractDao<T, K> dao;

    public UnitTestDaoAccess(SQLiteDatabaseWrapper db, Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope)
            throws Exception {
        DaoConfig daoConfig = new DaoConfig(db, daoClass);
        daoConfig.setIdentityScope(identityScope);
        Constructor<AbstractDao<T, K>> constructor = daoClass.getConstructor(DaoConfig.class);
        dao = constructor.newInstance(daoConfig);
    }

    public K getKey(T entity) {
        return dao.getKey(entity);
    }

    public Property[] getProperties() {
        return dao.getProperties();
    }

    public boolean isEntityUpdateable() {
        return dao.isEntityUpdateable();
    }

    public T readEntity(Cursor cursor, int offset) {
        return dao.readEntity(cursor, offset);
    }

    public K readKey(Cursor cursor, int offset) {
        return dao.readKey(cursor, offset);
    }

    public AbstractDao<T, K> getDao() {
        return dao;
    }

}
