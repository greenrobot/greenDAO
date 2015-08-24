package de.greenrobot.performance.activeandroid;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTestActiveAndroid extends ApplicationTestCase<Application> {

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 8;
    private static final String DATABASE_NAME = "active-android.db";

    public PerformanceTestActiveAndroid() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        setupDatabase();
    }

    protected void setupDatabase() {
        Configuration dbConfiguration = new Configuration.Builder(getContext())
                .setDatabaseName(DATABASE_NAME)
                .addModelClass(SimpleEntityNotNull.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);
    }

    @Override
    protected void tearDown() throws Exception {
        ActiveAndroid.dispose();
        getApplication().deleteDatabase(DATABASE_NAME);
        super.tearDown();
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d("DAO", "ActiveAndroid performance tests are disabled.");
            return;
        }

        runTests(100); // Warmup

        for (int i = 0; i < RUNS; i++) {
            deleteAll();
            runTests(BATCH_SIZE);
        }
        deleteAll();
        Log.d("DAO", "---------------End");
    }

    protected void deleteAll() {
        long start = System.currentTimeMillis();
        ActiveAndroid.execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
        long time = System.currentTimeMillis() - start;
        Log.d("DAO", "ActiveAndroid: Deleted all entities in " + time + "ms");
    }

    protected void runTests(int entityCount) throws Exception {
        Log.d("DAO", "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<SimpleEntityNotNull>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity());
        }
        System.gc();

        runOneByOne(list, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < entityCount; i++) {
                list.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO",
                "ActiveAndroid: Created (batch) " + list.size() + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < entityCount; i++) {
                list.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO",
                "ActiveAndroid: Updated (batch) " + list.size() + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = new Select()
                .all()
                .from(SimpleEntityNotNull.class)
                .execute();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "ActiveAndroid: Loaded " + reloaded.size() + " entities in " + time + "ms");

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
        Log.d("DAO",
                "ActiveAndroid: Accessed properties of " + reloaded.size() + " entities in " + time
                        + "ms");

        System.gc();
        Log.d("DAO", "---------------End: " + entityCount);
    }

    protected void runOneByOne(List<SimpleEntityNotNull> list, int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO",
                "ActiveAndroid: Inserted (one-by-one) " + count + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO",
                "ActiveAndroid: Updated (one-by-one) " + count + " entities in " + time + "ms");
    }
}
