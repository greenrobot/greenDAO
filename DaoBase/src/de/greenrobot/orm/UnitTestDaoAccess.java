package de.greenrobot.orm;

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

}
