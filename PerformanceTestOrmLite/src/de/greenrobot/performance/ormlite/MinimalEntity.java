package de.greenrobot.performance.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MINIMAL_ENTITY")
public class MinimalEntity {

    @DatabaseField(id = true, columnName="_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
