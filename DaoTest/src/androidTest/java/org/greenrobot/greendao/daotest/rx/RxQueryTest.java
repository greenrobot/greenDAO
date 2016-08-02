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
import org.greenrobot.greendao.daotest.TestEntityDao.Properties;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.rx.RxQuery;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;

import java.util.ArrayList;
import java.util.List;

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
        rxQuery = query.rx();
    }

    public void testList() {
        insertEntities(15);
        TestSubscriber<List<TestEntity>> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.list());
        assertEquals(1, testSubscriber.getValueCount());
        List<TestEntity> entitiesRead = testSubscriber.getOnNextEvents().get(0);
        assertEquals(10, entitiesRead.size());
    }

    public void testUnique() {
        insertEntities(1);
        TestSubscriber<TestEntity> testSubscriber = RxTestHelper.awaitTestSubscriber(rxQuery.unique());
        assertEquals(1, testSubscriber.getValueCount());
        TestEntity entityRead = testSubscriber.getOnNextEvents().get(0);
        assertNotNull(entityRead);
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
