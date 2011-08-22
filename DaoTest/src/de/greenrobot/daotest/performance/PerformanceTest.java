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
    boolean useTraceView = true;

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass, false);
    }

    public void testPerformance() throws Exception {
        runTests(100); // Warmup
        runTests(1000);
    }

    protected void runTests(int entityCount) {
        DaoLog.d("####################");
        DaoLog.d(getClass().getSimpleName() + ": " + entityCount + " entities on " + new Date());
        DaoLog.d("####################");
        clearIdentityScopeIfAny();

        long start, time;

        List<T> list = new ArrayList<T>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity());
        }
        System.gc();

        dao.deleteAll();
        runOneByOneTests(list, entityCount, entityCount / 100);
        dao.deleteAll();
        System.gc();
        DaoLog.d("------------------------");

        runBatchTests(list);

        start = System.currentTimeMillis();
        dao.deleteAll();
        time = System.currentTimeMillis() - start;
        DaoLog.d("Deleted all entities in " + time + "ms");
        System.gc();
    }

    protected void runOneByOneTests(List<T> list, int loadCount, int modifyCount) {
        long start;
        long time;
        dao.insertInTx(list);
        List<K> keys = new ArrayList<K>(loadCount);
        for (int i = 0; i < loadCount; i++) {
            keys.add(daoAccess.getKey(list.get(i)));
        }
        clearIdentityScopeIfAny();
        System.gc();
        
        // Debug.startMethodTracing("load-one-by-one-1");
        // list = runLoadOneByOne(keys);
        // Debug.stopMethodTracing();
        //
        // Debug.startMethodTracing("load-one-by-one-2");
        // list = runLoadOneByOne(keys);
        // Debug.stopMethodTracing();

        dao.deleteAll();
        System.gc();

        start = System.currentTimeMillis();
        for (int i = 0; i < modifyCount; i++) {
            dao.insert(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        DaoLog.d("Inserted (one-by-one) " + modifyCount + " entities in " + time + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < modifyCount; i++) {
            dao.update(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        DaoLog.d("Updated (one-by-one) " + modifyCount + " entities in " + time + "ms");
        System.gc();

        start = System.currentTimeMillis();
        for (int i = 0; i < modifyCount; i++) {
            dao.delete(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        DaoLog.d("Deleted (one-by-one) " + modifyCount + " entities in " + time + "ms");
        System.gc();
    }

    protected List<T> runLoadOneByOne(List<K> keys) {
        List<T> list = new ArrayList<T>(keys.size());
        long start;
        long time;
        start = System.currentTimeMillis();
        for (K key : keys) {
            list.add(dao.load(key));
        }
        time = System.currentTimeMillis() - start;
        DaoLog.d("Load (one-by-one) " + keys.size() + " entities in " + time + "ms");
        System.gc();
        return list;
    }

    protected void runBatchTests(List<T> list) {
        long start;
        long time;
        start = System.currentTimeMillis();
        dao.insertInTx(list);
        time = System.currentTimeMillis() - start;
        DaoLog.d("Inserted (batch) " + list.size() + " entities in " + time + "ms");

        list = null;
        System.gc();

        clearIdentityScopeIfAny();
        list = runLoadAll("load-all-1");
        list = runLoadAll("load-all-2");

        start = System.currentTimeMillis();
        dao.updateInTx(list);
        time = System.currentTimeMillis() - start;
        DaoLog.d("Updated (batch) " + list.size() + " entities in " + time + "ms");
    }

    protected List<T> runLoadAll(String traceName) {
        startClock(traceName);
        List<T> list = dao.loadAll();
        stopClock(list.size() + " entities");
        System.gc();
        return list;
    }

    protected void startClock(String traceName) {
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
    }

    protected abstract T createEntity();
}
