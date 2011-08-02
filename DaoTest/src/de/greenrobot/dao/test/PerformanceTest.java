package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.test.AbstractDaoTest;

public abstract class PerformanceTest<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass);
    }

    public void testPerformance() throws Exception {
        runTests(100); // Warmup
        tearDown();
        setUp();
        runTests(10000);
    }

    protected void runTests(int entityCount) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity());
        }
        System.gc();

        long start = System.currentTimeMillis();
        dao.insertInTx(list);
        long time = System.currentTimeMillis() - start;
        Log.d("DAO", "Inserted " + list.size() + " entities in " + time + "ms");

        list = null;
        System.gc();

        start = System.currentTimeMillis();
        list = dao.loadAll();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Loaded " + list.size() + " entities in " + time + "ms");

        System.gc();

        start = System.currentTimeMillis();
        dao.updateInTx(list);
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Updated " + list.size() + " entities in " + time + "ms");
    }

    protected abstract T createEntity();
}
