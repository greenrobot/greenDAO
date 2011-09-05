package de.greenrobot.daogenerator;

@Deprecated
/** @deprecated Currently unused. */
public class Column {
    private String name;
    private String type;
    private boolean notNull;
    private  boolean unique;

    public Column(String name, String type, boolean notNull, boolean unique) {
        this.name = name;
        this.type = type;
        this.notNull = notNull;
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

}
