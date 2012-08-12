package de.greenrobot.dao;

public class AsyncOperation {
    public static enum OperationType {
        Insert, InsertInTxIterable, InsertInTxArray, //
        InsertOrReplace, InsertOrReplaceInTxIterable, InsertOrReplaceInTxArray, //
        Update, UpdateInTxIterable, UpdateInTxArray, //
        Delete, DeleteInTxIterable, DeleteInTxArray, //
    }

    public static final int FLAG_MERGE_TX = 1;

    final OperationType type;
    final AbstractDao<Object, ?> dao;
    final Object entity;
    final int flags;

    Throwable throwable;

    public AsyncOperation(OperationType type, AbstractDao<?, ?> dao, Object entity) {
        this(type, dao, entity, 0);
    }

    @SuppressWarnings("unchecked")
    public AsyncOperation(OperationType type, AbstractDao<?, ?> dao, Object entity, int flags) {
        this.type = type;
        this.flags = flags;
        this.dao = (AbstractDao<Object, ?>) dao;
        this.entity = entity;
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

    public Object getEntity() {
        return entity;
    }

    public boolean isMergeTx() {
        return (flags & FLAG_MERGE_TX) != 0;
    }

}
