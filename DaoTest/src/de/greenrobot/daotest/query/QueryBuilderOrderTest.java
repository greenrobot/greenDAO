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

import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.daotest.TestEntity;
import de.greenrobot.daotest.TestEntityDao.Properties;
import de.greenrobot.daotest.entity.TestEntityTestBase;

public class QueryBuilderOrderTest extends TestEntityTestBase {
    @Override
    protected void setUp() {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
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
