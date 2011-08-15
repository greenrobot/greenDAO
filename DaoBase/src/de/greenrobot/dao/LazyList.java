package de.greenrobot.dao;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

import android.database.Cursor;

/**
 * A thread-safe, unmodifiable list
 * 
 * @author Markus
 * 
 * @param <E>
 *            Entity type.
 */
public class LazyList<E> implements List<E>, Closeable {
    protected class LazyIterator implements ListIterator<E> {
        private int index;

        public LazyIterator(int startLocation) {
            index = startLocation;
        }

        @Override
        public void add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public E previous() {
            if (index <= 0) {
                throw new NoSuchElementException();
            }
            return get(index - 1);
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
            return entity;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private final AbstractDao<E, ?> dao;
    private final Cursor cursor;
    private final List<E> entities;
    private final int size;
    private final ReentrantLock lock;
    private volatile int loadedCount;

    LazyList(AbstractDao<E, ?> dao, Cursor cursor) {
        this.dao = dao;
        this.cursor = cursor;
        size = cursor.getCount();
        entities = new ArrayList<E>(size);
        for (int i = 0; i < size; i++) {
            entities.add(null);
        }
        if (size == 0) {
            cursor.close();
        }

        lock = new ReentrantLock();
    }

    /** Loads the remaining entities (if any). */
    public void loadRemaining() {
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            get(i);
        }
    }

    /** Like get but does not load the entity if it was not loaded before. */
    public E peak(int location) {
        return entities.get(location);
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
        E entity = entities.get(location);
        if (entity == null) {
            lock.lock();
            try {
                entity = entities.get(location);
                if (entity == null) {
                    cursor.moveToPosition(location);
                    entity = dao.loadCurrent(cursor, 0);
                    if (entity == null) {
                        throw new DaoException("Loading of entity failed (null) at position " + location);
                    }
                    entities.set(location, entity);
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
        return new LazyIterator(0);
    }

    @Override
    public int lastIndexOf(Object object) {
        loadRemaining();
        return entities.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new LazyIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int location) {
        return new LazyIterator(location);
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
