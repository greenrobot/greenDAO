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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The context for entity identities. Provides the scope in which entities will be tracked and managed.
 * 
 * @author Markus
 * @param <K>
 * @param <T>
 */
public class IdentityScope<K, T> {
    private final Map<K, WeakReference<T>> identityScope;

    public IdentityScope() {
        identityScope = new ConcurrentHashMap<K, WeakReference<T>>();
    }

    public T get(K key) {
        WeakReference<T> ref = identityScope.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(K key, T entity) {
        identityScope.put(key, new WeakReference<T>(entity));
    }

    public boolean detach(K key, T entity) {
        if (get(key) == entity && entity != null) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    public void remove(K key) {
        identityScope.remove(key);
    }

    public void clear() {
        identityScope.clear();
    }

}
