package de.greenrobot.performance.parse;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.greenrobot.performance.BasePerfTestCase;
import de.greenrobot.performance.StringGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * https://parse.com/docs/android/guide
 */
public class PerformanceTestParse extends BasePerfTestCase {

    // reduced batch size due to memory leak when pinning (of bolts.Task?)
    private static final int BATCH_SIZE = 1000;
    // reduced query count due to slow performance
    private static final int QUERY_COUNT = 100;

    @Override
    protected String getLogTag() {
        return "PerfTestParse";
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

    @Override
    protected void doIndexedStringEntityQueries() throws Exception {
        // According to the documentation, Parse does NOT support defining indexes manually
        // or for the local datastore.
        // We still are going to determine query performance WITHOUT AN INDEX.

        // set up parse inside of test
        // setting it up in setUp() breaks Parse, as it keeps its init state between tests
        // in hidden ParsePlugins
        ParseObject.registerSubclass(IndexedStringEntity.class);
        setupParse();

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun();
        }
    }

    private void indexedStringEntityQueriesRun() throws ParseException {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.setIndexedString(fixedRandomStrings[i]);
            entities.add(entity);
        }
        log("Built entities.");

        // insert entities
        ParseObject.pinAll(entities);
        log("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        startClock();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];

            ParseQuery<IndexedStringEntity> query = ParseQuery.getQuery(IndexedStringEntity.class);
            query.whereEqualTo(IndexedStringEntity.INDEXED_STRING, fixedRandomStrings[nextIndex]);
            //noinspection unused
            List<IndexedStringEntity> result = query.find();
        }
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        ParseObject.unpinAll();
        log("Deleted all entities.");
    }

    @Override
    protected void doSingleAndBatchCrud() throws Exception {
        // set up parse inside of test
        // setting it up in setUp() breaks Parse, as it keeps its init state between tests
        // in hidden ParsePlugins
        setupParse();

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            singleAndBatchCrudRun(BATCH_SIZE);
        }
    }

    private void singleAndBatchCrudRun(int entityCount) throws ParseException {
        List<ParseObject> list = new ArrayList<>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity(i));
        }

        singleCrudRun(list, entityCount / 10);

        deleteAll();

        startClock();
        ParseObject.pinAll(list);
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
        ParseObject.pinAll(list);
        stopClock(LogMessage.BATCH_UPDATE);

        startClock();
        List<ParseObject> reloaded = ParseQuery.getQuery("SimpleEntity")
                .fromLocalDatastore()
                .find();
        stopClock(LogMessage.BATCH_READ);

        startClock();
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
        stopClock(LogMessage.BATCH_ACCESS);

        startClock();
        deleteAll();
        stopClock(LogMessage.BATCH_DELETE);
    }

    private void singleCrudRun(List<ParseObject> list, int count) throws ParseException {
        startClock();
        for (int i = 0; i < count; i++) {
            list.get(i).pin();
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            list.get(i).pin();
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);
    }

    private void deleteAll() throws ParseException {
        ParseObject.unpinAll();
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
