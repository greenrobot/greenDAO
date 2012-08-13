package de.greenrobot.dao;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.database.sqlite.SQLiteDatabase;

public class AsyncOperationExecutor implements Runnable {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private final BlockingQueue<AsyncOperation> queue;
    private volatile boolean executorRunning;
    private volatile int maxOperationCountToMerge;
    private volatile AsyncOperationListener listener;

    AsyncOperationExecutor() {
        queue = new LinkedBlockingQueue<AsyncOperation>();
        maxOperationCountToMerge = 50;
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            queue.add(operation);
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
                operation.dao.delete(operation.entity);
                break;
            case Insert:
                operation.dao.insert(operation.entity);
                break;
            case InsertInTxIterable:
                operation.dao.insertInTx((Iterable<Object>) operation.entity);
                break;
            case Update:
                operation.dao.update(operation.entity);
                break;
            default:
                throw new DaoException("Unsupported operation: " + operation.type);
            }
        } catch (Throwable th) {
            operation.throwable = th;
        }
        operation.timeCompleted = System.currentTimeMillis();
        operation.completed = true;
    }
}
