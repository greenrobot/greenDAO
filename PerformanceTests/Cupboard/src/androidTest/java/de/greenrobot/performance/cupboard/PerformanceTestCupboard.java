package de.greenrobot.performance.cupboard;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;
import android.util.Log;

import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Random;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * https://bitbucket.org/qbusict/cupboard/wiki/GettingStarted
 */
public class PerformanceTestCupboard extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestCupboard";

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 8;
    private static final int INDEXED_RUNS = 1000;

    private static final String DATABASE_NAME = "cupboard.db";
    private static final int DATABASE_VERSION = 1;

    private Cupboard cupboard;

    public PerformanceTestCupboard() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
        setupCupboard();
    }

    private void setupCupboard() {
        cupboard = new CupboardBuilder().useAnnotations().build();
    }

    @Override
    protected void tearDown() throws Exception {
        getApplication().deleteDatabase(DATABASE_NAME);

        super.tearDown();
    }

    public void testIndexedStringEntityQuery() {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }

        Log.d(TAG, "---------------Indexed Queries: Start");

        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = (long) i;
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }

        // setup database
        cupboard.register(IndexedStringEntity.class);
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        DatabaseCompartment database = cupboard.withDatabase(dbHelper.getWritableDatabase());

        // insert entities
        database.put(entities);

        // query for entities by indexed string at random
        Random random = new Random();
        random.setSeed(StringGenerator.SEED);

        long start = System.currentTimeMillis();
        for (int i = 0; i < INDEXED_RUNS; i++) {
            int nextIndex = random.nextInt(BATCH_SIZE);
            QueryResultIterable<IndexedStringEntity> query = database.query(
                    IndexedStringEntity.class)
                    .withSelection("indexedString = ?", fixedRandomStrings[nextIndex])
                    .query();
            //noinspection ForLoopReplaceableByForEach
            for (Iterator<IndexedStringEntity> iterator = query.iterator(); iterator.hasNext(); ) {
                // explicitly access each entity so it is reconstructed from queried data
                //noinspection unused
                IndexedStringEntity entity = iterator.next();
            }
            query.close();
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Queried for " + INDEXED_RUNS + " indexed entities in " + time + " ms");

        Log.d(TAG, "---------------Indexed Queries: End");
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }

        // setup database
        cupboard.register(SimpleEntityNotNull.class);
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        DatabaseCompartment database = cupboard.withDatabase(dbHelper.getWritableDatabase());

        Log.d(TAG, "---------------Start");
        for (int i = 0; i < RUNS; i++) {
            runTests(database, BATCH_SIZE);
        }
        Log.d(TAG, "---------------End");
    }

    private void runTests(DatabaseCompartment database, int entityCount) throws Exception {
        Log.d(TAG, "---------------Start: " + entityCount);

        long start, time;

        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }
        System.gc();

        runOneByOne(database, list, entityCount / 10);

        System.gc();
        deleteAll(database);

        start = System.currentTimeMillis();
        database.put(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        database.put(list);
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = database.query(SimpleEntityNotNull.class).list();
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

        deleteAll(database);

        System.gc();
        Log.d(TAG, "---------------End: " + entityCount);
    }

    private void deleteAll(DatabaseCompartment database) {
        long start = System.currentTimeMillis();
        database.delete(SimpleEntityNotNull.class, "");
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    private void runOneByOne(DatabaseCompartment database, List<SimpleEntityNotNull> list,
            int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    private class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            cupboard.withDatabase(db).createTables();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            cupboard.withDatabase(db).upgradeTables();
        }
    }
}
