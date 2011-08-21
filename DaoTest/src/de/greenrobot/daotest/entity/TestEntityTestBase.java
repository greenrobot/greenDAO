package de.greenrobot.daotest.entity;

import java.util.ArrayList;

import de.greenrobot.dao.LazyList;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.dao.test.TestEntity;
import de.greenrobot.dao.test.TestEntityDao;

public abstract class TestEntityTestBase extends AbstractDaoTest<TestEntityDao, TestEntity, Long> {

    public TestEntityTestBase() {
        super(TestEntityDao.class);
    }

    protected TestEntity createEntity(int simpleInteger, String simpleString) {
        TestEntity entity = new TestEntity();
        entity.setId(null);
        entity.setSimpleStringNotNull("green");
        entity.setSimpleInteger(simpleInteger);
        entity.setSimpleString(simpleString);
        return entity;
    }

    protected ArrayList<TestEntity> insert(int count) {
        ArrayList<TestEntity> list = new ArrayList<TestEntity>();
        for (int i = 0; i < count; i++) {
            TestEntity entity = createEntity(getSimpleInteger(i), getSimpleString(i));
            list.add(entity);
        }
        dao.insertInTx(list);
        return list;
    }

    protected String getSimpleString(int i) {
        return "String" + (i + 100);
    }

    protected int getSimpleInteger(int i) {
        return 100 + i;
    }

    protected void assertIds(ArrayList<TestEntity> list, LazyList<TestEntity> list2) {
        for (int i = 0; i < list.size(); i++) {
            TestEntity entity = list.get(i);
            TestEntity lazyEntity = list2.get(i);
            assertIds(entity, lazyEntity);
        }
    }

    protected void assertIds(TestEntity entity, TestEntity entity2) {
        assertEquals(entity.getId(), entity2.getId());
    }

}
