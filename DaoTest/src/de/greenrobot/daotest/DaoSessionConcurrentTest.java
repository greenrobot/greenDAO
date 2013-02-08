package de.greenrobot.daotest;

import java.util.concurrent.CountDownLatch;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.SystemClock;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class DaoSessionConcurrentTest extends AbstractDaoSessionTest<Application, DaoMaster, DaoSession> {

    private final static int TIME_TO_WAIT_FOR_THREAD = 1000; // Use 1000 to be on the safe side, 100 once stable

    private TestEntityDao dao;

    public DaoSessionConcurrentTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        dao = daoSession.getTestEntityDao();
    }

    public void testConcurrentInsertDuringTx() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    dao.insert(createEntity(null));
                    dao.insertInTx(createEntity(null));
                    daoSession.runInTx(new Runnable() {
                        @Override
                        public void run() {
                            dao.insert(createEntity(null));
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        });
        thread.start();
        // Builds the statement so it is ready immediately in the thread
        dao.insert(createEntity(null));
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                // Give the concurrent thread time so it will try to acquire locks
                try {
                    Thread.sleep(TIME_TO_WAIT_FOR_THREAD);
                } catch (InterruptedException e) {
                }
                dao.insert(createEntity(null));
            }
        });
        thread.join();
    }

    public void testConcurrentUpdateDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    dao.update(entity);
                    dao.updateInTx(entity);
                    daoSession.runInTx(new Runnable() {
                        @Override
                        public void run() {
                            dao.update(entity);
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        });
        thread.start();
        // Builds the statement so it is ready immediately in the thread
        dao.update(entity);
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                // Give the concurrent thread time so it will try to acquire locks
                try {
                    Thread.sleep(TIME_TO_WAIT_FOR_THREAD);
                } catch (InterruptedException e) {
                }
                dao.update(entity);
            }
        });
        thread.join();
    }

    /**
     * We could put the statements inside ThreadLocals (fast enough), but it comes with initialization penalty for new
     * threads and costs more memory.
     */
    public void _testThreadLocalSpeed() {
        final SQLiteDatabase db = dao.getDatabase();
        ThreadLocal<SQLiteStatement> threadLocal = new ThreadLocal<SQLiteStatement>() {
            @Override
            protected SQLiteStatement initialValue() {
                return db.compileStatement("SELECT 42");
            }
        };
        threadLocal.get();
        long start = SystemClock.currentThreadTimeMillis();
        for (int i = 0; i < 1000; i++) {
            SQLiteStatement sqLiteStatement = threadLocal.get();
            assertNotNull(sqLiteStatement);
        }
        Long time = SystemClock.currentThreadTimeMillis() - start;
        DaoLog.d("TIME: " + time + "ms");
        // Around 1ms on a S3
        assertTrue(time < 10);
    }

    protected TestEntity createEntity(Long key) {
        TestEntity entity = new TestEntity(key);
        entity.setSimpleStringNotNull("green");
        return entity;
    }
}
