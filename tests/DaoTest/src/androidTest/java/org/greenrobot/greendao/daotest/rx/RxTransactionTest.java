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

import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.rx.RxTransaction;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.observers.TestSubscriber;

public class RxTransactionTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    private RxTransaction rxTx;

    public RxTransactionTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rxTx = daoSession.rxTx();
    }

    public void testRun() {
        Observable<Void> observable = rxTx.run(new Runnable() {
            @Override
            public void run() {
                TestEntity entity = insertEntity("hello");
                entity.setSimpleString("world");
                daoSession.update(entity);
            }
        });
        TestSubscriber<Void> testSubscriber = assertTxExecuted(observable);
        assertNull(testSubscriber.getOnNextEvents().get(0));
    }

    public void testCall() {
        testCall(rxTx);
    }

    public void testCallPlain() {
        RxTransaction rxTxPlain = daoSession.rxTxPlain();
        assertNotSame(rxTx, rxTxPlain);
        testCall(rxTxPlain);
    }

    public void testCall(RxTransaction rxTx) {
        Observable<String> observable = rxTx.call(new Callable<String>() {
            @Override
            public String call() {
                TestEntity entity = insertEntity("hello");
                entity.setSimpleString("world");
                daoSession.update(entity);
                return "Just checking";
            }
        });
        TestSubscriber<String> testSubscriber = assertTxExecuted(observable);
        assertEquals("Just checking", testSubscriber.getOnNextEvents().get(0));
    }

    private <T> TestSubscriber<T> assertTxExecuted(Observable<T> observable) {
        TestSubscriber<T> testSubscriber = RxTestHelper.awaitTestSubscriber(observable);
        assertEquals(1, testSubscriber.getValueCount());

        daoSession.clear();
        List<TestEntity> all = daoSession.getTestEntityDao().loadAll();
        assertEquals(1, all.size());
        assertEquals("hello", all.get(0).getSimpleStringNotNull());
        assertEquals("world", all.get(0).getSimpleString());
        return testSubscriber;
    }

    protected TestEntity insertEntity(String simpleStringNotNull) {
        return RxTestHelper.insertEntity(daoSession.getTestEntityDao(), simpleStringNotNull);
    }

}
