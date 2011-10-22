package de.greenrobot.daotest.entity;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.greenrobot.daotest.AnActiveEntity;
import de.greenrobot.daotest.AnActiveEntityDao;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.ToManyEntityDao;
import de.greenrobot.daotest.ToManyTargetEntityDao;

public class AnActiveEntityTest extends  AbstractDaoSessionTest<DaoMaster, DaoSession> {


    private AnActiveEntityDao dao;

    public AnActiveEntityTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        dao = daoSession.getAnActiveEntityDao();
    }

    public void testThrowWhenDetached() {
        AnActiveEntity entity = new AnActiveEntity();
        try {
            entity.delete();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
        try {
            entity.refresh();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
        try {
            entity.update();
            fail("Should fail for detached entity");
        } catch (DaoException e) {
            // OK, expected
        }
    }
    
    public void testActiveUpdate() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        long rowId = dao.insert(entity);
        
        entity.setText("NEW");
        entity.update();
        
        daoSession.clear();
        AnActiveEntity entity2 = dao.load(rowId);
        assertNotSame(entity, entity2);
        assertEquals("NEW", entity2.getText());
    }


    public void testActiveRefresh() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        dao.insert(entity);
        
        AnActiveEntity entity2 = new AnActiveEntity(1l);
        entity2.setText("NEW");
        dao.update(entity2);
        
        entity.refresh();
        assertEquals("NEW", entity.getText());
    }

    public void testActiveDelete() {
        AnActiveEntity entity = new AnActiveEntity(1l);
        dao.insert(entity);
        
        entity.delete();
        assertNull( dao.load(1l));
    }

}
