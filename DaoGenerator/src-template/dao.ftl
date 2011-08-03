<#assign toBindType = {"Boolean":"Long", "Byte":"Long", "Short":"Long", "Int":"Long", "Long":"Long", "Float":"Double", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
<#assign toCursorType = {"Boolean":"Short", "Byte":"Short", "Short":"Short", "Int":"Int", "Long":"Long", "Float":"Float", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
package ${entity.javaPackageDao};

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.Column;

import ${entity.javaPackage}.${entity.className};
<#if entity.protobuf>
import ${entity.javaPackage}.${entity.className}.Builder;
</#if>

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * DAO for table ${entity.tableName} (schema version ${schema.version}).
*/
public class ${entity.classNameDao} extends AbstractDao<${entity.className}, ${entity.pkType}> {

    public static final String TABLENAME = "${entity.tableName}";

    public static Column[] COLUMN_MODEL = {
<#list entity.properties as property>
        new Column("${property.columnName}", ${property.primaryKey?string})<#if property_has_next>,</#if>
</#list>
    };

    public ${entity.classNameDao}(SQLiteDatabase db) {
        super(db);
    }
    
    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "${entity.tableName} (" + //
<#list entity.properties as property>
                "${property.columnName} ${property.columnType}<#if property.constraints??> ${property.constraints} </#if><#if property_has_next>," +<#else>)";</#if> // ${property_index}
</#list>         
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "${entity.tableName}";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    public String getTablename() {
        return TABLENAME;
    }

    /** @inheritdoc */
    @Override
    protected Column[] getColumnModel() {
        return COLUMN_MODEL;
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ${entity.className} entity) {
        stmt.clearBindings();
<#list entity.properties as property>
<#if property.notNull>
        stmt.bind${toBindType[property.propertyType]}(${property_index + 1}, entity.get${property.propertyName?cap_first}()<#if property.propertyType == "Boolean"> ? 1l: 0l</#if>);
<#else>
        ${property.javaType} ${property.propertyName} = entity.get${property.propertyName?cap_first}();
        if (${property.propertyName} != null) {
            stmt.bind${toBindType[property.propertyType]}(${property_index + 1}, ${property.propertyName}<#if property.propertyType == "Boolean"> ? 1l: 0l</#if>);
        }
</#if>
</#list>
    }

    /** @inheritdoc */
    @Override
    public ${entity.className} readFrom(Cursor cursor) {
<#if entity.protobuf>
        Builder builder = ${entity.className}.newBuilder();
<#list entity.properties as property>
        builder.set${property.propertyName?cap_first}(cursor.get${toCursorType[property.propertyType]}(${property_index}));
</#list>        
        return builder.build();
<#elseif entity.constructors>
<#--
############################## readFrom non-protobuff, constructor ############################## 
-->
        return new ${entity.className}( //
<#list entity.properties as property>
            <#if !property.notNull>cursor.isNull(${property_index}) ? null : </#if><#if
            property.propertyType == "Byte">(byte) </#if>cursor.get${toCursorType[property.propertyType]}(${property_index})<#if
            property.propertyType == "Boolean"> != 0</#if><#if property_has_next>,</#if> // ${property.propertyName}
</#list>        
        );
<#else>
<#--
############################## readFrom non-protobuff, setters ############################## 
-->
        ${entity.className} entity = new ${entity.className}();
<#list entity.properties as property>
<#if !property.notNull>
        if (!cursor.isNull(${property_index})) {
    </#if>        entity.set${property.propertyName?cap_first}(<#if property.propertyType == "Byte">(byte) <#--
--></#if>cursor.get${toCursorType[property.propertyType]}(${property_index})<#if property.propertyType == "Boolean"> != 0</#if>);
<#if !property.notNull>
        }
</#if>
</#list>        
        return entity;
</#if>
    }
    
    @Override
    protected void updateKeyAfterInsert(${entity.className} entity, long rowId) {
<#if entity.protobuf>
        // Do nothing: Cannot update protobuf entities after insert
<#else>
<#if entity.pkProperty?? && entity.pkProperty.propertyType == "Long">
        entity.set${entity.pkProperty.propertyName?cap_first}(rowId);
<#else>
        // TODO XXX Only Long PKs are supported currently
</#if>
</#if>
    }
    
    /** @inheritdoc */
    @Override
    public ${entity.pkType} getPrimaryKeyValue(${entity.className} entity) {
<#if entity.pkProperty??>
        if(entity != null) {
            return entity.get${entity.pkProperty.propertyName?cap_first}();
        } else {
            return null;
        }
<#else>
        return null;
</#if>    
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return ${(!entity.protobuf)?string};
    }

}
