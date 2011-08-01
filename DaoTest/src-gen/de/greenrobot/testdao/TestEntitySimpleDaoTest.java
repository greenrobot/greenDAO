package de.greenrobot.testdao;

import de.greenrobot.orm.test.AbstractDaoTest;

public class TestEntitySimpleDaoTest extends AbstractDaoTest<TestEntitySimpleDao, TestEntitySimple, Long> {

    public TestEntitySimpleDaoTest() {
        super(TestEntitySimpleDao.class);
    }

    @Override
    protected Long createRandomPk() {
        return random.nextLong();
    }

    @Override
    protected TestEntitySimple createEntity(Long key) {
        TestEntitySimple entity = new TestEntitySimple();
        entity.setId(key);
        entity.setSimpleStringNotNull("green");
        entity.setSimpleIntNotNull(Integer.MAX_VALUE);
        entity.setSimpleLongNotNull(Long.MAX_VALUE);
        return entity;
    }

}
