package org.greenrobot.greendao.daotest.entity;

import org.greenrobot.greendao.daotest.TestEntity;
import org.greenrobot.greendao.daotest.TestEntityDao;
import org.greenrobot.greendao.daotest.TestEntityDao.Properties;

import java.util.ArrayList;

public class TransactionTest extends TestEntityTestBase {
    public void testUpdateTxFailed() {
        String sql = "CREATE UNIQUE INDEX test_simple_string_unique ON " + TestEntityDao.TABLENAME + "(" +
                Properties.SimpleString.columnName + ")";
        dao.getDatabase().execSQL(sql);
        ArrayList<TestEntity> entities = insert(2);
        TestEntity entity1 = entities.get(0);
        String valueBeforeUpdate = entity1.getSimpleString();

        entity1.setSimpleString("unique");
        entities.get(1).setSimpleString("unique");
        try {
            dao.updateInTx(entities);
            fail("Should have thrown");
        } catch (RuntimeException e) {
            // OK
        }
        dao.refresh(entity1);
        assertEquals(valueBeforeUpdate, entity1.getSimpleString());
    }
}
