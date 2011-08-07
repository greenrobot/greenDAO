package de.greenrobot.testdao;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;

import de.greenrobot.testdao.SimpleEntityDao;
import de.greenrobot.testdao.SimpleEntityNotNullDao;
import de.greenrobot.testdao.TestEntityDao;
import de.greenrobot.testdao.RelationEntityDao;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        SimpleEntityDao.createTable(db, ifNotExists);
        SimpleEntityNotNullDao.createTable(db, ifNotExists);
        TestEntityDao.createTable(db, ifNotExists);
        RelationEntityDao.createTable(db, ifNotExists);
    }

    private final SimpleEntityDao simpleEntityDao;
    private final SimpleEntityNotNullDao simpleEntityNotNullDao;
    private final TestEntityDao testEntityDao;
    private final RelationEntityDao relationEntityDao;

    public DaoMaster(SQLiteDatabase db) {
        super(db);

        simpleEntityDao = new SimpleEntityDao(db);
        simpleEntityNotNullDao = new SimpleEntityNotNullDao(db);
        testEntityDao = new TestEntityDao(db);
        relationEntityDao = new RelationEntityDao(db, this);

        registerDao(SimpleEntity.class, simpleEntityDao);
        registerDao(SimpleEntityNotNull.class, simpleEntityNotNullDao);
        registerDao(TestEntity.class, testEntityDao);
        registerDao(RelationEntity.class, relationEntityDao);
    }
    
    public SimpleEntityDao getSimpleEntityDao() {
        return simpleEntityDao;
    }

    public SimpleEntityNotNullDao getSimpleEntityNotNullDao() {
        return simpleEntityNotNullDao;
    }

    public TestEntityDao getTestEntityDao() {
        return testEntityDao;
    }

    public RelationEntityDao getRelationEntityDao() {
        return relationEntityDao;
    }

}
