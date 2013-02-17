/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.dao.query;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

import android.database.Cursor;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.InternalQueryDaoAccess;

/**
 * A thread-safe, unmodifiable list that reads entities once they are accessed from an underlying database cursor. Make
 * sure to close the list once you are done with it. The lazy list can be cached or not. Cached lazy lists store the
 * entities in memory to avoid loading entities more than once. Some features of the list are limited to cached lists
 * (e.g. features that require the entire list). Cached lists close the cursor automatically once you queried all
 * entities. However, to avoid leaked cursors, you should not rely on this behavior: if an exception occurs before the
 * entire list is read, you should close the lazy list (and thus the underlying cursor) on your own to be on the safe
 * side.
 * 
 * @author Markus
 * 
 * @param <E>
 *            Entity type.
 */
public class LazyList<E> implements List<E>, Closeable {
    protected class LazyIterator implements CloseableListIterator<E> {
        private int index;
        private final boolean closeWhenDone;

        public LazyIterator(int startLocation, boolean closeWhenDone) {
            index = startLocation;
            this.closeWhenDone = closeWhenDone;
        }

        @Override
        public void add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override
        /** FIXME: before hasPrevious(), next() must be called. */
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        /** FIXME: before previous(), next() must be called. */
        public E previous() {
            if (index <= 0) {
                throw new NoSuchElementException();
            }
            index--;
            E entity = get(index);
            // if (index == size && closeWhenDone) {
            // close();
            // }
            return entity;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void set(E object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            if (index >= size) {
                throw new NoSuchElementException();
            }
            E entity = get(index);
            index++;
            if (index == size && closeWhenDone) {
                close();
            }
            return entity;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            LazyList.this.close();
        }

    }

    private final InternalQueryDaoAccess<E> daoAccess;
    private final Cursor cursor;
    private final List<E> entities;
    private final int size;
    private final ReentrantLock lock;
    private volatile int loadedCount;

    LazyList(InternalQueryDaoAccess<E> daoAccess, Cursor cursor, boolean cacheEntities) {
        this.cursor = cursor;
        this.daoAccess = daoAccess;
        size = cursor.getCount();
        if (cacheEntities) {
            entities = new ArrayList<E>(size);
            for (int i = 0; i < size; i++) {
                entities.add(null);
            }
        } else {
            entities = null;
        }
        if (size == 0) {
            cursor.close();
        }

        lock = new ReentrantLock();
    }

    /** Loads the remaining entities (if any) that were not loaded before. Applies to cached lazy lists only. */
    public void loadRemaining() {
        checkCached();
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            get(i);
        }
    }

    protected void checkCached() {
        if (entities == null) {
            throw new DaoException("This operation only works with cached lazy lists");
        }
    }

    /** Like get but does not load the entity if it was not loaded before. */
    public E peak(int location) {
        if (entities != null) {
            return entities.get(location);
        } else {
            return null;
        }
    }

    @Override
    /** Closes the underlying cursor: do not try to get entities not loaded (using get) before. */
    public void close() {
        cursor.close();
    }

    public boolean isClosed() {
        return cursor.isClosed();
    }

    public int getLoadedCount() {
        return loadedCount;
    }

    public boolean isLoadedCompletely() {
        return loadedCount == size;
    }

    @Override
    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int location, E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends E> arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        loadRemaining();
        return entities.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        loadRemaining();
        return entities.containsAll(collection);
    }

    @Override
    public E get(int location) {
        if (entities != null) {
            E entity = entities.get(location);
            if (entity == null) {
                lock.lock();
                try {
                    entity = entities.get(location);
                    if (entity == null) {
                        entity = loadEntity(location);
                        entities.set(location, entity);
                        // Ignore FindBugs: increment of volatile is fine here because we use a lock
                        loadedCount++;
                        if (loadedCount == size) {
                            cursor.close();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
            return entity;
        } else {
            return loadEntity(location);
        }
    }

    protected E loadEntity(int location) {
        cursor.moveToPosition(location);
        E entity = daoAccess.loadCurrent(cursor, 0, true);
        if (entity == null) {
            throw new DaoException("Loading of entity failed (null) at position " + location);
        }
        return entity;
    }

    @Override
    public int indexOf(Object object) {
        loadRemaining();
        return entities.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new LazyIterator(0, false);
    }

    @Override
    public int lastIndexOf(Object object) {
        loadRemaining();
        return entities.lastIndexOf(object);
    }

    @Override
    public CloseableListIterator<E> listIterator() {
        return new LazyIterator(0, false);
    }

    /** Closes this list's cursor once the iterator is fully iterated through. */
    public CloseableListIterator<E> listIteratorAutoClose() {
        return new LazyIterator(0, true);
    }

    @Override
    public ListIterator<E> listIterator(int location) {
        return new LazyIterator(location, false);
    }

    @Override
    public E remove(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int location, E object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<E> subList(int start, int end) {
        checkCached();
        for (int i = start; i < end; i++) {
            entities.get(i);
        }
        return entities.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        loadRemaining();
        return entities.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        loadRemaining();
        return entities.toArray(array);
    }

}
