<#if entity.toOneRelations?has_content>
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendCommaSeparated(builder, "T.", getAllColumns());
            builder.append(',');
<#list entity.toOneRelations as toOne>
            SqlUtils.appendCommaSeparated(builder, "T${toOne_index}.", daoMaster.get${toOne.targetEntity.classNameDao}().getAllColumns());
<#if toOne_has_next>
            builder.append(',');
</#if>
</#list>
            builder.append(" FROM ${entity.tableName} T");
<#list entity.toOneRelations as toOne>
            builder.append(" LEFT JOIN ${toOne.targetEntity.tableName} T${toOne_index}<#--
--> ON T.${toOne.fkProperties[0].columnName}=T${toOne_index}.${toOne.targetEntity.pkProperty.columnName}");
</#list>
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected ${entity.className} readDeepFrom(Cursor cursor) {
        ${entity.className} entity = fetchEntity(cursor, 0);
        int offset = getAllColumns().length;
<#list entity.toOneRelations as toOne>
        entity.set${toOne.name?cap_first}(daoMaster.get${toOne.targetEntity.classNameDao}().fetchEntity(cursor, offset));
<#if toOne_has_next>
        offset += daoMaster.get${toOne.targetEntity.classNameDao}().getAllColumns().length;
</#if>
</#list>
        return entity;    
    }

    public ${entity.className} loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendCommaSeparatedEqPlaceholder(builder, "T.", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return readDeepFrom(cursor);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<${entity.className}> readDeepAllFrom(Cursor cursor) {
        List<${entity.className}> list = new ArrayList<${entity.className}>();
        if (cursor.moveToFirst()) {
            do {
                list.add(readDeepFrom(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }
    
    protected List<${entity.className}> readDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return readDeepAllFrom(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<${entity.className}> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return readDeepAllAndCloseCursor(cursor);
    }
 
</#if>