package de.greenrobot.dao;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsyncOperationExecutor implements Runnable {
    private BlockingQueue<AsyncOperation> queue;
    private Thread thread;

    AsyncOperationExecutor() {
        queue = new LinkedBlockingQueue<AsyncOperation>();
    }

    public void enqueue(AsyncOperation operation) {
        synchronized (this) {
            queue.add(operation);
            if (thread == null) {
                thread = new Thread(this);
                thread.setName("greenDAO-AsyncOperationExecutor");
                thread.setDaemon(true);
                thread.start();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            AsyncOperation operation = null;
            try {
                operation = queue.poll(60, TimeUnit.SECONDS);
                if (operation == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        operation = queue.poll();
                        if (operation == null) {
                            DaoLog.d("AsyncOperationExecutor thread was idle for a minute; exiting...");
                            thread = null;
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                DaoLog.w(Thread.currentThread().getName() + " was interruppted", e);
            }
            if (operation != null) {
                executeOperation(operation);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void executeOperation(AsyncOperation operation) {
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

    }
}
