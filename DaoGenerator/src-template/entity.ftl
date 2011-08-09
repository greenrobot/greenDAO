<#assign toBindType = {"Boolean":"Long", "Byte":"Long", "Short":"Long", "Int":"Long", "Long":"Long", "Float":"Double", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
<#assign toCursorType = {"Boolean":"Short", "Byte":"Short", "Short":"Short", "Int":"Int", "Long":"Long", "Float":"Float", "Double":"Double", "String":"String", "ByteArray":"Blob" }>
package ${entity.javaPackage};

<#if entity.active>
import ${schema.defaultJavaPackageDao}.DaoMaster;
import de.greenrobot.dao.DaoException;

</#if>
// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Entity mapped to table ${entity.tableName} (schema version ${schema.version}).
*/
public class ${entity.className} <#if entity.active && false>extends ActiveEntity </#if>{

<#list entity.properties as property>
    private ${property.javaType} ${property.propertyName}; 
</#list>

<#if entity.active>
    /** Used to resolve relations */
    private DaoMaster daoMaster;

<#list entity.toOneRelations as toOne>
    private ${toOne.entity.className} ${toOne.name};
    private boolean ${toOne.name}__resolved;

</#list>    
</#if>
<#if entity.constructors>
    public ${entity.className}() {
    }
<#if entity.propertiesPk?has_content && entity.propertiesPk?size != entity.properties?size>

    public ${entity.className}(<#list entity.propertiesPk as
property>${property.javaType} ${property.propertyName}<#if property_has_next>, </#if></#list>) {
<#list entity.propertiesPk as property>
        this.${property.propertyName} = ${property.propertyName};
</#list>
    }
</#if>

    public ${entity.className}(<#list entity.properties as
property>${property.javaType} ${property.propertyName}<#if property_has_next>, </#if></#list>) {
<#list entity.properties as property>
        this.${property.propertyName} = ${property.propertyName};
</#list>
    }
</#if>

<#if entity.active>
    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoMaster(DaoMaster daoMaster) {
        this.daoMaster = daoMaster;
    }

</#if>
<#list entity.properties as property>
    public ${property.javaType} get${property.propertyName?cap_first}() {
        return ${property.propertyName};
    } 

    public void set${property.propertyName?cap_first}(${property.javaType} ${property.propertyName}) {
        this.${property.propertyName} = ${property.propertyName};
    } 

</#list>
<#list entity.toOneRelations as toOne>
    /** To-one relationship, resolved on first access. */ 
    public ${toOne.entity.className} get${toOne.name?cap_first}() {
        if(!${toOne.name}__resolved) {
            ${toOne.entity.classNameDao} dao = daoMaster.get${toOne.entity.classNameDao?cap_first}();
             ${toOne.name} = dao.load(<#list toOne.fkProperties as fk>${fk.propertyName}<#if fk_has_next>, </#if></#list>);
             ${toOne.name}__resolved = true;
        }
        return ${toOne.name};
    } 

    public void set${toOne.name?cap_first}(${toOne.entity.className} ${toOne.name}) {
<#if toOne.fkProperties[0].notNull>
        if(${toOne.name} == null) {
            throw new DaoException("To-one property '${toOne.fkProperties[0].propertyName}' has not-null constraint; cannot set to-one to null"); 
        }
</#if>
        this.${toOne.name} = ${toOne.name};
        ${toOne.fkProperties[0].propertyName} = <#if !toOne.fkProperties[0].notNull>${toOne.name} == null ? null : </#if>${toOne.name}.get${toOne.entity.pkProperty.propertyName?cap_first}();
        ${toOne.name}__resolved = true;
    } 

</#list>
<#--        
        return dao.query(", ${entity.tableName} T2 WHERE T.=T2. AND T.=?", <#list
            toOne.fkProperties as fk>${fk.propertyName}<#if fk_has_next>, </#if></#list>);
-->

}
