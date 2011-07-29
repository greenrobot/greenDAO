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
        "<#list entity.properties as property>${property.columnName}<#if property_has_next>,<#if property_index != 0 && property_index % 5 == 0>" +
        "</#if><#else>";</#if></#list>
    
    protected static final String VALUE_PLACEHOLDERS = "<#list entity.properties as property>?<#if property_has_next>,<#if property_index != 0 && property_index % 30 == 0>" +
        "</#if><#else>";</#if></#list>

    public enum Columns {
<#list entity.properties as property>
        /** Maps to property ${property.propertyName} and ordinal ${property_index}. */
        ${property.columnName}<#if property_has_next>,</#if>
</#list>         
    }

    public ${entity.classNameDao}(SQLiteDatabase db) {
        super(db);
    }
    
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "${entity.tableName} (" + //
<#list entity.properties as property>
            "${property.columnName} ${property.columnType}<#if property.constraints??> ${property.constraints} </#if><#if property_has_next>," +<#else>)";</#if> // ${property_index}
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
<#list entity.properties as property>
        stmt.bind${toBindType[property.propertyType]}(${property_index + 1}, entity.get${property.propertyName?cap_first}());
</#list>
    }

    /** @inheritdoc Reads the values from the current position of the given cursor and returns a new ${entity.className} object. */
    public ${entity.className} readFrom(Cursor cursor) {
        Builder builder = ${entity.className}.newBuilder();
<#list entity.properties as property>
        builder.set${property.propertyName?cap_first}(cursor.get${toCursorType[property.propertyType]}(${property_index}));
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
