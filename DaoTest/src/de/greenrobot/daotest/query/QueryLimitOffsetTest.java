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

import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryLimitOffsetTest extends TestEntityTestBase {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testQueryBuilderLimit() {
        insert(10);
        List<TestEntity> result = dao.queryBuilder().limit(3).orderAsc(Properties.SimpleInt).list();
        assertEquals(3, result.size());

        assertEquals(getSimpleInteger(0), result.get(0).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(1), result.get(1).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(2), result.get(2).getSimpleInteger().intValue());
    }

    public void testQueryBuilderOffsetAndLimit() {
        insert(10);
        List<TestEntity> result = dao.queryBuilder().offset(3).limit(3).orderAsc(Properties.SimpleInt).list();
        assertEquals(3, result.size());

        assertEquals(getSimpleInteger(3), result.get(0).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(4), result.get(1).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(5), result.get(2).getSimpleInteger().intValue());
    }

    public void testQueryBuilderOffsetAndLimitWithWhere() {
        insert(10);
        List<TestEntity> result = dao.queryBuilder().where(Properties.SimpleInteger.gt(getSimpleInteger(1))).offset(2)
                .limit(3).orderAsc(Properties.SimpleInt).list();
        assertEquals(3, result.size());

        assertEquals(getSimpleInteger(4), result.get(0).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(5), result.get(1).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(6), result.get(2).getSimpleInteger().intValue());
    }

    public void testQueryOffsetAndLimit() {
        insert(10);
        Query<TestEntity> query = dao.queryBuilder().where(Properties.SimpleInteger.gt(getSimpleInteger(-1))).offset(-1)
                .limit(-1).orderAsc(Properties.SimpleInt).build(); 
        query.setParameter(0, getSimpleInteger(1));
        query.setLimit(3);
        query.setOffset(2);
        List<TestEntity> result = query.list();
        assertEquals(3, result.size());

        assertEquals(getSimpleInteger(4), result.get(0).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(5), result.get(1).getSimpleInteger().intValue());
        assertEquals(getSimpleInteger(6), result.get(2).getSimpleInteger().intValue());
    }
    
    public void testQueryBuilderOffsetWithoutLimit() {
        try{
            dao.queryBuilder().offset(7).orderAsc(Properties.SimpleInt).build();
            fail("Offset may not be set alone");
        } catch(RuntimeException expected) {
            //OK
        }
    }
    
    public void testQueryLimitAndSetParameter() {
        Query<TestEntity> query = dao.queryBuilder().limit(5).offset(1).build();
        try{
            query.setParameter(0, null);
            fail("Offset/limit parameters must not interfere with user parameters");
        } catch(RuntimeException expected) {
            //OK
        }
    }
    
    public void testQueryUnsetLimit() {
        Query<TestEntity> query = dao.queryBuilder().build();
        try{
            query.setLimit(1);
            fail("Limit must be defined in builder first");
        } catch(RuntimeException expected) {
            //OK
        }
    } 

    public void testQueryUnsetOffset() {
        Query<TestEntity> query = dao.queryBuilder().limit(1).build();
        try{
            query.setOffset(1);
            fail("Offset must be defined in builder first");
        } catch(RuntimeException expected) {
            //OK
        }
    } 



}
