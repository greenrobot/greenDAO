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

import java.util.List;

public class ToMany {
    private final Schema schema;
    private String name;
    private final Entity sourceEntity;
    private final Entity targetEntity;
    private Property[] sourceProperties;
    private final Property[] targetProperties;

    public ToMany(Schema schema, Entity sourceEntity, Property[] sourceProperties, Entity targetEntity,
            Property[] targetProperties) {
        this.schema = schema;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.sourceProperties = sourceProperties;
        this.targetProperties = targetProperties;
    }

    public Entity getSourceEntity() {
        return sourceEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public Property[] getSourceProperties() {
        return sourceProperties;
    }

    public void setSourceProperties(Property[] sourceProperties) {
        this.sourceProperties = sourceProperties;
    }

    public Property[] getTargetProperties() {
        return targetProperties;
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
        if (sourceProperties == null) {
            List<Property> pks = sourceEntity.getPropertiesPk();
            sourceProperties = new Property[pks.size()];
            sourceProperties = pks.toArray(sourceProperties);
        }
        int count = sourceProperties.length;
        if (count != 1) {
            throw new RuntimeException("Currently only single FK columns are supported: " + this);
        }
        if (count != targetProperties.length) {
            throw new RuntimeException("Source properties do not match target properties: " + this);
        }

        for (int i = 0; i < count; i++) {
            Property sourceProperty = sourceProperties[i];
            Property targetProperty = targetProperties[i];

            PropertyType sourceType = sourceProperty.getPropertyType();
            PropertyType targetType = targetProperty.getPropertyType();
            if (sourceType == null || targetType == null) {
                throw new RuntimeException("Property type uninitialized");
            }
            if (sourceType != targetType) {
                System.err.println("Warning to-one property type does not match target key type: " + this);
            }
        }
    }

    public void init3ndPass() {
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
