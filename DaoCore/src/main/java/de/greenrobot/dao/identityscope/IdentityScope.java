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
package de.greenrobot.dao.identityscope;

/**
 * Common interface for a identity scopes needed internally by greenDAO. Identity scopes let greenDAO re-use Java
 * objects.
 * 
 * @author Markus
 * 
 * @param <K>
 *            Key
 * @param <T>
 *            Entity
 */
public interface IdentityScope<K, T> {

    T get(K key);

    void put(K key, T entity);

    T getNoLock(K key);

    void putNoLock(K key, T entity);

    boolean detach(K key, T entity);

    void remove(K key);

    void remove(Iterable<K> key);

    void clear();

    void lock();

    void unlock();

    void reserveRoom(int count);

}