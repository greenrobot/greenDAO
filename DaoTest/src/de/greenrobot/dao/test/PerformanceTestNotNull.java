package de.greenrobot.dao.test;

public class PerformanceTestNotNull extends PerformanceTest<SimpleEntityNotNullDao, SimpleEntityNotNull, Long> {

    static long sequence;

    public PerformanceTestNotNull() {
        super(SimpleEntityNotNullDao.class);
        // setIdentityScopeBeforeSetUp(new de.greenrobot.dao.IdentityScope<Long, SimpleEntityNotNull>());
    }

    @Override
    protected SimpleEntityNotNull createEntity() {
        return SimpleEntityNotNullHelper.createEntity(sequence++);
    }

}
