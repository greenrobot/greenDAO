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

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Internal class to collect WHERE conditions. */
class WhereCollector<T> {

    private final AbstractDao<T, ?> dao;
    private final List<WhereCondition> whereConditions;
    private final String tablePrefix;

    WhereCollector(AbstractDao<T, ?> dao, String tablePrefix) {
        this.dao = dao;
        this.tablePrefix = tablePrefix;
        whereConditions = new ArrayList<WhereCondition>();
    }

    void add(WhereCondition cond, WhereCondition... condMore) {
        checkCondition(cond);
        whereConditions.add(cond);
        for (WhereCondition whereCondition : condMore) {
            checkCondition(whereCondition);
            whereConditions.add(whereCondition);
        }
    }

    WhereCondition combineWhereConditions(String combineOp, WhereCondition cond1, WhereCondition cond2,
                                          WhereCondition... condMore) {
        StringBuilder builder = new StringBuilder("(");
        List<Object> combinedValues = new ArrayList<Object>();

        addCondition(builder, combinedValues, cond1);
        builder.append(combineOp);
        addCondition(builder, combinedValues, cond2);

        for (WhereCondition cond : condMore) {
            builder.append(combineOp);
            addCondition(builder, combinedValues, cond);
        }
        builder.append(')');
        return new WhereCondition.StringCondition(builder.toString(), combinedValues.toArray());
    }

    void addCondition(StringBuilder builder, List<Object> values, WhereCondition condition) {
        checkCondition(condition);
        condition.appendTo(builder, tablePrefix);
        condition.appendValuesTo(values);
    }

    void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof WhereCondition.PropertyCondition) {
            checkProperty(((WhereCondition.PropertyCondition) whereCondition).property);
        }
    }

    void checkProperty(Property property) {
        if (dao != null) {
            Property[] properties = dao.getProperties();
            boolean found = false;
            for (Property property2 : properties) {
                if (property == property2) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new DaoException("Property '" + property.name + "' is not part of " + dao);
            }
        }
    }

    void appendWhereClause(StringBuilder builder, String tablePrefixOrNull, List<Object> values) {
        ListIterator<WhereCondition> iter = whereConditions.listIterator();
        while (iter.hasNext()) {
            if (iter.hasPrevious()) {
                builder.append(" AND ");
            }
            WhereCondition condition = iter.next();
            condition.appendTo(builder, tablePrefixOrNull);
            condition.appendValuesTo(values);
        }
    }

    boolean isEmpty() {
        return whereConditions.isEmpty();
    }
}
