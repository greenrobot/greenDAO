package de.greenrobot.dao;

import de.greenrobot.dao.AsyncOperation.OperationType;

public class AsyncSession {
    final AbstractDaoSession session;
    final AsyncOperationExecutor executor;

    AsyncSession(AbstractDaoSession session, AsyncOperationExecutor executor) {
        this.session = session;
        this.executor = executor;
    }

    public void insert(Object entity) {
        AbstractDao<?, ?> dao = session.getDao(entity.getClass());
        AsyncOperation operation = new AsyncOperation(OperationType.Insert, dao, entity);
        executor.enqueue(operation);
    }

    public <E> void insertInTx(Class <E> entityClass, Iterable<E> entities) {
        AbstractDao<?, ?> dao = session.getDao(entityClass);
        AsyncOperation operation = new AsyncOperation(OperationType.InsertInTxIterable, dao, entities);
        executor.enqueue(operation);
    }

}
