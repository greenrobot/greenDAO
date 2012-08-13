package de.greenrobot.dao;

import de.greenrobot.dao.AsyncOperation.OperationType;

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

    public void insert(Object entity) {
        AbstractDao<?, ?> dao = session.getDao(entity.getClass());
        AsyncOperation operation = new AsyncOperation(OperationType.Insert, dao, entity);
        executor.enqueue(operation);
    }

    public <E> void insertInTx(Class<E> entityClass, Iterable<E> entities) {
        AbstractDao<?, ?> dao = session.getDao(entityClass);
        AsyncOperation operation = new AsyncOperation(OperationType.InsertInTxIterable, dao, entities);
        executor.enqueue(operation);
    }

}
