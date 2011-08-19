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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.greenrobot.dao.WhereCondition.PropertyCondition;

public class QueryBuilder<T> {

    /** Set to true to debug the SQL. */
    public static boolean LOG_SQL;

    /** Set to see the given values. */
    public static boolean LOG_VALUES;

    private StringBuilder orderBuilder;
    private StringBuilder tableBuilder;
    private StringBuilder joinBuilder;

    private final List<WhereCondition> whereConditions;

    private final List<Object> values;
    private final AbstractDao<T, ?> dao;
    private final String tablePrefix;

    public QueryBuilder() {
        this(null, "T");
    }

    public QueryBuilder(AbstractDao<T, ?> dao) {
        this(dao, "T");
    }

    public QueryBuilder(AbstractDao<T, ?> dao, String tablePrefix) {
        this.dao = dao;
        this.tablePrefix = tablePrefix;
        values = new ArrayList<Object>();
        whereConditions = new ArrayList<WhereCondition>();
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        whereConditions.add(cond);
        for (WhereCondition whereCondition : condMore) {
            checkCondition(whereCondition);
            whereConditions.add(whereCondition);
        }
        return this;
    }

    protected void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof PropertyCondition) {
            checkProperty(((PropertyCondition) whereCondition).property);
        }
    }

    public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        whereConditions.add(or(cond1, cond2, condMore));
        return this;
    }

    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    protected WhereCondition combineWhereConditions(String combineOp, WhereCondition cond1, WhereCondition cond2,
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

    protected void addCondition(StringBuilder builder, List<Object> values, WhereCondition condition) {
        checkCondition(condition);
        condition.appendTo(builder, tablePrefix);
        condition.appendValuesTo(values);
    }

    public <J> QueryBuilder<J> join(Class<J> entityClass, Property toOneProperty) {
        throw new UnsupportedOperationException();
        // return new QueryBuilder<J>();
    }

    public <J> QueryBuilder<J> joinToMany(Class<J> entityClass, Property toManyProperty) {
        throw new UnsupportedOperationException();
        // @SuppressWarnings("unchecked")
        // AbstractDao<J, ?> joinDao = (AbstractDao<J, ?>) dao.getSession().getDao(entityClass);
        // return new QueryBuilder<J>(joinDao, "TX");
    }

    public QueryBuilder<T> orderAsc(Property... properties) {
        checkOrderBuilder();
        for (Property property : properties) {
            append(orderBuilder, property).append(" ASC");
        }
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        checkProperty(property);
        builder.append(tablePrefix).append('.').append(property.columnName);
        return builder;
    }

    protected void checkProperty(Property property) {
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

    public Query<T> build() {
        String select;
        if (joinBuilder == null || joinBuilder.length() == 0) {
            select = dao.getStatements().getSelectAll();
        } else {
            select = SqlUtils.createSqlSelect(dao.getTablename(), tablePrefix, dao.getAllColumns());
        }
        StringBuilder builder = new StringBuilder(select);

        if (!whereConditions.isEmpty()) {
            builder.append(" WHERE ");
            ListIterator<WhereCondition> iter = whereConditions.listIterator();
            while (iter.hasNext()) {
                if (iter.hasPrevious()) {
                    builder.append(" AND ");
                }
                WhereCondition condition = iter.next();
                condition.appendTo(builder, tablePrefix);
                condition.appendValuesTo(values);
            }
        }

        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }

        String sql = builder.toString();
        if (LOG_SQL) {
            DaoLog.d("Built SQL: " + sql);
        }

        if (LOG_VALUES) {
            DaoLog.d("Collected values: " + values);
        }

        return new Query<T>(dao, sql, values);
    }
}
