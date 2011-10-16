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
public class Query<T> {
    private AbstractDao<T, ?> dao;
    private final String sql;
    private final String[] parameters;

    public Query(AbstractDao<T, ?> dao, String sql, Collection<Object> valueList) {
        this.dao = dao;
        this.sql = sql;

        parameters = new String[valueList.size()];
        int idx = 0;
        for (Object object : valueList) {
            if (object != null) {
                parameters[idx] = object.toString();
            } else {
                parameters[idx] = null;
            }
            idx++;
        }
    }

    // public void compile() {
    // // TODO implement compile
    // }

    /**
     * Sets the parameter (0 based) using the position in which it was added during building the query.
     */
    public void setParameter(int index, Object parameter) {
        if (parameter != null) {
            parameters[index] = parameter.toString();
        } else {
            parameters[index] = null;
        }
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
