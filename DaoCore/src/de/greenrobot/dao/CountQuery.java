package de.greenrobot.dao;

import java.util.Collection;

import android.database.Cursor;

public class CountQuery<T> extends AbstractQuery<T> {

    protected CountQuery(AbstractDao<T, ?> dao, String sql, Collection<Object> valueList) {
        super(dao, sql, valueList);
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
