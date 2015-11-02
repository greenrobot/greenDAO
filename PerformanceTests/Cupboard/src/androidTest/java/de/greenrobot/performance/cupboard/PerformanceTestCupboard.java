package de.greenrobot.performance.cupboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.greenrobot.performance.BasePerfTestCase;
import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.DatabaseCompartment;

/**
 * https://bitbucket.org/qbusict/cupboard/wiki/GettingStarted
 */
public class PerformanceTestCupboard extends BasePerfTestCase {

    private static final String DATABASE_NAME = "cupboard.db";
    private static final int DATABASE_VERSION = 1;

    private Cupboard cupboard;

    @Override
    protected String getLogTag() {
        return "PerfTestCupboard";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setUpCupboard();
    }

    private void setUpCupboard() {
        cupboard = new CupboardBuilder().useAnnotations().build();
    }

    @Override
    protected void tearDown() throws Exception {
        getApplication().deleteDatabase(DATABASE_NAME);

        super.tearDown();
    }

    @Override
    protected void doIndexedStringEntityQueries() {
        // set up database
        cupboard.register(IndexedStringEntity.class);
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        DatabaseCompartment database = cupboard.withDatabase(dbHelper.getWritableDatabase());
        log("Set up database.");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun(database);
        }
    }

    private void indexedStringEntityQueriesRun(DatabaseCompartment database) {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = (long) i;
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        log("Built entities.");

        // insert entities
        database.put(entities);
        log("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        startClock();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            //noinspection unused
            List<IndexedStringEntity> query = database.query(
                    IndexedStringEntity.class)
                    .withSelection("indexedString = ?", fixedRandomStrings[nextIndex])
                    .list();
        }
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        database.delete(IndexedStringEntity.class, "");
        log("Deleted all entities.");
    }

    @Override
    protected void doSingleAndBatchCrud() throws Exception {
        // set up database
        cupboard.register(SimpleEntityNotNull.class);
        DbHelper dbHelper = new DbHelper(getApplication(), DATABASE_NAME, DATABASE_VERSION);
        DatabaseCompartment database = cupboard.withDatabase(dbHelper.getWritableDatabase());

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            singleAndBatchCrudRun(database, BATCH_SIZE);
        }
    }

    private void singleAndBatchCrudRun(DatabaseCompartment database, int entityCount)
            throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }

        singleCrudRun(database, list, entityCount / 10);

        deleteAll(database);

        startClock();
        database.put(list);
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
        database.put(list);
        stopClock(LogMessage.BATCH_UPDATE);

        startClock();
        List<SimpleEntityNotNull> reloaded = database.query(SimpleEntityNotNull.class).list();
        stopClock(LogMessage.BATCH_READ);

        startClock();
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
        stopClock(LogMessage.BATCH_ACCESS);

        startClock();
        deleteAll(database);
        stopClock(LogMessage.BATCH_DELETE);
    }

    private void singleCrudRun(DatabaseCompartment database, List<SimpleEntityNotNull> list,
            int count) throws SQLException {
        startClock();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            database.put(list.get(i));
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);
    }

    private void deleteAll(DatabaseCompartment database) {
        database.delete(SimpleEntityNotNull.class, "");
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
