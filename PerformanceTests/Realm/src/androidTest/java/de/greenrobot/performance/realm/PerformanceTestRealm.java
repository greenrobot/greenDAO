package de.greenrobot.performance.realm;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTestRealm extends ApplicationTestCase<Application> {

    private static final int BATCH_SIZE = 10000;
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

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d("DAO", "Realm performance tests are disabled.");
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
        realm.beginTransaction();
        realm.allObjects(SimpleEntityNotNull.class).clear();
        realm.commitTransaction();
        long time = System.currentTimeMillis() - start;
        Log.d("DAO", "Realm: Deleted all entities in " + time + " ms");
    }

    protected void runTests(int entityCount) throws Exception {
        Log.d("DAO", "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<SimpleEntityNotNull>();
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
        Log.d("DAO", "Realm: Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.commitTransaction();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Realm: Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        RealmResults<SimpleEntityNotNull> reloaded = realm.allObjects(SimpleEntityNotNull.class);
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Realm: Loaded (batch) " + reloaded.size() + " entities in " + time + " ms");

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
        Log.d("DAO",
                "Realm: Accessed properties of " + reloaded.size() + " entities in " + time + " ms");

        System.gc();
        Log.d("DAO", "---------------End: " + entityCount);
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
        Log.d("DAO", "Realm: Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(list.get(i));
            realm.commitTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Realm: Updated (one-by-one) " + count + " entities in " + time + " ms");
    }
}
