package de.greenrobot.performance.firebase;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import de.greenrobot.performance.StringGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Make sure to run the performance tests while in AIRPLANE MODE, as
 * <code>Firebase.goOffline()</code> does not seem to work as expected.
 *
 * https://www.firebase.com/docs/android/guide/
 */
public class PerformanceTestFirebase extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestFirebase";

    private static final int BATCH_SIZE = 10000;
    // reduced query count as local datastore can not be indexed, resulting in low performance
    private static final int QUERY_COUNT = 100;
    private static final int RUNS = 8;

    private Firebase rootFirebaseRef;

    private List<SimpleEntityNotNull> reloaded;

    public PerformanceTestFirebase() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        setupFirebase();
    }

    private void setupFirebase() {
        // handle multiple tests calling setup
        if (!Firebase.getDefaultConfig().isFrozen()) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }
        Firebase.setAndroidContext(getApplication());
        Firebase.goOffline();

        rootFirebaseRef = new Firebase("https://luminous-inferno-2264.firebaseio.com");
    }

    @Override
    protected void tearDown() throws Exception {
        rootFirebaseRef.getApp().purgeOutstandingWrites();
        rootFirebaseRef.removeValue();

        getApplication().deleteDatabase("luminous-inferno-2264.firebaseio.com_default");

        super.tearDown();
    }

    public void testIndexedStringEntityQuery() throws InterruptedException {
        // Firebase does not support defining indexes locally, only in the cloud component
        // We measure the local datastore query time anyhow, but WITHOUT INDEXES.

        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "--------Indexed Queries: Start");

        // set up node for entities
        Firebase entityRef = rootFirebaseRef.child("indexedStringEntity");

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery(entityRef);
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    public void doIndexedStringEntityQuery(Firebase entityRef) throws InterruptedException {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = (long) i;
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        Log.d(TAG, "Built entities.");

        // insert entities
        entityRef.setValue(entities);
        Log.d(TAG, "Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];

            final CountDownLatch queryLock = new CountDownLatch(1);
            Query query = entityRef.orderByChild("indexedString");
            query.equalTo(fixedRandomStrings[nextIndex]);
            ChildEventListener queryEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //noinspection unused
                    IndexedStringEntity entity = dataSnapshot.getValue(IndexedStringEntity.class);
                    queryLock.countDown();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };
            query.addChildEventListener(queryEventListener);
            // wait until there are query results
            queryLock.await();
            query.removeEventListener(queryEventListener);
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                        + " ms.");

        // delete all entities
        entityRef.setValue(null);
        Log.d(TAG, "Deleted all entities.");
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "---------------Start");

        // set up node for entities
        Firebase simpleEntityRef = rootFirebaseRef.child("simpleEntities");

        for (int i = 0; i < RUNS; i++) {
            runTests(simpleEntityRef, BATCH_SIZE);
        }
        Log.d(TAG, "---------------End");
    }

    protected void runTests(Firebase simpleEntityRef, final int entityCount) throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(simpleEntityRef, list, entityCount / 10);

        System.gc();
        deleteAll(simpleEntityRef);

        // there is no such thing as batch storing of items in Firebase
        // so store the whole list of entities at once
        // https://www.firebase.com/docs/android/guide/understanding-data.html#section-arrays-in-firebase

        start = System.currentTimeMillis();
        simpleEntityRef.setValue(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        simpleEntityRef.setValue(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        final CountDownLatch loadLock = new CountDownLatch(1);
        start = System.currentTimeMillis();
        reloaded = new ArrayList<>(entityCount);
        simpleEntityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot entitySnapshot : dataSnapshot.getChildren()) {
                    SimpleEntityNotNull entity = entitySnapshot.getValue(SimpleEntityNotNull.class);
                    reloaded.add(entity);
                }
                loadLock.countDown();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        loadLock.await(5 * 60, TimeUnit.SECONDS);
        time = System.currentTimeMillis() - start;
        long childrenCount = reloaded.size();
        Log.d(TAG, "Loaded (batch) " + childrenCount + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < childrenCount; i++) {
            SimpleEntityNotNull entity = reloaded.get(i);
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Accessed properties of " + childrenCount + " entities in " + time + " ms");

        deleteAll(simpleEntityRef);

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    protected void runOneByOne(Firebase simpleEntityRef, List<SimpleEntityNotNull> list,
            int count) {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            // use the entity id as its key
            SimpleEntityNotNull entity = list.get(i);
            simpleEntityRef.child(String.valueOf(entity.getId())).setValue(entity);
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            // use the entity id as its key
            SimpleEntityNotNull entity = list.get(i);
            simpleEntityRef.child(String.valueOf(entity.getId())).setValue(entity);
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    protected void deleteAll(Firebase simpleEntityRef) throws InterruptedException {
        long start = System.currentTimeMillis();
        simpleEntityRef.setValue(null);
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }
}
