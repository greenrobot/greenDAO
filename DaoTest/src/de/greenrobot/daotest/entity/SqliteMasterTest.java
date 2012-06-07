package de.greenrobot.daotest.entity;

import java.util.List;

import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.SqliteMaster;
import de.greenrobot.daotest.SqliteMasterDao;
import de.greenrobot.daotest.SqliteMasterDao.Properties;

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
