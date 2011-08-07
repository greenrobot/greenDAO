package ${schema.defaultJavaPackageDao};

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;

<#list schema.entities as entity>
import ${entity.javaPackageDao}.${entity.classNameDao};
</#list>

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Master of DAO (schema version ${schema.version}): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
<#list schema.entities as entity>
        ${entity.classNameDao}.createTable(db, ifNotExists);
</#list>
    }

<#list schema.entities as entity>
    private final ${entity.classNameDao} ${entity.classNameDao?uncap_first};
</#list>        

    public DaoMaster(SQLiteDatabase db) {
        super(db);

<#list schema.entities as entity>
        ${entity.classNameDao?uncap_first} = new ${entity.classNameDao}(db<#if entity.active>, this</#if>);
</#list>        

<#list schema.entities as entity>
        registerDao(${entity.className}.class, ${entity.classNameDao?uncap_first});
</#list>        
    }
    
<#list schema.entities as entity>
    public ${entity.classNameDao} get${entity.classNameDao?cap_first}() {
        return ${entity.classNameDao?uncap_first};
    }

</#list>        
}
