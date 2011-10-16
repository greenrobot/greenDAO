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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.daogenerator.Property.PropertyBuilder;

/** Model class for an entity: a Java data object mapped to a data base table. */
public class Entity {
    private final Schema schema;
    private final String className;
    private final List<Property> properties;
    private List<Property> propertiesColumns;
    private final List<Property> propertiesPk;
    private final List<Property> propertiesNonPk;
    private final Set<String> propertyNames;
    private final List<Index> indexes;
    private final List<ToOne> toOneRelations;
    private final List<ToMany> toManyRelations;
    private final List<ToMany> incomingToManyRelations;

    private String tableName;
    private String classNameDao;
    private String classNameTest;
    private String javaPackage;
    private String javaPackageDao;
    private String javaPackageTest;
    private Property pkProperty;
    private String pkType;

    private boolean protobuf;
    private boolean constructors;
    private boolean skipGeneration;
    private boolean skipGenerationTest;
    private boolean active;
    private Boolean hasKeepSections;

    public Entity(Schema schema, String className) {
        this.schema = schema;
        this.className = className;
        properties = new ArrayList<Property>();
        propertiesPk = new ArrayList<Property>();
        propertiesNonPk = new ArrayList<Property>();
        propertyNames = new HashSet<String>();
        indexes = new ArrayList<Index>();
        toOneRelations = new ArrayList<ToOne>();
        toManyRelations = new ArrayList<ToMany>();
        incomingToManyRelations = new ArrayList<ToMany>();
        constructors = true;
    }

    public PropertyBuilder addBooleanProperty(String propertyName) {
        return addProperty(PropertyType.Boolean, propertyName);
    }

    public PropertyBuilder addByteProperty(String propertyName) {
        return addProperty(PropertyType.Byte, propertyName);
    }

    public PropertyBuilder addShortProperty(String propertyName) {
        return addProperty(PropertyType.Short, propertyName);
    }

    public PropertyBuilder addIntProperty(String propertyName) {
        return addProperty(PropertyType.Int, propertyName);
    }

    public PropertyBuilder addLongProperty(String propertyName) {
        return addProperty(PropertyType.Long, propertyName);
    }

    public PropertyBuilder addFloatProperty(String propertyName) {
        return addProperty(PropertyType.Float, propertyName);
    }

    public PropertyBuilder addDoubleProperty(String propertyName) {
        return addProperty(PropertyType.Double, propertyName);
    }

    public PropertyBuilder addByteArrayProperty(String propertyName) {
        return addProperty(PropertyType.ByteArray, propertyName);
    }

    public PropertyBuilder addStringProperty(String propertyName) {
        return addProperty(PropertyType.String, propertyName);
    }

    public PropertyBuilder addDateProperty(String propertyName) {
        return addProperty(PropertyType.Date, propertyName);
    }

    public PropertyBuilder addProperty(PropertyType propertyType, String propertyName) {
        if (!propertyNames.add(propertyName)) {
            throw new RuntimeException("Property already defined: " + propertyName);
        }
        PropertyBuilder builder = new Property.PropertyBuilder(schema, this, propertyType, propertyName);
        properties.add(builder.getProperty());
        return builder;
    }

    /** Adds a standard _id column required by standard Android classes, e.g. list adapters. */
    public PropertyBuilder addIdProperty() {
        PropertyBuilder builder = addLongProperty("id");
        builder.columnName("_id").primaryKey();
        return builder;
    }

    public ToOne addToOne(Entity target, Property fkProperty) {
        Property[] fkProperties = { fkProperty };
        ToOne toOne = new ToOne(schema, this, target, fkProperties, true);
        toOneRelations.add(toOne);
        return toOne;
    }

    public ToMany addToMany(Entity target, Property fkProperty) {
        Property[] sourceProperties = null;
        Property[] targetProperties = { fkProperty };

        ToMany toMany = new ToMany(schema, this, sourceProperties, target, targetProperties);
        toManyRelations.add(toMany);
        target.incomingToManyRelations.add(toMany);
        return toMany;
    }

    public ToOne addToOneWithoutProperty(String name, Entity target, String fkColumnName) {
        return addToOneWithoutProperty(name, target, fkColumnName, false, false);
    }

    public ToOne addToOneWithoutProperty(String name, Entity target, String fkColumnName, boolean notNull,
            boolean unique) {
        PropertyBuilder propertyBuilder = new PropertyBuilder(schema, this, null, name);
        if (notNull) {
            propertyBuilder.notNull();
        }
        if (unique) {
            propertyBuilder.unique();
        }
        propertyBuilder.columnName(fkColumnName);
        Property column = propertyBuilder.getProperty();
        Property[] fkColumns = { column };
        ToOne toOne = new ToOne(schema, this, target, fkColumns, false);
        toOne.setName(name);
        toOneRelations.add(toOne);
        return toOne;
    }

    protected void addIncomingToMany(ToMany toMany) {
        incomingToManyRelations.add(toMany);
    }

    /**
     * Adds a new index to the entity.
     */
    public Entity addIndex(Index index) {
        indexes.add(index);
        return this;
    }

    /** The entity is represented by a protocol buffers object. Requires some special actions like using builders. */
    Entity useProtobuf() {
        protobuf = true;
        return this;
    }

