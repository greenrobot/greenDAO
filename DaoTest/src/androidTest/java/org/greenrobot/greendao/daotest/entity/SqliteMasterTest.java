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
