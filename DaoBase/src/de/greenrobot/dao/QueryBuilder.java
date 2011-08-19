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

/**
 * Builds custom entity queries using constraints and parameters and without SQL (QueryBuilder creates SQL for you). To
 * acquire an QueryBuilder, use {@link AbstractDao#queryBuilder()} or {@link AbstractDaoSession#queryBuilder(Class)}.
 * Entity properties are referenced by Fields in the "Properties" inner class of the generated DAOs. This approach
 * allows compile time checks and prevents typo errors occuring at build time.<br/>
 * <br/>
 * Example: Query for all users with the first name "Joe" ordered by their last name. (The class Properties is an inner
 * class of UserDao and should be imported before.)<br/>
 * <code>
 *  List<User> joes = dao.queryBuilder().where(Properties.FirstName.eq("Joe")).orderAsc(Properties.LastName).list();
 *  </code>
 * 
 * @author Markus
 * 
 * @param <T>
 *            Entity class to create an query for.
 */
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

    protected QueryBuilder(AbstractDao<T, ?> dao) {
        this(dao, "T");
    }

    protected QueryBuilder(AbstractDao<T, ?> dao, String tablePrefix) {
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

    /**
     * Adds the given conditions to the where clause using an logical AND. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        whereConditions.add(cond);
        for (WhereCondition whereCondition : condMore) {
            checkCondition(whereCondition);
            whereConditions.add(whereCondition);
        }
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical OR. To create new conditions, use the properties
     * given in the generated dao classes.
     */
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

    protected void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof PropertyCondition) {
            checkProperty(((PropertyCondition) whereCondition).property);
        }
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

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#list() list()}; see {@link Query#list()} for
     * details. To execute a query more than once, you should build the query and keep the {@link Query} object for
     * efficiency reasons.
     */
    public List<T> list() {
        return build().list();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listLazy() listLazy()}; see
     * {@link Query#listLazy()} for details. To execute a query more than once, you should build the query and keep the
     * {@link Query} object for efficiency reasons.
     */
    public LazyList<T> listLazy() {
        return build().listLazy();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listLazyUncached() listLazyUncached()}; see
     * {@link Query#listLazyUncached()} for details. To execute a query more than once, you should build the query and
     * keep the {@link Query} object for efficiency reasons.
     */
    public LazyList<T> listLazyUncached() {
        return build().listLazyUncached();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#listIterator() listIterator()}; see
     * {@link Query#listIterator()} for details. To execute a query more than once, you should build the query and keep
     * the {@link Query} object for efficiency reasons.
     */
    public CloseableListIterator<T> listIterator() {
        return build().listIterator();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#unique() unique()}; see {@link Query#unique()}
     * for details. To execute a query more than once, you should build the query and keep the {@link Query} object for
     * efficiency reasons.
     */
    public T unique() {
        return build().unique();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#uniqueOrThrow() uniqueOrThrow()}; see
     * {@link Query#uniqueOrThrow()} for details. To execute a query more than once, you should build the query and keep
     * the {@link Query} object for efficiency reasons.
     */
    public T uniqueOrThrow() {
        return build().uniqueOrThrow();
    }

}
