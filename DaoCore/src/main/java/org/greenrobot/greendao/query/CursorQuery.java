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
package org.greenrobot.greendao.query;

import android.database.Cursor;
import org.greenrobot.greendao.AbstractDao;

import java.util.Date;

/**
 * A repeatable query returning a raw android.database.Cursor. Note, that using cursors is usually a hassle and
 * greenDAO provides a higher level abstraction using entities (see {@link org.greenrobot.greendao.query.Query}). This class
 * can nevertheless be useful to work with legacy code that is based on Cursors or CursorLoaders.
 *
 * @param <T> The entity class the query will return results for.
 * @author Markus
 */
public class CursorQuery<T> extends AbstractQueryWithLimit<T> {
    private final static class QueryData<T2> extends AbstractQueryData<T2, CursorQuery<T2>> {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(AbstractDao dao, String sql, String[] initialValues, int limitPosition, int offsetPosition) {
            super(dao, sql, initialValues);
            this.limitPosition = limitPosition;
            this.offsetPosition = offsetPosition;
        }

        @Override
        protected CursorQuery<T2> createQuery() {
            return new CursorQuery<T2>(this, dao, sql, initialValues.clone(), limitPosition, offsetPosition);
        }

    }

    /** For internal use by greenDAO only. */
    public static <T2> CursorQuery<T2> internalCreate(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return create(dao, sql, initialValues, -1, -1);
    }

    static <T2> CursorQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, int limitPosition,
                                       int offsetPosition) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues), limitPosition,
                offsetPosition);
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private CursorQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues, int limitPosition,
                        int offsetPosition) {
        super(dao, sql, initialValues, limitPosition, offsetPosition);
        this.queryData = queryData;
    }

    public CursorQuery forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /** Executes the query and returns a raw android.database.Cursor. Don't forget to close it. */
    public Cursor query() {
        checkThread();
        return dao.getDatabase().rawQuery(sql, parameters);
    }

    // copy setParameter methods to allow easy chaining
    @Override
    public CursorQuery<T> setParameter(int index, Object parameter) {
        return (CursorQuery<T>) super.setParameter(index, parameter);
    }

    @Override
    public CursorQuery<T> setParameter(int index, Date parameter) {
        return (CursorQuery<T>) super.setParameter(index, parameter);
    }

    @Override
    public CursorQuery<T> setParameter(int index, Boolean parameter) {
        return (CursorQuery<T>) super.setParameter(index, parameter);
    }

}
