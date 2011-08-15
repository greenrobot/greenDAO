package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import de.greenrobot.dao.LazyList;
import de.greenrobot.dao.test.TestEntityDao.Properties;

public class LazyListTest extends AbstractDaoTest<TestEntityDao, TestEntity, Long> {

    public LazyListTest() {
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
            TestEntity entity = createEntity(100 + i, "String" + (i + 100));
            list.add(entity);
        }
        dao.insertInTx(list);
        return list;
    }

    public void testSizeAndGetAndPeak() {
        ArrayList<TestEntity> list = insert(2);

        LazyList<TestEntity> listLazy = dao.queryBuilder().build().listLazy();
        assertEquals(list.size(), listLazy.size());
        assertNull(listLazy.peak(0));
        assertNull(listLazy.peak(1));

        assertNotNull(listLazy.get(1));
        assertNull(listLazy.peak(0));
        assertNotNull(listLazy.peak(1));

        assertNotNull(listLazy.get(0));
        assertNotNull(listLazy.peak(0));
        assertNotNull(listLazy.peak(1));
    }

    public void testGetAll100() {
        ArrayList<TestEntity> list = insert(100);

        LazyList<TestEntity> listLazy = dao.queryBuilder().orderAsc(Properties.SimpleInteger).build().listLazy();
        for (int i = 0; i < list.size(); i++) {
            TestEntity entity = list.get(i);
            TestEntity lazyEntity = listLazy.get(i);
            assertEquals(entity.getId(), lazyEntity.getId());
        }
        assertTrue(listLazy.isClosed());
    }

    public void testEmpty() {
        insert(1);

        LazyList<TestEntity> listLazy = dao.queryBuilder().eq(Properties.SimpleInteger, -1).build().listLazy();
        assertTrue(listLazy.isEmpty());
        assertTrue(listLazy.isClosed());
        try {
            listLazy.get(0);
            fail("Not empty");
        } catch (RuntimeException e) {
            // Expected, OK
        }

    }
}
