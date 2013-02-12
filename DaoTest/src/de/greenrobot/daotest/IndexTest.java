package de.greenrobot.daotest;

import java.util.List;

import junit.framework.Assert;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.test.AbstractDaoTest;
import de.greenrobot.daotest.SqliteMasterDao.Properties;

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
