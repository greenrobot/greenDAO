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
