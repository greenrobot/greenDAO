package de.greenrobot.dao;

import java.util.List;

public class Query<T> {
    private AbstractDao<T, ?> dao;
    private final String sql;

    public Query(AbstractDao<T, ?> dao, String sql, List<Object> values) {
        this.dao = dao;
        this.sql = sql;
    }

    public void compile() {

    }

    public List<T> list() {
        return null;
    }

    public List<T> listLazy() {
        return null;
    }

    public T unique() {
        return null;
    }

    public T uniqueOrThrow() {
        return null;
    }

}
