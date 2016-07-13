package org.greenrobot.greendao.daotest.rx;

import org.greenrobot.greendao.RxDao;
import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.test.AbstractDaoTest;

import java.util.List;

import rx.observables.BlockingObservable;

public class RxDaoTest extends AbstractDaoTest<TestEntityDao, TestEntity, Long> {

    private RxDao rxDao;

    public RxDaoTest() {
        super(TestEntityDao.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rxDao = new RxDao(dao);
    }

    public void testLoadAll() {
        insertEntity("foo");
        insertEntity("bar");
        BlockingObservable<List<TestEntity>> blockingObservable = rxDao.loadAll().toBlocking();
        List<TestEntity> entities = blockingObservable.first();
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

    protected TestEntity insertEntity(String simpleStringNotNull) {
        TestEntity entity = new TestEntity();
        entity.setSimpleStringNotNull(simpleStringNotNull);
        dao.insert(entity);
        return entity;
    }
}
