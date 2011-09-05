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
    private final String[] resolvedKeyJavaType;
    private final boolean[] resolvedKeyUseEquals;
    private String name;

    public ToOne(Schema schema, Entity sourceEntity, Entity targetEntity, Property[] fkProperties) {
        this.schema = schema;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.fkProperties = fkProperties;
        resolvedKeyJavaType = new String[fkProperties.length];
        resolvedKeyUseEquals = new boolean[fkProperties.length];
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

    }

    /** Constructs fkColumns. Depends on 2nd pass of target key properties. */
    public void init3ndPass() {

        Property targetPkProperty = targetEntity.getPkProperty();
        if (fkProperties.length != 1 || targetPkProperty == null) {
            throw new RuntimeException("Currently only single FK columns are supported: " + this);
        }

        Property property = fkProperties[0];
        PropertyType propertyType = property.getPropertyType();
        if (propertyType == null) {
            propertyType = targetPkProperty.getPropertyType();
            property.setPropertyType(propertyType);
        } else if (propertyType != targetPkProperty.getPropertyType()) {
            System.err.println("Warning to-one property type does not match target key type: " + this);
        }
        resolvedKeyJavaType[0] = schema.mapToJavaTypeNullable(propertyType);
        resolvedKeyUseEquals[0] = checkUseEquals(propertyType);
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
