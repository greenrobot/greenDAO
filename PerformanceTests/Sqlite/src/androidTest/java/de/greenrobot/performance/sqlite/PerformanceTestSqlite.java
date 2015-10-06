package de.greenrobot.performance.sqlite;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;
import android.util.Log;
import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://bitbucket.org/qbusict/cupboard/wiki/GettingStarted
 */
public class PerformanceTestSqlite extends ApplicationTestCase<Application> {

    private static final String TAG = "PerfTestSqlite";

    private static final int BATCH_SIZE = 10000;
    private static final int QUERY_COUNT = 1000;
    private static final int RUNS = 8;

    private static final String DATABASE_NAME = "sqlite.db";
    private static final int DATABASE_VERSION = 1;

    public PerformanceTestSqlite() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
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
        Log.d(TAG, "--------Indexed Queries: Start");

        // set up database
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Set up database.");

        for (int i = 0; i < RUNS; i++) {
            Log.d(TAG, "----Run " + (i + 1) + " of " + RUNS);
            doIndexedStringEntityQuery(database);
        }

        Log.d(TAG, "--------Indexed Queries: End");
    }

    public void doIndexedStringEntityQuery(SQLiteDatabase database) {
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
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < BATCH_SIZE; i++) {
                IndexedStringEntity entity = entities.get(i);
                values.put(DbHelper.IndexedEntityColumns._ID, entity._id);
                values.put(DbHelper.IndexedEntityColumns.INDEXED_STRING, entity.indexedString);

                database.insert(DbHelper.Tables.INDEXED_ENTITY, null, values);

                values.clear();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        Log.d(TAG, "Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];

            Cursor query = database.query(DbHelper.Tables.INDEXED_ENTITY,
                    IndexedQuery.PROJECTION, DbHelper.IndexedEntityColumns.INDEXED_STRING + "=?",
                    new String[] { fixedRandomStrings[nextIndex] }, null, null, null, null);
            // do NO null checks and count checks, should throw to indicate something is incorrect
            query.moveToFirst();

            // reconstruct entity
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = query.getLong(0);
            entity.indexedString = query.getString(1);

            query.close();
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG,
                "Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                        + " ms.");

        // delete all entities
        database.delete(DbHelper.Tables.INDEXED_ENTITY, null, null);
        Log.d(TAG, "Deleted all entities.");
    }

    public void testPerformance() throws Exception {
        //noinspection PointlessBooleanExpression
        if (!BuildConfig.RUN_PERFORMANCE_TESTS) {
            Log.d(TAG, "Performance tests are disabled.");
            return;
        }
        Log.d(TAG, "---------------Start");

        // set up database
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (int i = 0; i < RUNS; i++) {
            runTests(database, BATCH_SIZE);
        }

        Log.d(TAG, "---------------End");
    }

    private void runTests(SQLiteDatabase database, int entityCount) throws Exception {
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
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < BATCH_SIZE; i++) {
                SimpleEntityNotNull entity = list.get(i);
                values.put(DbHelper.SimpleEntityColumns._ID, entity.getId());
                buildContentValues(values, entity);
                database.insert(DbHelper.Tables.SIMPLE_ENTITY, null, values);
                values.clear();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Created (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (int i = 0; i < BATCH_SIZE; i++) {
                SimpleEntityNotNull entity = list.get(i);
                buildContentValues(values, entity);
                database.update(DbHelper.Tables.SIMPLE_ENTITY, values,
                        DbHelper.SimpleEntityColumns._ID + "=" + entity.getId(),
                        null);
                values.clear();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (batch) " + list.size() + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        List<SimpleEntityNotNull> reloaded = new ArrayList<>(BATCH_SIZE);
        Cursor query = database.query(DbHelper.Tables.SIMPLE_ENTITY, SimpleQuery.PROJECTION, null,
                null, null, null, null, null);
        while (query.moveToNext()) {
            SimpleEntityNotNull entity = new SimpleEntityNotNull();
            entity.setId(query.getLong(0));
            entity.setSimpleBoolean(query.getInt(1) == 1);
            entity.setSimpleByte((byte) query.getInt(2));
            entity.setSimpleShort(query.getShort(3));
            entity.setSimpleInt(query.getInt(4));
            entity.setSimpleLong(query.getLong(5));
            entity.setSimpleFloat(query.getFloat(6));
            entity.setSimpleDouble(query.getDouble(7));
            entity.setSimpleString(query.getString(8));
            entity.setSimpleByteArray(query.getBlob(9));
            reloaded.add(entity);
        }
        query.close();
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

    private void deleteAll(SQLiteDatabase database) {
        long start = System.currentTimeMillis();
        database.delete(DbHelper.Tables.SIMPLE_ENTITY, null, null);
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "Deleted all entities in " + time + " ms");
    }

    private void runOneByOne(SQLiteDatabase database, List<SimpleEntityNotNull> list,
            int count) throws SQLException {
        long start;
        long time;
        start = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        for (int i = 0; i < count; i++) {
            SimpleEntityNotNull entity = list.get(i);
            values.put(DbHelper.SimpleEntityColumns._ID, entity.getId());
            buildContentValues(values, entity);
            database.insert(DbHelper.Tables.SIMPLE_ENTITY, null, values);
            values.clear();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Inserted (one-by-one) " + count + " entities in " + time + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            SimpleEntityNotNull entity = list.get(i);
            buildContentValues(values, entity);
            database.update(DbHelper.Tables.SIMPLE_ENTITY, values,
                    DbHelper.SimpleEntityColumns._ID + "=" + entity.getId(),
                    null);
            values.clear();
        }
        time = System.currentTimeMillis() - start;
        Log.d(TAG, "Updated (one-by-one) " + count + " entities in " + time + " ms");
    }

    private void buildContentValues(ContentValues values, SimpleEntityNotNull entity) {
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_BOOLEAN, entity.getSimpleBoolean());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_BYTE, entity.getSimpleByte());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_SHORT, entity.getSimpleShort());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_INT, entity.getSimpleInt());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_LONG, entity.getSimpleLong());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_FLOAT, entity.getSimpleFloat());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_DOUBLE, entity.getSimpleDouble());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_STRING, entity.getSimpleString());
        values.put(DbHelper.SimpleEntityColumns.SIMPLE_BYTE_ARRAY, entity.getSimpleByteArray());
    }

    private interface IndexedQuery {
        String[] PROJECTION = new String[] {
                DbHelper.IndexedEntityColumns._ID, // 0
                DbHelper.IndexedEntityColumns.INDEXED_STRING // 1
        };
    }

    private interface SimpleQuery {
        String[] PROJECTION = new String[] {
                DbHelper.SimpleEntityColumns._ID, // 0
                DbHelper.SimpleEntityColumns.SIMPLE_BOOLEAN,
                DbHelper.SimpleEntityColumns.SIMPLE_BYTE, // 2
                DbHelper.SimpleEntityColumns.SIMPLE_SHORT,
                DbHelper.SimpleEntityColumns.SIMPLE_INT, // 4
                DbHelper.SimpleEntityColumns.SIMPLE_LONG,
                DbHelper.SimpleEntityColumns.SIMPLE_FLOAT, // 6
                DbHelper.SimpleEntityColumns.SIMPLE_DOUBLE,
                DbHelper.SimpleEntityColumns.SIMPLE_STRING, // 8
                DbHelper.SimpleEntityColumns.SIMPLE_BYTE_ARRAY
        };
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public interface Tables {
            String SIMPLE_ENTITY = "SIMPLE_ENTITY_NOT_NULL";
            String INDEXED_ENTITY = "INDEXED_STRING_ENTITY";
        }

        public interface SimpleEntityColumns {
            String _ID = "_id";
            String SIMPLE_BOOLEAN = "SIMPLE_BOOLEAN";
            String SIMPLE_BYTE = "SIMPLE_BYTE";
            String SIMPLE_SHORT = "SIMPLE_SHORT";
            String SIMPLE_INT = "SIMPLE_INT";
            String SIMPLE_LONG = "SIMPLE_LONG";
            String SIMPLE_FLOAT = "SIMPLE_FLOAT";
            String SIMPLE_DOUBLE = "SIMPLE_DOUBLE";
            String SIMPLE_STRING = "SIMPLE_STRING";
            String SIMPLE_BYTE_ARRAY = "SIMPLE_BYTE_ARRAY";
        }

        public interface IndexedEntityColumns {
            String _ID = "_id";
            String INDEXED_STRING = "INDEXED_STRING";
        }

        private static final String CREATE_SIMPLE_ENTITY_TABLE =
                "CREATE TABLE " + Tables.SIMPLE_ENTITY
                        + " ("
                        + SimpleEntityColumns._ID + " INTEGER PRIMARY KEY NOT NULL ,"  // 0
                        + SimpleEntityColumns.SIMPLE_BOOLEAN + " INTEGER NOT NULL ,"  // 1
                        + SimpleEntityColumns.SIMPLE_BYTE + " INTEGER NOT NULL ,"  // 2
                        + SimpleEntityColumns.SIMPLE_SHORT + " INTEGER NOT NULL ,"  // 3
                        + SimpleEntityColumns.SIMPLE_INT + " INTEGER NOT NULL ,"  // 4
                        + SimpleEntityColumns.SIMPLE_LONG + " INTEGER NOT NULL ,"  // 5
                        + SimpleEntityColumns.SIMPLE_FLOAT + " REAL NOT NULL ,"  // 6
                        + SimpleEntityColumns.SIMPLE_DOUBLE + " REAL NOT NULL ,"  // 7
                        + SimpleEntityColumns.SIMPLE_STRING + " TEXT NOT NULL ,"  // 8
                        + SimpleEntityColumns.SIMPLE_BYTE_ARRAY + " BLOB NOT NULL" // 9
                        + ")";

        private static final String CREATE_INDEXED_STRING_ENTITY_TABLE =
                "CREATE TABLE " + Tables.INDEXED_ENTITY
                        + " ("
                        + IndexedEntityColumns._ID + " INTEGER PRIMARY KEY NOT NULL ," // 0
                        + IndexedEntityColumns.INDEXED_STRING + " TEXT NOT NULL " // 1
                        + ")";

        private static final String CREATE_INDEX_ON_STRING =
                "CREATE INDEX indexed_string ON " + Tables.INDEXED_ENTITY + "("
                        + IndexedEntityColumns.INDEXED_STRING + ")";

        public DbHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SIMPLE_ENTITY_TABLE);
            db.execSQL(CREATE_INDEXED_STRING_ENTITY_TABLE);
            db.execSQL(CREATE_INDEX_ON_STRING);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SIMPLE_ENTITY);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.INDEXED_ENTITY);
            onCreate(db);
        }
    }
}
