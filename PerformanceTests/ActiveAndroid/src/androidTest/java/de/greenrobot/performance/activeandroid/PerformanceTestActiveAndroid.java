package de.greenrobot.performance.activeandroid;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/pardom/ActiveAndroid/wiki/Getting-started
 */
public class PerformanceTestActiveAndroid extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestActiveAndroid";

    private static final int BATCH_SIZE = 10000;
    private static final int QUERY_COUNT = 1000;
    private static final int RUNS = 8;

    private static final String DATABASE_NAME = "active-android.db";

    public PerformanceTestActiveAndroid() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }

    @Override
    protected void tearDown() throws Exception {
        if (Cache.isInitialized()) {
            ActiveAndroid.dispose();
        }
        getApplication().deleteDatabase(DATABASE_NAME);

        super.tearDown();
    }

    public void testIndexedStringEntityQuery() {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "--------Indexed Queries: Start");

        // set up database
        Configuration dbConfiguration = new Configuration.Builder(getContext())
                .setDatabaseName(DATABASE_NAME)
                .addModelClass(IndexedStringEntity.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);
        Log.d(TAG, "Set up database.");

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery();
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    private void doIndexedStringEntityQuery() {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        Log.d(TAG, "Built entities.");

        // insert entities
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < BATCH_SIZE; i++) {
                entities.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        Log.d(TAG, "Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            //noinspection unused
            List<IndexedStringEntity> query = new Select()
                    .from(IndexedStringEntity.class)
                    .where("INDEXED_STRING = ?", fixedRandomStrings[nextIndex])
                    .execute();
            // ActiveAndroid already builds all entities when executing the query, so move on
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                        + " ms.");

        // delete all entities
        ActiveAndroid.execSQL("DELETE FROM INDEXED_STRING_ENTITY");
        Log.d(TAG, "Deleted all entities.");
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "---------------Start");

        // set up database
        Configuration dbConfiguration = new Configuration.Builder(getContext())
                .setDatabaseName(DATABASE_NAME)
                .addModelClass(SimpleEntityNotNull.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);

        for (int i = 0; i < RUNS; i++) {
            runTests(BATCH_SIZE);
        }

        Log.d(TAG, "---------------End");
    }

    protected void runTests(int entityCount) throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<>();
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
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

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
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = new Select()
                .all()
                .from(SimpleEntityNotNull.class)
                .execute();
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

    protected void deleteAll() {
        long start = System.currentTimeMillis();
        ActiveAndroid.execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    protected void runOneByOne(List<SimpleEntityNotNull> list, int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }
}
