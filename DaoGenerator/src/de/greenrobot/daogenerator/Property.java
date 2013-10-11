/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daogenerator;

import java.util.Calendar;
import java.util.Date;

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

        public PropertyBuilder autoincrement() {
            if (!property.primaryKey || property.propertyType != PropertyType.Long) {
                throw new RuntimeException(
                        "AUTOINCREMENT is only available to primary key properties of type long/Long");
            }
            property.pkAutoincrement = true;
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

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Byte defaultValue) {
            if (property.propertyType != PropertyType.Byte) {
                throw new RuntimeException(
                        "Cannot apply default value of type byte to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Byte(\""+String.valueOf(defaultValue)+"\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Short defaultValue) {
            if (property.propertyType != PropertyType.Short) {
                throw new RuntimeException(
                        "Cannot apply default value of type short to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Short(\"" + String.valueOf(defaultValue) + "\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Integer defaultValue) {
            if (property.propertyType != PropertyType.Int) {
                throw new RuntimeException(
                        "Cannot apply default value of type int to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Integer(\"" + String.valueOf(defaultValue) + "\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Long defaultValue) {
            if (property.propertyType != PropertyType.Long) {
                throw new RuntimeException(
                        "Cannot apply default value of type long to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Long(\"" + String.valueOf(defaultValue) + "\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assigns a default value of 1 (true) or 0 (false) to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Boolean defaultValue) {
            if (property.propertyType != PropertyType.Boolean) {
                throw new RuntimeException(
                        "Cannot apply default value of type boolean to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = (defaultValue ? "true" : "false");
            property.sqlDefaultValue = defaultValue ? "1" : "0";
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Float defaultValue) {
            if (property.propertyType != PropertyType.Float) {
                throw new RuntimeException(
                        "Cannot apply default value of type float to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Float(\"" + String.valueOf(defaultValue) + "\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Double defaultValue) {
            if (property.propertyType != PropertyType.Double) {
                throw new RuntimeException(
                        "Cannot apply default value of type double to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "new Double(\"" + String.valueOf(defaultValue) + "\")";
            property.sqlDefaultValue = String.valueOf(defaultValue);
            return this;
        }

        /**
         * Assign a default value to this property.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(String defaultValue) {
            if (property.propertyType != PropertyType.String) {
                throw new RuntimeException(
                        "Cannot apply default value of type string to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            property.javaDefaultValue = "\""+defaultValue+"\"";
            property.sqlDefaultValue = "\'" + String.valueOf(defaultValue) + "\'";
            return this;
        }

        /**
         * Assign a default value to this property. The date is stored in unix time format.
         * @param defaultValue to be assigned to this property.
         * @return the property builder.
         */
        public PropertyBuilder addDefaultValue(Date defaultValue) {
            if (property.propertyType != PropertyType.Date) {
                throw new RuntimeException(
                        "Cannot apply default value of type java.util.Date to entity of type "+property.propertyType);
            }
            if (property.primaryKey ) {
                throw new RuntimeException("Cannot apply DEFAULT constraint to a primary key.");
            }
            Calendar time = Calendar.getInstance();
            time.setTime(defaultValue);
            property.javaDefaultValue = "new java.util.Date(" + time.getTimeInMillis()+ "L)";
            //store date in unix time
            property.sqlDefaultValue = String.valueOf(time.getTimeInMillis()/1000);
            return this;
        }

        public PropertyBuilder indexAsc(String indexNameOrNull, boolean isUnique) {
            Index index = new Index();
            index.addPropertyAsc(property);
            if (isUnique) {
                index.makeUnique();
            }
            index.setName(indexNameOrNull);
            property.entity.addIndex(index);
            return this;
        }

        public PropertyBuilder indexDesc(String indexNameOrNull, boolean isUnique) {
            Index index = new Index();
            index.addPropertyDesc(property);
            if (isUnique) {
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

    private final Schema schema;
    private final Entity entity;
    private PropertyType propertyType;
    private final String propertyName;

    private String columnName;
    private String columnType;

    private boolean primaryKey;
    private boolean pkAsc;
    private boolean pkDesc;
    private boolean pkAutoincrement;

    private boolean unique;
    private boolean notNull;

    //Value to be assigned in the sqlite DEFAULT column constraint
    private String sqlDefaultValue;
    private String javaDefaultValue;

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

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
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

    public boolean isAutoincrement() {
        return pkAutoincrement;
    }

    public String getConstraints() {
        return constraints;
    }

    /**
     * Returns the default value in the format expected by the sql DEFAULT constraint
     * @return the default value
     */
    public String getSqlDefaultValue() {
        return sqlDefaultValue;
    }

    /**
     * Returns the default value in the from 'new [Type]("value)'. Ex, 'new Short("0")'.
     * @return the default value
     */
    public String getJavaDefaultValue() {
        return javaDefaultValue;
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

    public Entity getEntity() {
        return entity;
    }

    void init2ndPass() {
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
        if (primaryKey) {
            constraintBuilder.append("PRIMARY KEY");
            if (pkAsc) {
                constraintBuilder.append(" ASC");
            }
            if (pkDesc) {
                constraintBuilder.append(" DESC");
            }
            if (pkAutoincrement) {
                constraintBuilder.append(" AUTOINCREMENT");
            }
        }
        // Always have String PKs NOT NULL because SQLite is pretty strange in this respect:
        // One could insert multiple rows with NULL PKs
        if (notNull || (primaryKey && propertyType == PropertyType.String)) {
            constraintBuilder.append(" NOT NULL");
        }
        if (unique) {
            constraintBuilder.append(" UNIQUE");
        }
        if (sqlDefaultValue != null) {
            constraintBuilder.append(" DEFAULT "+sqlDefaultValue);
        }
        String newContraints = constraintBuilder.toString().trim();
        if (constraintBuilder.length() > 0) {
            constraints = newContraints;
        }
    }

    void init3ndPass() {
        // Nothing to do so far
    }

    @Override
    public String toString() {
        return "Property " + propertyName + " of " + entity.getClassName();
    }

}
