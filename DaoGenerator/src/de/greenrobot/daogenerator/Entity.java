package de.greenrobot.daogenerator;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daogenerator.Property.ColumnBuilder;

public class Entity {
    private final Schema schema;
    private final String className;
    private String tableName;
    private String classNameDao;
    private final List<Property> properties;
    private final List<Property> propertiesPk;
    private final List<Property> propertiesNonPk;
    private String javaPackage;
    private String javaPackageDao;
    private Property pkProperty;
    private boolean protobuf;

    public Entity(Schema schema, String className) {
        this.schema = schema;
        this.className = className;
        properties = new ArrayList<Property>();
        propertiesPk = new ArrayList<Property>();
        propertiesNonPk = new ArrayList<Property>();
    }

    public ColumnBuilder addBooleanProperty(String propertyName) {
        return addProperty(PropertyType.Boolean, propertyName);
    }

    public ColumnBuilder addByteProperty(String propertyName) {
        return addProperty(PropertyType.Byte, propertyName);
    }

    public ColumnBuilder addShortProperty(String propertyName) {
        return addProperty(PropertyType.Short, propertyName);
    }

    public ColumnBuilder addIntProperty(String propertyName) {
        return addProperty(PropertyType.Int, propertyName);
    }

    public ColumnBuilder addLongProperty(String propertyName) {
        return addProperty(PropertyType.Long, propertyName);
    }

    public ColumnBuilder addFloatProperty(String propertyName) {
        return addProperty(PropertyType.Float, propertyName);
    }

    public ColumnBuilder addDoubleProperty(String propertyName) {
        return addProperty(PropertyType.Double, propertyName);
    }

    public ColumnBuilder addByteArrayProperty(String propertyName) {
        return addProperty(PropertyType.ByteArray, propertyName);
    }

    public ColumnBuilder addStringProperty(String propertyName) {
        return addProperty(PropertyType.String, propertyName);
    }

    public ColumnBuilder addProperty(PropertyType propertyType, String propertyName) {
        ColumnBuilder builder = new Property.ColumnBuilder(propertyType, propertyName);
        properties.add(builder.build());
        return builder;
    }

    /** Adds a standard _id column required by standard Android classes, e.g. list adapters. */
    public ColumnBuilder addIdProperty() {
        ColumnBuilder builder = new Property.ColumnBuilder(PropertyType.Int, "id");
        builder.columnName("_id").primaryKey().asc();
        properties.add(builder.build());
        return builder;
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

    void init2ndPass() {
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
        }

    }

}
