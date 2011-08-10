package de.greenrobot.daogenerator;


/** Model class for an entity's property: a Java property mapped to a data base column. */
public class Property {

    public static class PropertyBuilder {
        private final Property property;

        public PropertyBuilder(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
            property = new Property(schema, entity, propertyType, propertyName);
        }

        public PropertyBuilder columnName(String columnName) {
            property.columnName = columnName;
            return this;
        }

        public PropertyBuilder columnType(String columnType) {
            property.columnType = columnType;
            return this;
        }

        public PropertyBuilder primaryKey() {
            property.primaryKey = true;
            return this;
        }

        public PropertyBuilder primaryKeyAsc() {
            property.primaryKey = true;
            property.pkAsc = true;
            return this;
        }

        public PropertyBuilder primaryKeyDesc() {
            property.primaryKey = true;
            property.pkDesc = true;
            return this;
        }

        public PropertyBuilder unique() {
            property.unique = true;
            return this;
        }

        public PropertyBuilder notNull() {
            property.notNull = true;
            return this;
        }

        public PropertyBuilder index() {
            Index index = new Index();
            index.addProperty(property);
            property.entity.addIndex(index);
            return this;
        }

        public PropertyBuilder indexAsc(String indexNameOrNull, boolean isUnique) {
            Index index = new Index();
            index.addPropertyAsc(property);
            if(isUnique) {
                index.makeUnique();
            }
            index.setName(indexNameOrNull);
            property.entity.addIndex(index);
            return this;
        }

        public PropertyBuilder indexDesc(String indexNameOrNull, boolean isUnique) {
            Index index = new Index();
            index.addPropertyDesc(property);
            if(isUnique) {
                index.makeUnique();
            }
            index.setName(indexNameOrNull);
            property.entity.addIndex(index);
            return this;
        }

        public Property getProperty() {
            return property;
        }
    }

    @SuppressWarnings("unused")
    private final Schema schema;
    private final Entity entity;
    private final PropertyType propertyType;
    private final String propertyName;

    private String columnName;
    private String columnType;
    private boolean primaryKey;
    private boolean pkAsc;
    private boolean pkDesc;

    private boolean unique;
    private boolean notNull;

    /** Initialized in 2nd pass */
    private String constraints;
    
    private int ordinal;

    private String javaType;

    public Property(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
        this.schema = schema;
        this.entity = entity;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
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

    public String getColumnType() {
        return columnType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getConstraints() {
        return constraints;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public String getJavaType() {
        return javaType;
    }
    
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    void init2ndPass(Schema schema, Entity table) {
        initConstraint();
        if (columnType == null) {
            columnType = schema.mapToDbType(propertyType);
        }
        if (columnName == null) {
            columnName = DaoUtil.dbName(propertyName);
        }
        if (notNull) {
            javaType = schema.mapToJavaTypeNotNull(propertyType);
        } else {
            javaType = schema.mapToJavaTypeNullable(propertyType);
        }
    }

    private void initConstraint() {
        StringBuilder constraintBuilder = new StringBuilder();
        if (isPrimaryKey()) {
            constraintBuilder.append("PRIMARY KEY");
            if (pkAsc) {
                constraintBuilder.append(" ASC");
            }
            if (pkDesc) {
                constraintBuilder.append(" DESC");
            }
        }
        if (notNull) {
            constraintBuilder.append(" NOT NULL");
        }
        if (unique) {
            constraintBuilder.append(" UNIQUE");
        }
        String newContraints = constraintBuilder.toString().trim();
        if (constraintBuilder.length() > 0) {
            constraints = newContraints;
        }
    }

}
