package de.greenrobot.dao.test;

import java.util.List;

import de.greenrobot.dao.test.TestEntityDao.Properties;

public class SimpleQueryBuilderTest extends AbstractDaoTest<TestEntityDao, TestEntity, Long> {

    public SimpleQueryBuilderTest() {
        super(TestEntityDao.class);
    }

    protected TestEntity createEntity(Long key) {
        TestEntity entity = new TestEntity();
        entity.setId(key);
        entity.setSimpleStringNotNull("green");
        return entity;
    }

    protected TestEntity insert(int simpleInteger, String simpleString) {
        TestEntity entity = createEntity(null);
        entity.setSimpleInteger(simpleInteger);
        entity.setSimpleString(simpleString);
        dao.insert(entity);
        return entity;
    }

    public void testEq() {
        TestEntity entity = insert(41, "A");
        TestEntity entity1 = insert(42, "B");
        TestEntity entity2 = insert(43, "C");

        List<TestEntity> result = dao.queryBuilder().eq(Properties.SimpleInteger, 42).build().list();
        TestEntity resultEntity = result.get(0);

        assertEquals(42, (int) resultEntity.getSimpleInteger());
        assertEquals(entity1.getId(), resultEntity.getId());
    }

}
