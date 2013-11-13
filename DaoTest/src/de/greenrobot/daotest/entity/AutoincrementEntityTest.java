package de.greenrobot.daotest.entity;

import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.AutoincrementEntity;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.SimpleEntity;

public class AutoincrementEntityTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> {

    public AutoincrementEntityTest() {
        super(DaoMaster.class);
    }

    public void testAutoincrement() {
        AutoincrementEntity entity = new AutoincrementEntity();
        daoSession.insert(entity);
        Long id1 = entity.getId();
        assertNotNull(id1);
        daoSession.delete(entity);

        AutoincrementEntity entity2 = new AutoincrementEntity();
        daoSession.insert(entity2);
        assertEquals(id1 + 1, (long) entity2.getId());
    }

    public void testNoAutoincrement() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        Long id1 = entity.getId();
        assertNotNull(id1);
        daoSession.delete(entity);

        SimpleEntity entity2 = new SimpleEntity();
        daoSession.insert(entity2);
        assertEquals(id1, entity2.getId());
    }

}
