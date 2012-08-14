package de.greenrobot.daotest.async;

import java.util.concurrent.Callable;

import de.greenrobot.dao.AsyncOperation;
import de.greenrobot.daotest.SimpleEntity;

public class BasicAsyncTest extends AbstractAsyncTest {

    Thread txThread;

    public void testWaitForCompletionNoOps() {
        assertTrue(asyncSession.isCompleted());
        assertTrue(asyncSession.waitForCompletion(1));
        asyncSession.waitForCompletion();
    }

    public void testAsyncInsert() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("heho");
        AsyncOperation operation = asyncSession.insert(entity);
        assertWaitForCompletion1Sec();
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotNull(entity2);
        assertEquals("heho", entity2.getSimpleString());
        assertFalse(operation.isFailed());
        assertSingleOperationCompleted(operation);
    }

    public void testAsyncUpdate() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("heho");
        daoSession.insert(entity);
        entity.setSimpleString("updated");
        AsyncOperation operation = asyncSession.update(entity);
        assertWaitForCompletion1Sec();
        daoSession.clear();
        SimpleEntity entity2 = daoSession.load(SimpleEntity.class, entity.getId());
        assertNotNull(entity2);
        assertEquals("updated", entity2.getSimpleString());
        assertFalse(operation.isFailed());
        assertSingleOperationCompleted(operation);
    }

    public void testAsyncException() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        AsyncOperation operation = asyncSession.insert(entity);
        assertWaitForCompletion1Sec();
        assertSingleOperationCompleted(operation);

        assertTrue(operation.isFailed());
        assertNotNull(operation.getThrowable());
    }

    public void testAsyncOperationWaitMillis() {
        AsyncOperation operation = asyncSession.insert(new SimpleEntity());
        assertTrue(asyncSession.waitForCompletion(1000));
        assertSingleOperationCompleted(operation);
    }

    public void testAsyncOperationWait() {
        AsyncOperation operation = asyncSession.insert(new SimpleEntity());
        asyncSession.waitForCompletion();
        assertSingleOperationCompleted(operation);
    }

    public void testAsyncRunInTx() {
        AsyncOperation operation = asyncSession.runInTx(new Runnable() {

            @Override
            public void run() {
                txThread = Thread.currentThread();
            }
        });
        assertWaitForCompletion1Sec();
        assertSingleOperationCompleted(operation);
        assertNotNull(txThread);
        assertFalse(Thread.currentThread().equals(txThread));
    }

    public void testAsynCallInTx() {
        AsyncOperation operation = asyncSession.callInTx(new Callable<String>() {

            @Override
            public String call() throws Exception {
                txThread = Thread.currentThread();
                return "OK";
            }
        });
        assertEquals("OK", operation.waitForCompletion());
        assertNotNull(txThread);
        assertFalse(Thread.currentThread().equals(txThread));
    }

}
