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
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.apihint.Internal;
import org.greenrobot.greendao.rx.RxQuery;
import org.greenrobot.greendao.rx.RxTransaction;

import java.util.Date;
import java.util.List;

import rx.schedulers.Schedulers;

/**
 * A repeatable query returning entities.
 *
 * @param <T> The entity class the query will return results for.
 * @author Markus
 */
public class Query<T> extends AbstractQueryWithLimit<T> {
    private final static class QueryData<T2> extends AbstractQueryData<T2, Query<T2>> {
        private final int limitPosition;
        private final int offsetPosition;

        QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues, int limitPosition, int offsetPosition) {
            super(dao, sql, initialValues);
            this.limitPosition = limitPosition;
            this.offsetPosition = offsetPosition;
        }

        @Override
        protected Query<T2> createQuery() {
            return new Query<T2>(this, dao, sql, initialValues.clone(), limitPosition, offsetPosition);
        }

    }

    /** For internal use by greenDAO only. */
    public static <T2> Query<T2> internalCreate(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return create(dao, sql, initialValues, -1, -1);
    }

    static <T2> Query<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, int limitPosition,
                                 int offsetPosition) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues), limitPosition,
                offsetPosition);
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private volatile RxQuery rxTxPlain;
    private volatile RxQuery rxTxIo;

    private Query(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues, int limitPosition,
                  int offsetPosition) {
        super(dao, sql, initialValues, limitPosition, offsetPosition);
        this.queryData = queryData;
    }

    /**
     * Note: all parameters are reset to their initial values specified in {@link QueryBuilder}.
     */
    public Query<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /** Executes the query and returns the result as a list containing all entities loaded into memory. */
    public List<T> list() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return daoAccess.loadAllAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the result as a list that lazy loads the entities on first access. Entities are
     * cached, so accessing the same entity more than once will not result in loading an entity from the underlying
     * cursor again.Make sure to close it to close the underlying cursor.
     */
    public LazyList<T> listLazy() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return new LazyList<T>(daoAccess, cursor, true);
    }

    /**
     * Executes the query and returns the result as a list that lazy loads the entities on every access (uncached).
     * Make sure to close the list to close the underlying cursor.
     */
    public LazyList<T> listLazyUncached() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return new LazyList<T>(daoAccess, cursor, false);
    }

    /**
     * Executes the query and returns the result as a list iterator; make sure to close it to close the underlying
     * cursor. The cursor is closed once the iterator is fully iterated through.
     */
    public CloseableListIterator<T> listIterator() {
        return listLazyUncached().listIteratorAutoClose();
    }

    /**
     * Executes the query and returns the unique result or null.
     *
     * @return Entity or null if no matching entity was found
     * @throws DaoException if the result is not unique
     */
    public T unique() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        return daoAccess.loadUniqueAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the unique result (never null).
     *
     * @return Entity
     * @throws DaoException if the result is not unique or no entity was found
     */
    public T uniqueOrThrow() {
        T entity = unique();
        if (entity == null) {
            throw new DaoException("No entity found for query");
        }
        return entity;
    }

    @Override
    public Query<T> setParameter(int index, Object parameter) {
        return (Query<T>) super.setParameter(index, parameter);
    }

    @Override
    public Query<T> setParameter(int index, Date parameter) {
        return (Query<T>) super.setParameter(index, parameter);
    }

    @Override
    public Query<T> setParameter(int index, Boolean parameter) {
        return (Query<T>) super.setParameter(index, parameter);
    }

    /**
     * DO NOT USE.
     * The returned {@link RxTransaction} allows getting query results using Rx Observables without any Scheduler set
     * for subscribeOn.
     *
     * @see #__InternalRx()
     */
    @Internal
    public RxQuery __internalRxPlain() {
        if (rxTxPlain == null) {
            rxTxPlain = new RxQuery(this);
        }
        return rxTxPlain;
    }

    /**
     * DO NOT USE.
     * The returned {@link RxTransaction} allows getting query results using Rx Observables using RX's IO scheduler for
     * subscribeOn.
     *
     * @see #__internalRxPlain()
     */
    @Internal
    public RxQuery __InternalRx() {
        if (rxTxIo == null) {
            rxTxIo = new RxQuery(this, Schedulers.io());
        }
        return rxTxIo;
    }
}
