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
package de.greenrobot.daotest.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryBuilderOrderTest extends TestEntityTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testOrderAsc() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity entity = inserted.get(0);
        List<TestEntity> result = dao.queryBuilder().orderAsc(Properties.SimpleInteger).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
        result = dao.queryBuilder().orderAsc(Properties.SimpleInteger, Properties.SimpleString).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    public void testOrderDesc() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity entity = inserted.get(1);
        List<TestEntity> result = dao.queryBuilder().orderDesc(Properties.SimpleInteger).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
        result = dao.queryBuilder().orderDesc(Properties.SimpleInteger, Properties.SimpleString).list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    public void testOrderUpperLowercase() {
        List<TestEntity> list = new ArrayList<TestEntity>();
        TestEntity entityAA = addEntity(list, "aa");
        TestEntity entityAB = addEntity(list, "Ab");
        TestEntity entityAC = addEntity(list, "ac");
        TestEntity entityZA = addEntity(list, "ZA");
        TestEntity entityZB = addEntity(list, "zB");
        TestEntity entityZC = addEntity(list, "ZC");
        Collections.shuffle(list);
        dao.insertInTx(list);

        List<TestEntity> result = dao.queryBuilder().orderAsc(Properties.SimpleString).list();
        assertEquals(list.size(), result.size());
        assertEquals(entityAA.getId(), result.get(0).getId());
        assertEquals(entityAB.getId(), result.get(1).getId());
        assertEquals(entityAC.getId(), result.get(2).getId());
        assertEquals(entityZA.getId(), result.get(3).getId());
        assertEquals(entityZB.getId(), result.get(4).getId());
        assertEquals(entityZC.getId(), result.get(5).getId());
    }

    public void testOrderUmlauts() {
        List<TestEntity> list = new ArrayList<TestEntity>();
        TestEntity entityV = addEntity(list, "V");
        TestEntity entityB = addEntity(list, "B");
        TestEntity entityUE = addEntity(list, "Ü");
        TestEntity entityAE = addEntity(list, "Ä");
        dao.insertInTx(list);

        List<TestEntity> result = dao.queryBuilder().orderAsc(Properties.SimpleString).list();
        assertEquals(list.size(), result.size());
        assertEquals(entityAE.getId(), result.get(0).getId());
        assertEquals(entityB.getId(), result.get(1).getId());
        assertEquals(entityUE.getId(), result.get(2).getId());
        assertEquals(entityV.getId(), result.get(3).getId());
    }

    public void testOrderCustom() {
        List<TestEntity> list = new ArrayList<TestEntity>();
        TestEntity entityAA = addEntity(list, "Aa");
        TestEntity entityAB = addEntity(list, "ab");
        TestEntity entityAC = addEntity(list, "Ac");
        dao.insertInTx(list);

        List<TestEntity> result = dao.queryBuilder().orderCustom(Properties.SimpleString, "ASC").list();
        assertEquals(list.size(), result.size());
        assertEquals(entityAA.getId(), result.get(0).getId());
        assertEquals(entityAC.getId(), result.get(1).getId());
        assertEquals(entityAB.getId(), result.get(2).getId());
    }

    public void testOrderRaw() {
        ArrayList<TestEntity> inserted = insert(2);
        TestEntity entity = inserted.get(0);
        List<TestEntity> result = dao.queryBuilder().orderRaw(Properties.SimpleInteger.columnName + " ASC").list();
        assertEquals(2, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    private TestEntity addEntity(List<TestEntity> list, String simpleString) {
        TestEntity entityAB = createEntity(42, simpleString);
        list.add(entityAB);
        return entityAB;
    }

}
