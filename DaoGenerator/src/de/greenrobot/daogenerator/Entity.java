package de.greenrobot.daogenerator;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daogenerator.Column.ColumnBuilder;

public class Entity {
    private final Schema schema;
    private final String className;
    private String tableName;
    private String classNameDao;
    private final List<Column> columns;
    private final List<Column> columnsPk;
    private String javaPackage;
    private String javaPackageDao;
    private Column pkColumn;

    public Entity(Schema schema, String className) {
        this.schema = schema;
        this.className = className;
        columns = new ArrayList<Column>();
        columnsPk = new ArrayList<Column>();
    }

    public ColumnBuilder addBooleanColumn(String propertyName) {
        return addColumn(PropertyType.Boolean, propertyName);
    }

    public ColumnBuilder addByteColumn(String propertyName) {
        return addColumn(PropertyType.Byte, propertyName);
    }

    public ColumnBuilder addShortColumn(String propertyName) {
        return addColumn(PropertyType.Short, propertyName);
    }

    public ColumnBuilder addIntColumn(String propertyName) {
        return addColumn(PropertyType.Int, propertyName);
    }

    public ColumnBuilder addLongColumn(String propertyName) {
        return addColumn(PropertyType.Long, propertyName);
    }

    public ColumnBuilder addFloatColumn(String propertyName) {
        return addColumn(PropertyType.Float, propertyName);
    }

    public ColumnBuilder addDoubleColumn(String propertyName) {
        return addColumn(PropertyType.Double, propertyName);
    }

    public ColumnBuilder addByteArrayColumn(String propertyName) {
        return addColumn(PropertyType.ByteArray, propertyName);
    }

    public ColumnBuilder addStringColumn(String propertyName) {
        return addColumn(PropertyType.String, propertyName);
    }

    public ColumnBuilder addColumn(PropertyType propertyType, String propertyName) {
        ColumnBuilder builder = new Column.ColumnBuilder(propertyType, propertyName);
        columns.add(builder.build());
        return builder;
    }

    /** Adds a standard _id column required by standard Android classes, e.g. list adapters. */
    public ColumnBuilder addIdColumn() {
        ColumnBuilder builder = new Column.ColumnBuilder(PropertyType.Int, "id");
        builder.columnName("_id").primaryKey().asc();
        columns.add(builder.build());
        return builder;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getJavaPackageDao() {
        return javaPackageDao;
    }

    public void setJavaPackageDao(String javaPackageDao) {
        this.javaPackageDao = javaPackageDao;
    }

    public String getClassNameDao() {
        return classNameDao;
    }

    public void setClassNameDao(String classNameDao) {
        this.classNameDao = classNameDao;
    }

    public List<Column> getColumnsPk() {
        return columnsPk;
    }

    public Column getPkColumn() {
        return pkColumn;
    }

    void init2ndPass() {
        if (classNameDao == null) {
            classNameDao = className + "Dao";
        }
        if (tableName == null) {
            tableName = DaoUtil.dbName(className);
        }
        if (javaPackage == null) {
            javaPackage = schema.getDefaultJavaPackage();
        }

        if (javaPackageDao == null) {
            javaPackageDao = schema.getDefaultJavaPackageDao();
            if (javaPackageDao == null) {
                javaPackageDao =javaPackage;
            }

        }

        for (Column column : columns) {
            column.init2ndPass(schema, this);
            if (column.isPrimaryKey()) {
                columnsPk.add(column);
            }
        }

        if (columnsPk.size() == 1) {
            pkColumn = columnsPk.get(0);
        }

    }

}
