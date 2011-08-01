package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.orm.AbstractDao;
import de.greenrobot.orm.test.AbstractDaoTest;

public class PerformanceTest<D extends AbstractDao<T, K>, T, K> extends AbstractDaoTest<D, T, K> {

    public PerformanceTest(Class<D> daoClass) {
        super(daoClass);
    }

    public void testPerformance() {
        SimpleEntityNotNullTest helper = new SimpleEntityNotNullTest();
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < 10000; i++) {
            helper.createEntity((long) i);

        }
    }
}
