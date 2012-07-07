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

import de.greenrobot.dao.wrapper.SQLiteStatementWrapper;

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

    private SQLiteStatementWrapper compiledStatement;

    public DeleteQuery(AbstractDao<T, ?> dao, String sql, Collection<Object> valueList) {
        super(dao, sql, valueList);
    }

    /**
     * Deletes all matching entities without detaching them from the identity scope (aka session/cache). Note that this
     * method may lead to stale entity objects in the session cache. Stale entities may be returned when loaded by their
     * primary key, but not using queries.
     */
    public synchronized void executeDeleteWithoutDetachingEntities() {
        if (compiledStatement != null) {
            compiledStatement.clearBindings();
        } else {
            compiledStatement = dao.db.compileStatement(sql);
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
