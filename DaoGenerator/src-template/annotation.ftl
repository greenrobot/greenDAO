<#macro print_annotation annotation prefix>
${prefix}@${annotation.name}<#rt>
    <@compress single_line=true>
        <#if annotation.parameters?has_content>
            (
            <#assign keys = annotation.parameters?keys>
            <#list keys as key>
                <#if key != annotation_no_name>
                    ${key} =
                </#if>
                ${annotation.parameters[key]}<#t>
                <#if key_has_next>,<#t> </#if>
            </#list>
            )
        </#if>
    </@compress>
</#macro>

<#macro print_annotations annotations prefix>
    <#list annotations as annotation>
        <@print_annotation annotation, prefix/>

    </#list>
</#macro>