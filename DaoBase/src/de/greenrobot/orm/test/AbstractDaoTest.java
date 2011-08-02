package de.greenrobot.orm.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Random;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
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
public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends ApplicationTestCase<Application> {

    protected D dao;
    protected SQLiteDatabase db;
    protected Random random;
    protected final Class<D> daoClass;
    protected UnitTestDaoAccess<T, K> daoAccess;
    protected Column pkColumn;
    private final boolean inMemory;

    public AbstractDaoTest(Class<D> daoClass) {
        this(daoClass, true);
    }

    public AbstractDaoTest(Class<D> daoClass, boolean inMemory) {
        super(Application.class);
        this.inMemory = inMemory;
        random = new Random();
        this.daoClass = daoClass;
    }

    @Override
    protected void setUp() {
        createApplication();
        if (inMemory) {
            db = SQLiteDatabase.create(null);
        } else {
            getApplication().deleteDatabase("test-db");
            db = getApplication().openOrCreateDatabase("test-db", Context.MODE_PRIVATE, null);
        }
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
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }
    }

}