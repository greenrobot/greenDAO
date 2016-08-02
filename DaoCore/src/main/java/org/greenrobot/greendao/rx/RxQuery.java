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

import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/**
 * Gets {@link org.greenrobot.greendao.query.Query} results in Rx fashion.
 */
@Experimental
// TODO Pass parameters: currently, parameters are always set to their initial values because of forCurrentThread()
public class RxQuery<T> extends RxBase {
    private final Query<T> query;

    public RxQuery(Query<T> query) {
        this.query = query;
    }

    public RxQuery(Query<T> query, Scheduler scheduler) {
        super(scheduler);
        this.query = query;
    }

    /**
     * Rx version of {@link Query#list()} returning an Observable.
     */
    @Experimental
    public Observable<List<T>> list() {
        return wrap(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return query.forCurrentThread().list();
            }
        });
    }

    /**
     * Rx version of {@link Query#unique()} returning an Observable.
     */
    @Experimental
    public Observable<T> unique() {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return query.forCurrentThread().unique();
            }
        });
    }

    /**
     * Emits the resulting entities one by one, producing them on the fly ("streaming" entities).
     * Unlike {@link #list()}, it does not wait for the query to gather all results. Thus, the first entities are
     * immediately available as soon the underlying database cursor has data. This approach may be more memory
     * efficient for large number of entities (or large entities) at the cost of additional overhead caused by a
     * per-entity delivery through Rx.
     */
    public Observable<T> oneByOne() {
        Observable<T> observable = Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    LazyList<T> lazyList = query.forCurrentThread().listLazyUncached();
                    try {
                        for (T entity : lazyList) {
                            if (subscriber.isUnsubscribed()) {
                                break;
                            }
                            subscriber.onNext(entity);
                        }
                    } finally {
                        lazyList.close();
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    subscriber.onError(e);
                }
            }
        });
        return wrap(observable);
    }

//    @Experimental
//    public Query<T> getQuery() {
//        return query;
//    }
}
