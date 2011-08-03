package de.greenrobot.daogenerator;

/** Model class for an entity's property: a Java property mapped to a data base column. */
public class Property {

    public static class ColumnBuilder {
        private final Property column;

        public ColumnBuilder(PropertyType propertyType, String propertyName) {
            column = new Property(propertyType, propertyName);
        }

        public ColumnBuilder columnName(String columnName) {
            column.setColumnName(columnName);
            return this;
        }

        public ColumnBuilder columnType(String columnType) {
            column.setColumnType(columnType);
            return this;
        }

        public ColumnBuilder primaryKey() {
            column.setPrimaryKey(true);
            return this;
        }

        public ColumnBuilder asc() {
            if (!column.isPrimaryKey()) {
                throw new RuntimeException("asc/desc is only available to foreign key columns");
            }
            column.pkAsc = true;
            column.pkDesc = false;
            return this;
        }

        public ColumnBuilder desc() {
            if (!column.isPrimaryKey()) {
                throw new RuntimeException("asc/desc is only available to foreign key columns");
            }
            column.pkDesc = true;
            column.pkAsc = false;
            return this;
        }

        public ColumnBuilder unique() {
            column.setUnique(true);
            return this;
        }

        public ColumnBuilder notNull() {
            column.setNotNull(true);
            return this;
        }

        public ColumnBuilder setForeignKeyTable(String foreignKeyTable) {
            column.setForeignKeyTable(foreignKeyTable);
            return this;
        }

        public Property build() {
            return column;
        }
    }

    private final PropertyType propertyType;
    private final String propertyName;
    private String columnName;
    private String columnType;
    private boolean primaryKey;
    private boolean pkAsc;
    private boolean pkDesc;

    private boolean unique;
    private boolean notNull;
    private String foreignKeyTable;

    /** Initialized in 2nd pass */
    private String constraints;

    private String javaType;

    public Property(PropertyType propertyType, String propertyName) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        columnName = DaoUtil.dbName(propertyName);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }

    public String getJavaType() {
        return javaType;
    }

    void init2ndPass(Schema schema, Entity table) {
        initConstraint();
        if (columnType == null) {
            columnType = schema.mapToDbType(propertyType);
        }
        if (notNull) {
            javaType = schema.mapToJavaTypeNotNull(propertyType);
        } else {
            javaType = schema.mapToJavaTypeNullable(propertyType);
        }
    }

    private void initConstraint() {
        String constraint = "";
        if (isPrimaryKey()) {
            constraint += "PRIMARY KEY";
            if (pkAsc) {
                constraint += " ASC";
            }
            if (pkDesc) {
                constraint += " DESC";
            }
        }
        if (notNull) {
            constraint += " NOT NULL";
        }
        if (unique) {
            constraint += " UNIQUE";
        }
        constraint = constraint.trim();
        if (constraint.length() > 0) {
            setConstraints(constraint);
        }
    }

}
