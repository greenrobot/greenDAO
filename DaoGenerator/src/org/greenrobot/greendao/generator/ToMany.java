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

import java.util.List;

/** To-many relationship from a source entity to many target entities. */
@SuppressWarnings("unused")
public class ToMany extends ToManyBase {
    private Property[] sourceProperties;
    private final Property[] targetProperties;

    public ToMany(Schema schema, Entity sourceEntity, Property[] sourceProperties, Entity targetEntity,
            Property[] targetProperties) {
        super(schema, sourceEntity, targetEntity);
        this.sourceProperties = sourceProperties;
        this.targetProperties = targetProperties;
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

    void init2ndPass() {
        super.init2ndPass();
        if (sourceProperties == null) {
            List<Property> pks = sourceEntity.getPropertiesPk();
            if (pks.isEmpty()) {
                throw new RuntimeException("Source entity has no primary key, but we need it for " + this);
            }
            sourceProperties = new Property[pks.size()];
            sourceProperties = pks.toArray(sourceProperties);
        }
        int count = sourceProperties.length;
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

    void init3rdPass() {
        super.init3rdPass();
    }

}
