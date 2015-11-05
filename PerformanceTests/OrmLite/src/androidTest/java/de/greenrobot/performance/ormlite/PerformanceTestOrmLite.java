package de.greenrobot.performance.ormlite;

import com.j256.ormlite.dao.Dao;
import de.greenrobot.performance.BasePerfTestCase;
import de.greenrobot.performance.StringGenerator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * http://ormlite.com/sqlite_java_android_orm.shtml https://github.com/j256/ormlite-examples
 */
public class PerformanceTestOrmLite extends BasePerfTestCase {

    private boolean inMemory = false;
    private DbHelper dbHelper;

    @Override
    protected String getLogTag() {
        return "PerfTestOrmLite";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setUpOrmLite();
    }

    protected void setUpOrmLite() {
        String name;
        if (inMemory) {
            name = null;
        } else {
            name = "test-db";
            getApplication().deleteDatabase(name);
        }
        dbHelper = new DbHelper(getApplication(), name);
    }

    @Override
    protected void tearDown() throws Exception {
        if (!inMemory) {
            getApplication().deleteDatabase("test-db");
        }

        super.tearDown();
    }

    @Override
    protected void doIndexedStringEntityQueries() throws Exception {
        // set up data access
        final Dao<IndexedStringEntity, Long> dao = dbHelper.getDao(IndexedStringEntity.class);
        log("Set up data access.");

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            indexedStringEntityQueriesRun(dao, getBatchSize());
        }
    }

    private void indexedStringEntityQueriesRun(final Dao<IndexedStringEntity, Long> dao, int count)
            throws Exception {
        // create entities
        final List<IndexedStringEntity> entities = new ArrayList<>(count);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(count);
        for (int i = 0; i < count; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity._id = (long) i;
            entity.indexedString = fixedRandomStrings[i];
            entities.add(entity);
        }
        log("Built entities.");

        // insert entities
        dao.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (IndexedStringEntity entity : entities) {
                    dao.create(entity);
                }
                return null;
            }
        });
        log("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, count - 1);

        startClock();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            //noinspection unused
            List<IndexedStringEntity> query = dao.queryBuilder()
                    .where()
                    .eq("INDEXED_STRING", fixedRandomStrings[nextIndex])
                    .query();
            // ORMLite already builds all entities when executing the query, so move on
        }
        stopClock(LogMessage.QUERY_INDEXED);

        // delete all entities
        dbHelper.getWritableDatabase().execSQL("DELETE FROM INDEXED_STRING_ENTITY");
        log("Deleted all entities.");
    }

    @Override
    protected void doOneByOneAndBatchCrud() throws Exception {
        Dao<SimpleEntityNotNull, Long> dao = dbHelper.getDao(SimpleEntityNotNull.class);

        for (int i = 0; i < RUNS; i++) {
            log("----Run " + (i + 1) + " of " + RUNS);
            oneByOneCrudRun(dao, getBatchSize() / 10);
            batchCrudRun(dao, getBatchSize());
        }
    }

    private void oneByOneCrudRun(Dao<SimpleEntityNotNull, Long> dao, int count)
            throws SQLException {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }

        startClock();
        for (int i = 0; i < count; i++) {
            dao.create(list.get(i));
        }
        stopClock(LogMessage.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            dao.update(list.get(i));
        }
        stopClock(LogMessage.ONE_BY_ONE_UPDATE);

        deleteAll();
    }

    private void batchCrudRun(final Dao<SimpleEntityNotNull, Long> dao, int count)
            throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(SimpleEntityNotNullHelper.createEntity((long) i));
        }

        startClock();
        dao.callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for (SimpleEntityNotNull entity : list) {
                    dao.create(entity);
                }
                return null;
            }
        });
        stopClock(LogMessage.BATCH_CREATE);

        startClock();
        dao.callBatchTasks(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for (SimpleEntityNotNull entity : list) {
                    dao.update(entity);
                }
                return null;
            }
        });
        stopClock(LogMessage.BATCH_UPDATE);

        startClock();
        List<SimpleEntityNotNull> reloaded = dao.queryForAll();
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

    private void deleteAll() {
        dbHelper.getWritableDatabase().execSQL("DELETE FROM SIMPLE_ENTITY_NOT_NULL");
    }

    public void testSemantics() {
        try {
            Dao<MinimalEntity, Long> minimalDao = dbHelper.getDao(MinimalEntity.class);
            MinimalEntity data = new MinimalEntity();
            minimalDao.create(data);
            // ORMLite does update PK after insert if set to generatedId
            assertNotNull(data.getId());
            MinimalEntity data2 = minimalDao.queryForAll().get(0);
            MinimalEntity data3 = minimalDao.queryForId(data2.getId());
            // ORMLite does not provide object equality
            assertNotSame(data, data2);
            assertNotSame(data2, data3);
            assertEquals(data2.getId(), data3.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
