package de.greenrobot.testdao;

import de.greenrobot.orm.test.AbstractDaoTest;

public class TestEntitySimpleDaoTest extends AbstractDaoTest<TestEntitySimpleDao, TestEntitySimple, Integer> {

    public TestEntitySimpleDaoTest() {
        super(TestEntitySimpleDao.class);
    }

    @Override
    protected Integer createRandomPk() {
        return random.nextInt();
    }

    @Override
    protected TestEntitySimple createEntity(Integer key) {
        TestEntitySimple entity = new TestEntitySimple();
        entity.setId(key);
        entity.setSimpleStringNotNull("green");
        entity.setSimpleIntNotNull(Integer.MAX_VALUE);
        entity.setSimpleLongNotNull(Long.MAX_VALUE);
        return entity;
    }

}
