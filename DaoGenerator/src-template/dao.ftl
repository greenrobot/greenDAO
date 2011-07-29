<#assign toBindType = {"Boolean":"Long", "Byte":"Long", "Short":"Long", "Int":"Long", "Long":"Long", "Float":"Double", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
<#assign toCursorType = {"Boolean":"Short", "Byte":"Short", "Short":"Short", "Int":"Int", "Long":"Long", "Float":"Float", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
package de.greenrobot.tvguide.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;

import ${entity.javaPackage}.${entity.className};
import ${entity.javaPackage}.${entity.className}.Builder;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table ${entity.tableName} (schema version ${schema.version}).
*/
public class ${entity.classNameDao} extends AbstractDao<${entity.className}, Long> {

    public static final String TABLENAME = "${entity.tableName}";
    
    protected static final String COLUMNS_COMMA_SEPARATED = 
        "<#list entity.columns as column>${column.columnName}<#if column_has_next>,<#if column_index != 0 && column_index % 5 == 0>" +
        "</#if><#else>";</#if></#list>
    
    protected static final String VALUE_PLACEHOLDERS = "<#list entity.columns as column>?<#if column_has_next>,<#if column_index != 0 && column_index % 30 == 0>" +
        "</#if><#else>";</#if></#list>

    public enum Columns {
<#list entity.columns as column>
        /** Maps to property ${column.propertyName} and ordinal ${column_index}. */
        ${column.columnName}<#if column_has_next>,</#if>
</#list>         
    }

    public ${entity.classNameDao}(SQLiteDatabase db) {
        super(db);
    }
    
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "${entity.tableName} (" + //
<#list entity.columns as column>
            "${column.columnName} ${column.columnType}<#if column.constraints??> ${column.constraints} </#if><#if column_has_next>," +<#else>)";</#if> // ${column_index}
</#list>         
        db.execSQL(sql);
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "${entity.tableName}";
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
    protected void bindValues(SQLiteStatement stmt, ${entity.className} entity) {
        stmt.clearBindings();
<#list entity.columns as column>
        stmt.bind${toBindType[column.propertyType]}(${column_index + 1}, entity.get${column.propertyName?cap_first}());
</#list>
    }

    /** @inheritdoc Reads the values from the current position of the given cursor and returns a new ${entity.className} object. */
    public ${entity.className} readFrom(Cursor cursor) {
        Builder builder = ${entity.className}.newBuilder();
<#list entity.columns as column>
        builder.set${column.propertyName?cap_first}(cursor.get${toCursorType[column.propertyType]}(${column_index}));
</#list>        
        return builder.build();
    }
    
    protected void updateKeyAfterInsert(${entity.className} entity, long rowId) {
        // TODO updateKeyAfterInsert
    }
    
    @Override
    /** @inheritdoc */
    protected String getPkColumn() {
        return null;
    }
}
