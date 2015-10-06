package de.greenrobot.performance.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Simple entity with a string property.
 */
@ParseClassName("IndexedStringEntity")
public class IndexedStringEntity extends ParseObject {

    // Parse does not seem to support manual definition of indexes, so NOT actually indexed
    public static final String INDEXED_STRING = "indexedString";

    public String getIndexedString() {
        return getString(INDEXED_STRING);
    }

    public void setIndexedString(String value) {
        put(INDEXED_STRING, value);
    }

}
