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

public class ToOne {
    private final Schema schema;
    private final Entity sourceEntity;
    private final Entity targetEntity;
    private final Property[] fkProperties;
    private Column[] fkColumns;
    private final String[] resolvedKeyJavaType;
    private final boolean[] resolvedKeyUseEquals;
    private String name;

    public ToOne(Schema schema, Entity sourceEntity, Entity targetEntity, Property[] fkProperties) {
        this.schema = schema;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.fkProperties = fkProperties;
        this.fkColumns = null;
        resolvedKeyJavaType = new String[fkProperties.length];
        resolvedKeyUseEquals = new boolean[fkProperties.length];
    }

    public ToOne(Schema schema, Entity sourceEntity, String name, Entity targetEntity, Column[] fkColumns) {
        this.schema = schema;
        this.sourceEntity = sourceEntity;
        this.name = name;
        this.targetEntity = targetEntity;
        this.fkColumns = fkColumns;
        this.fkProperties = null;
        resolvedKeyJavaType = new String[fkColumns.length];
        resolvedKeyUseEquals = new boolean[fkColumns.length];
    }

    public Entity getSourceEntity() {
        return sourceEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public Property[] getFkProperties() {
        return fkProperties;
    }

    public Column[] getFkColumns() {
        return fkColumns;
    }

    public String[] getResolvedKeyJavaType() {
        return resolvedKeyJavaType;
    }

    public boolean[] getResolvedKeyUseEquals() {
        return resolvedKeyUseEquals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void init2ndPass() {
        if (name == null) {
            char[] nameCharArray = targetEntity.getClassName().toCharArray();
            nameCharArray[0] = Character.toLowerCase(nameCharArray[0]);
            name = new String(nameCharArray);
        }
        if (fkProperties != null) {
            for (int i = 0; i < fkProperties.length; i++) {
                Property property = fkProperties[i];
                PropertyType propertyType = property.getPropertyType();
                resolvedKeyJavaType[i] = schema.mapToJavaTypeNullable(propertyType);
                resolvedKeyUseEquals[i] = checkUseEquals(propertyType);
            }
        } else {
            if (fkColumns == null) {
                throw new RuntimeException("Neither FK properties or columns are present");
            }
        }
    }

    public void init3ndPass() {
        if (fkColumns != null) {
            if (fkColumns.length != 1) {
                throw new RuntimeException("Currently only single FK columns are supported: " + this);
            }
            Property targetPkProperty = targetEntity.getPkProperty();
            fkColumns[0].setType(targetPkProperty.getColumnType());
            PropertyType propertyType = targetPkProperty.getPropertyType();
            resolvedKeyJavaType[0] = schema.mapToJavaTypeNullable(propertyType);
            resolvedKeyUseEquals[0] = checkUseEquals(propertyType);
        } else {
            fkColumns = new Column[fkProperties.length];
            for (int i = 0; i < fkProperties.length; i++) {
                String name = fkProperties[i].getColumnName();
                String type = fkProperties[i].getColumnType();
                boolean notNull = fkProperties[i].isNotNull();
                fkColumns[i] = new Column(name, type, notNull);
            }
        }
    }

    protected boolean checkUseEquals(PropertyType propertyType) {
        boolean useEquals;
        switch (propertyType) {
        case Byte:
        case Short:
        case Int:
        case Long:
        case Boolean:
        case Float:
            useEquals = true;
            break;
        default:
            useEquals = false;
            break;
        }
        return useEquals;
    }

    @Override
    public String toString() {
        return "ToOne '" + name + "' from " + sourceEntity + " to " + targetEntity;
    }

}
