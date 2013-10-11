package de.greenrobot.dao.query;

import android.database.Cursor;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

public class CountQuery<T> extends AbstractQuery<T> {

    private final static class QueryData<T2> extends AbstractQueryData<T2, CountQuery<T2>> {

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        @Override
        protected CountQuery<T2> createQuery() {
            return new CountQuery<T2>(this, dao, sql, initialValues.clone());
        }
    }

    static <T2> CountQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        QueryData<T2> queryData = new QueryData<T2>(dao, sql, toStringArray(initialValues));
        return queryData.forCurrentThread();
    }

    private final QueryData<T> queryData;

    private CountQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
    }

    public CountQuery<T> forCurrentThread() {
        return queryData.forCurrentThread(this);
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
