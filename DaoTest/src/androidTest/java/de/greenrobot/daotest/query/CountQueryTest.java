/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
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

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

// TODO more tests
public class CountQueryTest extends TestEntityTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testCountQuerySimple() {
        int value = getSimpleInteger(1);
        CountQuery<TestEntity> query = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).buildCount();
        assertEquals(0, query.count());

        ArrayList<TestEntity> inserted = insert(3);
        assertEquals(1, query.count());

        inserted.get(2).setSimpleInteger(value);
        dao.update(inserted.get(2));
        assertEquals(2, query.count());

        dao.deleteAll();
        assertEquals(0, query.count());
    }

    public void testCountQueryTwoParameters() {
        int value = getSimpleInteger(1);
        String valueString = getSimpleString(1);
        
        QueryBuilder<TestEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.where(Properties.SimpleInteger.eq(value), Properties.SimpleString.eq(valueString));
        CountQuery<TestEntity> query = queryBuilder.buildCount();
        assertEquals(0, query.count());

        ArrayList<TestEntity> inserted = insert(3);
        assertEquals(1, query.count());

        inserted.get(2).setSimpleInteger(value);
        dao.update(inserted.get(2));
        assertEquals(1, query.count());
        
        inserted.get(2).setSimpleString(valueString);
        dao.update(inserted.get(2));
        assertEquals(2, query.count());

        dao.deleteAll();
        assertEquals(0, query.count());
    }

    public void testCountQueryTwoParametersOr() {
        int value = getSimpleInteger(1);
        String valueString = getSimpleString(2);
        
        QueryBuilder<TestEntity> queryBuilder = dao.queryBuilder();
        queryBuilder.whereOr(Properties.SimpleInteger.eq(value), Properties.SimpleString.eq(valueString));
        CountQuery<TestEntity> query = queryBuilder.buildCount();
        assertEquals(0, query.count());

        ArrayList<TestEntity> inserted = insert(3);
        assertEquals(2, query.count());

        inserted.get(1).setSimpleInteger(getSimpleInteger(2));
        dao.update(inserted.get(1));
        assertEquals(1, query.count());
        
        inserted.get(2).setSimpleString(getSimpleString(3));
        dao.update(inserted.get(2));
        assertEquals(0, query.count());
    }

    public void testCountQueryChangeParameter() {
        insert(3);

        String value = "not existing value";
        CountQuery<TestEntity> query = dao.queryBuilder().where(Properties.SimpleString.eq(value)).buildCount();
        assertEquals(0, query.count());
        query.setParameter(0, getSimpleString(1));
        assertEquals(1, query.count());
        query.setParameter(0, getSimpleString(2));
        assertEquals(1, query.count());
        query.setParameter(0, "you won't find me either");
        assertEquals(0, query.count());
    }

    public void testBuildQueryAndCountQuery() {
        insert(3);
        int value = getSimpleInteger(1);

        QueryBuilder<TestEntity> builder = dao.queryBuilder().where(Properties.SimpleInteger.eq(value));
        Query<TestEntity> query = builder.build();
        CountQuery<TestEntity> countQuery = builder.buildCount();

        assertEquals(1, query.list().size());
        assertEquals(1, countQuery.count());
    }

}
