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

import android.database.Cursor;
import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.daotest.TestEntityDao.Properties;
import org.greenrobot.greendao.daotest.entity.TestEntityTestBase;

// TODO more tests
public class CursorQueryTest extends TestEntityTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public void testCursorQuerySimple() {
        insert(3);
        CursorQuery query = dao.queryBuilder().orderAsc(Properties.SimpleInteger).buildCursor();
        Cursor cursor = query.query();
        try {
            assertEquals(3, cursor.getCount());
            assertTrue(cursor.moveToNext());
            int columnIndex = cursor.getColumnIndexOrThrow(Properties.SimpleInteger.columnName);
            assertEquals(getSimpleInteger(0), cursor.getInt(columnIndex));
            assertTrue(cursor.moveToNext());
            assertEquals(getSimpleInteger(1), cursor.getInt(columnIndex));
            assertTrue(cursor.moveToNext());
            assertEquals(getSimpleInteger(2), cursor.getInt(columnIndex));
            assertFalse(cursor.moveToNext());
        } finally {
            cursor.close();
        }
    }

}
