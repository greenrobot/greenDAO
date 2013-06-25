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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.greenrobot.daogenerator.Property.PropertyBuilder;

/**
 * Model class for an entity: a Java data object mapped to a data base table. A
 * new entity is added to a {@link Schema} by the method
 * {@link Schema#addEntity(String)} (there is no public constructor for
 * {@link Entity} itself). <br/>
 * <br/>
 * Use the various addXXX methods to add entity properties, indexes, and
 * relations to other entities (addToOne, addToMany).<br/>
 * <br/>
 * There are further configuration possibilities:
 * <ul>
 * <li>{@link Entity#implementsInterface(String...)} and
 * {@link #implementsSerializable()} to specify interfaces the entity will
 * implement</li>
 * <li>{@link #setSuperclass(String)} to specify a class of which the entity
 * will extend from</li>
 * <li>Various setXXX methods</li>
 * </ul>
 * 
 * @see <a
 *      href="http://greendao-orm.com/documentation/modelling-entities/">Modelling
 *      Entities (Documentation page)</a>
 * @see <a href="http://greendao-orm.com/documentation/relations/">Relations
 *      (Documentation page)</a>
 */
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
  private final Collection<String> additionalImportsEntity;
  private final Collection<String> additionalImportsDao;
  private final List<String> interfacesToImplement;

  private String tableName;
  private String classNameDao;
  private String classNameTest;
  private String javaPackage;
  private String javaPackageDao;
  private String javaPackageTest;
  private Property pkProperty;
  private String pkType;
  private String superclass;
  private String childclass;

  private boolean protobuf;
  private boolean constructors;
  private boolean skipGeneration;
  private boolean skipGenerationTest;
  private boolean skipTableCreation;
  private Boolean active;
  private Boolean hasKeepSections;
  private boolean entityQueryBuilder;

  Entity(Schema schema, String className) {
    this.schema = schema;
    this.className = className;
    this.properties = new ArrayList<Property>();
    this.propertiesPk = new ArrayList<Property>();
    this.propertiesNonPk = new ArrayList<Property>();
    this.propertyNames = new HashSet<String>();
    this.indexes = new ArrayList<Index>();
    this.toOneRelations = new ArrayList<ToOne>();
    this.toManyRelations = new ArrayList<ToMany>();
    this.incomingToManyRelations = new ArrayList<ToMany>();
    this.additionalImportsEntity = new TreeSet<String>();
    this.additionalImportsDao = new TreeSet<String>();
    this.interfacesToImplement = new ArrayList<String>();
    this.constructors = true;
  }

  public PropertyBuilder addBooleanProperty(String propertyName) {
    return this.addProperty(PropertyType.Boolean, propertyName);
  }

  public PropertyBuilder addByteProperty(String propertyName) {
    return this.addProperty(PropertyType.Byte, propertyName);
  }

  public PropertyBuilder addShortProperty(String propertyName) {
    return this.addProperty(PropertyType.Short, propertyName);
  }

  public PropertyBuilder addIntProperty(String propertyName) {
    return this.addProperty(PropertyType.Int, propertyName);
  }

  public PropertyBuilder addLongProperty(String propertyName) {
    return this.addProperty(PropertyType.Long, propertyName);
  }

  public PropertyBuilder addFloatProperty(String propertyName) {
    return this.addProperty(PropertyType.Float, propertyName);
  }

  public PropertyBuilder addDoubleProperty(String propertyName) {
    return this.addProperty(PropertyType.Double, propertyName);
  }

  public PropertyBuilder addByteArrayProperty(String propertyName) {
    return this.addProperty(PropertyType.ByteArray, propertyName);
  }

  public PropertyBuilder addStringProperty(String propertyName) {
    return this.addProperty(PropertyType.String, propertyName);
  }

  public PropertyBuilder addDateProperty(String propertyName) {
    return this.addProperty(PropertyType.Date, propertyName);
  }

  public PropertyBuilder addProperty(PropertyType propertyType, String propertyName) {
    if (!this.propertyNames.add(propertyName)) {
      throw new RuntimeException("Property already defined: " + propertyName);
    }
    PropertyBuilder builder = new Property.PropertyBuilder(this.schema, this, propertyType, propertyName);
    this.properties.add(builder.getProperty());
    return builder;
  }

  /**
   * Adds a standard _id column required by standard Android classes, e.g. list
   * adapters.
   */
  public PropertyBuilder addIdProperty() {
    PropertyBuilder builder = this.addLongProperty("id");
    builder.columnName("_id").primaryKey();
    return builder;
  }

  /**
   * Adds a to-many relationship; the target entity is joined to the PK property
   * of this entity (typically the ID).
   */
  public ToMany addToMany(Entity target, Property targetProperty) {
    Property[] targetProperties = { targetProperty };
    return this.addToMany(null, target, targetProperties);
  }

  /**
   * Convenience method for {@link Entity#addToMany(Entity, Property)} with a
   * subsequent call to {@link ToMany#setName(String)}.
   */
  public ToMany addToMany(Entity target, Property targetProperty, String name) {
    ToMany toMany = this.addToMany(target, targetProperty);
    toMany.setName(name);
    return toMany;
  }

  /**
   * Add a to-many relationship; the target entity is joined using the given
   * target property (of the target entity) and given source property (of this
   * entity).
   */
  public ToMany addToMany(Property sourceProperty, Entity target, Property targetProperty) {
    Property[] sourceProperties = { sourceProperty };
    Property[] targetProperties = { targetProperty };
    return this.addToMany(sourceProperties, target, targetProperties);
  }

  public ToMany addToMany(Property[] sourceProperties, Entity target, Property[] targetProperties) {
    if (this.protobuf) {
      throw new IllegalStateException("Protobuf entities do not support realtions, currently");
    }

    ToMany toMany = new ToMany(this.schema, this, sourceProperties, target, targetProperties);
    this.toManyRelations.add(toMany);
    target.incomingToManyRelations.add(toMany);
    return toMany;
  }

  /**
   * Adds a to-one relationship to the given target entity using the given given
   * foreign key property (which belongs to this entity).
   */
  public ToOne addToOne(Entity target, Property fkProperty) {
    if (this.protobuf) {
      throw new IllegalStateException("Protobuf entities do not support realtions, currently");
    }

    Property[] fkProperties = { fkProperty };
    ToOne toOne = new ToOne(this.schema, this, target, fkProperties, true);
    this.toOneRelations.add(toOne);
    return toOne;
  }

  /**
   * Convenience for {@link #addToOne(Entity, Property)} with a subsequent call
   * to {@link ToOne#setName(String)}.
   */
  public ToOne addToOne(Entity target, Property fkProperty, String name) {
    ToOne toOne = this.addToOne(target, fkProperty);
    toOne.setName(name);
    return toOne;
  }

  public ToOne addToOneWithoutProperty(String name, Entity target, String fkColumnName) {
    return this.addToOneWithoutProperty(name, target, fkColumnName, false, false);
  }

  public ToOne addToOneWithoutProperty(String name, Entity target, String fkColumnName, boolean notNull, boolean unique) {
    PropertyBuilder propertyBuilder = new PropertyBuilder(this.schema, this, null, name);
    if (notNull) {
      propertyBuilder.notNull();
    }
    if (unique) {
      propertyBuilder.unique();
    }
    propertyBuilder.columnName(fkColumnName);
    Property column = propertyBuilder.getProperty();
    Property[] fkColumns = { column };
    ToOne toOne = new ToOne(this.schema, this, target, fkColumns, false);
    toOne.setName(name);
    this.toOneRelations.add(toOne);
    return toOne;
  }

  protected void addIncomingToMany(ToMany toMany) {
    this.incomingToManyRelations.add(toMany);
  }

  /**
   * Adds a new index to the entity.
   */
  public Entity addIndex(Index index) {
    this.indexes.add(index);
    return this;
  }

  /**
   * The entity is represented by a protocol buffers object. Requires some
   * special actions like using builders.
   */
  Entity useProtobuf() {
    this.protobuf = true;
    return this;
  }

  public boolean isProtobuf() {
    return this.protobuf;
  }

  public Schema getSchema() {
    return this.schema;
  }

  public String getTableName() {
    return this.tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getClassName() {
    return this.className;
  }

  public String getReferencedClassName() {
    if (this.getHasChildclass()) {
      return this.childclass;
    } else {
      return this.className;
    }
  }

  public List<Property> getProperties() {
    return this.properties;
  }

  public List<Property> getPropertiesColumns() {
    return this.propertiesColumns;
  }

  public String getJavaPackage() {
    return this.javaPackage;
  }

  public void setJavaPackage(String javaPackage) {
    this.javaPackage = javaPackage;
  }

  public String getJavaPackageDao() {
    return this.javaPackageDao;
  }

  public void setJavaPackageDao(String javaPackageDao) {
    this.javaPackageDao = javaPackageDao;
  }

  public String getClassNameDao() {
    return this.classNameDao;
  }

  public void setClassNameDao(String classNameDao) {
    this.classNameDao = classNameDao;
  }

  public String getClassNameTest() {
    return this.classNameTest;
  }

  public void setClassNameTest(String classNameTest) {
    this.classNameTest = classNameTest;
  }

  public String getJavaPackageTest() {
    return this.javaPackageTest;
  }

  public void setJavaPackageTest(String javaPackageTest) {
    this.javaPackageTest = javaPackageTest;
  }

  public List<Property> getPropertiesPk() {
    return this.propertiesPk;
  }

  public List<Property> getPropertiesNonPk() {
    return this.propertiesNonPk;
  }

  public Property getPkProperty() {
    return this.pkProperty;
  }

  public List<Index> getIndexes() {
    return this.indexes;
  }

  public String getPkType() {
    return this.pkType;
  }

  public boolean isConstructors() {
    return this.constructors;
  }

  public void setConstructors(boolean constructors) {
    this.constructors = constructors;
  }

  public boolean isSkipGeneration() {
    return this.skipGeneration;
  }

  /**
   * Flag if the entity's code generation should be skipped. E.g. if you need to
   * change the class after initial generation.
   */
  public void setSkipGeneration(boolean skipGeneration) {
    this.skipGeneration = skipGeneration;
  }

  /**
   * Flag if CREATE & DROP TABLE scripts should be skipped in Dao.
   */
  public void setSkipTableCreation(boolean skipTableCreation) {
    this.skipTableCreation = skipTableCreation;
  }

  public boolean isSkipTableCreation() {
    return this.skipTableCreation;
  }

  public boolean isSkipGenerationTest() {
    return this.skipGenerationTest;
  }

  public void setSkipGenerationTest(boolean skipGenerationTest) {
    this.skipGenerationTest = skipGenerationTest;
  }

  public List<ToOne> getToOneRelations() {
    return this.toOneRelations;
  }

  public List<ToMany> getToManyRelations() {
    return this.toManyRelations;
  }

  public List<ToMany> getIncomingToManyRelations() {
    return this.incomingToManyRelations;
  }

  /**
   * Entities with relations are active, but this method allows to make the
   * entities active even if it does not have relations.
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

  public Boolean getActive() {
    return this.active;
  }

  public Boolean getHasKeepSections() {
    return this.hasKeepSections;
  }

  public Collection<String> getAdditionalImportsEntity() {
    return this.additionalImportsEntity;
  }

  public Collection<String> getAdditionalImportsDao() {
    return this.additionalImportsDao;
  }

  public void setHasKeepSections(Boolean hasKeepSections) {
    this.hasKeepSections = hasKeepSections;
  }

  public List<String> getInterfacesToImplement() {
    return this.interfacesToImplement;
  }

  public void implementsInterface(String... interfaces) {
    for (String interfaceToImplement : interfaces) {
      this.interfacesToImplement.add(interfaceToImplement);
    }
  }

  public void implementsSerializable() {
    this.interfacesToImplement.add("java.io.Serializable");
  }

  public String getSuperclass() {
    return this.superclass;
  }

  public void setSuperclass(String classToExtend) {
    this.superclass = classToExtend;
  }

  public String getChildclass() {
    return this.childclass;
  }

  public void setChildclass(String childclass) {
    this.childclass = childclass;
  }

  public boolean getHasChildclassWithPackage() {
    return this.getHasChildclass() && this.childclass.contains(".");
  }

  public boolean getHasChildclass() {
    return (this.childclass != null) && !"".equals(this.childclass.trim());
  }

  public boolean getHasChildclassInEntityPackage() {
    return this.getHasChildclass() && !this.childclass.contains(".");
  }

  public boolean getHasEntityQueryBuilder() {
    return this.entityQueryBuilder;
  }

  public void setEntityQueryBuilder(boolean entityQueryBuilder) {
    this.entityQueryBuilder = entityQueryBuilder;
  }

  void init2ndPass() {
    this.init2nPassNamesWithDefaults();

    for (int i = 0; i < this.properties.size(); i++) {
      Property property = this.properties.get(i);
      property.setOrdinal(i);
      property.init2ndPass();
      if (property.isPrimaryKey()) {
        this.propertiesPk.add(property);
      } else {
        this.propertiesNonPk.add(property);
      }
    }

    if (this.propertiesPk.size() == 1) {
      this.pkProperty = this.propertiesPk.get(0);
      this.pkType = this.schema.mapToJavaTypeNullable(this.pkProperty.getPropertyType());
    } else {
      this.pkType = "Void";
    }

    this.propertiesColumns = new ArrayList<Property>(this.properties);
    for (ToOne toOne : this.toOneRelations) {
      toOne.init2ndPass();
      Property[] fkProperties = toOne.getFkProperties();
      for (Property fkProperty : fkProperties) {
        if (!this.propertiesColumns.contains(fkProperty)) {
          this.propertiesColumns.add(fkProperty);
        }
      }
    }

    for (ToMany toMany : this.toManyRelations) {
      toMany.init2ndPass();
      // Source Properties may not be virtual, so we do not need the following
      // code:
      // for (Property sourceProperty : toMany.getSourceProperties()) {
      // if (!propertiesColumns.contains(sourceProperty)) {
      // propertiesColumns.add(sourceProperty);
      // }
      // }
    }

    if (this.active == null) {
      this.active = this.schema.isUseActiveEntitiesByDefault();
    }
    this.active |= !this.toOneRelations.isEmpty() || !this.toManyRelations.isEmpty();

    if (this.hasKeepSections == null) {
      this.hasKeepSections = this.schema.isHasKeepSectionsByDefault();
    }

    this.init2ndPassIndexNamesWithDefaults();
  }

  protected void init2nPassNamesWithDefaults() {
    if (this.tableName == null) {
      this.tableName = DaoUtil.dbName(this.className);
    }

    if (this.classNameDao == null) {
      this.classNameDao = this.className + "Dao";
    }
    if (this.classNameTest == null) {
      this.classNameTest = this.className + "Test";
    }

    if (this.javaPackage == null) {
      this.javaPackage = this.schema.getDefaultJavaPackage();
    }

    if (this.javaPackageDao == null) {
      this.javaPackageDao = this.schema.getDefaultJavaPackageDao();
      if (this.javaPackageDao == null) {
        this.javaPackageDao = this.javaPackage;
      }
    }
    if (this.javaPackageTest == null) {
      this.javaPackageTest = this.schema.getDefaultJavaPackageTest();
      if (this.javaPackageTest == null) {
        this.javaPackageTest = this.javaPackage;
      }
    }
  }

  protected void init2ndPassIndexNamesWithDefaults() {
    for (int i = 0; i < this.indexes.size(); i++) {
      Index index = this.indexes.get(i);
      if (index.getName() == null) {
        String indexName = "IDX_" + this.getTableName();
        List<Property> properties = index.getProperties();
        for (int j = 0; j < properties.size(); j++) {
          Property property = properties.get(j);
          indexName += "_" + property.getColumnName();
          if ("DESC".equalsIgnoreCase(index.getPropertiesOrder().get(j))) {
            indexName += "_DESC";
          }
        }
        // TODO can this get too long? how to shorten reliably without depending
        // on the order (i)
        index.setName(indexName);
      }
    }
  }

  void init3ndPass() {
    for (Property property : this.properties) {
      property.init3ndPass();
    }

    this.init3rdPassRelations();
    this.init3rdPassAdditionalImports();
  }

  private void init3rdPassRelations() {
    Set<String> toOneNames = new HashSet<String>();
    for (ToOne toOne : this.toOneRelations) {
      toOne.init3ndPass();
      if (!toOneNames.add(toOne.getName().toLowerCase())) {
        throw new RuntimeException("Duplicate name for " + toOne);
      }
    }

    Set<String> toManyNames = new HashSet<String>();
    for (ToMany toMany : this.toManyRelations) {
      toMany.init3ndPass();
      Entity targetEntity = toMany.getTargetEntity();
      for (Property targetProperty : toMany.getTargetProperties()) {
        if (!targetEntity.propertiesColumns.contains(targetProperty)) {
          targetEntity.propertiesColumns.add(targetProperty);
        }
      }
      if (!toManyNames.add(toMany.getName().toLowerCase())) {
        throw new RuntimeException("Duplicate name for " + toMany);
      }
    }
  }

  private void init3rdPassAdditionalImports() {
    if (this.active && !this.javaPackage.equals(this.javaPackageDao)) {
      this.additionalImportsEntity.add(this.javaPackageDao + "." + this.classNameDao);
    }

    for (ToOne toOne : this.toOneRelations) {
      Entity targetEntity = toOne.getTargetEntity();
      this.checkAdditionalImportsEntityTargetEntity(targetEntity);
      // For deep loading
      if (!targetEntity.getJavaPackage().equals(this.javaPackageDao)) {
        this.additionalImportsDao.add(targetEntity.getJavaPackage() + "." + targetEntity.getClassName());
      }
    }

    for (ToMany toMany : this.toManyRelations) {
      Entity targetEntity = toMany.getTargetEntity();
      this.checkAdditionalImportsEntityTargetEntity(targetEntity);
    }
  }

  private void checkAdditionalImportsEntityTargetEntity(Entity targetEntity) {
    if (!targetEntity.getJavaPackage().equals(this.javaPackage)) {
      this.additionalImportsEntity.add(targetEntity.getJavaPackage() + "." + targetEntity.getClassName());
    }
    if (!targetEntity.getJavaPackageDao().equals(this.javaPackage)) {
      this.additionalImportsEntity.add(targetEntity.getJavaPackageDao() + "." + targetEntity.getClassNameDao());
    }
  }

  public void validatePropertyExists(Property property) {
    if (!this.properties.contains(property)) {
      throw new RuntimeException("Property " + property + " does not exist in " + this);
    }
  }

  @Override
  public String toString() {
    return "Entity " + this.className + " (package: " + this.javaPackage + ")";
  }
}
