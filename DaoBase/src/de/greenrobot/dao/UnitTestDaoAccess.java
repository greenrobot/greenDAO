package de.greenrobot.dao;

/** Reserved for unit tests that want to access some non-public methods. Don't use for anything else. */
public class UnitTestDaoAccess<T, K> {
    private AbstractDao<T, K> dao;

    public UnitTestDaoAccess(AbstractDao<T, K> dao) {
        this.dao = dao;
    }

    public K getPrimaryKeyValue(T entity) {
        return dao.getPrimaryKeyValue(entity);
    }

    public Column[] getColumnModel() {
        return dao.getColumnModel();
    }

    public boolean isEntityUpdateable() {
        return dao.isEntityUpdateable();
    }

}
