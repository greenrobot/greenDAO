package de.greenrobot.performance.firebase;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import de.greenrobot.performance.BasePerfTestCase;
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
public class PerformanceTestFirebase extends BasePerfTestCase {

    // reduced query count as local datastore can not be indexed, resulting in low performance
    private static final int QUERY_COUNT = 100;

    private Firebase rootFirebaseRef;

    private List<SimpleEntityNotNull> reloaded;

    @Override
    protected String getLogTag() {
        return "PerfTestFirebase";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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

    @Override
    protected void doIndexedStringEntityQueries() throws Exception {
        // Firebase does not support defining indexes locally, only in the cloud component
        // We measure the local datastore query time anyhow, but WITHOUT INDEXES.

        // set up node for entities
        Firebase entityRef = rootFirebaseRef.child("indexedStringEntity");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun(entityRef, getBatchSize());
        }
    }

    private void indexedStringEntityQueriesRun(Firebase entityRef, int count) throws InterruptedException {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(count);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(count);
        for (int i = 0; i < count; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = (long) i;
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        log("Built entities.");

        // insert entities
        entityRef.setValue(entities);
        log("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, count - 1);

        startClock();
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
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        entityRef.setValue(null);
        log("Deleted all entities.");
    }

    @Override
    protected void doOneByOneAndBatchCrud() throws Exception {
        // set up node for entities
        Firebase simpleEntityRef = rootFirebaseRef.child("simpleEntities");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            oneByOneCrudRun(simpleEntityRef, getBatchSize() / 10);
            batchCrudRun(simpleEntityRef, getBatchSize());
        }
    }

    private void oneByOneCrudRun(Firebase simpleEntityRef, int count) throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }

        startClock();
        for (int i = 0; i < count; i++) {
            // use the entity id as its key
            SimpleEntityNotNull entity = list.get(i);
            simpleEntityRef.child(String.valueOf(entity.getId())).setValue(entity);
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            // use the entity id as its key
            SimpleEntityNotNull entity = list.get(i);
            simpleEntityRef.child(String.valueOf(entity.getId())).setValue(entity);
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);

        deleteAll(simpleEntityRef);
    }

    private void batchCrudRun(Firebase simpleEntityRef, final int count)
            throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }

        // there is no such thing as batch storing of items in Firebase
        // so store the whole list of entities at once
        // https://www.firebase.com/docs/android/guide/understanding-data.html#section-arrays-in-firebase

        startClock();
        simpleEntityRef.setValue(list);
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
        simpleEntityRef.setValue(list);
        stopClock(LogMessage.BATCH_UPDATE);

        final CountDownLatch loadLock = new CountDownLatch(1);
        startClock();
        reloaded = new ArrayList<>(count);
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
        long childrenCount = reloaded.size();
        stopClock(LogMessage.BATCH_READ);

        startClock();
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
        stopClock(LogMessage.BATCH_ACCESS);

        startClock();
        deleteAll(simpleEntityRef);
        stopClock(LogMessage.BATCH_DELETE);
    }

    private void deleteAll(Firebase simpleEntityRef) throws InterruptedException {
        simpleEntityRef.setValue(null);
    }
}
