package de.greenrobot.performance.realm;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import de.greenrobot.performance.StringGenerator;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://realm.io/docs/java/latest/ https://github.com/realm/realm-java/
 */
public class PerformanceTestRealm extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestRealm";

    private static final int BATCH_SIZE = 10000;
    private static final int QUERY_COUNT = 1000;
    private static final int RUNS = 8;

    private Realm realm;
    private boolean inMemory;

    public PerformanceTestRealm() {
        super(Application.class);
        inMemory = false;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        createRealm();
    }

    protected void createRealm() {
        RealmConfiguration.Builder configBuilder = new RealmConfiguration.Builder(getContext());
        if (inMemory) {
            configBuilder.name("inmemory.realm").inMemory();
        } else {
            configBuilder.name("ondisk.realm");
        }
        realm = Realm.getInstance(configBuilder.build());
    }

    @Override
    protected void tearDown() throws Exception {
        if (realm != null) {
            String path = realm.getPath();

            realm.close();

            if (!inMemory) {
                //noinspection ResultOfMethodCallIgnored
                new File(path).delete();
            }
        }

        super.tearDown();
    }

    public void testIndexedStringEntityQuery() {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "--------Indexed Queries: Start");

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery();
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    public void doIndexedStringEntityQuery() {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.setId((long) i);
            entity.setIndexedString(fixedRandomStrings[i]);
            entities.add(entity);
        }
        Log.d(TAG, "Built entities.");

        // insert entities
        realm.beginTransaction();
        realm.copyToRealm(entities);
        realm.commitTransaction();
        Log.d(TAG, "Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            RealmQuery<IndexedStringEntity> query = realm.where(IndexedStringEntity.class);
            query.equalTo("indexedString", fixedRandomStrings[nextIndex]);
            RealmResults<IndexedStringEntity> result = query.findAll();
            for (int j = 0, resultSize = result.size(); j < resultSize; j++) {
                // actually get each entity so its object is reconstructed, same with properties
                IndexedStringEntity entity = result.get(j);
                entity.getId();
                entity.getIndexedString();
            }
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                        + " ms.");

        // delete all entities
        realm.beginTransaction();
        realm.allObjects(IndexedStringEntity.class).clear();
        realm.commitTransaction();
        Log.d(TAG, "Deleted all entities.");
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

        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(list, entityCount / 10);

        System.gc();
        deleteAll();

        start = System.currentTimeMillis();
        realm.beginTransaction();
        realm.copyToRealm(list);
        realm.commitTransaction();
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.commitTransaction();
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        RealmResults<SimpleEntityNotNull> reloaded = realm.allObjects(SimpleEntityNotNull.class);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Loaded (batch) " + reloaded.size() + " entities in " + time + " ms");

        // as Realm is not actually loading data, just referencing it,
        // at least make sure we access every property to force it being loaded
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
            realm.beginTransaction();
            realm.copyToRealm(list.get(i));
            realm.commitTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(list.get(i));
            realm.commitTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    protected void deleteAll() {
        long start = System.currentTimeMillis();
        realm.beginTransaction();
        realm.allObjects(SimpleEntityNotNull.class).clear();
        realm.commitTransaction();
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }
}
