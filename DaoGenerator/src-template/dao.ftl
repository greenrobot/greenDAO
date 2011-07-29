<#assign toBindType = {"Boolean":"Long", "Byte":"Long", "Short":"Long", "Int":"Long", "Long":"Long", "Float":"Double", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
<#assign toCursorType = {"Boolean":"Short", "Byte":"Short", "Short":"Short", "Int":"Int", "Long":"Long", "Float":"Float", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
package de.greenrobot.tvguide.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;

import ${table.javaPackage}.${table.className};
import ${table.javaPackage}.${table.className}.Builder;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table ${table.tableName} (schema version ${schema.version}).
*/
public class ${table.classNameDao} extends AbstractDao<${table.className}, Long> {

    public static final String TABLENAME = "${table.tableName}";
    
    protected static final String COLUMNS_COMMA_SEPARATED = 
        "<#list table.columns as column>${column.columnName}<#if column_has_next>,<#if column_index != 0 && column_index % 5 == 0>" +
        "</#if><#else>";</#if></#list>
    
    protected static final String VALUE_PLACEHOLDERS = "<#list table.columns as column>?<#if column_has_next>,<#if column_index != 0 && column_index % 30 == 0>" +
        "</#if><#else>";</#if></#list>

    public enum Columns {
<#list table.columns as column>
        /** Maps to property ${column.propertyName} and ordinal ${column_index}. */
        ${column.columnName}<#if column_has_next>,</#if>
</#list>         
    }

    public ${table.classNameDao}(SQLiteDatabase db) {
        super(db);
    }
    
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "${table.tableName} (" + //
<#list table.columns as column>
            "${column.columnName} ${column.columnType}<#if column.constraints??> ${column.constraints} </#if><#if column_has_next>," +<#else>)";</#if> // ${column_index}
</#list>         
        db.execSQL(sql);
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "${table.tableName}";
        db.execSQL(sql);
    }

    @Override
    protected String getTablename() {
        return TABLENAME;
    }

    @Override
    protected String getValuePlaceholders() {
        return VALUE_PLACEHOLDERS;
    }

    @Override
    protected String getColumnsCommaSeparated() {
        return COLUMNS_COMMA_SEPARATED;
    }

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    protected void bindValues(SQLiteStatement stmt, ${table.className} entity) {
        stmt.clearBindings();
<#list table.columns as column>
        stmt.bind${toBindType[column.propertyType]}(${column_index + 1}, entity.get${column.propertyName?cap_first}());
</#list>
    }

    /** @inheritdoc Reads the values from the current position of the given cursor and returns a new ${table.className} object. */
    public ${table.className} readFrom(Cursor cursor) {
        Builder builder = ${table.className}.newBuilder();
<#list table.columns as column>
        builder.set${column.propertyName?cap_first}(cursor.get${toCursorType[column.propertyType]}(${column_index}));
</#list>        
        return builder.build();
    }
    
    protected void updateKeyAfterInsert(${table.className} entity, long rowId) {
        // TODO updateKeyAfterInsert
    }
    
    @Override
    /** @inheritdoc */
    protected String getPkColumn() {
        return null;
    }
}
