/*
 * Copyright (C) 2011-2013 Markus Junginger, greenrobot (http://greenrobot.de)
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

import de.greenrobot.dao.Query;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryThreadLocalTest extends TestEntityTestBase {

    public void testGetForCurrentThread_SameInstance() {
        Query<TestEntity> query = dao.queryBuilder().build();
        assertSame(query, query.forCurrentThread());
    }

    public void testGetForCurrentThread_ParametersAreReset() {
        insert(3);
        int value = getSimpleInteger(1);
        Query<TestEntity> query = dao.queryBuilder().where(Properties.SimpleInteger.eq(value)).build();
        query.setParameter(0, value + 1);
        TestEntity entityFor2 = query.unique();
        assertEquals(value + 1, (int) entityFor2.getSimpleInteger());
        query = query.forCurrentThread();
        TestEntity entityFor1 = query.unique();
        assertEquals(value, (int) entityFor1.getSimpleInteger());
    }

    // TODO more tests
}
