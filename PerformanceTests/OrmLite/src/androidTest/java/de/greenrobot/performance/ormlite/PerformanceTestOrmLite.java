package de.greenrobot.performance.ormlite;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * http://ormlite.com/sqlite_java_android_orm.shtml
 * https://github.com/j256/ormlite-examples
 */
public class PerformanceTestOrmLite extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestOrmLite";

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 8;

    private boolean inMemory;
    private DbHelper dbHelper;

    public PerformanceTestOrmLite() {
        super(Application.class);
        inMemory = false;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        setUpOrmLite();
    }

    protected void setUpOrmLite() {
        String name;
        if (inMemory) {
            name = null;
        } else {
            name = "test-db";
            getApplication().deleteDatabase(name);
        }
        dbHelper = new DbHelper(getApplication(), name);
    }

    @Override
    protected void tearDown() throws Exception {
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }

        super.tearDown();
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "---------------Start");

        Dao<SimpleEntityNotNull, Long> dao = dbHelper.getDao(SimpleEntityNotNull.class);

        for (int i = 0; i < RUNS; i++) {
            runTests(dao, BATCH_SIZE);
        }
        Log.d(TAG, "---------------End");
    }

    protected void runTests(final Dao<SimpleEntityNotNull, Long> dao, int entityCount)
            throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(dao, list, entityCount / 10);

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
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

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
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = dao.queryForAll();
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Loaded (batch) " + reloaded.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < reloaded.size(); i++) {
            SimpleEntityNotNull entity = reloaded.get(i);
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Accessed properties of " + reloaded.size() + " entities in " + time + " ms");

        deleteAll();

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    protected void runOneByOne(Dao<SimpleEntityNotNull, Long> dao, List<SimpleEntityNotNull> list,
            int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            dao.create(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            dao.update(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    protected void deleteAll() {
        long start = System.currentTimeMillis();
        dbHelper.getWritableDatabase().execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    public void testSemantics() {
        try {
            Dao<MinimalEntity, Long> minimalDao = dbHelper.getDao(MinimalEntity.class);
            MinimalEntity data = new MinimalEntity();
            minimalDao.create(data);
            assertNotNull(data.getId()); // ORMLite does update PK after insert if set to generatedId
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
