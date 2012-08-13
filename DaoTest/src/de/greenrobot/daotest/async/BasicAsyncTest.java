package de.greenrobot.daotest.async;

import android.app.Application;
import de.greenrobot.dao.AsyncSession;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;
import de.greenrobot.daotest.SimpleEntity;

public class BasicAsyncTest extends AbstractDaoSessionTest<Application, DaoMaster, DaoSession> {

    private AsyncSession asyncSession;

    public BasicAsyncTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() {
        super.setUp();
        asyncSession = daoSession.startAsyncSession();
    }

    public void testWaitForCompletionNoOps() {
        assertTrue(asyncSession.isCompleted());
        assertTrue(asyncSession.waitForCompletion(1));
        asyncSession.waitForCompletion();
    }

    public void testAsyncInsert() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("heho");
        asyncSession.insert(entity);
        assertTrue(asyncSession.waitForCompletion(1000));
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotNull(entity2);
        assertEquals("heho", entity2.getSimpleString());
    }

}
