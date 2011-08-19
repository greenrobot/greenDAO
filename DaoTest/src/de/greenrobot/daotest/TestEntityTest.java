package de.greenrobot.daotest;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.dao.test.TestEntity;
import de.greenrobot.dao.test.TestEntityDao;

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

    public void testRefresh() {
        TestEntity entity = createEntity(1l);
        entity.setSimpleInteger(42);
        entity.setSimpleString(null);
        dao.insert(entity);
        entity.setSimpleInteger(null);
        entity.setSimpleString("temp");
        dao.refresh(entity);
        assertEquals(42, (int) entity.getSimpleInteger());
        assertNull(entity.getSimpleString());
    }

    public void testRefreshIllegal() {
        TestEntity entity = createEntity(1l);
        try {
            dao.refresh(entity);
            fail("Exception expected");
        } catch (DaoException expected) {
        }
        dao.insert(entity);
        dao.delete(entity);
        try {
            dao.refresh(entity);
            fail("Exception expected");
        } catch (DaoException expected) {
        }
    }

}
