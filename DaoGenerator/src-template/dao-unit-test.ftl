<#--

Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)     
                                                                           
This file is part of greenDAO Generator.                                   
                                                                           
greenDAO Generator is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by       
the Free Software Foundation, either version 3 of the License, or          
(at your option) any later version.                                        
greenDAO Generator is distributed in the hope that it will be useful,      
but WITHOUT ANY WARRANTY; without even the implied warranty of             
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              
GNU General Public License for more details.                               
                                                                           
You should have received a copy of the GNU General Public License          
along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.

-->
package ${entity.javaPackageTest};

<#assign isStringPK = entity.pkProperty?? && entity.pkProperty.propertyType == "String" />
<#if isStringPK>
import de.greenrobot.dao.test.AbstractDaoTestStringPk;
<#else>
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
</#if>

import ${entity.javaPackage}.${entity.className};
import ${entity.javaPackageDao}.${entity.classNameDao};

public class ${entity.classNameTest} extends <#if
isStringPK>AbstractDaoTestStringPk<${entity.classNameDao}, ${entity.className}><#else>AbstractDaoTestLongPk<${entity.classNameDao}, ${entity.className}></#if> {

    public ${entity.classNameTest}() {
        super(${entity.classNameDao}.class);
    }

    @Override
    protected ${entity.className} createEntity(<#if isStringPK>String<#else>Long</#if> key) {
        ${entity.className} entity = new ${entity.className}();
<#if entity.pkProperty??>
        entity.set${entity.pkProperty.propertyName?cap_first}(key);
</#if>
<#list entity.properties as property>
<#if property.notNull>
        entity.set${property.propertyName?cap_first}();
</#if> 
</#list>
        return entity;
    }

}
