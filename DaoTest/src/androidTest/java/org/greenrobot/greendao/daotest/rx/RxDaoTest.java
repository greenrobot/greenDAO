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

package org.greenrobot.greendao.daotest.rx;

import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.test.AbstractDaoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.observers.TestSubscriber;

public class RxDaoTest extends AbstractDaoTest<TestEntityDao, TestEntity, Long> {

    private RxDao rxDao;

    public RxDaoTest() {
        super(TestEntityDao.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rxDao = dao.rx();
    }

    public void testScheduler() {
        TestSubscriber<List<TestEntity>> testSubscriber = awaitTestSubscriber(rxDao.loadAll());
        Thread lastSeenThread = testSubscriber.getLastSeenThread();
        assertNotSame(lastSeenThread, Thread.currentThread());
    }

    public void testNoScheduler() {
        RxDao<TestEntity, Long> rxDaoNoScheduler = dao.rxPlain();
        TestSubscriber<List<TestEntity>> testSubscriber = awaitTestSubscriber(rxDaoNoScheduler.loadAll());
        Thread lastSeenThread = testSubscriber.getLastSeenThread();
        assertSame(lastSeenThread, Thread.currentThread());
    }

    public void testLoadAll() {
        insertEntity("foo");
        insertEntity("bar");

        TestSubscriber<List<TestEntity>> testSubscriber = awaitTestSubscriber(rxDao.loadAll());
        assertEquals(1, testSubscriber.getValueCount());
        List<TestEntity> entities = testSubscriber.getOnNextEvents().get(0);

        // Order of entities is unspecified
        int foo = 0, bar = 0;
        for (TestEntity entity : entities) {
            String value = entity.getSimpleStringNotNull();
            if (value.equals("foo")) {
                foo++;
            } else if (value.equals("bar")) {
                bar++;
            } else {
                fail(value);
            }
        }
        assertEquals(1, foo);
        assertEquals(1, bar);
    }

    public void testLoad() {
        TestEntity foo = insertEntity("foo");
        TestSubscriber<TestEntity> testSubscriber = awaitTestSubscriber(rxDao.load(foo.getId()));
        assertEquals(1, testSubscriber.getValueCount());
        TestEntity foo2 = testSubscriber.getOnNextEvents().get(0);
        assertEquals(foo.getSimpleStringNotNull(), foo2.getSimpleStringNotNull());
    }

    public void testLoad_noResult() {
        TestSubscriber<TestEntity> testSubscriber = awaitTestSubscriber(rxDao.load(42));
        assertEquals(1, testSubscriber.getValueCount());
        // Should we really propagate null through Rx?
        assertNull(testSubscriber.getOnNextEvents().get(0));
    }

    public void testInsert() {
        TestEntity foo = createEntity("foo");
        TestSubscriber<TestEntity> testSubscriber = awaitTestSubscriber(rxDao.insert(foo));
        assertEquals(1, testSubscriber.getValueCount());
        TestEntity foo2 = testSubscriber.getOnNextEvents().get(0);
        assertSame(foo, foo2);

        List<TestEntity> all = dao.loadAll();
        assertEquals(1, all.size());
        assertEquals(foo.getSimpleStringNotNull(), all.get(0).getSimpleStringNotNull());
    }

    public void testInsertInTx() {
        TestEntity foo = createEntity("foo");
        TestEntity bar = createEntity("bar");
        TestSubscriber<Object[]> testSubscriber = awaitTestSubscriber(rxDao.insertInTx(foo, bar));
        assertEquals(1, testSubscriber.getValueCount());
        Object[] array = testSubscriber.getOnNextEvents().get(0);
        assertSame(foo, array[0]);
        assertSame(bar, array[1]);

        List<TestEntity> all = dao.loadAll();
        assertEquals(2, all.size());
        assertEquals(foo.getSimpleStringNotNull(), all.get(0).getSimpleStringNotNull());
        assertEquals(bar.getSimpleStringNotNull(), all.get(1).getSimpleStringNotNull());
    }

    public void testInsertInTxList() {
        TestEntity foo = createEntity("foo");
        TestEntity bar = createEntity("bar");
        List<TestEntity> list = new ArrayList<>();
        list.add(foo);
        list.add(bar);
        TestSubscriber<List<TestEntity>> testSubscriber = awaitTestSubscriber(rxDao.insertInTx(list));
        assertEquals(1, testSubscriber.getValueCount());
        List<TestEntity> result= testSubscriber.getOnNextEvents().get(0);
        assertSame(foo, result.get(0));
        assertSame(bar, result.get(1));

        List<TestEntity> all = dao.loadAll();
        assertEquals(2, all.size());
        assertEquals(foo.getSimpleStringNotNull(), all.get(0).getSimpleStringNotNull());
        assertEquals(bar.getSimpleStringNotNull(), all.get(1).getSimpleStringNotNull());
    }

    public void testCount() {
        TestEntity foo = insertEntity("foo");
        TestSubscriber<Long> testSubscriber = awaitTestSubscriber(rxDao.count());
        assertEquals(1, testSubscriber.getValueCount());
        Long count = testSubscriber.getOnNextEvents().get(0);
        assertEquals(1L, (long) count);
    }

    private TestSubscriber<List<TestEntity>> awaitTestSubscriber(Observable<List<TestEntity>> observable) {
        TestSubscriber<List<TestEntity>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(3, TimeUnit.SECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        return testSubscriber;
    }

    protected TestEntity insertEntity(String simpleStringNotNull) {
        TestEntity entity = createEntity(simpleStringNotNull);
        dao.insert(entity);
        return entity;
    }

    private TestEntity createEntity(String simpleStringNotNull) {
        TestEntity entity = new TestEntity();
        entity.setSimpleStringNotNull(simpleStringNotNull);
        return entity;
    }
}