    public boolean isProtobuf() {
        return protobuf;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Property> getPropertiesColumns() {
        return propertiesColumns;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getJavaPackageDao() {
        return javaPackageDao;
    }

    public void setJavaPackageDao(String javaPackageDao) {
        this.javaPackageDao = javaPackageDao;
    }

    public String getClassNameDao() {
        return classNameDao;
    }

    public void setClassNameDao(String classNameDao) {
        this.classNameDao = classNameDao;
    }

    public String getClassNameTest() {
        return classNameTest;
    }

    public void setClassNameTest(String classNameTest) {
        this.classNameTest = classNameTest;
    }

    public String getJavaPackageTest() {
        return javaPackageTest;
    }

    public void setJavaPackageTest(String javaPackageTest) {
        this.javaPackageTest = javaPackageTest;
    }

    public List<Property> getPropertiesPk() {
        return propertiesPk;
    }

    public List<Property> getPropertiesNonPk() {
        return propertiesNonPk;
    }

    public Property getPkProperty() {
        return pkProperty;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public String getPkType() {
        return pkType;
    }

    public boolean isConstructors() {
        return constructors;
    }

    public void setConstructors(boolean constructors) {
        this.constructors = constructors;
    }

    public boolean isSkipGeneration() {
        return skipGeneration;
    }

    /**
     * Flag if the entity's code generation should be skipped. E.g. if you need to change the class after initial
     * generation.
     */
    public void setSkipGeneration(boolean skipGeneration) {
        this.skipGeneration = skipGeneration;
    }

    public boolean isSkipGenerationTest() {
        return skipGenerationTest;
    }

    public void setSkipGenerationTest(boolean skipGenerationTest) {
        this.skipGenerationTest = skipGenerationTest;
    }

    public List<ToOne> getToOneRelations() {
        return toOneRelations;
    }

    public List<ToMany> getToManyRelations() {
        return toManyRelations;
    }

    public List<ToMany> getIncomingToManyRelations() {
        return incomingToManyRelations;
    }

    public boolean isActive() {
        return active;
    }

    void init2ndPass() {
        initNamesWithDefaults();

        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            property.setOrdinal(i);
            property.init2ndPass();
            if (property.isPrimaryKey()) {
                propertiesPk.add(property);
            } else {
                propertiesNonPk.add(property);
            }
        }

        if (propertiesPk.size() == 1) {
            pkProperty = propertiesPk.get(0);
            pkType = schema.mapToJavaTypeNullable(pkProperty.getPropertyType());
        } else {
            pkType = "Void";
        }

        active = !toOneRelations.isEmpty() || !toManyRelations.isEmpty();
        propertiesColumns = new ArrayList<Property>(properties);
        for (ToOne toOne : toOneRelations) {
            toOne.init2ndPass();
            Property[] fkProperties = toOne.getFkProperties();
            for (Property fkProperty : fkProperties) {
                if (!propertiesColumns.contains(fkProperty)) {
                    propertiesColumns.add(fkProperty);
                }
            }
        }
        for (ToMany toMany : toManyRelations) {
            toMany.init2ndPass();
            // Source Properties may not be virtual, so we do not need the following code:
            // for (Property sourceProperty : toMany.getSourceProperties()) {
            // if (!propertiesColumns.contains(sourceProperty)) {
            // propertiesColumns.add(sourceProperty);
            // }
            // }
        }

        initIndexNamesWithDefaults();
    }

    void init3ndPass() {
        for (Property property : properties) {
            property.init3ndPass();
        }

        for (ToOne toOne : toOneRelations) {
            toOne.init3ndPass();
        }

        for (ToMany toMany : toManyRelations) {
            toMany.init3ndPass();
            Entity targetEntity = toMany.getTargetEntity();
            for (Property targetProperty : toMany.getTargetProperties()) {
                if (!targetEntity.propertiesColumns.contains(targetProperty)) {
                    targetEntity.propertiesColumns.add(targetProperty);
                }
            }
        }

    }

    protected void initNamesWithDefaults() {
        if (tableName == null) {
            tableName = DaoUtil.dbName(className);
        }

        if (classNameDao == null) {
            classNameDao = className + "Dao";
        }
        if (classNameTest == null) {
            classNameTest = className + "Test";
        }

        if (javaPackage == null) {
            javaPackage = schema.getDefaultJavaPackage();
        }

        if (javaPackageDao == null) {
            javaPackageDao = schema.getDefaultJavaPackageDao();
            if (javaPackageDao == null) {
                javaPackageDao = javaPackage;
            }
        }
        if (javaPackageTest == null) {
            javaPackageTest = schema.getDefaultJavaPackageTest();
            if (javaPackageTest == null) {
                javaPackageTest = javaPackage;
            }
        }
    }

    protected void initIndexNamesWithDefaults() {
        for (int i = 0; i < indexes.size(); i++) {
            Index index = indexes.get(i);
            if (index.getName() == null) {
                String indexName = "IDX_" + getTableName();
                List<Property> properties = index.getProperties();
                for (int j = 0; j < properties.size(); j++) {
                    Property property = properties.get(j);
                    indexName += "_" + property.getColumnName();
                    if ("DESC".equalsIgnoreCase(index.getPropertiesOrder().get(j))) {
                        indexName += "_DESC";
                    }
                }
                // TODO can this get too long? how to shorten reliably without depending on the order (i)
                index.setName(indexName);
            }
        }
    }
}
