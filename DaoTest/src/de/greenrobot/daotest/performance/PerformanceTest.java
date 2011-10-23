/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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
package de.greenrobot.daotest.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Debug;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoTest;

public abstract class PerformanceTest<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {
    long start;
    private String traceName;
    boolean useTraceView = false;

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass, false);
    }

    public void testPerformance() throws Exception {
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
        // runTests(1000);
    }

    protected void runTests(int entityCount) {
        DaoLog.d("####################");
        DaoLog.d(getClass().getSimpleName() + ": " + entityCount + " entities on " + new Date());
        DaoLog.d("####################");
        clearIdentityScopeIfAny();

        List<T> list = new ArrayList<T>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity());
        }
        System.gc();

        dao.deleteAll();
        // runOneByOneTests(list, entityCount, entityCount / 10);
        dao.deleteAll();
        DaoLog.d("------------------------");
        System.gc();

        // runBatchTests(list);

        startClock("delete-all");
        dao.deleteAll();
        stopClock();
        System.gc();
    }

    protected void runOneByOneTests(List<T> list, int loadCount, int modifyCount) {
        dao.insertInTx(list);
        List<K> keys = new ArrayList<K>(loadCount);
        for (int i = 0; i < loadCount; i++) {
            keys.add(daoAccess.getKey(list.get(i)));
        }
        clearIdentityScopeIfAny();
        System.gc();

        list = runLoadOneByOne(keys, "load-one-by-one-1");
        list = runLoadOneByOne(keys, "load-one-by-one-2");
        Debug.stopMethodTracing();

        dao.deleteAll();
        System.gc();

        startClock("insert-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.insert(list.get(i));
        }
        stopClock(modifyCount + " entities");
        System.gc();

        startClock("update-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.update(list.get(i));
        }
        stopClock(modifyCount + " entities");
        System.gc();

        startClock("delete-one-by-one");
        for (int i = 0; i < modifyCount; i++) {
            dao.delete(list.get(i));
        }
        stopClock(modifyCount + " entities");
        System.gc();
    }

    protected List<T> runLoadOneByOne(List<K> keys, String traceName) {
        List<T> list = new ArrayList<T>(keys.size());
        startClock(traceName);
        for (K key : keys) {
            list.add(dao.load(key));
        }
        stopClock(keys.size() + " entities");
        return list;
    }

    protected void runBatchTests(List<T> list) {
        startClock("insert");
        dao.insertInTx(list);
        stopClock(list.size() + " entities");

        list = null;
        System.gc();

        clearIdentityScopeIfAny();
        list = runLoadAll("load-all-1");
        list = runLoadAll("load-all-2");

        startClock("update");
        dao.updateInTx(list);
        stopClock(list.size() + " entities");
    }

    protected List<T> runLoadAll(String traceName) {
        startClock(traceName);
        List<T> list = dao.loadAll();
        stopClock(list.size() + " entities");
        return list;
    }

    protected void startClock(String traceName) {
        System.gc();
        this.traceName = traceName;
        if (useTraceView) {
            Debug.startMethodTracing(traceName);
        }
        start = System.currentTimeMillis();
    }

    protected void stopClock() {
        stopClock(null);
    }

    protected void stopClock(String extraInfoOrNull) {
        long time = System.currentTimeMillis() - start;
        String extraLog = extraInfoOrNull != null ? " (" + extraInfoOrNull + ")" : "";
        DaoLog.d(traceName + " completed in " + time + "ms" + extraLog);
        if (useTraceView) {
            Debug.stopMethodTracing();
        }
        System.gc();
    }

    protected abstract T createEntity();
}
