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
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryThreadLocalTest extends TestEntityTestBase {
    private Query<TestEntity> queryFromOtherThread;

    public void testGetForCurrentThread_SameInstance() {
        Query<TestEntity> query = dao.queryBuilder().build();
        assertSame(query, query.forCurrentThread());
    }

    public void testGetForCurrentThread_ParametersAreReset() {
        insert(3);
        int value = getSimpleInteger(1);
        Query<TestEntity> query = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).build();
        query.setParameter(0, value + 1);
        TestEntity entityFor2 = query.unique();
        assertEquals(value + 1, (int) entityFor2.getSimpleInteger());
        query = query.forCurrentThread();
        TestEntity entityFor1 = query.unique();
        assertEquals(value, (int) entityFor1.getSimpleInteger());
    }

    public void testGetForCurrentThread_TwoThreads() throws InterruptedException {
        insert(3);
        createQueryFromOtherThread();
        Query<TestEntity> query = queryFromOtherThread.forCurrentThread();
        assertNotSame(queryFromOtherThread, query);
        query.setLimit(10);
        query.setOffset(0);
        assertEquals(getSimpleInteger(1), (int) query.uniqueOrThrow().getSimpleInteger());
        int expected = getSimpleInteger(2);
        query.setParameter(0, expected);
        assertEquals(expected, (int) query.list().get(0).getSimpleInteger());
        assertEquals(expected, (int) query.listLazy().get(0).getSimpleInteger());
        assertEquals(expected, (int) query.listLazyUncached().get(0).getSimpleInteger());
        assertEquals(expected, (int) query.unique().getSimpleInteger());
        assertEquals(expected, (int) query.uniqueOrThrow().getSimpleInteger());
    }

    public void testThrowOutsideOwnerThread() throws InterruptedException {
        createQueryFromOtherThread();
        try {
            queryFromOtherThread.list();
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.listIterator();
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.listLazyUncached();
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.setLimit(2);
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.setOffset(2);
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.setParameter(0, 42);
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.unique();
            fail("Did not throw");
        } catch (DaoException expected) {
        }
        try {
            queryFromOtherThread.uniqueOrThrow();
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
                builder.limit(10).offset(20);
                queryFromOtherThread = builder.build();
            }
        };
        thread.start();
        thread.join();
        assertNotNull(queryFromOtherThread);
    }

}
