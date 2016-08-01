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

package org.greenrobot.greendao.daotest.entity;

import java.util.List;

import org.greenrobot.greendao.DaoLog;
import org.greenrobot.greendao.test.AbstractDaoTest;
import org.greenrobot.greendao.daotest.SqliteMaster;
import org.greenrobot.greendao.daotest.SqliteMasterDao;
import org.greenrobot.greendao.daotest.SqliteMasterDao.Properties;

public class SqliteMasterTest extends AbstractDaoTest<SqliteMasterDao, SqliteMaster, Void> {

    public SqliteMasterTest() {
        super(SqliteMasterDao.class);
    }

    public void testLoadAll() {
        List<SqliteMaster> all = dao.loadAll();
        for (SqliteMaster meta : all) {
            DaoLog.v(meta.toString());
        }
    }

    public void testQueryRaw() {
        List<SqliteMaster> tables = dao.queryRaw("WHERE " + Properties.Type.columnName + "=?", "table");
        for (SqliteMaster table : tables) {
            DaoLog.v(table.toString());
        }
    }

}
