package de.greenrobot.dao.query;

import java.lang.ref.WeakReference;

import android.os.Process;
import android.util.SparseArray;
import de.greenrobot.dao.AbstractDao;

abstract class AbstractQueryData<T> {
    final String sql;
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Query<T>>> queriesForThreads;

    AbstractQueryData(AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        this.dao = dao;
        this.sql = sql;
        this.initialValues = initialValues;
        queriesForThreads = new SparseArray<WeakReference<Query<T>>>();
    }

    /** Just gets the instance, won't reset anything like initial parameters. */
    Query<T> forCurrentThread() {
        int threadId = Process.myTid();
        Query<T> query;
        synchronized (queriesForThreads) {
            WeakReference<Query<T>> queryRef = queriesForThreads.get(threadId);
            query = queryRef != null ? queryRef.get() : null;
            if (query == null) {
                gc();
                query = createQuery();
                queriesForThreads.put(threadId, new WeakReference<Query<T>>(query));
            }
        }

        return query;
    }

    abstract protected Query<T> createQuery();

    void gc() {
        synchronized (queriesForThreads) {
            int size = queriesForThreads.size();
            for (int i = 0; i < size; i++) {
                if (queriesForThreads.valueAt(i).get() == null) {
                    queriesForThreads.remove(queriesForThreads.keyAt(i));
                }
            }
        }
    }

}
