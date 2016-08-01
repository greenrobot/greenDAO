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
import org.greenrobot.greendao.AbstractDaoSession;
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
 * {@link Observable#subscribeOn(Scheduler)} (see {@link AbstractDao#rx()}, which uses the IO scheduler).
 *
 * @param <T> Entity type
 * @param <K> Primary key (PK) type; use Void if entity does not have exactly one PK
 * @see AbstractDao#rx()
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
     * The plain DAO that may be useful if you are inside a transaction, e.g {@link #runInTx(Runnable)}.
     */
    @Experimental
    public AbstractDao<T, K> getDao() {
        return dao;
    }

}
