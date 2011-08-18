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

public class QueryBuilder<T> {

    private StringBuilder orderBuilder;
    private StringBuilder tableBuilder;
    private StringBuilder joinBuilder;

    private final List<String> whereConditions;

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
        whereConditions = new ArrayList<String>();
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    public QueryBuilder<T> or() {
        whereConditions.add("OR");
        return this;
    }

    public QueryBuilder<T> eq(Property property, Object value) {
        appendWhere(property, "=?", value);
        return this;
    }

    public QueryBuilder<T> whereSql(String rawSqlWhere, Object... values) {
        whereConditions.add(rawSqlWhere);
        for (Object value : values) {
            this.values.add(value);
        }
        return this;
    }

    public QueryBuilder<T> notEq(Property property, Object value) {
        appendWhere(property, "<>?", value);
        return this;
    }

    public QueryBuilder<T> like(Property property, String value) {
        appendWhere(property, " LIKE ?", value);
        return this;
    }

    public QueryBuilder<T> between(Property property, Object value1, Object value2) {
        appendWhere(property, " BETWEEN ? AND ?", value1);
        values.add(value2);
        return this;
    }

    public QueryBuilder<T> in(Property property, Object... inValues) {
        StringBuilder condition = append(new StringBuilder(), property).append(" IN (");
        SqlUtils.appendPlaceholders(condition, inValues.length);
        condition.append(')');
        whereConditions.add(condition.toString());

        for (Object value : inValues) {
            this.values.add(value);
        }
        return this;
    }

    public QueryBuilder<T> gt(Property property, Object value) {
        appendWhere(property, ">?", value);
        return this;
    }

    public QueryBuilder<T> lt(Property property, Object value) {
        appendWhere(property, "<?", value);
        return this;
    }

    public QueryBuilder<T> ge(Property property, Object value) {
        appendWhere(property, ">=?", value);
        return this;
    }

    public QueryBuilder<T> le(Property property, Object value) {
        appendWhere(property, "<=?", value);
        return this;
    }

    public QueryBuilder<T> or(QueryBuilder<T> qb1, QueryBuilder<T> qb2, QueryBuilder<T>... qbs) {
        int len = 2 + qbs.length;
        StringBuilder builder = new StringBuilder("(");
        int size = whereConditions.size();
        int pos = size - len;
        for (int i = 0; i < len; i++) {
            builder.append(whereConditions.get(pos));
            if (i < len - 1) {
                builder.append(" OR ");
            }
            whereConditions.remove(pos);
        }
        builder.append(')');
        whereConditions.add(builder.toString());
        return this;
    }

    protected void appendWhere(Property property, String op, Object value) {
        appendWhere(property, op);
        values.add(value);
    }

    protected void appendWhere(Property property, String op) {
        StringBuilder condition = append(new StringBuilder(), property).append(op);
        whereConditions.add(condition.toString());
    }

    public <J> QueryBuilder<J> join(Class<J> entityClass, Property toOneProperty) {
        return new QueryBuilder<J>();
    }

    public <J> QueryBuilder<J> joinToMany(Class<J> entityClass, Property toManyProperty) {
        @SuppressWarnings("unchecked")
        AbstractDao<J, ?> joinDao = (AbstractDao<J, ?>) dao.getSession().getDao(entityClass);
        return new QueryBuilder<J>(joinDao, "TX");
    }

    public QueryBuilder<T> isNull(Property property) {
        appendWhere(property, " IS NULL");
        return this;
    }

    public QueryBuilder<T> isNotNull(Property property) {
        appendWhere(property, " IS NOT NULL");
        return this;
    }

    public QueryBuilder<T> orderAsc(Property... properties) {
        checkOrderBuilder();
        for (Property property : properties) {
            append(orderBuilder, property).append(" ASC");
        }
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
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
        builder.append("T.").append(property.columnName);
        return builder;
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
            ListIterator<String> iter = whereConditions.listIterator();
            boolean lastWasOr = false;
            while (iter.hasNext()) {
                if (iter.hasPrevious()) {
                    if (lastWasOr) {
                        builder.append(" OR ");
                    } else {
                        builder.append(" AND ");
                    }
                }
                String condition = iter.next();
                lastWasOr = condition.equalsIgnoreCase("OR");
                if (!lastWasOr) {
                    builder.append(condition);
                }
            }
        }


        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }
        
        System.out.println("><>>>" + builder);

        return new Query<T>(dao, builder.toString(), values);
    }
}
