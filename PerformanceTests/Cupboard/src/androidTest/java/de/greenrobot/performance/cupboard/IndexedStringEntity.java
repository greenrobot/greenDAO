package de.greenrobot.performance.cupboard;

import nl.qbusict.cupboard.annotation.Index;

/**
 * Simple entity with a string property that is indexed.
 */
public class IndexedStringEntity {

    public Long _id;

    @Index
    public String indexedString;

}
