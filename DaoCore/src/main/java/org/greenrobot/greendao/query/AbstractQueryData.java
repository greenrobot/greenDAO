/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.query;

import org.greenrobot.greendao.AbstractDao;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

abstract class AbstractQueryData<T, Q extends AbstractQuery<T>> {
    final String sql;
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final Map<Long, WeakReference<Q>> queriesForThreads;

    AbstractQueryData(AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        this.dao = dao;
        this.sql = sql;
        this.initialValues = initialValues;
        queriesForThreads = new HashMap<>();
    }

    /**
     * Just an optimized version, which performs faster if the current thread is already the query's owner thread.
     * Note: all parameters are reset to their initial values specified in {@link QueryBuilder}.
     */
    Q forCurrentThread(Q query) {
        if (Thread.currentThread() == query.ownerThread) {
            System.arraycopy(initialValues, 0, query.parameters, 0, initialValues.length);
            return query;
        } else {
            return forCurrentThread();
        }
    }

    /**
     * Note: all parameters are reset to their initial values specified in {@link QueryBuilder}.
     */
    Q forCurrentThread() {
        // Process.myTid() seems to have issues on some devices (see Github #376) and Robolectric (#171):
        // We use currentThread().getId() instead (unfortunately return a long, can not use SparseArray).
        // PS.: thread ID may be reused, which should be fine because old thread will be gone anyway.
        long threadId = Thread.currentThread().getId();
        synchronized (queriesForThreads) {
            WeakReference<Q> queryRef = queriesForThreads.get(threadId);
            Q query = queryRef != null ? queryRef.get() : null;
            if (query == null) {
                gc();
                query = createQuery();
                queriesForThreads.put(threadId, new WeakReference<Q>(query));
            } else {
                System.arraycopy(initialValues, 0, query.parameters, 0, initialValues.length);
            }
            return query;
        }
    }

    abstract protected Q createQuery();

    void gc() {
        synchronized (queriesForThreads) {
            Iterator<Entry<Long, WeakReference<Q>>> iterator = queriesForThreads.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Long, WeakReference<Q>> entry = iterator.next();
                if (entry.getValue().get() == null) {
                    iterator.remove();
                }
            }
        }
    }

}
