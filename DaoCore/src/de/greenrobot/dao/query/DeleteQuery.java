/*
 * Copyright (C) 2011-2013 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.dao.query;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;

/**
 * A repeatable query for deleting entities.<br/>
 * New API note: this is more likely to change.
 * 
 * @author Markus
 * 
 * @param <T>
 *            The enitity class the query will delete from.
 */
public class DeleteQuery<T> extends AbstractQuery<T> {
    private final static class QueryData<T2> extends AbstractQueryData<T2, DeleteQuery<T2>> {

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        @Override
        protected DeleteQuery<T2> createQuery() {
            return new DeleteQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> DeleteQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;
    private SQLiteStatement compiledStatement;

    private DeleteQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
    }

    public DeleteQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /**
     * Deletes all matching entities without detaching them from the identity scope (aka session/cache). Note that this
     * method may lead to stale entity objects in the session cache. Stale entities may be returned when loaded by their
     * primary key, but not using queries.
     */
    public void executeDeleteWithoutDetachingEntities() {
        checkThread();
        SQLiteDatabase db = dao.getDatabase();
        if (db.isDbLockedByCurrentThread()) {
            executeDeleteWithoutDetachingEntitiesInsideTx();
        } else {
            // Do TX to acquire a connection before locking this to avoid deadlocks
            // Locking order as described in AbstractDao
            db.beginTransaction();
            try {
                executeDeleteWithoutDetachingEntitiesInsideTx();
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    private void executeDeleteWithoutDetachingEntitiesInsideTx() {
        if (compiledStatement != null) {
            compiledStatement.clearBindings();
        } else {
            compiledStatement = dao.getDatabase().compileStatement(sql);
        }
        for (int i = 0; i < parameters.length; i++) {
            String value = parameters[i];
            if (value != null) {
                compiledStatement.bindString(i + 1, value);
            } else {
                compiledStatement.bindNull(i + 1);
            }
        }
        compiledStatement.execute();
    }

}
