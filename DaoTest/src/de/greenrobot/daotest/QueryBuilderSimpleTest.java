/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daotest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

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

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(value, (int) resultEntity.getSimpleInteger());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testEqString() {
        ArrayList<TestEntity> inserted = insert(3);
        String value = getSimpleString(1);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleString.eq(value)).list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(value, resultEntity.getSimpleString());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testIn() {
        ArrayList<TestEntity> inserted = insert(10);
        String value1 = getSimpleString(2);
        String value2 = getSimpleString(8);
        String value3 = getSimpleString(9);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleString.in(value1, value2, value3))
                .orderAsc(Properties.SimpleString).list();
        assertEquals(3, result.size());

        TestEntity resultEntity1 = result.get(0);
        assertEquals(value1, resultEntity1.getSimpleString());
        assertEquals(inserted.get(2).getId(), resultEntity1.getId());

        TestEntity resultEntity2 = result.get(1);
        assertEquals(value2, resultEntity2.getSimpleString());
        assertEquals(inserted.get(8).getId(), resultEntity2.getId());

        TestEntity resultEntity3 = result.get(2);
        assertEquals(value3, resultEntity3.getSimpleString());
        assertEquals(inserted.get(9).getId(), resultEntity3.getId());

    }

    public void testEqStringAndInteger() {
        ArrayList<TestEntity> inserted = insert(3);
        String valueStr = getSimpleString(1);
        int valueInt = getSimpleInteger(1);

        List<TestEntity> result = dao.queryBuilder()
                .where(Properties.SimpleString.eq(valueStr), Properties.SimpleInteger.eq(valueInt)).list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testNotEqString() {
        ArrayList<TestEntity> inserted = insert(3);
        String value = getSimpleString(1);

        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleString.notEq(value)).list();
        assertEquals(2, result.size());

        TestEntity resultEntity1 = result.get(0);
        TestEntity resultEntity2 = result.get(1);
        long loId = Math.min(resultEntity1.getId(), resultEntity2.getId());
        long hiId = Math.max(resultEntity1.getId(), resultEntity2.getId());
        assertEquals((long) inserted.get(0).getId(), loId);
        assertEquals((long) inserted.get(2).getId(), hiId);
    }

    public void testEqDate() {
        ArrayList<TestEntity> inserted = insert(3);
        TestEntity testEntity = inserted.get(1);

        Date date = new Date();
        testEntity.setSimpleDate(date);
        dao.update(testEntity);

        TestEntity testEntity2 = dao.queryBuilder().where(Properties.SimpleDate.eq(date)).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());

        testEntity2 = dao.queryBuilder().where(Properties.SimpleDate.eq(date.getTime())).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());
    }

    public void testEqBoolean() {
        ArrayList<TestEntity> inserted = insert(3);
        TestEntity testEntity = inserted.get(1);

        testEntity.setSimpleBoolean(true);
        dao.update(testEntity);

        TestEntity testEntity2 = dao.queryBuilder().where(Properties.SimpleBoolean.eq(true)).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());

        testEntity2 = dao.queryBuilder().where(Properties.SimpleBoolean.eq(Boolean.TRUE)).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());

        testEntity2 = dao.queryBuilder().where(Properties.SimpleBoolean.eq("TRUE")).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());

        testEntity2 = dao.queryBuilder().where(Properties.SimpleBoolean.eq("truE")).uniqueOrThrow();
        assertEquals(testEntity.getId(), testEntity2.getId());
    }

    public void testIsNullIsNotNull() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity testEntityNull = inserted.get(0);
        TestEntity testEntityNotNull = inserted.get(1);

        testEntityNull.setSimpleInteger(null);
        testEntityNotNull.setSimpleInteger(42);
        dao.update(testEntityNull);
        dao.update(testEntityNotNull);
        
        TestEntity testEntityNull2 = dao.queryBuilder().where(Properties.SimpleInteger.isNull()).uniqueOrThrow();
        assertEquals(testEntityNull.getId(), testEntityNull2.getId());
        
        TestEntity testEntityNotNull2 = dao.queryBuilder().where(Properties.SimpleInteger.isNotNull()).uniqueOrThrow();
        assertEquals(testEntityNotNull.getId(), testEntityNotNull2.getId());
    }

    public void testOrderAsc() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity entity = inserted.get(0);
        List<TestEntity> result = dao.queryBuilder().orderAsc(Properties.SimpleInteger).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0). getId());
        result = dao.queryBuilder().orderAsc(Properties.SimpleInteger, Properties.SimpleString).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0). getId());
    }

    public void testOrderDesc() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity entity = inserted.get(1);
        List<TestEntity> result = dao.queryBuilder().orderDesc(Properties.SimpleInteger).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0). getId());
        result = dao.queryBuilder().orderDesc(Properties.SimpleInteger, Properties.SimpleString).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0). getId());
    }

}
