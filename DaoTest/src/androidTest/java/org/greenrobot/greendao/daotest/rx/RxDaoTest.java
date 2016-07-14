package org.greenrobot.greendao.daotest.rx;

import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.test.AbstractDaoTest;

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
        RxDao<TestEntity, Long> rxDaoNoScheduler = new RxDao<>(dao);
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

    private TestSubscriber<List<TestEntity>> awaitTestSubscriber(Observable<List<TestEntity>> observable) {
        TestSubscriber<List<TestEntity>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(3, TimeUnit.SECONDS);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        return testSubscriber;
    }

    protected TestEntity insertEntity(String simpleStringNotNull) {
        TestEntity entity = new TestEntity();
        entity.setSimpleStringNotNull(simpleStringNotNull);
        dao.insert(entity);
        return entity;
    }
}
