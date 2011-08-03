package de.greenrobot.dao;

public class Column {
    public final String name;
    public final boolean primaryKey;

    public Column(String name, boolean primaryKey) {
        this.name = name;
        this.primaryKey = primaryKey;
    }

}
