/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.dao;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.database.sqlite.SQLiteDatabase;

class AsyncOperationExecutor implements Runnable {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private final BlockingQueue<AsyncOperation> queue;
    private volatile boolean executorRunning;
    private volatile int maxOperationCountToMerge;
    private volatile AsyncOperationListener listener;

    private int countOperationsEnqueued;
    private int countOperationsCompleted;

    AsyncOperationExecutor() {
        queue = new LinkedBlockingQueue<AsyncOperation>();
        maxOperationCountToMerge = 50;
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            queue.add(operation);
            countOperationsEnqueued++;
            if (!executorRunning) {
                executorRunning = true;
                executorService.execute(this);
            }
        }
    }

    public int getMaxOperationCountToMerge() {
        return maxOperationCountToMerge;
    }

    public void setMaxOperationCountToMerge(int maxOperationCountToMerge) {
        this.maxOperationCountToMerge = maxOperationCountToMerge;
    }

    public AsyncOperationListener getListener() {
        return listener;
    }

    public void setListener(AsyncOperationListener listener) {
        this.listener = listener;
    }

    public synchronized boolean isCompleted() {
        return countOperationsEnqueued == countOperationsCompleted;
    }

    public synchronized void waitForCompletion() throws InterruptedException {
        while (!isCompleted()) {
            wait();
        }
    }

    public synchronized boolean waitForCompletion(int maxMillis) throws InterruptedException {
        if (!isCompleted()) {
            wait(maxMillis);
        }
        return isCompleted();
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    AsyncOperation operation = queue.poll(1, TimeUnit.SECONDS);
                    if (operation == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            operation = queue.poll();
                            if (operation == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    if (operation != null) {
                        if (operation.isMergeTx()) {
                            // Wait some ms for another operation to merge because a TX is expensive
                            AsyncOperation operation2 = queue.poll(50, TimeUnit.MILLISECONDS);
                            if (operation2 != null) {
                                if (operation.isMergeableWith(operation2)) {
                                    mergeTxAndExecute(operation, operation2);
                                } else {
                                    // Cannot merge, execute both
                                    executeOperationAndPostCompleted(operation);
                                    executeOperationAndPostCompleted(operation2);
                                }
                                continue;
                            }
                        }
                        executeOperationAndPostCompleted(operation);
                    }
                }
            } catch (InterruptedException e) {
                DaoLog.w(Thread.currentThread().getName() + " was interruppted", e);
            }
        } finally {
            executorRunning = false;
        }
    }

    private void mergeTxAndExecute(AsyncOperation operation, AsyncOperation operation2) {
        ArrayList<AsyncOperation> mergedOps = new ArrayList<AsyncOperation>();
        mergedOps.add(operation);
        mergedOps.add(operation2);

        SQLiteDatabase db = operation.dao.getDatabase();
        db.beginTransaction();
        boolean failed = false;
        try {
            for (int i = 0; i < mergedOps.size(); i++) {
                executeOperation(operation);
                if (operation.isFailed()) {
                    // Operation may still have changed the DB, roll back everything
                    failed = true;
                    break;
                }
                if (i == mergedOps.size() - 1) {
                    AsyncOperation peekedOp = queue.peek();
                    if (i < maxOperationCountToMerge && operation.isMergeableWith(peekedOp)) {
                        AsyncOperation removedOp = queue.remove();
                        if (removedOp != peekedOp) {
                            // Paranoia check, should not occur unless threading is broken
                            throw new DaoException("Internal error: peeked op did not match removed op");
                        }
                        mergedOps.add(removedOp);
                    } else {
                        // No more ops in the queue to merge, finish it
                        db.setTransactionSuccessful();
                    }
                }
            }
        } finally {
            db.endTransaction();
        }
        if (failed) {
            DaoLog.i("Revered merged transaction because one of the operations failed. Executing operations one by one instead...");
            for (AsyncOperation asyncOperation : mergedOps) {
                asyncOperation.reset();
                executeOperationAndPostCompleted(asyncOperation);
            }
        } else {
            if (listener != null) {
                for (AsyncOperation asyncOperation : mergedOps) {
                    // Check listener again in case the listener removed itself in the callback
                    if (listener != null) {
                        postOperationCompleted(asyncOperation);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void postOperationCompleted(AsyncOperation operation) {
        AsyncOperationListener listenerToCall = listener;
        if (listenerToCall != null) {
            listenerToCall.onAsyncOperationCompleted(operation);
        }
        synchronized (this) {
            countOperationsCompleted++;
            if (countOperationsCompleted == countOperationsEnqueued) {
                notifyAll();
            }
        }
    }

    private void executeOperationAndPostCompleted(AsyncOperation operation) {
        executeOperation(operation);
        postOperationCompleted(operation);
    }

    @SuppressWarnings("unchecked")
    private void executeOperation(AsyncOperation operation) {
        operation.timeStarted = System.currentTimeMillis();
        try {
            switch (operation.type) {
            case Delete:
                operation.dao.delete(operation.parameter);
                break;
            case DeleteInTxIterable:
                operation.dao.deleteInTx((Iterable<Object>) operation.parameter);
                break;
            case DeleteInTxArray:
                operation.dao.deleteInTx((Object[]) operation.parameter);
                break;
            case Insert:
                operation.dao.insert(operation.parameter);
                break;
            case InsertInTxIterable:
                operation.dao.insertInTx((Iterable<Object>) operation.parameter);
                break;
            case InsertInTxArray:
                operation.dao.insertInTx((Object[]) operation.parameter);
                break;
            case InsertOrReplace:
                operation.dao.insertOrReplace(operation.parameter);
                break;
            case InsertOrReplaceInTxIterable:
                operation.dao.insertOrReplaceInTx((Iterable<Object>) operation.parameter);
                break;
            case InsertOrReplaceInTxArray:
                operation.dao.insertOrReplaceInTx((Object[]) operation.parameter);
                break;
            case Update:
                operation.dao.update(operation.parameter);
                break;
            case UpdateInTxIterable:
                operation.dao.updateInTx((Iterable<Object>) operation.parameter);
                break;
            case UpdateInTxArray:
                operation.dao.updateInTx((Object[]) operation.parameter);
                break;
            case TransactionRunnable:
                executeTransactionRunnable(operation);
                break;
            case TransactionCallable:
                executeTransactionCallable(operation);
            default:
                throw new DaoException("Unsupported operation: " + operation.type);
            }
        } catch (Throwable th) {
            operation.throwable = th;
        }
        operation.timeCompleted = System.currentTimeMillis();
        operation.completed = true;
    }

    private void executeTransactionRunnable(AsyncOperation operation) {
        SQLiteDatabase db = operation.dao.getDatabase();
        db.beginTransaction();
        try {
            ((Runnable) operation.parameter).run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeTransactionCallable(AsyncOperation operation) throws Exception {
        SQLiteDatabase db = operation.dao.getDatabase();
        db.beginTransaction();
        try {
            operation.result = ((Callable<Object>) operation.parameter).call();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
