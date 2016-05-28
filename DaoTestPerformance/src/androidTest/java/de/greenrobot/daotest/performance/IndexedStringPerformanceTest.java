package de.greenrobot.daotest.performance;

import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.IndexedStringEntity;
import de.greenrobot.daotest.IndexedStringEntityDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores entities with an indexed string property and measures the duration to query them by this
 * string. The generated strings are in a fixed random sequence. The strings to query for are also
 * in a fixed random sequence.
 */
public class IndexedStringPerformanceTest
        extends AbstractDaoTest<IndexedStringEntityDao, IndexedStringEntity, Long> {

    private static final int BATCH_SIZE = 10000;
    private static final int QUERY_COUNT = 1000;
    private static final int RUNS = 8;

    public IndexedStringPerformanceTest() {
        super(IndexedStringEntityDao.class, false);
    }

    public void testIndexedStringEntityQuery() {
        // disabled for regular builds
//        DaoLog.d("--------Indexed Queries: Start");
//        for (int i = 0; i < RUNS; i++) {
//            DaoLog.d("----Run " + (i + 1) + " of " + RUNS);
//            doIndexedStringEntityQuery();
//        }
//        DaoLog.d("--------Indexed Queries: End");
    }

    private void doIndexedStringEntityQuery() {
        // create entities
        List<IndexedStringEntity> entities = new ArrayList<>(BATCH_SIZE);
        String[] fixedRandomStrings = StringGenerator.createFixedRandomStrings(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            IndexedStringEntity entity = new IndexedStringEntity();
            entity.setId((long) i);
            entity.setIndexedString(fixedRandomStrings[i]);
            entities.add(entity);
        }
        DaoLog.d("Built entities.");

        // insert entities
        dao.insertInTx(entities);
        DaoLog.d("Inserted entities.");

        // query for entities by indexed string at random
        int[] randomIndices = StringGenerator.getFixedRandomIndices(QUERY_COUNT, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < QUERY_COUNT; i++) {
            int nextIndex = randomIndices[i];
            //noinspection unused
            List<IndexedStringEntity> query = dao.queryBuilder()
                    .where(IndexedStringEntityDao.Properties.IndexedString.eq(
                            fixedRandomStrings[nextIndex]))
                    .list();
        }
        long time = System.currentTimeMillis() - start;
        DaoLog.d("Queried for " + QUERY_COUNT + " of " + BATCH_SIZE + " indexed entities in " + time
                + " ms.");

        // delete all entities
        dao.deleteAll();
        DaoLog.d("Deleted all entities.");
    }
}
