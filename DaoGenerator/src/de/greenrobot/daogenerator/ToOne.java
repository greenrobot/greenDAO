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
        for (int i = 0; i < fkProperties.length; i++) {
            Property property = fkProperties[i];
            PropertyType propertyType = property.getPropertyType();
            resolvedKeyJavaType[i] = schema.mapToJavaTypeNullable(propertyType);
            resolvedKeyUseEquals[i] = checkUseEquals(propertyType);
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

}
