/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.rx;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.apihint.Experimental;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Scheduler;

/**
 * Like {@link AbstractDao} but with Rx support. Most methods from AbstractDao are present here, but will return an
 * {@link Observable}. Modifying operations return the given entities, so they can be further processed in Rx.
 * <p>
 * Instances of RxDao may have an default {@link rx.Scheduler}, which is used to configure returned observables with
 * {@link Observable#subscribeOn(Scheduler)} (see {@link AbstractDao#rxPlain()}, which uses the IO scheduler).
 *
 * Note: DO NOT call more than one data modification operation when you can use a transaction instead (see
 * {@link RxTransaction}. Individual calls use a transaction each and are much slower.
 *
 * @param <T> Entity type
 * @param <K> Primary key (PK) type; use Void if entity does not have exactly one PK
 * @see AbstractDao#rxPlain()
 */
@Experimental
public class RxDao<T, K> extends RxBase {

    private final AbstractDao<T, K> dao;

    /**
     * Creates a new RxDao without a default scheduler.
     */
    @Experimental
    public RxDao(AbstractDao<T, K> dao) {
        this(dao, null);
    }

    /**
     * Creates a new RxDao with a default scheduler, which is used to configure returned observables with
     * {@link Observable#subscribeOn(Scheduler)}.
     */
    @Experimental
    public RxDao(AbstractDao<T, K> dao, Scheduler scheduler) {
        super(scheduler);
        this.dao = dao;
    }

    /**
     * Rx version of {@link AbstractDao#loadAll()} returning an Observable.
     */
    @Experimental
    public Observable<List<T>> loadAll() {
        return wrap(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return dao.loadAll();
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
                return dao.load(key);
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
                dao.refresh(entity);
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
                dao.insert(entity);
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
                dao.insertInTx(entities);
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
                dao.insertInTx(entities);
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
                dao.insertOrReplace(entity);
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
                dao.insertOrReplaceInTx(entities);
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
                dao.insertOrReplaceInTx(entities);
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
                dao.save(entity);
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
                dao.saveInTx(entities);
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
                dao.saveInTx(entities);
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
                dao.update(entity);
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
                dao.updateInTx(entities);
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
                dao.updateInTx(entities);
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
                dao.delete(entity);
                return null;
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
                dao.deleteByKey(key);
                return null;
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
                dao.deleteAll();
                return null;
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
                dao.deleteInTx(entities);
                return null;
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
                dao.deleteInTx(entities);
                return null;
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
                dao.deleteByKeyInTx(keys);
                return null;
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
                dao.deleteByKeyInTx(keys);
                return null;
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
                return dao.count();
            }
        });
    }

    /**
     * The plain DAO.
     */
    @Experimental
    public AbstractDao<T, K> getDao() {
        return dao;
    }

}
