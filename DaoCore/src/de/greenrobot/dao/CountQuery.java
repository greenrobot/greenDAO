package de.greenrobot.dao;

import android.database.Cursor;

public class CountQuery<T> extends AbstractQuery<T> {
    private final static class ThreadLocalQuery<T2> extends ThreadLocal<CountQuery<T2>> {
        private final String sql;
        private final AbstractDao<T2, ?> dao;
        private final Object[] initialValues;

        private ThreadLocalQuery(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
            this.dao = dao;
            this.sql = sql;
            this.initialValues = initialValues;
        }

        @Override
        protected CountQuery<T2> initialValue() {
            return new CountQuery<T2>(this, dao, sql, initialValues);
        }
    }

    static <T2> CountQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        ThreadLocalQuery<T2> threadLocal = new ThreadLocalQuery<T2>(dao, sql, initialValues);
        return threadLocal.get();
    }

    private CountQuery(ThreadLocalQuery<T> threadLocalQuery, AbstractDao<T, ?> dao, String sql, Object[] initialValues) {
        super(dao, sql, initialValues);
    }

    /** Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics. */
    public long count() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
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
