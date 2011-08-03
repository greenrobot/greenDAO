package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import de.greenrobot.dao.AbstractDao;

public abstract class PerformanceTest<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass, false);
    }

    public void testPerformance() throws Exception {
        runTests(100); // Warmup
        runTests(1000);
    }

    protected void runTests(int entityCount) {
        long start, time;

        List<T> list = new ArrayList<T>();
        for (int i = 0; i < entityCount; i++) {
            list.add(createEntity());
        }
        System.gc();

        // runOneByOneTests(list, entityCount/10);

        start = System.currentTimeMillis();
        dao.deleteAll();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Deleted all entities in " + time + "ms");
        System.gc();

        runBatchTests(list);

        start = System.currentTimeMillis();
        dao.deleteAll();
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Deleted all entities in " + time + "ms");
        System.gc();
    }

    protected void runOneByOneTests(List<T> list, int entityCount) {
        long start;
        long time;
        start = System.currentTimeMillis();
        for (int i = 0; i < entityCount ; i++) {
            dao.insert(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Inserted (one-by-one) " + entityCount + " entities in " + time + "ms");
        System.gc();

        start = System.currentTimeMillis();
        for (int i = 0; i < entityCount / 10; i++) {
            dao.update(list.get(i));
        }
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Updated (one-by-one) " + entityCount  + " entities in " + time + "ms");
        System.gc();
    }
    

    protected void runBatchTests(List<T> list) {
        long start;
        long time;
        start = System.currentTimeMillis();
        dao.insertInTx(list);
        time = System.currentTimeMillis() - start;
        Log.d("DAO", "Inserted (batch) " + list.size() + " entities in " + time + "ms");

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
        Log.d("DAO", "Updated (batch) " + list.size() + " entities in " + time + "ms");
    }


    protected abstract T createEntity();
}
