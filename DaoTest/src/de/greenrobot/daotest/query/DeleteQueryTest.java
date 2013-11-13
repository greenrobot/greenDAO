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
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

// TODO more tests
public class DeleteQueryTest extends TestEntityTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testDeleteQuerySimple() {
        ArrayList<TestEntity> inserted = insert(3);
        int value = getSimpleInteger(1);
        inserted.get(2).setSimpleInteger(value);
        dao.update(inserted.get(2));

        DeleteQuery<TestEntity> deleteQuery = dao.queryBuilder().where(Properties.SimpleInteger.eq(value))
                .buildDelete();

        deleteQuery.executeDeleteWithoutDetachingEntities();

        List<TestEntity> allAfterDelete = dao.loadAll();
        assertEquals(1, allAfterDelete.size());
        assertEquals(getSimpleInteger(0), (int) allAfterDelete.get(0).getSimpleInteger());
    }

    public void testDeleteQueryOr() {
        ArrayList<TestEntity> inserted = insert(3);

        QueryBuilder<TestEntity> queryBuilder = dao.queryBuilder();
        Integer value1 = inserted.get(0).getSimpleInteger();
        Integer value2 = inserted.get(2).getSimpleInteger();
        queryBuilder.whereOr(Properties.SimpleInteger.eq(value1), Properties.SimpleInteger.eq(value2));
        DeleteQuery<TestEntity> deleteQuery = queryBuilder.buildDelete();

        deleteQuery.executeDeleteWithoutDetachingEntities();

        List<TestEntity> allAfterDelete = dao.loadAll();
        assertEquals(1, allAfterDelete.size());
        assertEquals(inserted.get(1).getSimpleInteger(), allAfterDelete.get(0).getSimpleInteger());
    }

    public void testDeleteQueryExecutingMultipleTimes() {
        insert(3);

        String value = getSimpleString(1);
        DeleteQuery<TestEntity> deleteQuery = dao.queryBuilder().where(Properties.SimpleString.eq(value)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(2, dao.count());

        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(2, dao.count());

        insert(3);
        assertEquals(5, dao.count());

        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(4, dao.count());
    }

    public void testDeleteQueryChangeParameter() {
        insert(3);

        String value = getSimpleString(1);
        DeleteQuery<TestEntity> deleteQuery = dao.queryBuilder().where(Properties.SimpleString.eq(value)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(2, dao.count());

        deleteQuery.setParameter(0, getSimpleString(0));
        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(1, dao.count());

        TestEntity remaining = dao.loadAll().get(0);
        assertEquals(getSimpleString(2), remaining.getSimpleString());
    }

    public void testBuildQueryAndDeleteQuery() {
        insert(3);
        int value = getSimpleInteger(1);

        QueryBuilder<TestEntity> builder = dao.queryBuilder().where(Properties.SimpleInteger.eq(value));
        Query<TestEntity> query = builder.build();
        DeleteQuery<TestEntity> deleteQuery = builder.buildDelete();

        assertEquals(1, query.list().size());
        deleteQuery.executeDeleteWithoutDetachingEntities();
        assertEquals(0, query.list().size());
    }

}
