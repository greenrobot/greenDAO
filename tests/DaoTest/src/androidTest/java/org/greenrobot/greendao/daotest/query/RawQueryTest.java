/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.daotest.query;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao.Properties;
import org.greenrobot.greendao.daotest.entity.TestEntityTestBase;

public class RawQueryTest extends TestEntityTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testRawQueryEmptySql() {
        insert(3);
        Query<TestEntity> query = dao.queryRawCreate("");
        List<TestEntity> result = query.list();
        assertEquals(3, result.size());
    }

    public void testRawQueryEqualsString() {
        ArrayList<TestEntity> inserted = insert(3);
        String value = getSimpleString(1);

        String sql = "WHERE " + Properties.SimpleString.columnName + "=?";
        List<TestEntity> result = dao.queryRawCreate(sql, value).list();
        assertEquals(1, result.size());

        TestEntity resultEntity = result.get(0);
        assertEquals(value, resultEntity.getSimpleString());
        assertEquals(inserted.get(1).getId(), resultEntity.getId());
    }

    public void testRawQueryCreate_setParameterInQuery() {
        insert(3);
        String value = getSimpleString(2);

        String sql = "WHERE " + Properties.SimpleString.columnName + "=?";
        Query<TestEntity> query = dao.queryRawCreate(sql, getSimpleString(1));
        query.list();

        query.setParameter(0, value);
        List<TestEntity> result = query.list();

        assertEquals(1, result.size());
        assertEquals(value, result.get(0).getSimpleString());
    }
    
    public void testRawQueryLazyList() {
        ArrayList<TestEntity> list = insert(2);

        LazyList<TestEntity> listLazy = dao.queryRawCreate("").listLazy();
        assertEquals(list.size(), listLazy.size());
        assertNull(listLazy.peek(0));
        assertNull(listLazy.peek(1));

        assertNotNull(listLazy.get(1));
        assertNull(listLazy.peek(0));
        assertNotNull(listLazy.peek(1));

        assertNotNull(listLazy.get(0));
        assertNotNull(listLazy.peek(0));
        assertNotNull(listLazy.peek(1));
    }

}
