package de.greenrobot.dao.test;

import de.greenrobot.testdao.TestEntity;
import de.greenrobot.testdao.TestEntityDao;

public class TestEntityTest extends AbstractDaoTestLongPk<TestEntityDao, TestEntity> {

    public TestEntityTest() {
        super(TestEntityDao.class);
    }

    @Override
    protected TestEntity createEntity(Long key) {
        TestEntity entity = new TestEntity();
        entity.setId(key);
        entity.setSimpleStringNotNull("green");
        return entity;
    }

}
