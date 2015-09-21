package de.greenrobot.daotest.performance;

import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.IndexedStringEntity;
import de.greenrobot.daotest.IndexedStringEntityDao;
import de.greenrobot.performance.StringGenerator;
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
    private static final int INDEXED_RUNS = 1000;

    public IndexedStringPerformanceTest() {
        super(IndexedStringEntityDao.class, false);
    }

    public void testIndexedStringEntityQuery() {
        // disabled for regular builds
//        doIndexedStringEntityQuery();
    }

    private void doIndexedStringEntityQuery() {
        DaoLog.d("---------------Indexed Queries: Start");

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
        int[] randomIndices = StringGenerator.getFixedRandomIndices(INDEXED_RUNS, BATCH_SIZE - 1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < INDEXED_RUNS; i++) {
            int nextIndex = randomIndices[i];
            dao.queryBuilder()
                    .where(IndexedStringEntityDao.Properties.IndexedString.eq(
                            fixedRandomStrings[nextIndex]))
                    .list();
        }
        long time = System.currentTimeMillis() - start;
        DaoLog.d("Queried for " + INDEXED_RUNS + " indexed entities in " + time + " ms");

        DaoLog.d("---------------Indexed Queries: End");
    }
}
