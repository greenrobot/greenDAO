package de.greenrobot.performance.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Simple entity with a string property that is indexed.
 */
@DatabaseTable(tableName = "INDEXED_STRING_ENTITY")
public class IndexedStringEntity {

    @DatabaseField(id = true)
    public Long _id;

    @DatabaseField(columnName="INDEXED_STRING", index = true)
    public String indexedString;

}
