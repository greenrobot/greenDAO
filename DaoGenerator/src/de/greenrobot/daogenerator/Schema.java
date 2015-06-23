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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The "root" model class to which you can add entities to.
 * 
 * @see <a href="http://greendao-orm.com/documentation/modelling-entities/">Modelling Entities (Documentation page)</a>
 */
public class Schema {
    private final int version;
    private final String defaultJavaPackage;
    private String defaultJavaPackageDao;
    private String defaultJavaPackageTest;
    private final List<Entity> entities;
    private Map<PropertyType, String> propertyToDbType;
    private Map<PropertyType, String> propertyToJavaTypeNotNull;
    private Map<PropertyType, String> propertyToJavaTypeNullable;
    private boolean hasKeepSectionsByDefault;
    private boolean useActiveEntitiesByDefault;

    public Schema(int version, String defaultJavaPackage) {
        this.version = version;
        this.defaultJavaPackage = defaultJavaPackage;
        this.entities = new ArrayList<Entity>();
        initTypeMappings();
    }

    public void enableKeepSectionsByDefault() {
        hasKeepSectionsByDefault = true;
    }

    public void enableActiveEntitiesByDefault() {
        useActiveEntitiesByDefault = true;
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
        propertyToDbType.put(PropertyType.Date, "INTEGER");

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
        propertyToJavaTypeNotNull.put(PropertyType.Date, "java.util.Date");

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
        propertyToJavaTypeNullable.put(PropertyType.Date, "java.util.Date");
    }

    /**
     * Adds a new entity to the schema. There can be multiple entities per table, but only one may be the primary entity
     * per table to create table scripts, etc.
     */
    public Entity addEntity(String className) {
        Entity entity = new Entity(this, className);
        entities.add(entity);
        return entity;
    }

    /**
     * Adds a new protocol buffers entity to the schema. There can be multiple entities per table, but only one may be
     * the primary entity per table to create table scripts, etc.
     */
    public Entity addProtobufEntity(String className) {
        Entity entity = addEntity(className);
        entity.useProtobuf();
        return entity;
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

    public String getDefaultJavaPackageTest() {
        return defaultJavaPackageTest;
    }

    public void setDefaultJavaPackageTest(String defaultJavaPackageTest) {
        this.defaultJavaPackageTest = defaultJavaPackageTest;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public boolean isHasKeepSectionsByDefault() {
        return hasKeepSectionsByDefault;
    }

    public boolean isUseActiveEntitiesByDefault() {
        return useActiveEntitiesByDefault;
    }

    void init2ndPass() {
        if (defaultJavaPackageDao == null) {
            defaultJavaPackageDao = defaultJavaPackage;
        }
        if (defaultJavaPackageTest == null) {
            defaultJavaPackageTest = defaultJavaPackageDao;
        }
        for (Entity entity : entities) {
            entity.init2ndPass();
        }
    }

    void init3ndPass() {
        for (Entity entity : entities) {
            entity.init3ndPass();
        }
    }

}
