package de.greenrobot.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.database.sqlite.SQLiteDatabase;

public class AbstractDaoSession {
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

    public AbstractDaoSession(SQLiteDatabase db) {
        this.db = db;
        this.entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();
    }

    protected <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao) {
        entityToDao.put(entityClass, dao);
    }

    @SuppressWarnings("rawtypes")
    protected IdentityScope createIdentityScope(IdentityScopeType type) {
        if (type == IdentityScopeType.None) {
            return null;
        } else if (type == IdentityScopeType.Session) {
            return new IdentityScope();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public <T> long insert(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        return dao.insert(entity);
    }

    public <T, K> T load(Class<T> entityClass, K key) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
        return dao.load(key);
    }

    public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
        AbstractDao<?, ?> dao = entityToDao.get(entityClass);
        if (dao == null) {
            throw new RuntimeException("No DAO registered for " + entityClass);
        }
        return dao;
    }

    /**
     * Run the given Runnable inside a database transaction. If you except a result, consider callInTx.
     */
    public void runInTx(Runnable runnable) {
        db.beginTransaction();
        try {
            runnable.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Calls the given Callable inside a database transaction and returns the result of the Callable. If you don't
     * except a result, consider runInTx.
     */
    public <V> V callInTx(Callable<V> callable) throws Exception {
        db.beginTransaction();
        try {
            V result = callable.call();
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

}
