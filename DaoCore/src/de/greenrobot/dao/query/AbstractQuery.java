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

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.InternalQueryDaoAccess;

/**
 * A repeatable query returning entities.
 * 
 * @author Markus
 * 
 * @param <T>
 *            The enitity class the query will return results for.
 */
// TODO support long, double, blob types directly
abstract class AbstractQuery<T> {
    protected final AbstractDao<T, ?> dao;
    protected final InternalQueryDaoAccess<T> daoAccess;
    protected final String sql;
    protected final String[] parameters;
    protected final Thread ownerThread;

    protected static String[] toStringArray(Object[] values) {
        int length = values.length;
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = values[i];
            if (object != null) {
                strings[i] = object.toString();
            } else {
                strings[i] = null;
            }
        }
        return strings;
    }

    protected AbstractQuery(AbstractDao<T, ?> dao, String sql, String[] parameters) {
        this.dao = dao;
        this.daoAccess = new InternalQueryDaoAccess<T>(dao);
        this.sql = sql;
        this.parameters = parameters;
        ownerThread = Thread.currentThread();
    }

    // public void compile() {
    // // TODO implement compile
    // }

    /**
     * Sets the parameter (0 based) using the position in which it was added during building the query.
     */
    public void setParameter(int index, Object parameter) {
        checkThread();
        if (parameter != null) {
            parameters[index] = parameter.toString();
        } else {
            parameters[index] = null;
        }
    }

    protected void checkThread() {
        if (Thread.currentThread() != ownerThread) {
            throw new DaoException(
                    "Method may be called only in owner thread, use forCurrentThread to get an instance for this thread");
        }
    }

}
