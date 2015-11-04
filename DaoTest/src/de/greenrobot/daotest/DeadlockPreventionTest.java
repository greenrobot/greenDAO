/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daotest;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.test.AbstractDaoSessionTest;

public class DeadlockPreventionTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    volatile boolean done;
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
        Thread thread = new InsertThread();
        Thread thread2 = new InsertBatchThread();
        thread.start();
        thread2.start();

        for (int i = 0; i < 10; i++) {
            System.out.println("Starting loadAll #" + i);
            dao.loadAll();
        }

        done = true;
        thread.join();
        thread2.join();
    }

    private class InsertThread extends Thread {
        @Override
        public void run() {
            int counter = 0;
            while (!done) {
                TestEntity entity = new TestEntity();
                entity.setSimpleStringNotNull("TextThread" + counter);
                dao.insert(entity);
                counter++;
                if (counter % 10 == 0) {
                    System.out.println("Thread inserted " + counter);
                }
            }
        }
    }

    private class InsertBatchThread extends Thread {
        @Override
        public void run() {
            int counter = 0;
            List<TestEntity> batch = new ArrayList<>();
            while (!done) {
                TestEntity entity = new TestEntity();
                entity.setSimpleStringNotNull("TextThreadBatch" + counter);
                batch.add(entity);
                counter++;
                if (counter % 10 == 0) {
                    dao.insertInTx(batch);
                    System.out.println("Batch Thread inserted " + counter);
                    batch.clear();
                }
            }
        }
    }
}