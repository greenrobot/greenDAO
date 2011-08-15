package de.greenrobot.dao;

import java.util.Collection;
import java.util.List;

import android.database.Cursor;

public class Query<T> {
    private AbstractDao<T, ?> dao;
    private final String sql;
    private final String[] parameters;

    public Query(AbstractDao<T, ?> dao, String sql, Collection<Object> valueList) {
        this.dao = dao;
        this.sql = sql;

        parameters = new String[valueList.size()];
        int idx = 0;
        for (Object object : valueList) {
            if (object != null) {
                parameters[idx] = object.toString();
            } else {
                parameters[idx] = null;
            }
            idx++;
        }
    }

    public void compile() {
    }

    public List<T> list() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return dao.loadAllAndCloseCursor(cursor);
    }

    public LazyList<T> listLazy() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return new LazyList<T>(dao, cursor);
    }

    public T unique() {
        Cursor cursor = dao.db.rawQuery(sql, parameters);
        return dao.loadUniqueAndCloseCursor(cursor);
    }

    public T uniqueOrThrow() {
        return null;
    }

}
