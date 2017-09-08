package org.greenrobot.greendao.rx2;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.apihint.Experimental;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by Zhang Tingkuo.
 * Date: 2017-04-28
 * Time: 14:28
 */
@Experimental
public class Rx2Dao<T, K> extends Rx2Base {

    private final AbstractDao<T, K> mDao;

    /**
     * Creates a new RxDao without a default scheduler.
     */
    @Experimental
    public Rx2Dao(AbstractDao<T, K> dao) {
        this(dao, null);
    }

    /**
     * Creates a new RxDao with a default scheduler, which is used to configure returned observables with
     * {@link Observable#subscribeOn(Scheduler)}.
     */
    @Experimental
    public Rx2Dao(AbstractDao<T, K> dao, Scheduler scheduler) {
        super(scheduler);
        mDao = dao;
    }

    /**
     * Rx version of {@link AbstractDao#loadAll()} returning an Observable.
     */
    @Experimental
    public Observable<List<T>> loadAll() {
        return wrap(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return mDao.loadAll();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#loadAll()} returning an Observable.
     */
    @Experimental
    public Observable<T> load(final K key) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return mDao.load(key);
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#refresh(Object)} returning an Observable.
     * Note that the Observable will emit the given entity back to its subscribers.
     */
    @Experimental
    public Observable<T> refresh(final T entity) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                mDao.refresh(entity);
                return entity;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insert(Object)} returning an Observable.
     * Note that the Observable will emit the given entity back to its subscribers.
     */
    @Experimental
    public Observable<T> insert(final T entity) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                mDao.insert(entity);
                return entity;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insertInTx(Iterable)} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Iterable<T>> insertInTx(final Iterable<T> entities) {
        return wrap(new Callable<Iterable<T>>() {
            @Override
            public Iterable<T> call() throws Exception {
                mDao.insertInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insertInTx(Object[])} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Object[]> insertInTx(final T... entities) {
        return wrap(new Callable<Object[]>() {
            @Override
            public Object[] call() throws Exception {
                mDao.insertInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insertOrReplace(Object)} returning an Observable.
     * Note that the Observable will emit the given entity back to its subscribers.
     */
    @Experimental
    public Observable<T> insertOrReplace(final T entity) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                mDao.insertOrReplace(entity);
                return entity;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insertOrReplaceInTx(Iterable)} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Iterable<T>> insertOrReplaceInTx(final Iterable<T> entities) {
        return wrap(new Callable<Iterable<T>>() {
            @Override
            public Iterable<T> call() throws Exception {
                mDao.insertOrReplaceInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#insertOrReplaceInTx(Object[])} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Object[]> insertOrReplaceInTx(final T... entities) {
        return wrap(new Callable<Object[]>() {
            @Override
            public Object[] call() throws Exception {
                mDao.insertOrReplaceInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#save(Object)} returning an Observable.
     * Note that the Observable will emit the given entity back to its subscribers.
     */
    @Experimental
    public Observable<T> save(final T entity) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                mDao.save(entity);
                return entity;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#saveInTx(Iterable)} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Iterable<T>> saveInTx(final Iterable<T> entities) {
        return wrap(new Callable<Iterable<T>>() {
            @Override
            public Iterable<T> call() throws Exception {
                mDao.saveInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#saveInTx(Object[])} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Object[]> saveInTx(final T... entities) {
        return wrap(new Callable<Object[]>() {
            @Override
            public Object[] call() throws Exception {
                mDao.saveInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#update(Object)} returning an Observable.
     * Note that the Observable will emit the given entity back to its subscribers.
     */
    @Experimental
    public Observable<T> update(final T entity) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                mDao.update(entity);
                return entity;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#updateInTx(Iterable)} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Iterable<T>> updateInTx(final Iterable<T> entities) {
        return wrap(new Callable<Iterable<T>>() {
            @Override
            public Iterable<T> call() throws Exception {
                mDao.updateInTx(entities);
                return entities;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#updateInTx(Object[])} returning an Observable.
     * Note that the Observable will emit the given entities back to its subscribers.
     */
    @Experimental
    public Observable<Object[]> updateInTx(final T... entities) {
        return wrap(new Callable<Object[]>() {
            @Override
            public Object[] call() throws Exception {
                mDao.updateInTx(entities);
                return entities;
            }
        });
    }


    /**
     * Rx version of {@link AbstractDao#delete(Object)} returning an Observable.
     */
    @Experimental
    public Observable<Void> delete(final T entity) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.delete(entity);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteByKey(Object)} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteByKey(final K key) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteByKey(key);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteAll()} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteAll() {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteAll();
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteInTx(Iterable)} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteInTx(final Iterable<T> entities) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteInTx(entities);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteInTx(Object[])} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteInTx(final T... entities) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteInTx(entities);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteByKeyInTx(Iterable)} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteByKeyInTx(final Iterable<K> keys) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteByKeyInTx(keys);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#deleteByKeyInTx(Object[])} returning an Observable.
     */
    @Experimental
    public Observable<Void> deleteByKeyInTx(final K... keys) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDao.deleteByKeyInTx(keys);
                return Void.TYPE.newInstance();
            }
        });
    }

    /**
     * Rx version of {@link AbstractDao#count()} returning an Observable.
     */
    @Experimental
    public Observable<Long> count() {
        return wrap(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDao.count();
            }
        });
    }

    /**
     * The plain DAO.
     */
    @Experimental
    public AbstractDao<T, K> getDao() {
        return mDao;
    }

}
