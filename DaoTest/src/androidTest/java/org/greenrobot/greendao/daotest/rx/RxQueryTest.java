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

import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao.Properties;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.rx.RxQuery;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

public class RxQueryTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private Query<TestEntity> query;
    private RxQuery<TestEntity> rxQuery;

    public RxQueryTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        query = daoSession.getTestEntityDao().queryBuilder().where(Properties.SimpleInt.lt(10)).build();
        rxQuery = query.__InternalRx();
    }

    public void testList() {
        insertEntities(15);
        TestSubscriber<List<TestEntity>> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.list());
        assertEquals(1, testSubscriber.getValueCount());
        List<TestEntity> entitiesRead = testSubscriber.getOnNextEvents().get(0);
        assertEquals(10, entitiesRead.size());
    }

    // TODO figure out how to pass params to rxQuery
    public void _testListSetParameters() {
        insertEntities(15);

        // TODO how to pass those to rxQuery?
        query.setParameter(0, 5);

        TestSubscriber<List<TestEntity>> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.list());
        assertEquals(1, testSubscriber.getValueCount());
        List<TestEntity> entitiesRead = testSubscriber.getOnNextEvents().get(0);
        assertEquals(5, entitiesRead.size());
    }

    public void testUnique() {
        insertEntities(1);
        TestSubscriber<TestEntity> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.unique());
        assertEquals(1, testSubscriber.getValueCount());
        TestEntity entityRead = testSubscriber.getOnNextEvents().get(0);
        assertNotNull(entityRead);
    }

    public void testOneByOne() {
        insertEntities(15);
        TestSubscriber<TestEntity> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.oneByOne());
        assertEquals(10, testSubscriber.getValueCount());
        for (int i = 0; i < 10; i++) {
            TestEntity entity = testSubscriber.getOnNextEvents().get(i);
            assertEquals(i, entity.getSimpleInt());
        }
    }

    public void testOneByOneUnsubscribe() {
        insertEntities(1000);
        RxQuery<TestEntity> bigQuery = daoSession.getTestEntityDao().queryBuilder().rx();
        TestSubscriber<TestEntity> testSubscriber = new TestSubscriber<>();
        Observable<TestEntity> observable = bigQuery.oneByOne();
        Subscription subscription = observable.subscribe(testSubscriber);
        subscription.unsubscribe();
        testSubscriber.assertUnsubscribed();
        int count = testSubscriber.getValueCount();
        testSubscriber.awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
        int count2 = testSubscriber.getValueCount();
        DaoLog.d("Count 1: " + count + " vs. count 2: " + count2);
        // Not strictly multi-threading correct, but anyway:
        assertTrue(count2 < 1000);
    }

    protected List<TestEntity> insertEntities(int count) {
        List<TestEntity> entities = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            TestEntity entity = RxTestHelper.createEntity("My entity ");
            entity.setSimpleInt(i);
            entities.add(entity);
        }

        daoSession.getTestEntityDao().insertInTx(entities);
        return entities;
    }

}
