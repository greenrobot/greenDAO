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

package org.greenrobot.greendao.daotest;

import java.util.concurrent.CountDownLatch;

import android.os.SystemClock;

import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;

public class DaoSessionConcurrentTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {
    class TestThread extends Thread {
        final Runnable runnable;

        public TestThread(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            latchThreadsReady.countDown();
            try {
                latchInsideTx.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runnable.run();
            latchThreadsDone.countDown();
        }

    }

    private final static int TIME_TO_WAIT_FOR_THREAD = 100; // Use 1000 to be on the safe side, 100 once stable

    protected TestEntityDao dao;

    protected CountDownLatch latchThreadsReady;
    protected CountDownLatch latchInsideTx;
    protected CountDownLatch latchThreadsDone;

    public DaoSessionConcurrentTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = daoSession.getTestEntityDao();
    }

    protected void initThreads(Runnable... runnables) throws InterruptedException {
        latchThreadsReady = new CountDownLatch(runnables.length);
        latchInsideTx = new CountDownLatch(1);
        latchThreadsDone = new CountDownLatch(runnables.length);
        for (Runnable runnable : runnables) {
            new TestThread(runnable).start();
        }
        latchThreadsReady.await();
    }

    public void testConcurrentInsertDuringTx() throws InterruptedException {
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                dao.insert(createEntity(null));
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                dao.insertInTx(createEntity(null));
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                daoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        dao.insert(createEntity(null));
                    }
                });
            }
        };
        Runnable runnable4 = new Runnable() {
            @Override
            public void run() {
                dao.insertWithoutSettingPk(createEntity(null));
            }
        };
        Runnable runnable5 = new Runnable() {
            @Override
            public void run() {
                dao.insertOrReplace(createEntity(null));
            }
        };
        initThreads(runnable1, runnable2, runnable3, runnable4, runnable5);
        // Builds the statement so it is ready immediately in the thread
        dao.insert(createEntity(null));
        doTx(new Runnable() {
            @Override
            public void run() {
                dao.insert(createEntity(null));
            }
        });
        latchThreadsDone.await();
        assertEquals(7, dao.count());
    }

    public void testConcurrentUpdateDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                dao.update(entity);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                dao.updateInTx(entity);
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                daoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        dao.update(entity);
                    }
                });
            }
        };
        initThreads(runnable1, runnable2, runnable3);
        // Builds the statement so it is ready immediately in the thread
        dao.update(entity);
        doTx(new Runnable() {
            @Override
            public void run() {
                dao.update(entity);
            }
        });
        latchThreadsDone.await();
    }

    public void testConcurrentDeleteDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                dao.delete(entity);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                dao.deleteInTx(entity);
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                daoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        dao.delete(entity);
                    }
                });
            }
        };
        initThreads(runnable1, runnable2, runnable3);
        // Builds the statement so it is ready immediately in the thread
        dao.delete(entity);
        doTx(new Runnable() {
            @Override
            public void run() {
                dao.delete(entity);
            }
        });
        latchThreadsDone.await();
    }

    // Query doesn't involve any statement locking currently, but just to stay on the safe side...
    public void testConcurrentQueryDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        final Query<TestEntity> query = dao.queryBuilder().build();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                query.forCurrentThread().list();
            }
        };

        initThreads(runnable1);
        // Builds the statement so it is ready immediately in the thread
        query.list();
        doTx(new Runnable() {
            @Override
            public void run() {
                query.list();
            }
        });
        latchThreadsDone.await();
    }

    public void testConcurrentLockAndQueryDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        final Query<TestEntity> query = dao.queryBuilder().build();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                query.forCurrentThread().list();
            }
        };

        initThreads(runnable1);
        // Builds the statement so it is ready immediately in the thread
        query.list();
        doTx(new Runnable() {
            @Override
            public void run() {
                query.list();
            }
        });
        latchThreadsDone.await();
    }

    public void testConcurrentDeleteQueryDuringTx() throws InterruptedException {
        final TestEntity entity = createEntity(null);
        dao.insert(entity);
        final DeleteQuery<TestEntity> query = dao.queryBuilder().buildDelete();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                query.forCurrentThread().executeDeleteWithoutDetachingEntities();
            }
        };

        initThreads(runnable1);
        // Builds the statement so it is ready immediately in the thread
        query.executeDeleteWithoutDetachingEntities();
        doTx(new Runnable() {
            @Override
            public void run() {
                query.executeDeleteWithoutDetachingEntities();
            }
        });
        latchThreadsDone.await();
    }

    public void testConcurrentResolveToMany() throws InterruptedException {
        final ToManyEntity entity = new ToManyEntity();
        ToManyEntityDao toManyDao = daoSession.getToManyEntityDao();
        toManyDao.insert(entity);

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                entity.getToManyTargetEntityList();
            }
        };

        initThreads(runnable1);
        doTx(new Runnable() {
            @Override
            public void run() {
                entity.getToManyTargetEntityList();
            }
        });
        latchThreadsDone.await();
    }

    public void testConcurrentResolveToOne() throws InterruptedException {
        final TreeEntity entity = new TreeEntity();
        TreeEntityDao toOneDao = daoSession.getTreeEntityDao();
        toOneDao.insert(entity);

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                entity.getParent();
            }
        };

        initThreads(runnable1);
        doTx(new Runnable() {
            @Override
            public void run() {
                entity.getParent();
            }
        });
        latchThreadsDone.await();
    }

    /**
     * We could put the statements inside ThreadLocals (fast enough), but it comes with initialization penalty for new
     * threads and costs more memory.
     */
    public void _testThreadLocalSpeed() {
        final Database db = dao.getDatabase();
        ThreadLocal<DatabaseStatement> threadLocal = new ThreadLocal<DatabaseStatement>() {
            @Override
            protected DatabaseStatement initialValue() {
                return db.compileStatement("SELECT 42");
            }
        };
        threadLocal.get();
        long start = SystemClock.currentThreadTimeMillis();
        for (int i = 0; i < 1000; i++) {
            DatabaseStatement sqLiteStatement = threadLocal.get();
            assertNotNull(sqLiteStatement);
        }
        Long time = SystemClock.currentThreadTimeMillis() - start;
        DaoLog.d("TIME: " + time + "ms");
        // Around 1ms on a S3
        assertTrue(time < 10);
    }

    protected void doTx(final Runnable runnableInsideTx) {
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                latchInsideTx.countDown();
                // Give the concurrent thread time so it will try to acquire locks
                try {
                    Thread.sleep(TIME_TO_WAIT_FOR_THREAD);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runnableInsideTx.run();
            }
        });
    }

    protected TestEntity createEntity(Long key) {
        TestEntity entity = new TestEntity(key);
        entity.setSimpleStringNotNull("green");
        return entity;
    }
}
