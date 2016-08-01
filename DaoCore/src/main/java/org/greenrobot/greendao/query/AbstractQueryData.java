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

import java.lang.ref.WeakReference;

import android.os.Process;
import android.util.SparseArray;
import org.greenrobot.greendao.AbstractDao;

abstract class AbstractQueryData<T, Q extends AbstractQuery<T>> {
    final String sql;
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Q>> queriesForThreads;

    AbstractQueryData(AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        this.dao = dao;
        this.sql = sql;
        this.initialValues = initialValues;
        queriesForThreads = new SparseArray<WeakReference<Q>>();
    }

    /** Just an optimized version, which performs faster if the current thread is already the query's owner thread. */
    Q forCurrentThread(Q query) {
        if (Thread.currentThread() == query.ownerThread) {
            System.arraycopy(initialValues, 0, query.parameters, 0, initialValues.length);
            return query;
        } else {
            return forCurrentThread();
        }
    }

    Q forCurrentThread() {
        int threadId = Process.myTid();
        if (threadId == 0) {
            // Workaround for Robolectric, always returns 0
            long id = Thread.currentThread().getId();
            if (id < 0 || id > Integer.MAX_VALUE) {
                throw new RuntimeException("Cannot handle thread ID: " + id);
            }
            threadId = (int) id;
        }
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
            for (int i = queriesForThreads.size() - 1; i >= 0; i--) {
                if (queriesForThreads.valueAt(i).get() == null) {
                    queriesForThreads.remove(queriesForThreads.keyAt(i));
                }
            }
        }
    }

}
