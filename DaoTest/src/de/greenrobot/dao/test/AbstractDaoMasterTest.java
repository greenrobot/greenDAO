package de.greenrobot.dao.test;

import java.util.Random;

import junit.framework.TestCase;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.testdao.SimpleEntity;
import de.greenrobot.testdao.SimpleEntityDao;

public  class AbstractDaoMasterTest extends TestCase {

    protected SQLiteDatabase db;
    protected Random random;
    private AbstractDaoMaster daoMaster;

    public AbstractDaoMasterTest() {
        random = new Random();
    }

    @Override
    protected void setUp() {
        db = SQLiteDatabase.create(null);
        AbstractDaoMaster.unregisterAllDaos();
        AbstractDaoMaster.registerDao(SimpleEntity.class, SimpleEntityDao.class);
        AbstractDaoMaster.createEntityTables(db);
        daoMaster = new AbstractDaoMaster(db);
    }

    @Override
    protected void tearDown() throws Exception {
        db.close();
    }

    public void testInsertAndLoad() {
        SimpleEntity entity = new SimpleEntity();
        daoMaster.insert(entity);
         Long id = entity.getId();
         assertNotNull(id);
        SimpleEntity entity2 = daoMaster.load(SimpleEntity.class, id);
        assertNotNull(entity2);
        assertNotSame(entity, entity2); // Unless we'll cache stuff one day
    }

}