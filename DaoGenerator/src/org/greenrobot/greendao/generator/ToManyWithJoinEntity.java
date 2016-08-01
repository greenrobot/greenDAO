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

/** To-many relationship to many target entities using a join entity (aka JOIN table). */
@SuppressWarnings("unused")
public class ToManyWithJoinEntity extends ToManyBase {
    private final Entity joinEntity;
    private final Property sourceProperty;
    private final Property targetProperty;

    public ToManyWithJoinEntity(Schema schema, Entity sourceEntity, Entity targetEntity, Entity joinEntity,
                                Property sourceProperty, Property targetProperty) {
        super(schema, sourceEntity, targetEntity);
        this.joinEntity = joinEntity;
        this.sourceProperty = sourceProperty;
        this.targetProperty = targetProperty;
    }

    public Entity getJoinEntity() {
        return joinEntity;
    }

    public Property getSourceProperty() {
        return sourceProperty;
    }

    public Property getTargetProperty() {
        return targetProperty;
    }

    void init3rdPass() {
        super.init3rdPass();
        List<Property> pks = sourceEntity.getPropertiesPk();
        if (pks.isEmpty()) {
            throw new RuntimeException("Source entity has no primary key, but we need it for " + this);
        }
        List<Property> pks2 = targetEntity.getPropertiesPk();
        if (pks2.isEmpty()) {
            throw new RuntimeException("Target entity has no primary key, but we need it for " + this);
        }
    }

}
