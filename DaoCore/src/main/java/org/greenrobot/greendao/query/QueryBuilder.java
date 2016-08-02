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

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds custom entity queries using constraints and parameters and without SQL (QueryBuilder creates SQL for you). To
 * acquire an QueryBuilder, use {@link AbstractDao#queryBuilder()} or {@link AbstractDaoSession#queryBuilder(Class)}.
 * Entity properties are referenced by Fields in the "Properties" inner class of the generated DAOs. This approach
 * allows compile time checks and prevents typo errors occuring at build time.<br/>
 * <br/>
 * Example: Query for all users with the first name "Joe" ordered by their last name. (The class Properties is an inner
 * class of UserDao and should be imported before.)<br/>
 * <code>
 * List<User> joes = dao.queryBuilder().where(Properties.FirstName.eq("Joe")).orderAsc(Properties.LastName).list();
 * </code>
 *
 * @param <T> Entity class to create an query for.
 * @author Markus
 */
public class QueryBuilder<T> {

    /** Set to true to debug the SQL. */
    public static boolean LOG_SQL;

    /** Set to see the given values. */
    public static boolean LOG_VALUES;
    private final WhereCollector<T> whereCollector;

    private StringBuilder orderBuilder;

    private final List<Object> values;
    private final List<Join<T, ?>> joins;
    private final AbstractDao<T, ?> dao;
    private final String tablePrefix;

    private Integer limit;
    private Integer offset;
    private boolean distinct;

    /** stored with a leading space */
    private String stringOrderCollation;

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
        joins = new ArrayList<Join<T, ?>>();
        whereCollector = new WhereCollector<T>(dao, tablePrefix);
        stringOrderCollation = " COLLATE NOCASE";
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    /** Use a SELECT DISTINCT to avoid duplicate entities returned, e.g. when doing joins. */
    public QueryBuilder<T> distinct() {
        distinct = true;
        return this;
    }

    /**
     * If using Android's embedded SQLite, this enables localized ordering of strings
     * (see {@link #orderAsc(Property...)} and {@link #orderDesc(Property...)}). This uses "COLLATE LOCALIZED", which
     * is unavailable in SQLCipher (in that case, the ordering is unchanged).
     *
     * @see #stringOrderCollation
     */
    public QueryBuilder<T> preferLocalizedStringOrder() {
        // SQLCipher 3.5.0+ does not understand "COLLATE LOCALIZED"
        if (dao.getDatabase().getRawDatabase() instanceof SQLiteDatabase) {
            stringOrderCollation = " COLLATE LOCALIZED";
        }
        return this;
    }

    /**
     * Customizes the ordering of strings used by {@link #orderAsc(Property...)} and {@link #orderDesc(Property...)}.
     * Default is "COLLATE NOCASE".
     *
     * @see #preferLocalizedStringOrder
     */
    public QueryBuilder<T> stringOrderCollation(String stringOrderCollation) {
        // SQLCipher 3.5.0+ does not understand "COLLATE LOCALIZED"
        if (dao.getDatabase().getRawDatabase() instanceof SQLiteDatabase) {
            this.stringOrderCollation = stringOrderCollation == null || stringOrderCollation.startsWith(" ") ?
                    stringOrderCollation : " " + stringOrderCollation;
        }
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical AND. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        whereCollector.add(cond, condMore);
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical OR. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        whereCollector.add(or(cond1, cond2, condMore));
        return this;
    }

    /**
     * Creates a WhereCondition by combining the given conditions using OR. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    /**
     * Creates a WhereCondition by combining the given conditions using AND. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The primary key property of the primary entity for
     * this QueryBuilder is used to match the given destinationProperty.
     */
    public <J> Join<T, J> join(Class<J> destinationEntityClass, Property destinationProperty) {
        return join(dao.getPkProperty(), destinationEntityClass, destinationProperty);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the primary
     * key property of the given destinationEntity.
     */
    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass) {
        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
        Property destinationProperty = destinationDao.getPkProperty();
        return addJoin(tablePrefix, sourceProperty, destinationDao, destinationProperty);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceProperty is used to match the given
     * destinationProperty of the given destinationEntity.
     */
    public <J> Join<T, J> join(Property sourceProperty, Class<J> destinationEntityClass, Property destinationProperty) {
        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
        return addJoin(tablePrefix, sourceProperty, destinationDao, destinationProperty);
    }

    /**
     * Expands the query to another entity type by using a JOIN. The given sourceJoin's property is used to match the
     * given destinationProperty of the given destinationEntity. Note that destination entity of the given join is used
     * as the source for the new join to add. In this way, it is possible to compose complex "join of joins" across
     * several entities if required.
     */
    public <J> Join<T, J> join(Join<?, T> sourceJoin, Property sourceProperty, Class<J> destinationEntityClass,
                               Property destinationProperty) {
        AbstractDao<J, ?> destinationDao = (AbstractDao<J, ?>) dao.getSession().getDao(destinationEntityClass);
        return addJoin(sourceJoin.tablePrefix, sourceProperty, destinationDao, destinationProperty);
    }

    private <J> Join<T, J> addJoin(String sourceTablePrefix, Property sourceProperty, AbstractDao<J, ?> destinationDao,
                                   Property destinationProperty) {
        String joinTablePrefix = "J" + (joins.size() + 1);
        Join<T, J> join = new Join<T, J>(sourceTablePrefix, sourceProperty, destinationDao, destinationProperty,
                joinTablePrefix);
        joins.add(join);
        return join;
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
            if (String.class.equals(property.type) && stringOrderCollation != null) {
                orderBuilder.append(stringOrderCollation);
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
     * Adds the given raw SQL string to the ORDER BY section. Do not use this for standard properties: orderAsc and
     * orderDesc are preferred.
     */
    public QueryBuilder<T> orderRaw(String rawOrder) {
        checkOrderBuilder();
        orderBuilder.append(rawOrder);
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        whereCollector.checkProperty(property);
        builder.append(tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
        return builder;
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
        StringBuilder builder = createSelectBuilder();
        int limitPosition = checkAddLimit(builder);
        int offsetPosition = checkAddOffset(builder);

        String sql = builder.toString();
        checkLog(sql);

        return Query.create(dao, sql, values.toArray(), limitPosition, offsetPosition);
    }

    /**
     * Builds a reusable query object for low level android.database.Cursor access.
     * (Query objects can be executed more efficiently than creating a QueryBuilder for each execution.
     */
    public CursorQuery buildCursor() {
        StringBuilder builder = createSelectBuilder();
        int limitPosition = checkAddLimit(builder);
        int offsetPosition = checkAddOffset(builder);

        String sql = builder.toString();
        checkLog(sql);

        return CursorQuery.create(dao, sql, values.toArray(), limitPosition, offsetPosition);
    }

    private StringBuilder createSelectBuilder() {
        String select = SqlUtils.createSqlSelect(dao.getTablename(), tablePrefix, dao.getAllColumns(), distinct);
        StringBuilder builder = new StringBuilder(select);

        appendJoinsAndWheres(builder, tablePrefix);

        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }
        return builder;
    }

    private int checkAddLimit(StringBuilder builder) {
        int limitPosition = -1;
        if (limit != null) {
            builder.append(" LIMIT ?");
            values.add(limit);
            limitPosition = values.size() - 1;
        }
        return limitPosition;
    }

    private int checkAddOffset(StringBuilder builder) {
        int offsetPosition = -1;
        if (offset != null) {
            if (limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            builder.append(" OFFSET ?");
            values.add(offset);
            offsetPosition = values.size() - 1;
        }
        return offsetPosition;
    }

    /**
     * Builds a reusable query object for deletion (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public DeleteQuery<T> buildDelete() {
        if (!joins.isEmpty()) {
            throw new DaoException("JOINs are not supported for DELETE queries");
        }
        String tablename = dao.getTablename();
        String baseSql = SqlUtils.createSqlDelete(tablename, null);
        StringBuilder builder = new StringBuilder(baseSql);

        // tablePrefix gets replaced by table name below. Don't use tableName here because it causes trouble when
        // table name ends with tablePrefix.
        appendJoinsAndWheres(builder, tablePrefix);

        String sql = builder.toString();
        // Remove table aliases, not supported for DELETE queries.
        // TODO(?): don't create table aliases in the first place.
        sql = sql.replace(tablePrefix + ".\"", '"' + tablename + "\".\"");
        checkLog(sql);

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
        appendJoinsAndWheres(builder, tablePrefix);

        String sql = builder.toString();
        checkLog(sql);

        return CountQuery.create(dao, sql, values.toArray());
    }

    private void checkLog(String sql) {
        if (LOG_SQL) {
            DaoLog.d("Built SQL for query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.d("Values for query: " + values);
        }
    }

    private void appendJoinsAndWheres(StringBuilder builder, String tablePrefixOrNull) {
        values.clear();
        for (Join<T, ?> join : joins) {
            builder.append(" JOIN ").append(join.daoDestination.getTablename()).append(' ');
            builder.append(join.tablePrefix).append(" ON ");
            SqlUtils.appendProperty(builder, join.sourceTablePrefix, join.joinPropertySource).append('=');
            SqlUtils.appendProperty(builder, join.tablePrefix, join.joinPropertyDestination);
        }
        boolean whereAppended = !whereCollector.isEmpty();
        if (whereAppended) {
            builder.append(" WHERE ");
            whereCollector.appendWhereClause(builder, tablePrefixOrNull, values);
        }
        for (Join<T, ?> join : joins) {
            if (!join.whereCollector.isEmpty()) {
                if (!whereAppended) {
                    builder.append(" WHERE ");
                    whereAppended = true;
                } else {
                    builder.append(" AND ");
                }
                join.whereCollector.appendWhereClause(builder, join.tablePrefix, values);
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
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#__InternalRx()}.
     */
    @Experimental
    public RxQuery<T> rx() {
        return build().__InternalRx();
    }

    /**
     * Shorthand for {@link QueryBuilder#build() build()}.{@link Query#__internalRxPlain()}.
     */
    @Experimental
    public RxQuery<T> rxPlain() {
        return build().__internalRxPlain();
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
     * {@link Query#uniqueOrThrow()} for details. To execute a query more than once, you should build the query and
     * keep
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
