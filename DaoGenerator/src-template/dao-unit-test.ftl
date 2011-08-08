package ${entity.javaPackageTest};

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import ${entity.javaPackage}.${entity.className};
import ${entity.javaPackageDao}.${entity.classNameDao};

public class ${entity.classNameTest} extends AbstractDaoTestLongPk<${entity.classNameDao}, ${entity.className}> {

    public ${entity.classNameTest}() {
        super(${entity.classNameDao}.class);
    }

    @Override
    protected ${entity.className} createEntity(Long key) {
        ${entity.className} entity = new ${entity.className}();
        entity.setId(key);
<#list entity.properties as property>
<#if property.notNull>
        entity.set${property.propertyName?cap_first}();
</#if> 
</#list>
        return entity;
    }

}
