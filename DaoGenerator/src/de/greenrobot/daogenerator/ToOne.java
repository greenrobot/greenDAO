package de.greenrobot.daogenerator;

public class ToOne {
    private Entity entity;
    private Property[] fkProperties;
    private String name;

    public ToOne(Entity entity, Property[] fkProperties) {
        this.entity = entity;
        this.fkProperties = fkProperties;
    }

    public Entity getEntity() {
        return entity;
    }

    public Property[] getFkProperties() {
        return fkProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
