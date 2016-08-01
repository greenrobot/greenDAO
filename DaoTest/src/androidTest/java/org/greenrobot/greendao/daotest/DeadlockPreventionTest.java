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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.greenrobot.greendao.daotest.DaoMaster;
import org.greenrobot.greendao.daotest.DaoSession;
import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.test.AbstractDaoSessionTest;

/**
 * Test to reproduce https://github.com/greenrobot/greenDAO/issues/223 (works at least on a Android 2.3 emulator).
 */
public class DeadlockPreventionTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    CountDownLatch done = new CountDownLatch(1);
    private TestEntityDao dao;

    public DeadlockPreventionTest() {
        super(DaoMaster.class);
    }

    // Runs pretty long, only run manually
    public void _testLoadAll() throws InterruptedException {
        dao = daoSession.getTestEntityDao();
        List<TestEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            TestEntity entity = new TestEntity();
            entity.setSimpleStringNotNull("Text" + i);
            entities.add(entity);
        }
        dao.insertInTx(entities);
        System.out.println("Entities inserted");

        LoadThread loadThread = new LoadThread();
        InsertThread insertThread = new InsertThread();
        InsertBatchThread insertBatchThread = new InsertBatchThread();
        loadThread.start();
        insertThread.start();
        insertBatchThread.start();

        int lastCounterInsert = insertThread.counter;
        int lastCounterInsertBatch = insertBatchThread.counter;
        int noProgressCount = 0;
        while (!done.await(10, TimeUnit.SECONDS)) {
            if (lastCounterInsert == insertThread.counter && lastCounterInsertBatch == insertBatchThread.counter) {
                noProgressCount++;
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("No progress #" + noProgressCount + ", dumping threads");
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                dumpStacktrace("LOAD", loadThread);
                dumpStacktrace("INSERT", insertThread);
                dumpStacktrace("INSERT BATCH", insertBatchThread);

                if (noProgressCount >= 3) {
                    // Test seems to be stuck, kill everything!
                    System.exit(1);
                }
            } else {
                lastCounterInsert = insertThread.counter;
                lastCounterInsertBatch = insertBatchThread.counter;
                noProgressCount = 0;
            }
        }

        loadThread.join();
        insertThread.join();
        insertBatchThread.join();
    }

    private void dumpStacktrace(String name, Thread thread) {
        System.err.println("--- Thread dump of " + name + " ------------------------");
        for (StackTraceElement element : thread.getStackTrace()) {
            System.err.println(element);
        }
    }

    private class LoadThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("Starting loadAll #" + i);
                dao.loadAll();
            }
            done.countDown();
        }
    }


    private class InsertThread extends Thread {
        volatile int counter = 0;

        @Override
        public void run() {
            List<TestEntity> toDelete = new ArrayList<>();
            while (done.getCount() > 0) {
                TestEntity entity = new TestEntity();
                entity.setSimpleStringNotNull("TextThread" + counter);
                dao.insert(entity);
                toDelete.add(entity);
                counter++;
                if (counter % 10 == 0) {
                    System.out.println("Thread inserted " + counter+ ", now deleting");
                    dao.deleteInTx(toDelete);
                    toDelete.clear();
                }
            }
        }
    }

    private class InsertBatchThread extends Thread {
        volatile int counter = 0;

        @Override
        public void run() {
            List<TestEntity> batch = new ArrayList<>();
            List<TestEntity> toDelete = new ArrayList<>();
            while (done.getCount() > 0) {
                TestEntity entity = new TestEntity();
                entity.setSimpleStringNotNull("TextThreadBatch" + counter);
                batch.add(entity);
                counter++;
                if (counter % 10 == 0) {
                    dao.insertInTx(batch);
                    System.out.println("Batch Thread inserted " + counter);
                    toDelete.addAll(batch);
                    batch.clear();
                }
                if (counter % 1000 == 0) {
                    dao.deleteInTx(toDelete);
                    toDelete.clear();
                    System.out.println("Batch Thread deleted " + counter);
                }
            }
        }
    }
}