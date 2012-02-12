package de.greenrobot.performance.ormlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class PerformanceTestOrmLite extends ApplicationTestCase<Application> {

    private Dao<SimpleEntityNotNull, Long> dao;
    private boolean inMemory;
    private DbHelper dbHelper;
    private AndroidConnectionSource connectionSource;

    public PerformanceTestOrmLite() {
        super(Application.class);
        inMemory = false;
    }

    @Override
    protected void setUp() {
        createApplication();
        prepareDb();
    }

    protected void prepareDb() {
        String name;
        if (inMemory) {
            name = null;
        } else {
            name = "test-db";
            getApplication().deleteDatabase(name);
        }
        dbHelper = new DbHelper(getApplication(), name);
        connectionSource = new AndroidConnectionSource(dbHelper);
        try {
            dao = DaoManager.createDao(connectionSource, SimpleEntityNotNull.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }
    }

    public void testPerformance() throws Exception {
        runTests(100); // Warmup
        deleteAll();
        runTests(1000);
        deleteAll();
        runTests(1000);
        deleteAll();
        runTests(1000);
        deleteAll();
        runTests(1000);
        deleteAll();
        runTests(1000);
        Log.d("DAO", "---------------End");
    }

    protected void deleteAll() {
        dbHelper.getWritableDatabase().execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
    }

    protected void runTests(int entityCount) throws Exception {
        Log.d("DAO", "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<SimpleEntityNotNull>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(list, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        dao.callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for (SimpleEntityNotNull entity : list) {
                    dao.create(entity);
                }
                return null;
            }
        });
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ORMLite: Created (batch) " + list.size() + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        dao.callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for (SimpleEntityNotNull entity : list) {
                    dao.update(entity);
                }
                return null;
            }
        });
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ORMLite: Updated (batch) " + list.size() + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = dao.queryForAll();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ORMLite: Loaded " + reloaded.size() + " entities in " + time + "ms");

        System.gc();
        Log.d("DAO", "---------------End: " + entityCount);
    }

    protected void runOneByOne(List<SimpleEntityNotNull> list, int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            dao.create(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ORMLite: Inserted (one-by-one) " + count + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            dao.update(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ORMLite: Updated (one-by-one) " + count + " entities in " + time + "ms");
    }

    public void testSemantics() {
        try {
            Dao<MinimalEntity, Long> minimalDao = DaoManager.createDao(connectionSource, MinimalEntity.class);
            MinimalEntity data = new MinimalEntity();
            minimalDao.create(data);
            assertNull(data.getId()); // ORMLite does not update PK after insert
            MinimalEntity data2 = minimalDao.queryForAll().get(0);
            MinimalEntity data3 = minimalDao.queryForId(data2.getId());
            assertNotSame(data, data2);
            assertNotSame(data2, data3); // ORMLite does not provide object equality
            assertEquals(data2.getId(), data3.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
