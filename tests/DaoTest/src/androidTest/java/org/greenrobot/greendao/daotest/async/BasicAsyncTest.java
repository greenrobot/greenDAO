/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.greenrobot.greendao.daotest.async;

import java.util.concurrent.Callable;

import android.os.Looper;
import org.greenrobot.greendao.async.AsyncDaoException;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.daotest.SimpleEntity;

public class BasicAsyncTest extends AbstractAsyncTest {

    Thread txThread;
    boolean testListenerMainThread_done;

    public void testSequenceNumber() {
        AsyncOperation op1 = asyncSession.count(SimpleEntity.class);
        assertEquals(1, op1.getSequenceNumber());
        AsyncOperation op2 = asyncSession.count(SimpleEntity.class);
        assertEquals(2, op2.getSequenceNumber());
    }

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

    public void testOperationGetResult() {
        SimpleEntity entity = new SimpleEntity();
        entity.setSimpleString("heho");
        daoSession.insert(entity);
        daoSession.clear();

        AsyncOperation operation = asyncSession.load(SimpleEntity.class, entity.getId());
        SimpleEntity result = (SimpleEntity) operation.getResult();
        assertTrue(operation.isCompleted());
        assertTrue(operation.isCompletedSucessfully());
        assertNotNull(result);
        assertNotSame(entity, result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getSimpleString(), result.getSimpleString());
    }

    public void testOperationGetResultException() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        AsyncOperation operation = asyncSession.insert(entity);
        try {
            operation.getResult();
            fail("getResult should have thrown");
        } catch (AsyncDaoException expected) {
            // OK
        }
        assertTrue(operation.isCompleted());
        assertFalse(operation.isCompletedSucessfully());
        assertTrue(operation.isFailed());
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

    public void testAsyncExceptionCreator() {
        SimpleEntity entity = new SimpleEntity();
        daoSession.insert(entity);
        AsyncOperation operation = asyncSession.insert(entity);
        assertWaitForCompletion1Sec();
        assertNull(operation.getCreatorStacktrace());

        operation = asyncSession.insert(entity, AsyncOperation.FLAG_TRACK_CREATOR_STACKTRACE);
        assertWaitForCompletion1Sec();
        assertNotNull(operation.getCreatorStacktrace());

        asyncSession.setSessionFlags(AsyncOperation.FLAG_TRACK_CREATOR_STACKTRACE);
        operation = asyncSession.insert(entity);
        assertWaitForCompletion1Sec();
        assertNotNull(operation.getCreatorStacktrace());
        StackTraceElement[] stack = operation.getCreatorStacktrace().getStackTrace();
        boolean found = false;
        for (StackTraceElement stackTraceElement : stack) {
            found |= stackTraceElement.getClassName().equals(getClass().getName());
        }
        assertTrue(found);
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

    public void testListenerMainThread() throws InterruptedException {
        AsyncOperationListener listener = new AsyncOperationListener() {
            @Override
            public synchronized void onAsyncOperationCompleted(AsyncOperation operation) {
                assertEquals(Looper.getMainLooper(), Looper.myLooper());
                testListenerMainThread_done = true;
                notifyAll();
            }
        };
        asyncSession.setListenerMainThread(listener);
        asyncSession.insert(new SimpleEntity());
        assertWaitForCompletion1Sec();
        while (!testListenerMainThread_done) {
            synchronized (listener) {
                listener.wait(1000);
            }
        }
    }


}
