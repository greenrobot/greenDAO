<#assign toBindType = {"Boolean":"Long", "Byte":"Long", "Short":"Long", "Int":"Long", "Long":"Long", "Float":"Double", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
<#assign toCursorType = {"Boolean":"Short", "Byte":"Short", "Short":"Short", "Int":"Int", "Long":"Long", "Float":"Float", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
package de.greenrobot.tvguide.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.MyLog;

import ${table.javaPackage}.${table.className};
import ${table.javaPackage}.${table.className}.Builder;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table ${table.tableName} (schema version ${schema.version}).<br/>
 * Columns:<br/>
<#list table.columns as column>
 *  ${column.columnName}<#if column_has_next>,</#if>
</#list> 
*/
public class ${table.classNameDao} {

    public static final String TABLENAME = "${table.tableName}";
    
    protected static final String COLUMNS_COMMA_SEPARATED = 
        "<#list table.columns as column>${column.columnName}<#if column_has_next>,<#if column_index != 0 && column_index % 4 == 0>" +
        "</#if><#else>";</#if></#list>
    
    /** ends with an space to simplify appending to this string. */    
    protected static final String SELECT_ALL = "SELECT " + COLUMNS_COMMA_SEPARATED + " FROM ${table.tableName} "; 
        
    protected static final String SELECT_BY_KEY = SELECT_ALL + "WHERE _id=?"; 
        

    protected static final String VALUE_PLACEHOLDERS = "<#list table.columns as column>?<#if column_has_next>,<#if column_index != 0 && column_index % 30 == 0>" +
        "</#if><#else>";</#if></#list>
    
    public enum Columns {
<#list table.columns as column>
        /** Maps to property ${column.propertyName} and ordinal ${column_index}. */
        ${column.columnName}<#if column_has_next>,</#if>
</#list>         
    } 

    private final SQLiteDatabase db;
    
    private SQLiteStatement insertStatement;
    private SQLiteStatement insertOrReplaceStatement;


    public ${table.classNameDao}(SQLiteDatabase db) {
        this.db = db;
    }
    
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "${table.tableName} (" + //
<#list table.columns as column>
            "${column.columnName} ${column.columnType}<#if column.constraints??> ${column.constraints} </#if><#if column_has_next>," +<#else>)";</#if> // ${column_index}      
</#list>         
        db.execSQL(sql);
    }
    
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists? "IF EXISTS ": "") + "${table.tableName}";
        db.execSQL(sql);
    }
    
    private SQLiteStatement getInsertStatement() {
        if(insertStatement == null) {
            String sql = "INSERT INTO ${table.tableName} (" + COLUMNS_COMMA_SEPARATED +
                ") VALUES (" + VALUE_PLACEHOLDERS + ")";
            insertStatement = db.compileStatement(sql);
        }
        return insertStatement;
    }

    private SQLiteStatement getInsertOrReplaceStatement() {
        if(insertOrReplaceStatement == null) {
            String sql = "INSERT OR REPLACE INTO ${table.tableName} (" + COLUMNS_COMMA_SEPARATED +
                ") VALUES (" + VALUE_PLACEHOLDERS + ")";
            insertOrReplaceStatement = db.compileStatement(sql);
        }
        return insertOrReplaceStatement;
    }

    public ${table.className} load(<#list table.columnsPk as column>${column.javaType} ${column.propertyName}<#if column_has_next>, </#if></#list>) {
        String[] keyArray = new String[] { Long.toString(id) };
        Cursor cursor = db.rawQuery(SELECT_BY_KEY, keyArray);
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("ID must be unique but count was " + cursor.getCount());
            }
            return readFrom(cursor);
        } finally {
            cursor.close();
        }
    }
    
    public List<${table.className}> loadAll() {
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        return readAllFrom(cursor);
    }

    public void insertInTx(List<${table.className}> list) {
        long start = System.currentTimeMillis();
        boolean success = false;
        db.beginTransaction();
        try {
            for (${table.className} entity : list) {
                insert(entity);
            }
            db.setTransactionSuccessful();
            success = true;
        } finally {
            db.endTransaction();
            if (success) {
                long time = System.currentTimeMillis() - start;
                MyLog.d("Inserted " + list.size() + " ${table.className} in " + time + "ms");
            }
        }
    }

    /** Insert a ${table.className} object into the table ${table.tableName}. */
    public long insert(${table.className} entity) {
        SQLiteStatement stmt = getInsertStatement();
        synchronized(stmt) {
            bindValues(stmt, entity);
            long rowId = stmt.executeInsert();
            // TODO entity.setId(rowId);
            return rowId;
        }
    }

    /** Insert a ${table.className} object into the table ${table.tableName}. */
    public long insertOrReplace(${table.className} entity) {
        SQLiteStatement stmt = getInsertOrReplaceStatement();
        synchronized(stmt) {
            bindValues(stmt, entity);
            long rowId = stmt.executeInsert();
            // TODO entity.setId(rowId);
            return rowId;
        }
    }

    /** Binds the entity's values to the statement. Make sure to synchronize the statement outside of the method. */
    protected void bindValues(SQLiteStatement stmt, ${table.className} entity) {
        stmt.clearBindings();
<#list table.columns as column>
        stmt.bind${toBindType[column.propertyType]}(${column_index + 1}, entity.get${column.propertyName?cap_first}());
</#list>
    }

    /** Reads the values from the current position of the given cursor and returns a new ${table.className} object. */
    public ${table.className} readFrom(Cursor cursor) {
        Builder builder = ${table.className}.newBuilder();
<#list table.columns as column>
        builder.set${column.propertyName?cap_first}(cursor.get${toCursorType[column.propertyType]}(${column_index}));
</#list>        
        return builder.build();
    }

    /** Reads all available rows from the given cursor and returns a list of new ${table.className} objects. */
    protected List<${table.className}> readAllFrom(Cursor cursor) {
        List<${table.className}> list = new ArrayList<${table.className}>();
        if (cursor.moveToFirst()) {
            do {
                ${table.className} entity = readFrom(cursor);
                list.add(entity);
            } while (cursor.moveToNext());
        }
        return list;
    }
    

    public boolean delete(long id) {
        String[] idArray = new String[] { Long.toString(id) };
        int affectedRows = db.delete(TABLENAME, "_id==?", idArray);
        return affectedRows >= 1;
    }
    
    /** SUBJECT TO CHANGE: A raw-style query where you can pass any WHERE clause and arguments. */ 
    public List<ImageTO> query(String where, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(SELECT_ALL + where, selectionArgs);
        return readAllFrom(cursor);
    }
    
    public long count() {
        return DatabaseUtils.queryNumEntries(db, TABLENAME);
    }
}
