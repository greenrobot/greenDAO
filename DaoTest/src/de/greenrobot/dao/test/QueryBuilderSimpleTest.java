package de.greenrobot.dao.test;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.test.TestEntityDao.Properties;

public class QueryBuilderSimpleTest extends TestEntityTestBase {
    @Override
    protected void setUp() {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testEqInteger() {
        ArrayList<TestEntity> inserted = insert(3);
        int value = getSimpleInteger(1);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).build().list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(value, (int) resultEntity.getSimpleInteger());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testEqString() {
        ArrayList<TestEntity> inserted = insert(3);
        String value = getSimpleString(1);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleString.eq(value)).build().list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(value, resultEntity.getSimpleString());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testEqStringAndInteger() {
        ArrayList<TestEntity> inserted = insert(3);
        String valueStr = getSimpleString(1);
        int valueInt = getSimpleInteger(1);

        List<TestEntity> result = dao.queryBuilder()
                .where(Properties.SimpleString.eq(valueStr), Properties.SimpleInteger.eq(valueInt)).build().list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testNotEqString() {
        ArrayList<TestEntity> inserted = insert(3);
        String value = getSimpleString(1);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleString.notEq(value)).build().list();
        assertEquals(2, result.size());

        TestEntity resultEntity1 = result.get(0);
        TestEntity resultEntity2 = result.get(1);
        long loId = Math.min(resultEntity1.getId(), resultEntity2.getId());
        long hiId = Math.max(resultEntity1.getId(), resultEntity2.getId());
        assertEquals((long) inserted.get(0).getId(), loId);
        assertEquals((long) inserted.get(2).getId(), hiId);
    }

}
