/*******************************************************************************
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/

package org.greenrobot.greendao.performance.target;

import org.greenrobot.greendao.DaoLog;

import java.util.Iterator;

/**
 * An long to object hashmap, taken from Amarena2D (thanks Christoph :p).
 * 
 * @author christop widulle
 * 
 * @param <T>
 */
public class LongHashMapAmarena2DZechner<T> {

    private Entry[] table;
    private float loadFactor;
    private int size, mask, capacity, threshold;

    public LongHashMapAmarena2DZechner() {
        this(16, 0.75f);
    }

    public LongHashMapAmarena2DZechner(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public LongHashMapAmarena2DZechner(int initialCapacity, float loadFactor) {
        if (initialCapacity > 1 << 30)
            throw new IllegalArgumentException("initialCapacity is too large.");
        if (initialCapacity < 0)
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        if (loadFactor <= 0)
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.mask = capacity - 1;
    }

    public boolean containsValue(Object value) {
        Entry[] table = this.table;
        for (int i = table.length; i-- > 0;)
            for (Entry e = table[i]; e != null; e = e.next)
                if (e.value.equals(value))
                    return true;
        return false;
    }

    public boolean containsKey(long key) {
        int index = ((int) key) & mask;
        for (Entry e = table[index]; e != null; e = e.next)
            if (e.key == key)
                return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    public T get(long key) {
        int index = (int) (key & mask);
        for (Entry e = table[index]; e != null; e = e.next)
            if (e.key == key)
                return (T) e.value;
        return null;
    }

    @SuppressWarnings("unchecked")
    public T put(long key, T value) {
        int index = (int) (key & mask);
        // Check if key already exists.
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.key != key)
                continue;
            Object oldValue = e.value;
            e.value = value;
            return (T) oldValue;
        }
        table[index] = new Entry(key, value, table[index]);
        if (size++ >= threshold) {
            // Rehash.
            int newCapacity = 2 * capacity;
            Entry[] newTable = new Entry[newCapacity];
            Entry[] src = table;
            int bucketmask = newCapacity - 1;
            for (int j = 0; j < src.length; j++) {
                Entry e = src[j];
                if (e != null) {
                    src[j] = null;
                    do {
                        Entry next = e.next;
                        index = (int) (e.key & bucketmask);
                        e.next = newTable[index];
                        newTable[index] = e;
                        e = next;
                    } while (e != null);
                }
            }
            table = newTable;
            capacity = newCapacity;
            threshold = (int) (newCapacity * loadFactor);
            mask = capacity - 1;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public T remove(long key) {
        int index = (int) (key & mask);
        Entry prev = table[index];
        Entry e = prev;
        while (e != null) {
            Entry next = e.next;
            if (e.key == key) {
                size--;
                if (prev == e) {
                    table[index] = next;
                } else {
                    prev.next = next;
                }
                return (T) e.value;
            }
            prev = e;
            e = next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Entry[] table = this.table;
        for (int index = table.length; --index >= 0;)
            table[index] = null;
        size = 0;
    }

    static class Entry {
        final long key;
        Object value;
        Entry next;

        Entry(long k, Object v, Entry n) {
            key = k;
            value = v;
            next = n;
        }
    }

    EntryIterable iterable = new EntryIterable();

    public Iterable<T> values() {
        iterable.reset();
        return iterable;
    }

    class EntryIterable implements Iterable<T> {
        int currIndex = -1;
        Entry currEntry = null;

        Iterator<T> iter = new Iterator<T>() {
            @Override
            public boolean hasNext() {
                if (currEntry == null) {
                    if (!loadNextEntry())
                        return false;
                } else {
                    if (currEntry.next == null) {
                        if (loadNextEntry() == false)
                            return false;
                    } else
                        currEntry = currEntry.next;
                }

                return true;
            }

            @SuppressWarnings("synthetic-access")
            private boolean loadNextEntry() {
                while (true) {
                    currIndex++;
                    if (currIndex >= table.length)
                        return false;

                    if (table[currIndex] == null)
                        continue;
                    else {
                        currEntry = table[currIndex];
                        return true;
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                return (T) currEntry.value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("not implemented");
            }

        };

        public void reset() {
            currIndex = -1;
            currEntry = null;
        }

        @Override
        public Iterator<T> iterator() {
            return iter;
        }

    }

    public void logStats() {
        int collisions = 0;
        for (Entry entry : table) {
            while (entry != null && entry.next != null) {
                collisions++;
                entry = entry.next;
            }
        }
        DaoLog.d("load: " + ((float) size) / capacity + ", size: " + size + ", capa: " + capacity + ", collisions: "
                + collisions + ", collision ratio: " + ((float) collisions) / size);
    }

}
