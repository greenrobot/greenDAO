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

import org.greenrobot.greendao.generator.Property.PropertyBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Model class for an entity: a Java data object mapped to a data base table. A new entity is added to a {@link Schema}
 * by the method {@link Schema#addEntity(String)} (there is no public constructor for {@link Entity} itself). <br/>
 * <br/> Use the various addXXX methods to add entity properties, indexes, and relations to other entities (addToOne,
 * addToMany).<br/> <br/> There are further configuration possibilities: <ul> <li>{@link
 * Entity#implementsInterface(String...)} and {@link #implementsSerializable()} to specify interfaces the entity will
 * implement</li> <li>{@link #setSuperclass(String)} to specify a class of which the entity will extend from</li>
 * <li>Various setXXX methods</li> </ul>
 *
 * @see <a href="http://greendao-orm.com/documentation/modelling-entities/">Modelling Entities (Documentation page)</a>
 * @see <a href="http://greendao-orm.com/documentation/relations/">Relations (Documentation page)</a>
 */
@SuppressWarnings("unused")
public class Entity {
    private final Schema schema;
    private final String className;
    private final List<Property> properties;
    private List<Property> propertiesColumns;
    private final List<Property> propertiesPk;
    private final List<Property> propertiesNonPk;
    private final Set<String> propertyNames;
    private final List<Index> indexes;
    private final List<Index> multiIndexes;
    private final List<ToOne> toOneRelations;
    private final List<ToManyBase> toManyRelations;
    private final List<ToManyBase> incomingToManyRelations;
    private final Collection<String> additionalImportsEntity;
    private final Collection<String> additionalImportsDao;
    private final List<String> interfacesToImplement;
    private final List<ContentProvider> contentProviders;

    private String dbName;
    private boolean nonDefaultDbName;
    private String classNameDao;
    private String classNameTest;
    private String javaPackage;
    private String javaPackageDao;
    private String javaPackageTest;
    private Property pkProperty;
    private String pkType;
    private String superclass;
    private String javaDoc;
    private String codeBeforeClass;

    private boolean protobuf;
    private boolean constructors;
    private boolean skipGeneration;
    private boolean skipGenerationTest;
    private boolean skipCreationInDb;
    private Boolean active;
    private Boolean hasKeepSections;

    Entity(Schema schema, String className) {
        this.schema = schema;
        this.className = className;
        properties = new ArrayList<>();
        propertiesPk = new ArrayList<>();
        propertiesNonPk = new ArrayList<>();
        propertyNames = new HashSet<>();
        indexes = new ArrayList<>();
        multiIndexes = new ArrayList<>();
        toOneRelations = new ArrayList<>();
        toManyRelations = new ArrayList<>();
        incomingToManyRelations = new ArrayList<>();
        additionalImportsEntity = new TreeSet<>();
        additionalImportsDao = new TreeSet<>();
        interfacesToImplement = new ArrayList<>();
        contentProviders = new ArrayList<>();
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
        PropertyBuilder builder = new PropertyBuilder(schema, this, propertyType, propertyName);
        properties.add(builder.getProperty());
        return builder;
    }

    /** Adds a standard _id column required by standard Android classes, e.g. list adapters. */
    public PropertyBuilder addIdProperty() {
        PropertyBuilder builder = addLongProperty("id");
        builder.dbName("_id").primaryKey();
        return builder;
    }

    /** Adds a to-many relationship; the target entity is joined to the PK property of this entity (typically the ID). */
    public ToMany addToMany(Entity target, Property targetProperty) {
        Property[] targetProperties = {targetProperty};
        return addToMany(null, target, targetProperties);
    }

    /**
     * Convenience method for {@link Entity#addToMany(Entity, Property)} with a subsequent call to {@link
     * ToMany#setName(String)}.
     */
    public ToMany addToMany(Entity target, Property targetProperty, String name) {
        ToMany toMany = addToMany(target, targetProperty);
        toMany.setName(name);
        return toMany;
    }

    /**
     * Add a to-many relationship; the target entity is joined using the given target property (of the target entity)
     * and given source property (of this entity).
     */
    public ToMany addToMany(Property sourceProperty, Entity target, Property targetProperty) {
        Property[] sourceProperties = {sourceProperty};
        Property[] targetProperties = {targetProperty};
        return addToMany(sourceProperties, target, targetProperties);
    }

    public ToMany addToMany(Property[] sourceProperties, Entity target, Property[] targetProperties) {
        if (protobuf) {
            throw new IllegalStateException("Protobuf entities do not support relations, currently");
        }

        ToMany toMany = new ToMany(schema, this, sourceProperties, target, targetProperties);
        toManyRelations.add(toMany);
        target.incomingToManyRelations.add(toMany);
        return toMany;
    }

    public ToManyWithJoinEntity addToMany(Entity target, Entity joinEntity, Property id1, Property id2) {
        ToManyWithJoinEntity toMany = new ToManyWithJoinEntity(schema, this, target, joinEntity, id1, id2);
        toManyRelations.add(toMany);
        target.incomingToManyRelations.add(toMany);
        return toMany;
    }

    /**
     * Adds a to-one relationship to the given target entity using the given given foreign key property (which belongs
     * to this entity).
     */
    public ToOne addToOne(Entity target, Property fkProperty) {
        if (protobuf) {
            throw new IllegalStateException("Protobuf entities do not support realtions, currently");
        }

        Property[] fkProperties = {fkProperty};
        ToOne toOne = new ToOne(schema, this, target, fkProperties, true);
        toOneRelations.add(toOne);
        return toOne;
    }

    /** Convenience for {@link #addToOne(Entity, Property)} with a subsequent call to {@link ToOne#setName(String)}. */
    public ToOne addToOne(Entity target, Property fkProperty, String name) {
        ToOne toOne = addToOne(target, fkProperty);
        toOne.setName(name);
        return toOne;
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
        propertyBuilder.dbName(fkColumnName);
        Property column = propertyBuilder.getProperty();
        Property[] fkColumns = {column};
        ToOne toOne = new ToOne(schema, this, target, fkColumns, false);
        toOne.setName(name);
        toOneRelations.add(toOne);
        return toOne;
    }

    protected void addIncomingToMany(ToMany toMany) {
        incomingToManyRelations.add(toMany);
    }

    public ContentProvider addContentProvider() {
        List<Entity> entities = new ArrayList<>();
        entities.add(this);
        ContentProvider contentProvider = new ContentProvider(schema, entities);
        contentProviders.add(contentProvider);
        return contentProvider;
    }

    /** Adds a new index to the entity. */
    public Entity addIndex(Index index) {
        indexes.add(index);
        return this;
    }

    public Entity addImport(String additionalImport) {
        additionalImportsEntity.add(additionalImport);
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

    public String getDbName() {
        return dbName;
    }

    @Deprecated
    /**
     * @deprecated Use setDbName
     */
    public void setTableName(String tableName) {
        setDbName(tableName);
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        this.nonDefaultDbName = dbName != null;
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

    /** Internal property used by templates, don't use during entity definition. */
    public List<Property> getPropertiesPk() {
        return propertiesPk;
    }

    /** Internal property used by templates, don't use during entity definition. */
    public List<Property> getPropertiesNonPk() {
        return propertiesNonPk;
    }

    /** Internal property used by templates, don't use during entity definition. */
    public Property getPkProperty() {
        return pkProperty;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    /** Internal property used by templates, don't use during entity definition. */
    public String getPkType() {
        return pkType;
    }

    public boolean isConstructors() {
        return constructors;
    }

    /** Flag to define if constructors should be generated. */
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

    @Deprecated
    /**
     * @deprecated Use setSkipCreationInDb
     */
    public void setSkipTableCreation(boolean skipTableCreation) {
        setSkipCreationInDb(skipTableCreation);
    }

    /** Flag if CREATE & DROP TABLE scripts should be skipped in Dao. */
    public void setSkipCreationInDb(boolean skipCreationInDb) {
        this.skipCreationInDb = skipCreationInDb;
    }

    public boolean isSkipCreationInDb() {
        return skipCreationInDb;
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

    public List<ToManyBase> getToManyRelations() {
        return toManyRelations;
    }

    public List<ToManyBase> getIncomingToManyRelations() {
        return incomingToManyRelations;
    }

    /**
     * Entities with relations are active, but this method allows to make the entities active even if it does not have
     * relations.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getHasKeepSections() {
        return hasKeepSections;
    }

    public Collection<String> getAdditionalImportsEntity() {
        return additionalImportsEntity;
    }

    public Collection<String> getAdditionalImportsDao() {
        return additionalImportsDao;
    }

    public void setHasKeepSections(Boolean hasKeepSections) {
        this.hasKeepSections = hasKeepSections;
    }

    public List<String> getInterfacesToImplement() {
        return interfacesToImplement;
    }

    public List<ContentProvider> getContentProviders() {
        return contentProviders;
    }

    public void implementsInterface(String... interfaces) {
        for (String interfaceToImplement : interfaces) {
            if (interfacesToImplement.contains(interfaceToImplement)) {
                throw new RuntimeException("Interface defined more than once: " + interfaceToImplement);
            }
            interfacesToImplement.add(interfaceToImplement);
        }
    }

    public void implementsSerializable() {
        interfacesToImplement.add("java.io.Serializable");
    }

    public String getSuperclass() {
        return superclass;
    }

    public void setSuperclass(String classToExtend) {
        this.superclass = classToExtend;
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public void setJavaDoc(String javaDoc) {
        this.javaDoc = DaoUtil.checkConvertToJavaDoc(javaDoc, "");
    }

    public String getCodeBeforeClass() {
        return codeBeforeClass;
    }

    public void setCodeBeforeClass(String codeBeforeClass) {
        this.codeBeforeClass = codeBeforeClass;
    }

    void init2ndPass() {
        init2ndPassNamesWithDefaults();

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

        for (int i = 0; i < indexes.size(); i++) {
            final Index index = indexes.get(i);
            final int propertiesSize = index.getProperties().size();
            if (propertiesSize == 1) {
                final Property property = index.getProperties().get(0);
                property.setIndex(index);
            } else if (propertiesSize > 1) {
                multiIndexes.add(index);
            }
        }

        if (propertiesPk.size() == 1) {
            pkProperty = propertiesPk.get(0);
            pkType = schema.mapToJavaTypeNullable(pkProperty.getPropertyType());
        } else {
            pkType = "Void";
        }

        propertiesColumns = new ArrayList<>(properties);
        for (ToOne toOne : toOneRelations) {
            toOne.init2ndPass();
            Property[] fkProperties = toOne.getFkProperties();
            for (Property fkProperty : fkProperties) {
                if (!propertiesColumns.contains(fkProperty)) {
                    propertiesColumns.add(fkProperty);
                }
            }
        }

        for (ToManyBase toMany : toManyRelations) {
            toMany.init2ndPass();
            // Source Properties may not be virtual, so we do not need the following code:
            // for (Property sourceProperty : toMany.getSourceProperties()) {
            // if (!propertiesColumns.contains(sourceProperty)) {
            // propertiesColumns.add(sourceProperty);
            // }
            // }
        }

        if (active == null) {
            active = schema.isUseActiveEntitiesByDefault();
        }
        active |= !toOneRelations.isEmpty() || !toManyRelations.isEmpty();

        if (hasKeepSections == null) {
            hasKeepSections = schema.isHasKeepSectionsByDefault();
        }

        init2ndPassIndexNamesWithDefaults();

        for (ContentProvider contentProvider : contentProviders) {
            contentProvider.init2ndPass();
        }
    }

    protected void init2ndPassNamesWithDefaults() {
        if (dbName == null) {
            dbName = DaoUtil.dbName(className);
            nonDefaultDbName = false;
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

    protected void init2ndPassIndexNamesWithDefaults() {
        for (int i = 0; i < indexes.size(); i++) {
            Index index = indexes.get(i);
            if (index.getName() == null) {
                String indexName = "IDX_" + getDbName();
                List<Property> properties = index.getProperties();
                for (int j = 0; j < properties.size(); j++) {
                    Property property = properties.get(j);
                    indexName += "_" + property.getDbName();
                    if ("DESC".equalsIgnoreCase(index.getPropertiesOrder().get(j))) {
                        indexName += "_DESC";
                    }
                }
                // TODO can this get too long? how to shorten reliably without depending on the order (i)
                index.setDefaultName(indexName);
            }
        }
    }

    void init3rdPass() {
        for (Property property : properties) {
            property.init3ndPass();
        }

        init3rdPassRelations();
        init3rdPassAdditionalImports();
    }

    private void init3rdPassRelations() {
        Set<String> toOneNames = new HashSet<>();
        for (ToOne toOne : toOneRelations) {
            toOne.init3ndPass();
            if (!toOneNames.add(toOne.getName().toLowerCase())) {
                throw new RuntimeException("Duplicate name for " + toOne);
            }
        }

        Set<String> toManyNames = new HashSet<>();
        for (ToManyBase toMany : toManyRelations) {
            toMany.init3rdPass();
            if (toMany instanceof ToMany) {
                Entity targetEntity = toMany.getTargetEntity();
                for (Property targetProperty : ((ToMany) toMany).getTargetProperties()) {
                    if (!targetEntity.propertiesColumns.contains(targetProperty)) {
                        targetEntity.propertiesColumns.add(targetProperty);
                    }
                }
            }
            if (!toManyNames.add(toMany.getName().toLowerCase())) {
                throw new RuntimeException("Duplicate name for " + toMany);
            }
        }
    }

    private void init3rdPassAdditionalImports() {
        if (active && !javaPackage.equals(javaPackageDao)) {
            additionalImportsEntity.add(javaPackageDao + "." + classNameDao);
        }

        for (ToOne toOne : toOneRelations) {
            Entity targetEntity = toOne.getTargetEntity();
            checkAdditionalImportsEntityTargetEntity(targetEntity);
            // For deep loading
            checkAdditionalImportsDaoTargetEntity(targetEntity);
        }

        for (ToManyBase toMany : toManyRelations) {
            Entity targetEntity = toMany.getTargetEntity();
            checkAdditionalImportsEntityTargetEntity(targetEntity);
        }

        for (ToManyBase incomingToMany : incomingToManyRelations) {
            if (incomingToMany instanceof ToManyWithJoinEntity) {
                final ToManyWithJoinEntity toManyWithJoinEntity = (ToManyWithJoinEntity) incomingToMany;
                final Entity joinEntity = toManyWithJoinEntity.getJoinEntity();
                checkAdditionalImportsDaoTargetEntity(joinEntity);
            }
        }

        for (Property property : properties) {
            String customType = property.getCustomType();
            if (customType != null) {
                String pack = DaoUtil.getPackageFromFullyQualified(customType);
                if (pack != null && !pack.equals(javaPackage)) {
                    additionalImportsEntity.add(customType);
                }
                if (pack != null && !pack.equals(javaPackageDao)) {
                    additionalImportsDao.add(customType);
                }
            }

            String converter = property.getConverter();
            if (converter != null) {
                String pack = DaoUtil.getPackageFromFullyQualified(converter);
                if (pack != null && !pack.equals(javaPackageDao)) {
                    additionalImportsDao.add(converter);
                }
            }

        }
    }

    private void checkAdditionalImportsEntityTargetEntity(Entity targetEntity) {
        if (!targetEntity.getJavaPackage().equals(javaPackage)) {
            additionalImportsEntity.add(targetEntity.getJavaPackage() + "." + targetEntity.getClassName());
        }
        if (!targetEntity.getJavaPackageDao().equals(javaPackage)) {
            additionalImportsEntity.add(targetEntity.getJavaPackageDao() + "." + targetEntity.getClassNameDao());
        }
    }

    private void checkAdditionalImportsDaoTargetEntity(Entity targetEntity) {
        if (!targetEntity.getJavaPackage().equals(javaPackageDao)) {
            additionalImportsDao.add(targetEntity.getJavaPackage() + "." + targetEntity.getClassName());
        }
    }

    public void validatePropertyExists(Property property) {
        if (!properties.contains(property)) {
            throw new RuntimeException("Property " + property + " does not exist in " + this);
        }
    }

    public List<Index> getMultiIndexes() {
        return multiIndexes;
    }

    public boolean isNonDefaultDbName() {
        return nonDefaultDbName;
    }

    @Override
    public String toString() {
        return "Entity " + className + " (package: " + javaPackage + ")";
    }

}
