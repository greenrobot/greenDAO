package de.greenrobot.dao;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T> {

    private StringBuilder whereBuilder;
    private StringBuilder orderBuilder;
    private StringBuilder tableBuilder;
    private StringBuilder joinBuilder;

    private List<Object> values;
    private AbstractDao<T, ?> dao;
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
    }

    private void checkWhereBuilder() {
        if (whereBuilder == null) {
            whereBuilder = new StringBuilder();
        } else if (whereBuilder.length() > 0) {
            whereBuilder.append(" AND ");
        }
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    public QueryBuilder<T> eq(Property property, Object value) {
        appendWhere(property, "=?", value);
        return this;
    }

    public QueryBuilder<T> whereSql(String rawSqlWhere, Object... values) {
        checkWhereBuilder();
        whereBuilder.append(rawSqlWhere);
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
        checkWhereBuilder();
        append(whereBuilder, property).append(" IN (");
        SqlUtils.appendPlaceholders(whereBuilder, inValues.length);
        whereBuilder.append(')');
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

    protected void appendWhere(Property property, String op, Object value) {
        checkWhereBuilder();
        append(whereBuilder, property).append(op);
        values.add(value);
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
        checkWhereBuilder();
        append(whereBuilder, property).append(" IS NULL");
        return this;
    }

    public QueryBuilder<T> isNotNull(Property property) {
        checkWhereBuilder();
        whereBuilder.append(property.columnName).append(" IS NOT NULL");
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
        builder.append(" WHERE ").append(whereBuilder);
        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append("ORDER BY ").append(orderBuilder);
        }
        return new Query<T>(dao, builder.toString(), values);
    }
}
