/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao;

import android.database.Cursor;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScope;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.Constructor;

/** Reserved for internal unit tests that want to access some non-public methods. Don't use for anything else. */
public class InternalUnitTestDaoAccess<T, K> {
    private final AbstractDao<T, K> dao;

    public InternalUnitTestDaoAccess(Database db, Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope)
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
