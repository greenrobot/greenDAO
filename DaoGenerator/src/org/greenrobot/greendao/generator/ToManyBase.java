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

/** Base class for to-many relationship from source entities to target entities. */
@SuppressWarnings("unused")
public abstract class ToManyBase {
    @SuppressWarnings("unused")
    private final Schema schema;
    private String name;
    protected final Entity sourceEntity;
    protected final Entity targetEntity;
    private final PropertyOrderList propertyOrderList;

    public ToManyBase(Schema schema, Entity sourceEntity, Entity targetEntity) {
        this.schema = schema;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        propertyOrderList = new PropertyOrderList();
    }

    public Entity getSourceEntity() {
        return sourceEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the relation, which is used as the property name in the entity (the source entity owning the
     * to-many relationship).
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Property of target entity used for ascending order. */
    public void orderAsc(Property... properties) {
        for (Property property : properties) {
            targetEntity.validatePropertyExists(property);
            propertyOrderList.addPropertyAsc(property);
        }
    }

    /** Property of target entity used for descending order. */
    public void orderDesc(Property... properties) {
        for (Property property : properties) {
            targetEntity.validatePropertyExists(property);
            propertyOrderList.addPropertyDesc(property);
        }
    }

    public String getOrder() {
        if (propertyOrderList.isEmpty()) {
            return null;
        } else {
            // Table prefix must match default of QueryBuilder in DaoCore
            return propertyOrderList.getCommaSeparatedString("T");
        }
    }

    /** order spec to be used in generated @OrderBy annotation */
    public String getOrderSpec() {
        if (propertyOrderList.isEmpty()) {
            return null;
        } else {
            return propertyOrderList.getOrderSpec();
        }
    }

    void init2ndPass() {
        if (name == null) {
            char[] nameCharArray = targetEntity.getClassName().toCharArray();
            nameCharArray[0] = Character.toLowerCase(nameCharArray[0]);
            name = new String(nameCharArray) + "List";
        }
    }

    void init3rdPass() {
    }

    @Override
    public String toString() {
        String sourceName = sourceEntity != null ? sourceEntity.getClassName() : null;
        String targetName = targetEntity != null ? targetEntity.getClassName() : null;
        return "ToMany '" + name + "' from " + sourceName + " to " + targetName;
    }

}
