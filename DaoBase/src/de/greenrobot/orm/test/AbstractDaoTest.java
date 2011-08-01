package de.greenrobot.orm.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import junit.framework.TestCase;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.Column;
import de.greenrobot.orm.UnitTestDaoAccess;

/**
 * Base class for DAO related testing. Prepares an in-memory DB and DAO.
 * 
 * @author Markus
 * 
 * @param <D>
 *            DAO class
 * @param <T>
 *            Entity type of the DAO
 * @param <K>
 *            Key type of the DAO
 */
public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends TestCase {

    protected D dao;
    protected SQLiteDatabase db;
    protected Random random;
    protected final Class<D> daoClass;
    protected UnitTestDaoAccess<T, K> daoAccess;
    protected Column pkColumn;

    public AbstractDaoTest(Class<D> daoClass) {
        random = new Random();
        this.daoClass = daoClass;
    }

    @Override
    protected void setUp() {
        db = SQLiteDatabase.create(null);
        try {
            Constructor<D> constructor = daoClass.getConstructor(SQLiteDatabase.class);
            dao = constructor.newInstance(db);

            Method createTableMethod = daoClass.getMethod("createTable", SQLiteDatabase.class, boolean.class);
            createTableMethod.invoke(null, db, false);
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO Test", e);
        }
        daoAccess = new UnitTestDaoAccess<T, K>(dao);

    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
    }

}