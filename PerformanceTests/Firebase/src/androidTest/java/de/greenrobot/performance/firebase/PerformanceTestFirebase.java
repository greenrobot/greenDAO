package de.greenrobot.performance.firebase;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * https://www.firebase.com/docs/android/guide/
 */
public class PerformanceTestFirebase extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestFirebase";

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 8;

    private Firebase rootFirebaseRef;
    private Firebase simpleEntityRef;

    private CountDownLatch loadLock;
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
        simpleEntityRef = rootFirebaseRef.child("simpleEntities");
    }

    @Override
    protected void tearDown() throws Exception {
        rootFirebaseRef.getApp().purgeOutstandingWrites();
        rootFirebaseRef.removeValue();

        getApplication().deleteDatabase("luminous-inferno-2264.firebaseio.com_default");

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
        simpleEntityRef.removeValue();
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    protected void runTests(final int entityCount) throws Exception {
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

        loadLock = new CountDownLatch(1);
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

        deleteAll();

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    protected void runOneByOne(List<SimpleEntityNotNull> list, int count) {
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
}
