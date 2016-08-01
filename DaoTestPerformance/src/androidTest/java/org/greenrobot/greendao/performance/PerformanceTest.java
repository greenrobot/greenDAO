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

package org.greenrobot.greendao.performance;

import android.app.Application;
import android.os.Debug;
import android.os.Environment;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.test.AbstractDaoTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class PerformanceTest<D extends AbstractDao<T, K>, T, K>
        extends AbstractDaoTest<D, T, K> {

    private static final int BATCH_SIZE = 10000;
    private static final int RUNS = 10;

    boolean useTraceView = false;
    private Benchmark benchmark;
    private ArrayList<T> entities;

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass, false);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DaoLog.d("####################");
        DaoLog.d(getClass().getSimpleName() + ": " + BATCH_SIZE + " entities on " + new Date());
        DaoLog.d("####################");
        clearIdentityScopeIfAny();

        entities = new ArrayList(BATCH_SIZE);
        for (int i = 0; i < BATCH_SIZE; i++) {
            entities.add(createEntity());
        }

        dao.deleteAll();
    }

    // disable for regular builds
    public void _testPerformanceOneByOne() throws Exception {
        int count = BATCH_SIZE / 10;
        File benchFile = getBenchFile("greendao-1by1-" + count + ".tsv");
        benchmark = new Benchmark(benchFile);
        benchmark.addFixedColumnDevice().warmUpRuns(2);
        for (int i = 0; i < RUNS; i++) {
            runOneByOneTests(entities, count, count);

            startClock("delete-all");
            dao.deleteAll();
            stopClock();

            benchmark.commit();
        }
    }

    private File getBenchFile(String name) {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, name);
        if (dir == null || !dir.canWrite()) {
            Application app = createApplication(Application.class);
            File appFile = new File(app.getFilesDir(), name);
            DaoLog.d("Using file " + appFile.getAbsolutePath() + ", (cannot write to " + file.getAbsolutePath() + ")");
            file = appFile;
        }
        return file;
    }

    // disable for regular builds
    public void testPerformanceBatch() throws Exception {
        File benchFile = getBenchFile("greendao-batch-" + BATCH_SIZE + ".tsv");
        benchmark = new Benchmark(benchFile);
        benchmark.addFixedColumnDevice().warmUpRuns(2);

        for (int i = 0; i < RUNS; i++) {
            runBatchTests(entities);

            startClock("delete-all");
            dao.deleteAll();
            stopClock();

            benchmark.commit();
        }
    }

    protected void runOneByOneTests(List<T> list, int loadCount, int modifyCount) {
        dao.insertInTx(list);
        List<K> keys = new ArrayList<K>(loadCount);
        for (int i = 0; i < loadCount; i++) {
            keys.add(daoAccess.getKey(list.get(i)));
        }
        clearIdentityScopeIfAny();

        list = runLoadOneByOne(keys, "load-one-by-one-1");
        list = runLoadOneByOne(keys, "load-one-by-one-2");
        Debug.stopMethodTracing();

        dao.deleteAll();

        startClock("insert-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.insert(list.get(i));
        }
        stopClock();

        startClock("update-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.update(list.get(i));
        }
        stopClock();

        startClock("delete-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.delete(list.get(i));
        }
        stopClock();
    }

    protected List<T> runLoadOneByOne(List<K> keys, String traceName) {
        List<T> list = new ArrayList<T>(keys.size());
        startClock(traceName);
        for (K key : keys) {
            list.add(dao.load(key));
        }
        stopClock();
        return list;
    }

    protected void runBatchTests(List<T> list) {
        startClock("insert");
        dao.insertInTx(list);
        stopClock();

        list = null;
        System.gc();

        clearIdentityScopeIfAny();
        list = runLoadAll("load-all-1");
        accessAll(list, "access-all-1");
        list = runLoadAll("load-all-2");
        accessAll(list, "access-all-2");

        startClock("update");
        dao.updateInTx(list);
        stopClock();
    }

    protected List<T> runLoadAll(String traceName) {
        startClock(traceName);
        List<T> list = dao.loadAll();
        stopClock();
        return list;
    }

    protected void startClock(String name) {
        benchmark.start(name);
        if (useTraceView) {
            Debug.startMethodTracing(name);
        }
    }

    protected void stopClock() {
        benchmark.stop();
        if (useTraceView) {
            Debug.stopMethodTracing();
        }
    }

    protected abstract T createEntity();

    /**
     * Access every property of the entity under test and record execution time with {@link
     * #startClock(String)} and {@link #stopClock()}.
     */
    protected abstract void accessAll(List<T> list, String traceName);
}
