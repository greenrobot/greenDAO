package de.greenrobot.dao.test;

import de.greenrobot.testdao.SimpleEntityNotNull;
import de.greenrobot.testdao.SimpleEntityNotNullDao;

public class PerformanceTestNotNull extends PerformanceTest<SimpleEntityNotNullDao, SimpleEntityNotNull, Long> {

    static long sequence;

    public PerformanceTestNotNull() {
        super(SimpleEntityNotNullDao.class);
    }

    @Override
    protected SimpleEntityNotNull createEntity() {
        return SimpleEntityNotNullHelper.createEntity(sequence++);
    }

}
