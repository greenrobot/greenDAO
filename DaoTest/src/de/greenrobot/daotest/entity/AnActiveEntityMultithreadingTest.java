/*
 * Copyright (C) 2013 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daotest.entity;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.AnActiveEntity;
import de.greenrobot.daotest.AnActiveEntityDao;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;

public class AnActiveEntityMultithreadingTest extends AbstractDaoSessionTest<Application, DaoMaster, DaoSession> {

    /** Serious multithreading tests require this set to true. */
    private static final boolean LONG_RUNNING = false;
    private static final int ENTITIES_TO_CHECK = LONG_RUNNING ? 1000000 : 10000;

    private AnActiveEntityDao dao;
    private CountDownLatch latch = new CountDownLatch(2);
    volatile boolean running = true;

    public AnActiveEntityMultithreadingTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        dao = daoSession.getAnActiveEntityDao();
    }

    public void testAlwaysAttachedWithInsertDelete() throws Exception {
        doTestAlwaysAttached(new InsertDeleteThread());
    }

    public void testAlwaysAttachedWithDetach() throws Exception {
        doTestAlwaysAttached(new DetachThread());
    }

    private void doTestAlwaysAttached(Thread thread) throws Exception {
        thread.start();

        Field daoSessionField = AnActiveEntity.class.getDeclaredField("daoSession");
        daoSessionField.setAccessible(true);

        int countEntity = 0;
        countDownAndAwaitLatch();

        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            for (int i = 0;; i++) {
                AnActiveEntity entity = dao.load(1l);
                if (entity != null) {
                    countEntity++;
                    assertNotNull(daoSessionField.get(entity));
                }
                if (i == 1000000 && countEntity == 0) {
                    fail("No entity available");
                }
                if (countEntity % 10000 == 0) {
                    DaoLog.d("Checked entities " + countEntity + " in " + i + " iterations");
                }
                if (countEntity == ENTITIES_TO_CHECK) {
                    break;
                }
            }
        } finally {
            running = false;
            thread.join();
        }
    }

    private void countDownAndAwaitLatch() {
        latch.countDown();
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    class InsertDeleteThread extends Thread {
        @Override
        public void run() {
            countDownAndAwaitLatch();

            while (running) {
                AnActiveEntity entity = null;
                entity = new AnActiveEntity(1l);
                dao.insert(entity);
                dao.delete(entity);
            }
        }
    }

    class DetachThread extends Thread {
        @Override
        public void run() {
            countDownAndAwaitLatch();

            AnActiveEntity entity = new AnActiveEntity(1l);
            dao.insert(entity);
            while (running) {
                dao.detach(entity);
                entity = dao.load(1l);
            }
        }
    }
}
