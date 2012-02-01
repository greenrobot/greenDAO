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

import java.util.Collection;
import java.util.List;

import android.database.Cursor;

/**
 * A repeatable query returning entities.
 * 
 * @author Markus
 * 
 * @param <T>
 *            The enitity class the query will return results for.
 */
// TODO support long, double and other types, not just Strings, for parameters
// TODO Make parameters setable by Property (if unique in paramaters)
// TODO Query for PKs/ROW IDs
public class Query<T> extends AbstractQuery<T> {
    private int limitPosition = -1;
    private int offsetPosition = -1;

    Query(AbstractDao<T, ?> dao, String sql, Collection<Object> valueList) {
        super(dao, sql, valueList);
    }

    void setLimitPosition(int limitPosition) {
        this.limitPosition = limitPosition;
    }

    void setOffsetPosition(int offsetPosition) {
        this.offsetPosition = offsetPosition;
    }

    // public void compile() {
    // // TODO implement compile
    // }

    /**
     * Sets the parameter (0 based) using the position in which it was added during building the query.
     */
    public void setParameter(int index, Object parameter) {
        if (index >= 0 && (index == limitPosition || index == offsetPosition)) {
            throw new IllegalArgumentException("Illegal parameter index: " + index);
        }
        if (parameter != null) {
            parameters[index] = parameter.toString();
        } else {
            parameters[index] = null;
        }
    }

    public void setLimit(int limit) {
        if (limitPosition == -1) {
            throw new IllegalStateException("Limit must be set with QueryBuilder before it can be used here");
        }
        parameters[limitPosition] = Integer.toString(limit);
    }

    public void setOffset(int offset) {
        if (offsetPosition == -1) {
            throw new IllegalStateException("Offset must be set with QueryBuilder before it can be used here");
        }
        parameters[offsetPosition] = Integer.toString(offset);
    }

    /** Executes the query and returns the result as a list containing all entities loaded into memory. */
    public List<T> list() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return dao.loadAllAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the result as a list that lazy loads the entities on first access. Entities are
     * cached, so accessing the same entity more than once will not result in loading an entity from the underlying
     * cursor again.Make sure to close it to close the underlying cursor.
     */
    public LazyList<T> listLazy() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return new LazyList<T>(dao, cursor, true);
    }

    /**
     * Executes the query and returns the result as a list that lazy loads the entities on every access (uncached). Make
     * sure to close the list to close the underlying cursor.
     */
    public LazyList<T> listLazyUncached() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return new LazyList<T>(dao, cursor, false);
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
     * @throws DaoException
     *             if the result is not unique
     * @return Entity or null if no matching entity was found
     */
    public T unique() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return dao.loadUniqueAndCloseCursor(cursor);
    }

    /**
     * Executes the query and returns the unique result (never null).
     * 
     * @throws DaoException
     *             if the result is not unique or no entity was found
     * @return Entity
     */
    public T uniqueOrThrow() {
        T entity = unique();
        if (entity == null) {
            throw new DaoException("No entity found for query");
        }
        return entity;
    }

}
