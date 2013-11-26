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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.InternalQueryDaoAccess;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.WhereCondition.PropertyCondition;

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
    private StringBuilder joinBuilder;

    private final List<WhereCondition> whereConditions;

    private final List<Object> values;
    private final AbstractDao<T, ?> dao;
    private final String tablePrefix;

    private Integer limit;

    private Integer offset;

    /** For internal use by greenDAO only. */
    public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2, ?> dao) {
        return new QueryBuilder<T2>(dao);
    }

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

    /**
     * Creates a WhereCondition by combining the given conditions using OR. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    /**
     * Creates a WhereCondition by combining the given conditions using AND. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
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

    /** Not supported yet. */
    public <J> QueryBuilder<J> join(Class<J> entityClass, Property toOneProperty) {
        throw new UnsupportedOperationException();
        // return new QueryBuilder<J>();
    }

    /** Not supported yet. */
    public <J> QueryBuilder<J> joinToMany(Class<J> entityClass, Property toManyProperty) {
        throw new UnsupportedOperationException();
        // @SuppressWarnings("unchecked")
        // AbstractDao<J, ?> joinDao = (AbstractDao<J, ?>) dao.getSession().getDao(entityClass);
        // return new QueryBuilder<J>(joinDao, "TX");
    }

    /** Adds the given properties to the ORDER BY section using ascending order. */
    public QueryBuilder<T> orderAsc(Property... properties) {
        orderAscOrDesc(" ASC", properties);
        return this;
    }

    /** Adds the given properties to the ORDER BY section using descending order. */
    public QueryBuilder<T> orderDesc(Property... properties) {
        orderAscOrDesc(" DESC", properties);
        return this;
    }

    private void orderAscOrDesc(String ascOrDescWithLeadingSpace, Property... properties) {
        for (Property property : properties) {
            checkOrderBuilder();
            append(orderBuilder, property);
            if (String.class.equals(property.type)) {
                orderBuilder.append(" COLLATE LOCALIZED");
            }
            orderBuilder.append(ascOrDescWithLeadingSpace);
        }
    }

    /** Adds the given properties to the ORDER BY section using the given custom order. */
    public QueryBuilder<T> orderCustom(Property property, String customOrderForProperty) {
        checkOrderBuilder();
        append(orderBuilder, property).append(' ');
        orderBuilder.append(customOrderForProperty);
        return this;
    }

    /**
     * Adds the given raw SQL string to the ORDER BY section. Do not use this for standard properties: ordedAsc and
     * orderDesc are prefered.
     */
    public QueryBuilder<T> orderRaw(String rawOrder) {
        checkOrderBuilder();
        orderBuilder.append(rawOrder);
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        checkProperty(property);
        builder.append(tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
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

    /** Limits the number of results returned by queries. */
    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the offset for query results in combination with {@link #limit(int)}. The first {@code limit} results are
     * skipped and the total number of results will be limited by {@code limit}. You cannot use offset without limit.
     */
    public QueryBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Builds a reusable query object (Query objects can be executed more efficiently than creating a QueryBuilder for
     * each execution.
     */
    public Query<T> build() {
        String select;
        if (joinBuilder == null || joinBuilder.length() == 0) {
            select = InternalQueryDaoAccess.getStatements(dao).getSelectAll();
        } else {
            select = SqlUtils.createSqlSelect(dao.getTablename(), tablePrefix, dao.getAllColumns());
        }
        StringBuilder builder = new StringBuilder(select);

        appendWhereClause(builder, tablePrefix);

        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }

        int limitPosition = -1;
        if (limit != null) {
            builder.append(" LIMIT ?");
            values.add(limit);
            limitPosition = values.size() - 1;
        }

        int offsetPosition = -1;
        if (offset != null) {
            if (limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            builder.append(" OFFSET ?");
            values.add(offset);
            offsetPosition = values.size() - 1;
        }

        String sql = builder.toString();
        if (LOG_SQL) {
            DaoLog.d("Built SQL for query: " + sql);
        }

        if (LOG_VALUES) {
            DaoLog.d("Values for query: " + values);
        }

        return Query.create(dao, sql, values.toArray(), limitPosition, offsetPosition);
    }

    /**
     * Builds a reusable query object for deletion (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public DeleteQuery<T> buildDelete() {
        String tablename = dao.getTablename();
        String baseSql = SqlUtils.createSqlDelete(tablename, null);
        StringBuilder builder = new StringBuilder(baseSql);

        // tablePrefix gets replaced by table name below. Don't use tableName here because it causes trouble when
        // table name ends with tablePrefix.
        appendWhereClause(builder, tablePrefix);

        String sql = builder.toString();

        // Remove table aliases, not supported for DELETE queries.
        // TODO(?): don't create table aliases in the first place.
        sql = sql.replace(tablePrefix + ".'", tablename + ".'");

        if (LOG_SQL) {
            DaoLog.d("Built SQL for delete query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for delete query: " + values);
        }

        return DeleteQuery.create(dao, sql, values.toArray());
    }

    /**
     * Builds a reusable query object for counting rows (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public CountQuery<T> buildCount() {
        String tablename = dao.getTablename();
        String baseSql = SqlUtils.createSqlSelectCountStar(tablename, tablePrefix);
        StringBuilder builder = new StringBuilder(baseSql);
        appendWhereClause(builder, tablePrefix);
        String sql = builder.toString();

        if (LOG_SQL) {
            DaoLog.d("Built SQL for count query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for count query: " + values);
        }

        return CountQuery.create(dao, sql, values.toArray());
    }

    private void appendWhereClause(StringBuilder builder, String tablePrefixOrNull) {
        values.clear();
        if (!whereConditions.isEmpty()) {
            builder.append(" WHERE ");
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

    /**
     * Shorthand for {@link QueryBuilder#buildCount() buildCount()}.{@link CountQuery#count() count()}; see
     * {@link CountQuery#count()} for details. To execute a query more than once, you should build the query and keep
     * the {@link CountQuery} object for efficiency reasons.
     */
    public long count() {
        return buildCount().count();
    }

}
