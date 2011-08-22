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
package de.greenrobot.dao;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The context for entity identities. Provides the scope in which entities will be tracked and managed.
 * 
 * @author Markus
 * @param <K>
 * @param <T>
 */
public class IdentityScopeObject<K, T> implements IdentityScope<K, T> {
    private final Map<K, WeakReference<T>> identityScope;
    private final ReentrantLock lock;

    public IdentityScopeObject() {
        // identityScope = new ConcurrentHashMap<K, WeakReference<T>>();
        identityScope = new HashMap<K, WeakReference<T>>();
        lock = new ReentrantLock();
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#get(K)
     */
    @Override
    public T get(K key) {
        lock.lock();
        WeakReference<T> ref = identityScope.get(key);
        lock.unlock();
        if (ref != null) {
            return ref.get();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#put(K, T)
     */
    @Override
    public void put(K key, T entity) {
        lock.lock();
        identityScope.put(key, new WeakReference<T>(entity));
        lock.unlock();
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#detach(K, T)
     */
    @Override
    public boolean detach(K key, T entity) {
        lock.lock();
        if (get(key) == entity && entity != null) {
            remove(key);
            lock.unlock();
            return true;
        } else {
            lock.unlock();
            return false;
        }
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#remove(K)
     */
    @Override
    public void remove(K key) {
        lock.lock();
        identityScope.remove(key);
        lock.unlock();
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#clear()
     */
    @Override
    public void clear() {
        lock.lock();
        identityScope.clear();
        lock.unlock();
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#lock()
     */
    @Override
    public void lock() {
        lock.lock();
    }

    /* (non-Javadoc)
     * @see de.greenrobot.dao.IIdentityScope#unlock()
     */
    @Override
    public void unlock() {
        lock.unlock();
    }

}
