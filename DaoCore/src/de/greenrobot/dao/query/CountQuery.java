package de.greenrobot.dao.query;

import android.database.Cursor;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

public class CountQuery<T> extends AbstractQuery<T> {

    private final static class ThreadLocalQuery<T2> extends ThreadLocal<CountQuery<T2>> {
        private final String sql;
        private final AbstractDao<T2, ?> dao;
        private final String[] initialValues;

        private ThreadLocalQuery(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            this.dao = dao;
            this.sql = sql;
            this.initialValues = initialValues;
        }

        @Override
        protected CountQuery<T2> initialValue() {
            return new CountQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> CountQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        ThreadLocalQuery<T2> threadLocal = new ThreadLocalQuery<T2>(dao, sql, toStringArray(initialValues));
        return threadLocal.get();
    }

    private final ThreadLocalQuery<T> threadLocalQuery;

    private CountQuery(ThreadLocalQuery<T> threadLocalQuery, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.threadLocalQuery = threadLocalQuery;
    }

    public CountQuery<T> forCurrentThread() {
        CountQuery<T> query = threadLocalQuery.get();
        String[] initialValues = threadLocalQuery.initialValues;
        System.arraycopy(initialValues, 0, query.parameters, 0, initialValues.length);
        return query;
    }

    /** Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics. */
    public long count() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        try {
            if (!cursor.moveToNext()) {
                throw new DaoException("No result for count");
            } else if (!cursor.isLast()) {
                throw new DaoException("Unexpected row count: " + cursor.getCount());
            } else if (cursor.getColumnCount() != 1) {
                throw new DaoException("Unexpected column count: " + cursor.getColumnCount());
            }
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

}
