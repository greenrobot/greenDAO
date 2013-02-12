/*
 * Copyright (C) 2011-2013 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daotest.query;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class DeleteQueryThreadLocalTest extends TestEntityTestBase {
    private DeleteQuery<TestEntity> queryFromOtherThread;

    public void testGetForCurrentThread_SameInstance() {
        DeleteQuery<TestEntity> query = dao.queryBuilder().buildDelete();
        assertSame(query, query.forCurrentThread());
    }

    public void testGetForCurrentThread_ParametersAreReset() {
        insert(3);
        int value = getSimpleInteger(1);
        DeleteQuery<TestEntity> query = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).buildDelete();
        query.setParameter(0, value + 100);
        query.executeDeleteWithoutDetachingEntities();
        assertEquals(3, dao.count());
        query = query.forCurrentThread();
        query.executeDeleteWithoutDetachingEntities();
        assertEquals(2, dao.count());
    }

    public void testGetForCurrentThread_TwoThreads() throws InterruptedException {
        insert(3);
        createQueryFromOtherThread();
        DeleteQuery<TestEntity> query = queryFromOtherThread.forCurrentThread();
        assertNotSame(queryFromOtherThread, query);
        query.setParameter(0, getSimpleInteger(2));
        query.executeDeleteWithoutDetachingEntities();
        assertEquals(2, dao.count());
    }

    public void testThrowOutsideOwnerThread() throws InterruptedException {
        createQueryFromOtherThread();
        try {
            queryFromOtherThread.executeDeleteWithoutDetachingEntities();
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.setParameter(0, 42);
            fail("Did not throw");
        } catch (DaoException expected) {
        }
    }

    private void createQueryFromOtherThread() throws InterruptedException {
        Thread thread = new Thread() {

            @Override
            public void run() {
                QueryBuilder<TestEntity> builder = dao.queryBuilder();
                builder.where(Properties.SimpleInteger.eq(getSimpleInteger(1)));
                queryFromOtherThread = builder.buildDelete();
            }
        };
        thread.start();
        thread.join();
        assertNotNull(queryFromOtherThread);
    }

}
