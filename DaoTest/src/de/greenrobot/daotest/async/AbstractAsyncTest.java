package de.greenrobot.daotest.async;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.test.AbstractDaoSessionTest;
import de.greenrobot.daotest.DaoMaster;
import de.greenrobot.daotest.DaoSession;

public abstract class AbstractAsyncTest extends AbstractDaoSessionTest<DaoMaster, DaoSession> implements
        AsyncOperationListener {

    protected AsyncSession asyncSession;
    protected List<AsyncOperation> completedOperations;

    public AbstractAsyncTest() {
        super(DaoMaster.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    public void assertWaitForCompletion1Sec() {
        assertTrue(asyncSession.waitForCompletion(1000));
        assertTrue(asyncSession.isCompleted());
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    protected void assertSingleOperationCompleted(AsyncOperation operation) {
        assertSame(operation, completedOperations.get(0));
        assertEquals(1, completedOperations.size());
        assertTrue(operation.isCompleted());
    }

}
