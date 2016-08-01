/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.performance;

import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.daotest.IndexedStringEntity;
import org.greenrobot.greendao.daotest.IndexedStringEntityDao;
import org.greenrobot.greendao.test.AbstractDaoTest;

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
