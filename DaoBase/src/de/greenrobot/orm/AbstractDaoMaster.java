package de.greenrobot.orm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

public class AbstractDaoMaster {
    private static final Map<Class<?>, Class<? extends AbstractDao<?, ?>>> entityToDaoClass = new HashMap<Class<?>, Class<? extends AbstractDao<?, ?>>>();

    public static void registerDao(Class<?> entityClass, Class<? extends AbstractDao<?, ?>> daoClass) {
        entityToDaoClass.put(entityClass, daoClass);
    }
    
    private final SQLiteDatabase db;

    public AbstractDaoMaster(SQLiteDatabase db) {
        this.db = db;

    }

    public static void createEntityTables(SQLiteDatabase db) {
        Collection<Class<? extends AbstractDao<?, ?>>> daoClasses = entityToDaoClass.values();
        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {
            
        }

    }

}
