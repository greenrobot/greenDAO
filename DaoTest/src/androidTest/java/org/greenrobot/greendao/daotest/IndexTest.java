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

package org.greenrobot.greendao.daotest;

import java.util.List;

import junit.framework.Assert;
import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.daotest.SqliteMaster;
import org.greenrobot.greendao.daotest.SqliteMasterDao;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.test.AbstractDaoTest;
import org.greenrobot.greendao.daotest.SqliteMasterDao.Properties;

public class IndexTest extends AbstractDaoTest<SqliteMasterDao, SqliteMaster, Void> {

    public IndexTest() {
        super(SqliteMasterDao.class);
    }

    public void testIndexesCreated() {
        Assert.assertEquals(0, getIndexes().size());

        TestEntityDao.createTable(db, false);
        List<SqliteMaster> indexes = getIndexes();
        Assert.assertEquals(2, indexes.size());

        SqliteMaster index1 = indexes.get(0);
        SqliteMaster index2 = indexes.get(1);
        Assert.assertEquals(TestEntityDao.TABLENAME, index1.getTableName());
        Assert.assertEquals(TestEntityDao.TABLENAME, index2.getTableName());
        Assert.assertEquals("IDX_TEST_ENTITY_INDEXED_STRING", index1.getName());
        Assert.assertEquals("IDX_TEST_ENTITY_INDEXED_STRING_ASC_UNIQUE", index2.getName());

        for (SqliteMaster index : indexes) {
            DaoLog.v(index.toString());
        }
    }
    
    public void testIndexCreateIfNotExists() {
        Assert.assertEquals(0, getIndexes().size());
        TestEntityDao.createTable(db, false);
        Assert.assertEquals(2, getIndexes().size());
        TestEntityDao.createTable(db, true);
        Assert.assertEquals(2, getIndexes().size());
    }

    private List<SqliteMaster> getIndexes() {
        String where = "WHERE " + Properties.Type.columnName + "=? ORDER BY " + Properties.Name.columnName;
        List<SqliteMaster> indexes = dao.queryRaw(where, "index");
        return indexes;
    }

}
