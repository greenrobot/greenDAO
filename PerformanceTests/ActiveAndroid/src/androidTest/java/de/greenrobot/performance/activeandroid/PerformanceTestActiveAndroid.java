package de.greenrobot.performance.activeandroid;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Configuration;
import com.activeandroid.query.Select;
import de.greenrobot.performance.BasePerfTestCase;
import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/pardom/ActiveAndroid/wiki/Getting-started
 */
public class PerformanceTestActiveAndroid extends BasePerfTestCase {

    private static final String DATABASE_NAME = "active-android.db";

    @Override
    protected String getLogTag() {
        return "PerfTestActiveAndroid";
    }

    @Override
    protected void tearDown() throws Exception {
        if (Cache.isInitialized()) {
            ActiveAndroid.dispose();
        }
        getApplication().deleteDatabase(DATABASE_NAME);

        super.tearDown();
    }

    @Override
    protected void doIndexedStringEntityQueries() throws Exception {
        // set up database
        Configuration dbConfiguration = new Configuration.Builder(getContext())
                .setDatabaseName(DATABASE_NAME)
                .addModelClass(IndexedStringEntity.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);
        log("Set up database.");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun();
        }
    }

    private void indexedStringEntityQueriesRun() {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        log("Built entities.");

        // insert entities
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < BATCH_SIZE; i++) {
                entities.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        log("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        startClock();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            //noinspection unused
            List<IndexedStringEntity> query = new Select()
                    .from(IndexedStringEntity.class)
                    .where("INDEXED_STRING = ?", fixedRandomStrings[nextIndex])
                    .execute();
            // ActiveAndroid already builds all entities when executing the query, so move on
        }
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        ActiveAndroid.execSQL("DELETE FROM INDEXED_STRING_ENTITY");
        log("Deleted all entities.");
    }

    @Override
    protected void doSingleAndBatchCrud() throws Exception {
        // set up database
        Configuration dbConfiguration = new Configuration.Builder(getContext())
                .setDatabaseName(DATABASE_NAME)
                .addModelClass(SimpleEntityNotNull.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            singleAndBatchCrudRun(BATCH_SIZE);
        }
    }

    private void singleAndBatchCrudRun(int entityCount) throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity());
        }

        singleCrudRun(list, entityCount / 10);

        deleteAll();

        startClock();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < entityCount; i++) {
                list.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < entityCount; i++) {
                list.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        stopClock(LogMessage.BATCH_UPDATE);

        startClock();
        List<SimpleEntityNotNull> reloaded = new Select()
                .all()
                .from(SimpleEntityNotNull.class)
                .execute();
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
        deleteAll();
        stopClock(LogMessage.BATCH_DELETE);
    }

    private void singleCrudRun(List<SimpleEntityNotNull> list, int count) throws SQLException {
        startClock();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            list.get(i).save();
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);
    }

    private void deleteAll() {
        ActiveAndroid.execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
    }
}
