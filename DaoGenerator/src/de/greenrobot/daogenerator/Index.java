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
