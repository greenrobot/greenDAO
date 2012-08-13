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

import android.database.sqlite.SQLiteDatabase;

/**
 * An operation that will be enqueued for asynchronous execution.
 * 
 * @author Markus
 * 
 * @see AsyncSession
 */
public class AsyncOperation {
    public static enum OperationType {
        Insert, InsertInTxIterable, InsertInTxArray, //
        InsertOrReplace, InsertOrReplaceInTxIterable, InsertOrReplaceInTxArray, //
        Update, UpdateInTxIterable, UpdateInTxArray, //
        Delete, DeleteInTxIterable, DeleteInTxArray, //
        DeleteByKey, DeleteAll, //
        TransactionRunnable, TransactionCallable, //
        QueryList, QueryUnique, //
        Load, LoadAll, //
        Count, Refresh
    }

    public static final int FLAG_MERGE_TX = 1;

    final OperationType type;
    final AbstractDao<Object, Object> dao;
    private final SQLiteDatabase database;
    /** Entity, Iterable<Entity>, Entity[], or Runnable. */
    final Object parameter;
    final int flags;

    volatile long timeStarted;
    volatile long timeCompleted;
    private volatile boolean completed;
    volatile Throwable throwable;
    volatile Object result;

    @SuppressWarnings("unchecked")
    AsyncOperation(OperationType type, AbstractDao<?, ?> dao, Object parameter, int flags) {
        this.type = type;
        this.flags = flags;
        this.dao = (AbstractDao<Object, Object>) dao;
        this.database = null;
        this.parameter = parameter;
    }

    AsyncOperation(OperationType type, SQLiteDatabase database, Object parameter, int flags) {
        this.type = type;
        this.database = database;
        this.flags = flags;
        this.dao = null;
        this.parameter = parameter;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public OperationType getType() {
        return type;
    }

    public Object getParameter() {
        return parameter;
    }

    /**
     * The operation's result after it has completed. Will throw an Execption if no result is available yet. If you want
     * to wait for completion, see {@link #waitForCompletion()}.
     * 
     * @return The operation's result or null if the operation type does not produce any result.
     */
    public Object getResult() {
        if (!completed) {
            throw new DaoException("The operation did not complete yet, consider waitForCompletion instead");
        }
        return result;
    }

    /** @return true if this operation may be merged with others into a single database transaction. */
    public boolean isMergeTx() {
        return (flags & FLAG_MERGE_TX) != 0;
    }

    SQLiteDatabase getDatabase() {
        return database != null ? database : dao.getDatabase();
    }

    /**
     * @return true if this operation is mergeable with the given operation. Checks for null, {@link #FLAG_MERGE_TX},
     *         and if the database instances match.
     */
    boolean isMergeableWith(AsyncOperation other) {
        return other != null && isMergeTx() && other.isMergeTx() && getDatabase() == other.getDatabase();
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public long getTimeCompleted() {
        return timeCompleted;
    }

    public long getDuration() {
        if (timeCompleted == 0) {
            throw new DaoException("This operation did not yet complete");
        } else {
            return timeCompleted - timeStarted;
        }
    }

    public boolean isFailed() {
        return throwable != null;
    }

    public boolean isCompleted() {
        return completed;
    }

    /**
     * Waits until the operation is complete. If the thread gets interrupted, any {@link InterruptedException} will be
     * rethrown as a {@link DaoException}.
     * 
     * @return Result if any, see {@link #getResult()}
     */
    public synchronized Object waitForCompletion() {
        while (!completed) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return result;
    }

    /**
     * Waits until the operation is complete, but at most the given amount of milliseconds.If the thread gets
     * interrupted, any {@link InterruptedException} will be rethrown as a {@link DaoException}.
     * 
     * @return true if the operation completed in the given time frame.
     */
    public synchronized Object waitForCompletion(int maxMillis) {
        if (!completed) {
            try {
                wait(maxMillis);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return completed;
    }

    /** Called when the operation is done. Notifies any threads waiting for this operation's completion. */
    synchronized void setCompleted() {
        completed = true;
        notifyAll();
    }

    public boolean isCompletedSucessfully() {
        return completed && throwable == null;
    }

    /** Reset to prepare another execution run. */
    void reset() {
        timeStarted = 0;
        timeCompleted = 0;
        completed = false;
        throwable = null;
        result = null;
    }

}
