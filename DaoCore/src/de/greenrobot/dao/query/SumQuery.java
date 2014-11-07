package de.greenrobot.dao.query;

import android.database.Cursor;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

public class SumQuery<T> extends AbstractQuery<T> {

    private final static class QueryData<T2> extends AbstractQueryData<T2, SumQuery<T2>> {

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        @Override
        protected SumQuery<T2> createQuery() {
            return new SumQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> SumQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private SumQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
    }

    public SumQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /** Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics. */
    public long sum() {
        checkThread();
        Cursor cursor = dao.getDatabase().rawQuery(sql, parameters);
        try {
            if (!cursor.moveToNext()) {
                throw new DaoException("No result for sum");
            } else if (!cursor.isLast()) {
                throw new DaoException("Unexpected row sum: " + cursor.getCount());
            } else if (cursor.getColumnCount() != 1) {
                throw new DaoException("Unexpected column sum: " + cursor.getColumnCount());
            }
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

}
