package de.greenrobot.performance.parse;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.greenrobot.performance.StringGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * https://parse.com/docs/android/guide
 */
public class PerformanceTestParse extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestParse";

    // reduced batch size due to memory leak when pinning (of bolts.Task?)
    private static final int BATCH_SIZE = 1000;
    // reduced query count due to slow performance
    private static final int QUERY_COUNT = 100;
    private static final int RUNS = 8;

    public PerformanceTestParse() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase("ParseOfflineStore");

        super.tearDown();
    }

    private void setupParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(getContext());

        // Add your initialization code here
        Parse.initialize(getContext(), "X9MEmCvnlX9oGRLmVhunkatw33jlF7wMPZZFw8lZ",
                "FKI8s0UnK6nT6PdGVO2XgKlcsPZnGJlI8qoPpUKa");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public void testIndexedStringEntityQuery() throws ParseException {
        // According to the documentation, Parse does NOT support defining indexes manually
        // or for the local datastore.
        // We still are going to determine query performance WITHOUT AN INDEX.

        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "--------Indexed Queries: Start");

        // set up parse inside of test
        // setting it up in setUp() breaks Parse, as it keeps its init state between tests
        // in hidden ParsePlugins
        ParseObject.registerSubclass(IndexedStringEntity.class);
        setupParse();

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery();
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    private void doIndexedStringEntityQuery() throws ParseException {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.setIndexedString(fixedRandomStrings[i]);
            entities.add(entity);
        }
        Log.d(TAG, "Built entities.");

        // insert entities
        ParseObject.pinAll(entities);
        Log.d(TAG, "Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];

            ParseQuery<IndexedStringEntity> query = ParseQuery.getQuery(IndexedStringEntity.class);
            query.whereEqualTo(IndexedStringEntity.INDEXED_STRING, fixedRandomStrings[nextIndex]);
            //noinspection unused
            List<IndexedStringEntity> result = query.find();
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " UNINDEXED entities in "
                        + time + " ms.");

        // delete all entities
        ParseObject.unpinAll();
        Log.d(TAG, "Deleted all entities.");
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "---------------Start");

        // set up parse inside of test
        // setting it up in setUp() breaks Parse, as it keeps its init state between tests
        // in hidden ParsePlugins
        setupParse();

        for (int i = 0; i < RUNS; i++) {
            runTests(BATCH_SIZE);
        }

        Log.d(TAG, "---------------End");
    }

    private void runTests(int entityCount) throws ParseException {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        List<ParseObject> list = new ArrayList<>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity(i));
        }
        System.gc();

        runOneByOne(list, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        ParseObject.pinAll(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        ParseObject.pinAll(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<ParseObject> reloaded = ParseQuery.getQuery("SimpleEntity")
                .fromLocalDatastore()
                .find();
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Loaded (batch) " + reloaded.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < reloaded.size(); i++) {
            ParseObject entity = reloaded.get(i);
            entity.getBoolean("simpleBoolean");
            entity.getInt("simpleByte");
            entity.getInt("simpleShort");
            entity.getInt("simpleInt");
            entity.getLong("simpleLong");
            entity.getDouble("simpleFloat");
            entity.getDouble("simpleDouble");
            entity.getString("simpleString");
            entity.getBytes("simpleByteArray");
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Accessed properties of " + reloaded.size() + " entities in " + time + " ms");

        deleteAll();

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    private void runOneByOne(List<ParseObject> list, int count) throws ParseException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).pin();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            list.get(i).pin();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    private void deleteAll() throws ParseException {
        long start = System.currentTimeMillis();
        ParseObject.unpinAll();
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    private ParseObject createEntity(int nr) {
        ParseObject entity = new ParseObject("SimpleEntity");
        entity.put("simpleBoolean", true);
        entity.put("simpleByte", nr & 0xff);
        entity.put("simpleShort", nr & 0xffff);
        entity.put("simpleInt", nr);
        entity.put("simpleLong", Long.MAX_VALUE - nr);
        entity.put("simpleFloat", (float) (Math.PI * nr));
        entity.put("simpleDouble", Math.E * nr);
        entity.put("simpleString", "greenrobot greenDAO");
        byte[] bytes = { 42, -17, 23, 0, 127, -128 };
        entity.put("simpleByteArray", bytes);
        return entity;
    }
}
