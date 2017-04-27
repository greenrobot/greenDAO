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

import java.util.HashMap;
import java.util.Map;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * The master of dao will guide you: start dao sessions with the master.
 * 
 * @author Markus
 */
public abstract class AbstractDaoMaster {
    protected final Database db;
    protected final int schemaVersion;
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;

    public AbstractDaoMaster(Database db, int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;

        daoConfigMap = new HashMap<Class<? extends AbstractDao<?, ?>>, DaoConfig>();
    }

    protected void registerDaoClass(Class<? extends AbstractDao<?, ?>> daoClass) {
        DaoConfig daoConfig = new DaoConfig(db, daoClass);
        daoConfigMap.put(daoClass, daoConfig);
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    /** Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities. */
    public Database getDatabase() {
        return db;
    }

    public abstract AbstractDaoSession newSession();

    public abstract AbstractDaoSession newSession(IdentityScopeType type);
}
