package de.greenrobot.daogenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema {
    private final int version;
    private final String defaultJavaPackage;
    private String defaultJavaPackageDao;
    private final List<Entity> tables;
    private Map<PropertyType, String> propertyToDbType;
    private Map<PropertyType, String> propertyToJavaTypeNotNull;
    private Map<PropertyType, String> propertyToJavaTypeNullable;

    public Schema(int version, String defaultJavaPackage) {
        this.version = version;
        this.defaultJavaPackage = defaultJavaPackage;
        this.tables = new ArrayList<Entity>();

        initTypeMappings();
    }

    private void initTypeMappings() {
        propertyToDbType = new HashMap<PropertyType, String>();
        propertyToDbType.put(PropertyType.Boolean, "INTEGER");
        propertyToDbType.put(PropertyType.Byte, "INTEGER");
        propertyToDbType.put(PropertyType.Short, "INTEGER");
        propertyToDbType.put(PropertyType.Int, "INTEGER");
        propertyToDbType.put(PropertyType.Long, "INTEGER");
        propertyToDbType.put(PropertyType.Float, "REAL");
        propertyToDbType.put(PropertyType.Double, "REAL");
        propertyToDbType.put(PropertyType.String, "TEXT");
        propertyToDbType.put(PropertyType.ByteArray, "BLOB");
        
        propertyToJavaTypeNotNull = new HashMap<PropertyType, String>();
        propertyToJavaTypeNotNull.put(PropertyType.Boolean, "boolean");
        propertyToJavaTypeNotNull.put(PropertyType.Byte, "byte");
        propertyToJavaTypeNotNull.put(PropertyType.Short, "short");
        propertyToJavaTypeNotNull.put(PropertyType.Int, "int");
        propertyToJavaTypeNotNull.put(PropertyType.Long, "long");
        propertyToJavaTypeNotNull.put(PropertyType.Float, "float");
        propertyToJavaTypeNotNull.put(PropertyType.Double, "double");
        propertyToJavaTypeNotNull.put(PropertyType.String, "String");
        propertyToJavaTypeNotNull.put(PropertyType.ByteArray, "byte[]");

        propertyToJavaTypeNullable = new HashMap<PropertyType, String>();
        propertyToJavaTypeNullable.put(PropertyType.Boolean, "Boolean");
        propertyToJavaTypeNullable.put(PropertyType.Byte, "Byte");
        propertyToJavaTypeNullable.put(PropertyType.Short, "Short");
        propertyToJavaTypeNullable.put(PropertyType.Int, "Integer");
        propertyToJavaTypeNullable.put(PropertyType.Long, "Long");
        propertyToJavaTypeNullable.put(PropertyType.Float, "Float");
        propertyToJavaTypeNullable.put(PropertyType.Double, "Double");
        propertyToJavaTypeNullable.put(PropertyType.String, "String");
        propertyToJavaTypeNullable.put(PropertyType.ByteArray, "byte[]");
    }

    public Entity addTable(String className) {
        Entity table = new Entity(this, className);
        tables.add(table);
        return table;
    }

    public String mapToDbType(PropertyType propertyType) {
        return mapType(propertyToDbType, propertyType);
    }

    public String mapToJavaTypeNullable(PropertyType propertyType) {
        return mapType(propertyToJavaTypeNullable, propertyType);
    }

    public String mapToJavaTypeNotNull(PropertyType propertyType) {
        return mapType(propertyToJavaTypeNotNull, propertyType);
    }

    private String mapType(Map<PropertyType, String> map, PropertyType propertyType) {
        String dbType = map.get(propertyType);
        if (dbType == null) {
            throw new IllegalStateException("No mapping for " + propertyType);
        }
        return dbType;
    }

    public int getVersion() {
        return version;
    }

    public String getDefaultJavaPackage() {
        return defaultJavaPackage;
    }
    
    public String getDefaultJavaPackageDao() {
        return defaultJavaPackageDao;
    }

    public void setDefaultJavaPackageDao(String defaultJavaPackageDao) {
        this.defaultJavaPackageDao = defaultJavaPackageDao;
    }

    public List<Entity> getTables() {
        return tables;
    }
    
    void init2ndPass() {
        for (Entity table : tables) {
            table.init2ndPass();
        }
    }

}
