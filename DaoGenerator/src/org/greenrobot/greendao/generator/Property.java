/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.generator;

/** Model class for an entity's property: a Java property mapped to a data base column. */
@SuppressWarnings("unused")
public class Property {

    public static class PropertyBuilder {
        private final Property property;

        public PropertyBuilder(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
            property = new Property(schema, entity, propertyType, propertyName);
        }

        @Deprecated
        /**
         * @deprecated use dbName
         */
        public PropertyBuilder columnName(String columnName) {
            return dbName(columnName);
        }

        public PropertyBuilder dbName(String dbName) {
            property.dbName = dbName;
            property.nonDefaultDbName = dbName != null;
            return this;
        }

        @Deprecated
        /**
         * @deprecated use dbType
         */
        public PropertyBuilder columnType(String columnType) {
            return dbType(columnType);
        }

        public PropertyBuilder dbType(String dbType) {
            property.dbType = dbType;
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

        public PropertyBuilder nonPrimitiveType() {
            if (!property.propertyType.isScalar()) {
                throw new RuntimeException("Type is already non-primitive");
            }
            property.nonPrimitiveType = true;
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

        public PropertyBuilder customType(String customType, String converter) {
            property.customType = customType;
            property.customTypeClassName = DaoUtil.getClassnameFromFullyQualified(customType);
            property.converter = converter;
            property.converterClassName = DaoUtil.getClassnameFromFullyQualified(converter);
            return this;
        }

        public PropertyBuilder codeBeforeField(String code) {
            property.codeBeforeField = code;
            return this;
        }

        public PropertyBuilder codeBeforeGetter(String code) {
            property.codeBeforeGetter = code;
            return this;
        }

        public PropertyBuilder codeBeforeSetter(String code) {
            property.codeBeforeSetter = code;
            return this;
        }

        public PropertyBuilder codeBeforeGetterAndSetter(String code) {
            property.codeBeforeGetter = code;
            property.codeBeforeSetter = code;
            return this;
        }

        public PropertyBuilder javaDocField(String javaDoc) {
            property.javaDocField = checkConvertToJavaDoc(javaDoc);
            return this;
        }

        private String checkConvertToJavaDoc(String javaDoc) {
            return DaoUtil.checkConvertToJavaDoc(javaDoc, "    ");
        }

        public PropertyBuilder javaDocGetter(String javaDoc) {
            property.javaDocGetter = checkConvertToJavaDoc(javaDoc);
            return this;
        }

        public PropertyBuilder javaDocSetter(String javaDoc) {
            property.javaDocSetter = checkConvertToJavaDoc(javaDoc);
            return this;
        }

        public PropertyBuilder javaDocGetterAndSetter(String javaDoc) {
            javaDoc = checkConvertToJavaDoc(javaDoc);
            property.javaDocGetter = javaDoc;
            property.javaDocSetter = javaDoc;
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

    private String dbName;
    private String dbType;

    private String customType;
    private String customTypeClassName;
    private String converter;
    private String converterClassName;

    private String codeBeforeField;
    private String codeBeforeGetter;
    private String codeBeforeSetter;

    private String javaDocField;
    private String javaDocGetter;
    private String javaDocSetter;

    private boolean primaryKey;
    private boolean pkAsc;
    private boolean pkDesc;
    private boolean pkAutoincrement;

    private boolean unique;
    private boolean notNull;
    private boolean nonPrimitiveType;

    /** Initialized in 2nd pass */
    private String constraints;

    private int ordinal;

    private String javaType;

    private boolean nonDefaultDbName;

    /**
     * Index, which has only this property
     * Can be added by user via {@link PropertyBuilder} or via {@link Entity#addIndex(Index)}
     * Initialized in 2nd pass
     */
    private Index index;

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

    public String getDbName() {
        return dbName;
    }

    public boolean isNonDefaultDbName() {
        return nonDefaultDbName;
    }

    public String getDbType() {
        return dbType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isPkAsc() {
        return pkAsc;
    }

    public boolean isPkDesc() {
        return pkDesc;
    }

    public boolean isAutoincrement() {
        return pkAutoincrement;
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

    public boolean isNonPrimitiveType() {
        return nonPrimitiveType || !propertyType.isScalar();
    }

    public String getJavaType() {
        return javaType;
    }

    public String getJavaTypeInEntity() {
        if (customTypeClassName != null) {
            return customTypeClassName;
        } else {
            return javaType;
        }
    }

    public int getOrdinal() {
        return ordinal;
    }

    void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getCustomType() {
        return customType;
    }

    public String getCustomTypeClassName() {
        return customTypeClassName;
    }

    public String getConverter() {
        return converter;
    }

    public String getConverterClassName() {
        return converterClassName;
    }

    public String getCodeBeforeField() {
        return codeBeforeField;
    }

    public String getCodeBeforeGetter() {
        return codeBeforeGetter;
    }

    public String getCodeBeforeSetter() {
        return codeBeforeSetter;
    }

    public String getJavaDocField() {
        return javaDocField;
    }

    public String getJavaDocGetter() {
        return javaDocGetter;
    }

    public String getJavaDocSetter() {
        return javaDocSetter;
    }

    public String getDatabaseValueExpression() {
        return getDatabaseValueExpression(propertyName);
    }

    public String getDatabaseValueExpressionNotNull() {
        return getDatabaseValueExpression("entity.get" + DaoUtil.capFirst(propertyName) + "()");
    }

    // Got too messy in template:
    // <#if property.customType?has_content>${property.propertyName}Converter.convertToDatabaseValue(</#if><#--
    // -->entity.get${property.propertyName?cap_first}()<#if property.customType?has_content>)</#if><#if
    // property.propertyType == "Boolean"> ? 1l: 0l</#if><#if property.propertyType == "Date">.getTime()</#if>
    public String getDatabaseValueExpression(String entityValue) {
        StringBuilder builder = new StringBuilder();
        if (customType != null) {
            builder.append(propertyName).append("Converter.convertToDatabaseValue(");
        }
        builder.append(entityValue);
        if (customType != null) {
            builder.append(')');
        }
        if (propertyType == PropertyType.Boolean) {
            builder.append(" ? 1L: 0L");
        } else if (propertyType == PropertyType.Date) {
            builder.append(".getTime()");
        }
        return builder.toString();
    }

    // Got too messy in template:
    // <#if property.propertyType == "Byte">(byte) </#if>
    // <#if property.propertyType == "Date">new java.util.Date(</#if>
    // cursor.get${toCursorType[property.propertyType]}(offset + ${property_index})
    // <#if property.propertyType == "Boolean"> != 0</#if>
    // <#if property.propertyType == "Date">)</#if>
    public String getEntityValueExpression(String databaseValue) {
        StringBuilder builder = new StringBuilder();
        if (customType != null) {
            builder.append(propertyName).append("Converter.convertToEntityProperty(");
        }
        if (propertyType == PropertyType.Byte) {
            builder.append("(byte) ");
        } else if (propertyType == PropertyType.Date) {
            builder.append("new java.util.Date(");
        }
        builder.append(databaseValue);
        if (propertyType == PropertyType.Boolean) {
            builder.append(" != 0");
        } else if (propertyType == PropertyType.Date) {
            builder.append(")");
        }
        if (customType != null) {
            builder.append(')');
        }
        return builder.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    void init2ndPass() {
        initConstraint();
        if (dbType == null) {
            dbType = schema.mapToDbType(propertyType);
        }
        if (dbName == null) {
            dbName = DaoUtil.dbName(propertyName);
            nonDefaultDbName = false;
        } else if (primaryKey && propertyType == PropertyType.Long && dbName.equals("_id")) {
            nonDefaultDbName = false;
        }

        // For backwards compatibility, consider notNull. It should be only dependent on nonPrimitiveType in the future.
        if (notNull && !nonPrimitiveType) {
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
