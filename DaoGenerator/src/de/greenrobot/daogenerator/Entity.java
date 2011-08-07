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
    private final List<Property> propertiesPk;
    private final List<Property> propertiesNonPk;
    private final Set<String> propertyNames;
    private final List<Index> indexes;
    private final List<ToOne> toOneRelations;

    private String tableName;
    private String classNameDao;
    private String javaPackage;
    private String javaPackageDao;
    private Property pkProperty;
    private String pkType;
    private boolean protobuf;
    private boolean constructors;
    private boolean skipGeneration;
    private boolean active;

    public Entity(Schema schema, String className) {
        this.schema = schema;
        this.className = className;
        properties = new ArrayList<Property>();
        propertiesPk = new ArrayList<Property>();
        propertiesNonPk = new ArrayList<Property>();
        propertyNames = new HashSet<String>();
        indexes = new ArrayList<Index>();
        toOneRelations = new ArrayList<ToOne>();
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

    public ToOne addToOne(Entity target, Property... fkProperties) {
        if (fkProperties.length == 0) {
            throw new IllegalArgumentException("fkProperties missing");
        }
        ToOne toOne = new ToOne(target, fkProperties);
        toOneRelations.add(toOne);
        return toOne;
    }

    /**
     * Adds a new index to the entity.
     */
    public Entity addIndex(Index index) {
        indexes.add(index);
        return this;
    }

    /** The entity is represented by a protocol buffers object. Requires some special actions like using builders. */
    public Entity useProtobuf() {
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

    public String getClassName() {
        return className;
    }

    public List<Property> getProperties() {
        return properties;
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

    public List<ToOne> getToOneRelations() {
        return toOneRelations;
    }

    public boolean isActive() {
        return active;
    }

    void init2ndPass() {
        initNamesWithDefaults();

        for (Property property : properties) {
            property.init2ndPass(schema, this);
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

        active = !toOneRelations.isEmpty();
        for (ToOne toOne : toOneRelations) {
            if (toOne.getName() == null) {
                char[] nameCharArray = toOne.getEntity().getClassName().toCharArray();
                nameCharArray[0] = Character.toLowerCase(nameCharArray[0]);
                toOne.setName(new String(nameCharArray));
            }
        }

        initIndexNamesWithDefaults();
    }

    protected void initNamesWithDefaults() {
        if (classNameDao == null) {
            classNameDao = className + "Dao";
        }
        if (tableName == null) {
            tableName = DaoUtil.dbName(className);
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
