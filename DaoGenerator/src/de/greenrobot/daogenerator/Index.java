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
import java.util.List;

public class Index {
    private String name;
    private boolean unique;
    private List<Property> properties;
    private List<String> propertiesOrder;

    public Index() {
        properties = new ArrayList<Property>();
        propertiesOrder = new ArrayList<String>();
    }

    public void addProperty(Property property) {
        properties.add(property);
        propertiesOrder.add(null);
    }

    public void addPropertyAsc(Property property) {
        properties.add(property);
        propertiesOrder.add("ASC");
    }

    public void addPropertyDesc(Property property) {
        properties.add(property);
        propertiesOrder.add("DESC");
    }

    public String getName() {
        return name;
    }

    public Index setName(String name) {
        this.name = name;
        return this;
    }

    public List<Property> getProperties() {
        return properties;
    }
    
    List<String> getPropertiesOrder() {
        return propertiesOrder;
    }

    public Index makeUnique() {
        unique = true;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

}
