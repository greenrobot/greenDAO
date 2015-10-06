package de.greenrobot.performance.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Simple entity with a string property that is indexed.
 */
@Table(name = "INDEXED_STRING_ENTITY")
public class IndexedStringEntity extends Model {

    @Column(name = "INDEXED_STRING", index =  true)
    public String indexedString;

}
