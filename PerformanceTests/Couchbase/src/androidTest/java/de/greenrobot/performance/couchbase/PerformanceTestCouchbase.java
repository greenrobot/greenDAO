package de.greenrobot.performance.couchbase;

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
import de.greenrobot.performance.BasePerfTestCase;
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
public class PerformanceTestCouchbase extends BasePerfTestCase {

    private static final String DB_NAME = "couchbase-test";
    private static final String DOC_TYPE = "simpleentity";

    private Database database;

    @Override
    protected String getLogTag() {
        return "PerfTestCouchbase";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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

    @Override
    protected void doIndexedStringEntityQueries() throws Exception {
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
        log("Set up view.");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun(indexedStringView);
        }
    }

    private void indexedStringEntityQueriesRun(View indexedStringView) throws CouchbaseLiteException {
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
        log("Built and inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        startClock();
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
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        deleteAll();
        log("Deleted all entities.");
    }

    @Override
    protected void doSingleAndBatchCrud() throws Exception {
        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            singleAndBatchCrudRun(BATCH_SIZE);
        }
    }

    private void singleAndBatchCrudRun(int entityCount) throws Exception {
        // precreate property maps for documents
        List<Map<String, Object>> maps = new ArrayList<>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            maps.add(createDocumentMap(i));
        }

        singleCrudRun(maps, entityCount / 10);

        deleteAll();

        startClock();
        List<Document> documents = new ArrayList<>(entityCount);
        database.beginTransaction();
        for (int i = 0; i < entityCount; i++) {
            // use our own ids (use .createDocument() for random UUIDs)
            Document document = database.getDocument(String.valueOf(i));
            document.putProperties(maps.get(i));
            documents.add(document);
        }
        database.endTransaction(true);
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
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
        stopClock(LogMessage.BATCH_UPDATE);

        startClock();
        List<Document> reloaded = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            reloaded.add(database.getDocument(String.valueOf(i)));
        }
        stopClock(LogMessage.BATCH_READ);

        // Couchbase is not actually loading properties when getting a document
        // so load them for each one to measure how long it takes to get to the actual data
        startClock();
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
        stopClock(LogMessage.BATCH_ACCESS);

        startClock();
        deleteAll();
        stopClock(LogMessage.BATCH_DELETE);
    }

    private void singleCrudRun(List<Map<String, Object>> maps, int count)
            throws CouchbaseLiteException {
        startClock();
        List<Document> documents = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            // use our own ids (use .createDocument() for random UUIDs)
            Document document = database.getDocument(String.valueOf(i));
            document.putProperties(maps.get(i));
            documents.add(document);
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            Document document = documents.get(i);
            Map<String, Object> updatedProperties = new HashMap<>();
            // copy existing properties to get _rev property
            updatedProperties.putAll(document.getProperties());
            updatedProperties.putAll(maps.get(i));
            document.putProperties(updatedProperties);
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);
    }

    private void deleteAll() throws CouchbaseLiteException {
        // query all documents, mark them as deleted
        Query query = database.createAllDocumentsQuery();
        QueryEnumerator result = query.run();
        database.beginTransaction();
        while (result.hasNext()) {
            QueryRow row = result.next();
            row.getDocument().purge();
        }
        database.endTransaction(true);
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
