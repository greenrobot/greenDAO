package de.greenrobot.performance.cupboard;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class PerformanceTestCupboard extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestCupboard";

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 8;

    private static final String DATABASE_NAME = "cupboard.db";
    private static final int DATABASE_VERSION = 1;

    private DatabaseCompartment database;

    public PerformanceTestCupboard() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        prepareDb();
    }

    protected void prepareDb() {
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        database = cupboard().withDatabase(dbHelper.getWritableDatabase());
    }

    @Override
    protected void tearDown() throws Exception {
        getApplication().deleteDatabase(DATABASE_NAME);

        super.tearDown();
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }

        Log.d(TAG, "---------------Start");
        for (int i = 0; i < RUNS; i++) {
            runTests(BATCH_SIZE);
        }
        Log.d(TAG, "---------------End");
    }

    protected void deleteAll() {
        long start = System.currentTimeMillis();
        database.delete(SimpleEntityNotNull.class, "");
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    protected void runTests(int entityCount) throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(list, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        database.put(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        database.put(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = database.query(SimpleEntityNotNull.class).list();
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

    protected void runOneByOne(List<SimpleEntityNotNull> list, int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }
}
