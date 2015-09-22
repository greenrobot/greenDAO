package de.greenrobot.performance.couchbase;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import de.greenrobot.performance.StringGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://developer.couchbase.com/documentation/mobile/1.1.0/develop/training/build-first-android-app/index.html
 * https://github.com/couchbaselabs/ToDoLite-Android
 */
public class PerformanceTestCouchbase extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestCouchbase";

    private static final int BATCH_SIZE = 10000;
    private static final int QUERY_COUNT = 1000;
    private static final int RUNS = 8;

    private static final String DB_NAME = "couchbase-test";
    private static final String DOC_TYPE = "simpleentity";

    private Database database;

    public PerformanceTestCouchbase() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        setupCouchbase();
    }

    private void setupCouchbase() throws CouchbaseLiteException, IOException {
        Manager manager = new Manager(new AndroidContext(getApplication()),
                Manager.DEFAULT_OPTIONS);
        database = manager.getDatabase(DB_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        database.delete();
        database = null;

        super.tearDown();
    }

    public void testIndexedStringEntityQuery() throws CouchbaseLiteException {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "--------Indexed Queries: Start");

        // set up index on string property
        View indexedStringView = database.getView("indexedStringView");
        indexedStringView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                String indexedString = (String) document.get("indexedString");
                // only need an index of strings mapped to the document, so provide no value
                emitter.emit(indexedString, null);
            }
        }, "1");
        Log.d(TAG, "Set up view.");

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery(indexedStringView);
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    private void doIndexedStringEntityQuery(View indexedStringView) throws CouchbaseLiteException {
        // create entities
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        database.beginTransaction();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Document entity = database.getDocument(String.valueOf(i));
            Map<String, Object> properties = new HashMap<>();
            properties.put("indexedString", fixedRandomStrings[i]);
            entity.putProperties(properties);
        }
        database.endTransaction(true);
        Log.d(TAG, "Built and inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            List<Object> keyToQuery = new ArrayList<>(1);
            keyToQuery.add(fixedRandomStrings[nextIndex]);

            Query query = indexedStringView.createQuery();
            query.setKeys(keyToQuery);
            QueryEnumerator result = query.run();
            while (result.hasNext()) {
                QueryRow row = result.next();
                //noinspection unused
                Document document = row.getDocument();
            }
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                        + " ms.");

        // delete all entities
        deleteAll();
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

    protected void runTests(int entityCount) throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        // precreate property maps for documents
        List<Map<String, Object>> maps = new ArrayList<>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            maps.add(createDocumentMap(i));
        }
        System.gc();

        runOneByOne(maps, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        List<Document> documents = new ArrayList<>(entityCount);
        database.beginTransaction();
        for (int i = 0; i < entityCount; i++) {
            // use our own ids (use .createDocument() for random UUIDs)
            Document document = database.getDocument(String.valueOf(i));
            document.putProperties(maps.get(i));
            documents.add(document);
        }
        database.endTransaction(true);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + BATCH_SIZE + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        database.beginTransaction();
        for (int i = 0; i < entityCount; i++) {
            Document document = documents.get(i);
            Map<String, Object> updatedProperties = new HashMap<>();
            // copy existing properties to get _rev property
            updatedProperties.putAll(document.getProperties());
            updatedProperties.putAll(maps.get(i));
            document.putProperties(updatedProperties);
        }
        database.endTransaction(true);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + BATCH_SIZE + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<Document> reloaded = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            reloaded.add(database.getDocument(String.valueOf(i)));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Loaded (one-by-one) " + reloaded.size() + " entities in " + time + " ms");

        // Couchbase is not actually loading properties when getting a document
        // so load them for each one to measure how long it takes to get to the actual data
        start = System.currentTimeMillis();
        for (int i = 0; i < reloaded.size(); i++) {
            Document document = reloaded.get(i);
            Map<String, Object> properties = document.getProperties();
            properties.get("simpleBoolean");
            properties.get("simpleByte");
            properties.get("simpleShort");
            properties.get("simpleInt");
            properties.get("simpleLong");
            properties.get("simpleFloat");
            properties.get("simpleDouble");
            properties.get("simpleString");
            properties.get("simpleByteArray");
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Accessed properties of " + reloaded.size() + " entities in " + time + " ms");

        deleteAll();

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    private void runOneByOne(List<Map<String, Object>> maps, int count)
            throws CouchbaseLiteException {
        long start;
        long time;
        start = System.currentTimeMillis();
        List<Document> documents = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            // use our own ids (use .createDocument() for random UUIDs)
            Document document = database.getDocument(String.valueOf(i));
            document.putProperties(maps.get(i));
            documents.add(document);
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            Document document = documents.get(i);
            Map<String, Object> updatedProperties = new HashMap<>();
            // copy existing properties to get _rev property
            updatedProperties.putAll(document.getProperties());
            updatedProperties.putAll(maps.get(i));
            document.putProperties(updatedProperties);
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    protected void deleteAll() throws CouchbaseLiteException {
        long start = System.currentTimeMillis();
        // query all documents, mark them as deleted
        Query query = database.createAllDocumentsQuery();
        QueryEnumerator result = query.run();
        database.beginTransaction();
        while (result.hasNext()) {
            QueryRow row = result.next();
            row.getDocument().purge();
        }
        database.endTransaction(true);
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    private Map<String, Object> createDocumentMap(int seed) throws CouchbaseLiteException {
        Map<String, Object> map = new HashMap<>();
        map.put("type", DOC_TYPE);
        map.put("simpleBoolean", true);
        map.put("simpleByte", seed & 0xff);
        map.put("simpleShort", seed & 0xffff);
        map.put("simpleInt", seed);
        map.put("simpleLong", Long.MAX_VALUE - seed);
        map.put("simpleFloat", (float) (Math.PI * seed));
        map.put("simpleDouble", Math.E * seed);
        map.put("simpleString", "greenrobot greenDAO");
        byte[] bytes = { 42, -17, 23, 0, 127, -128 };
        map.put("simpleByteArray", bytes);
        return map;
    }
}
