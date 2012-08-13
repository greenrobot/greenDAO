package de.greenrobot.dao;

import de.greenrobot.dao.AsyncOperation.OperationType;

/**
 * Asynchronous interface to entity operations. All operations will enqueued a @link {@link AsyncOperation} and return
 * immediately (fine to call on the UI/main thread). The queue will be processed in a (single) background thread. The
 * processing order is the call order of the operations. It's possible to start multiple AsyncSessions that will execute
 * concurrently.
 * 
 * @author Markus
 * 
 * @see AbstractDaoSession#startAsyncSession()
 */
// Facade to AsyncOperationExecutor: prepares operations and delegates work to AsyncOperationExecutor.
public class AsyncSession {
    final AbstractDaoSession session;
    final AsyncOperationExecutor executor;

    AsyncSession(AbstractDaoSession session) {
        this.session = session;
        this.executor = new AsyncOperationExecutor();
    }

    public int getMaxOperationCountToMerge() {
        return executor.getMaxOperationCountToMerge();
    }

    public void setMaxOperationCountToMerge(int maxOperationCountToMerge) {
        executor.setMaxOperationCountToMerge(maxOperationCountToMerge);
    }

    public AsyncOperationListener getListener() {
        return executor.getListener();
    }

    public void setListener(AsyncOperationListener listener) {
        executor.setListener(listener);
    }

    public boolean isCompleted() {
        return executor.isCompleted();
    }

    public void waitForCompletion() throws InterruptedException {
        executor.waitForCompletion();
    }

    public boolean waitForCompletion(int maxMillis) throws InterruptedException {
        return executor.waitForCompletion(maxMillis);
    }

    /** Asynchronous version of {@link AbstractDao#insert(Object)}. */
    public AsyncOperation insert(Object entity) {
        return insert(entity, 0);
    }

    /** Asynchronous version of {@link AbstractDao#insert(Object)}. */
    public AsyncOperation insert(Object entity, int flags) {
        AbstractDao<?, ?> dao = session.getDao(entity.getClass());
        AsyncOperation operation = new AsyncOperation(OperationType.Insert, dao, entity, flags);
        executor.enqueue(operation);
        return operation;
    }

    /** Asynchronous version of {@link AbstractDao#insertInTx(Iterable)}. */
    public <E> AsyncOperation insertInTx(Class<E> entityClass, Iterable<E> entities) {
        return insertInTx(entityClass, entities, 0);
    }

    /** Asynchronous version of {@link AbstractDao#insertInTx(Iterable)}. */
    public <E> AsyncOperation insertInTx(Class<E> entityClass, Iterable<E> entities, int flags) {
        AbstractDao<?, ?> dao = session.getDao(entityClass);
        AsyncOperation operation = new AsyncOperation(OperationType.InsertInTxIterable, dao, entities, flags);
        executor.enqueue(operation);
        return operation;
    }

}
