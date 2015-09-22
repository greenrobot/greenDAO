package de.greenrobot.performance.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Simple entity with a string property that is indexed.
 */
public class IndexedStringEntity extends RealmObject {

    @PrimaryKey
    private long id;

    @Index
    private String indexedString;

    // Be aware that the getters and setters will be overridden by the generated proxy class
    // used in the back by RealmObjects, so any custom logic you add to the getters & setters
    // will not actually be executed
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIndexedString() {
        return indexedString;
    }

    public void setIndexedString(String indexedString) {
        this.indexedString = indexedString;
    }
}
