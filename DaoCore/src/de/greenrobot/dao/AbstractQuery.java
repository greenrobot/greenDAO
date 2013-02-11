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
package de.greenrobot.dao;


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
    protected final String sql;
    protected final String[] parameters;

    protected AbstractQuery(AbstractDao<T, ?> dao, String sql, Object[] values) {
        this.dao = dao;
        this.sql = sql;

        parameters = new String[values.length];
        int idx = 0;
        for (Object object : values) {
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

}
