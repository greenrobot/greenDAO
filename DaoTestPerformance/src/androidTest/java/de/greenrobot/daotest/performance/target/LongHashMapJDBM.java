/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.greenrobot.daotest.performance.target;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hash Map which uses primitive long as key. 
 * Main advantage is new instanceof of Long does not have to be created for each lookup.
 * <p>
 * This code comes from Android, which in turns comes to Apache Harmony. 
 * This class was modified to use primitive longs and stripped down to consume less space. 
 * <p>
 * Author of JDBM modifications: Jan Kotek
 * <p>
 * Note: This map have weakened hash function, this works well for JDBM, but may be wrong for many other applications.  
 */
public class LongHashMapJDBM<V> implements  Serializable {
    private static final long serialVersionUID = 362499999763181265L;

    protected int elementCount;

    protected Entry<V>[] elementData;

    private final float loadFactor;

    protected int threshold;

    transient int modCount = 0;	

    private static final int DEFAULT_SIZE = 16;
    
    transient Entry<V> reuseAfterDelete = null;

    static final class Entry<V> {

        Entry<V> next;

        long key;
        V value;
        
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Entry) {
                Entry<?> entry = (Entry) object;
                return ( key == entry.key)
                        && (value == null ? entry.value == null : value
                                .equals(entry.value));
            }
            return false;
        }


        
        public int hashCode() {
            return (int)(key)
                    ^ (value == null ? 0 : value.hashCode());
        }

        
        
        public String toString() {
            return key + "=" + value;
        }
        

        
        Entry(long theKey) {
        	this.key = theKey;
        	this.value = null;
        }

        Entry(long theKey, V theValue) {
            this.key = theKey;
            this.value = theValue;
            //origKeyHash = (int)(theKey ^ (theKey >>> 32));
        }

    }
    

    interface EntryType<RT,VT> {
        RT get(Entry<VT> entry);
    }

    static class HashMapIterator<E,VT> implements Iterator<E> {
        private int position = 0;

        int expectedModCount;

        final EntryType<E, VT> type;

        boolean canRemove = false;

        Entry<VT> entry;

        Entry<VT> lastEntry;

        final LongHashMapJDBM<VT> associatedMap;

        HashMapIterator(EntryType<E, VT> value, LongHashMapJDBM<VT> hm) {
            associatedMap = hm;
            type = value;
            expectedModCount = hm.modCount;
        }

        public boolean hasNext() {
            if (entry != null) {
                return true;
            }
            // BEGIN android-changed
            Entry<VT>[] elementData = associatedMap.elementData;
            int length = elementData.length;
            int newPosition = position;
            boolean result = false;

            while (newPosition < length) {
                if (elementData[newPosition] == null) {
                    newPosition++;
                } else {
                    result = true;
                    break;
                }
            }

            position = newPosition;
            return result;
            // END android-changed
        }

        void checkConcurrentMod() throws ConcurrentModificationException {
            if (expectedModCount != associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        public E next() {
            // BEGIN android-changed
            // inline checkConcurrentMod()
            if (expectedModCount != associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<VT> result;
            Entry<VT> _entry  = entry;
            if (_entry == null) {
                result = lastEntry = associatedMap.elementData[position++];
                entry = lastEntry.next;
            } else {
                if (lastEntry.next != _entry) {
                    lastEntry = lastEntry.next;
                }
                result = _entry;
                entry = _entry.next;
            }
            canRemove = true;
            return type.get(result);
            // END android-changed
        }

        public void remove() {
            checkConcurrentMod();
            if (!canRemove) {
                throw new IllegalStateException();
            }

            canRemove = false;
            associatedMap.modCount++;
            if (lastEntry.next == entry) {
                while (associatedMap.elementData[--position] == null) {
                    // Do nothing
                }
                associatedMap.elementData[position] = associatedMap.elementData[position].next;
                entry = null;
            } else {
                lastEntry.next = entry;
            }
            if(lastEntry!=null){
            	Entry<VT> reuse = lastEntry;
            	lastEntry = null;
            	reuse.key = Long.MIN_VALUE;
            	reuse.value = null;
            	associatedMap.reuseAfterDelete = reuse;
            }

            associatedMap.elementCount--;
            expectedModCount++;
        }
    }



    @SuppressWarnings("unchecked")
    Entry<V>[] newElementArray(int s) {
        return new Entry[s];
    }

    /**
     * Constructs a new empty {@code HashMap} instance.
     * 
     * @since Android 1.0
     */
    public LongHashMapJDBM() {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of this hash map.
     * @throws IllegalArgumentException
     *                when the capacity is less than zero.
     * @since Android 1.0
     */
    public LongHashMapJDBM(int capacity) {
        if (capacity >= 0) {
            elementCount = 0;
            elementData = newElementArray(capacity == 0 ? 1 : capacity);
            loadFactor = 0.75f; // Default load factor of 0.75
            computeMaxSize();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity and
     * load factor.
     * 
     * @param capacity
     *            the initial capacity of this hash map.
     * @param loadFactor
     *            the initial load factor.
     * @throws IllegalArgumentException
     *                when the capacity is less than zero or the load factor is
     *                less or equal to zero.
     * @since Android 1.0
     */
    public LongHashMapJDBM(int capacity, float loadFactor) {
        if (capacity >= 0 && loadFactor > 0) {
            elementCount = 0;
            elementData = newElementArray(capacity == 0 ? 1 : capacity);
            this.loadFactor = loadFactor;
            computeMaxSize();
        } else {
            throw new IllegalArgumentException();
        }
    }



    // BEGIN android-changed
    /**
     * Removes all mappings from this hash map, leaving it empty.
     * 
     * @see #isEmpty
     * @see #size
     * @since Android 1.0
     */
    
    public void clear() {
        if (elementCount > 0) {
            elementCount = 0;
            Arrays.fill(elementData, null);
            modCount++;
        }
    }
    // END android-changed

    /**
     * Returns a shallow copy of this map.
     * 
     * @return a shallow copy of this map.
     * @since Android 1.0
     */
    

    private void computeMaxSize() {
        threshold = (int) (elementData.length * loadFactor);
    }

    /**
     * Returns whether this map contains the specified key.
     * 
     * @param key
     *            the key to search for.
     * @return {@code true} if this map contains the specified key,
     *         {@code false} otherwise.
     * @since Android 1.0
     */
    
    public boolean containsKey(long key) {
            int hash = (int)(key);
            int index = (hash & 0x7FFFFFFF) % elementData.length;
            Entry<V> m = findNonNullKeyEntry(key, index, hash);
        return m != null;
    }

    /**
     * Returns whether this map contains the specified value.
     * 
     * @param value
     *            the value to search for.
     * @return {@code true} if this map contains the specified value,
     *         {@code false} otherwise.
     * @since Android 1.0
     */
    
    public boolean containsValue(Object value) {
        if (value != null) {
            for (int i = elementData.length; --i >= 0;) {
                Entry<V> entry = elementData[i];
                while (entry != null) {
                    if (value.equals(entry.value)) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            for (int i = elementData.length; --i >= 0;) {
                Entry<V> entry = elementData[i];
                while (entry != null) {
                    if (entry.value == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }


    /**
     * Returns the value of the mapping with the specified key.
     * 
     * @param key
     *            the key.
     * @return the value of the mapping with the specified key, or {@code null}
     *         if no mapping for the specified key is found.
     * @since Android 1.0
     */
    
    public V get(long key) {
        Entry<V> m;
        int hash = (int)(key);
        int index = (hash & 0x7FFFFFFF) % elementData.length;
        m = findNonNullKeyEntry(key, index, hash);

        if (m != null) {
            return m.value;
        }
        return null;
    }

    final Entry<V> findNonNullKeyEntry(long key, int index, int keyHash) {
        Entry<V> m = elementData[index];
            while (m != null) {

                    if (key == m.key) {
                        return m;
                    }

                m = m.next;

        }
        return null;
    }


    /**
     * Returns whether this map is empty.
     * 
     * @return {@code true} if this map has no elements, {@code false}
     *         otherwise.
     * @see #size()
     * @since Android 1.0
     */
    
    public boolean isEmpty() {
        return elementCount == 0;
    }

    /**
     * @return iterator over keys
     */
    
//      public Iterator<K> keyIterator(){
//                 return new HashMapIterator<K, K, V>(
//                            new MapEntry.Type<K, K, V>() {
//                                public K get(Entry<K, V> entry) {
//                                    return entry.key;
//                                }
//                            }, HashMap.this);
//
//     }

    /**
     * Maps the specified key to the specified value.
     * 
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @return the value of any previous mapping with the specified key or
     *         {@code null} if there was no such mapping.
     * @since Android 1.0
     */
    

    public V put(long key, V value) {
        Entry<V> entry;
            int hash =(int)(key);
            int index = (hash & 0x7FFFFFFF) % elementData.length;
            entry = findNonNullKeyEntry(key, index, hash);
            if (entry == null) {
                modCount++;
                if (++elementCount > threshold) {
                    rehash();
                    index = (hash & 0x7FFFFFFF) % elementData.length;
                }
                entry = createHashedEntry(key, index);
            }


        V result = entry.value;
        entry.value = value;
        return result;
    }

    Entry<V> createEntry(long key, int index, V value) {
        Entry<V> entry = reuseAfterDelete; 
        if(entry == null){ 
        	entry = new Entry<V>(key, value);
        }else{
        	reuseAfterDelete = null;
        	entry.key = key;
        	entry.value = value;
        }
        
        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }

    Entry<V> createHashedEntry(long key, int index) {
        Entry<V> entry = reuseAfterDelete; 
        if(entry == null) {
        	entry = new Entry<V>(key);
        }else{
        	reuseAfterDelete = null;
        	entry.key = key;
        	entry.value = null;
        }

        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }


    void rehash(int capacity) {
        int length = (capacity == 0 ? 1 : capacity << 1);

        Entry<V>[] newData = newElementArray(length);
        for (int i = 0; i < elementData.length; i++) {
            Entry<V> entry = elementData[i];
            while (entry != null) {
                int index = ((int)entry.key & 0x7FFFFFFF) % length;
                Entry<V> next = entry.next;
                entry.next = newData[index];
                newData[index] = entry;
                entry = next;
            }
        }
        elementData = newData;
        computeMaxSize();
    }

    void rehash() {
        rehash(elementData.length);
    }

    /**
     * Removes the mapping with the specified key from this map.
     * 
     * @param key
     *            the key of the mapping to remove.
     * @return the value of the removed mapping or {@code null} if no mapping
     *         for the specified key was found.
     * @since Android 1.0
     */
    
    public V remove(long key) {
        Entry<V> entry = removeEntry(key);
        if(entry == null)
        	return null;
        V ret = entry.value;
        entry.value = null;
        entry.key = Long.MIN_VALUE;
        reuseAfterDelete = entry;
        
        return ret;
    }

    Entry<V> removeEntry(long key) {
        int index = 0;
        Entry<V> entry;
        Entry<V> last = null;

        int hash = (int)(key);
        index = (hash & 0x7FFFFFFF) % elementData.length;
        entry = elementData[index];
         while (entry != null && !(/*((int)entry.key) == hash &&*/ key == entry.key)) {
             last = entry;
              entry = entry.next;
         }
         
         if (entry == null) {
             return null;
         }
         
        if (last == null) {
            elementData[index] = entry.next;
        } else {
            last.next = entry.next;
        }
        modCount++;
        elementCount--;
        return entry;
    }

    /**
     * Returns the number of elements in this map.
     * 
     * @return the number of elements in this map.
     * @since Android 1.0
     */
    
    public int size() {
        return elementCount;
    }

    /**
     * @returns iterator over values in map
     */
    public Iterator<V> valuesIterator() {
        return new HashMapIterator<V, V>(
                new EntryType<V,  V>() {
                    public V get(Entry< V> entry) {
                        return entry.value;
                    }
                }, LongHashMapJDBM.this);

    }


}



