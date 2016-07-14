/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.de)
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

import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/**
 * Like {@link AbstractDao} but with Rx support. Most methods from AbstractDao are present here, but will return an
 * {@link Observable}.
 *
 * @param <T> Entity type
 * @param <K> Primary key (PK) type; use Void if entity does not have exactly one PK
 */
public class RxDao<T, K> {

    private final AbstractDao<T, K> dao;
    private final Scheduler scheduler;

    public RxDao(AbstractDao<T, K> dao) {
        this(dao, null);
    }

    public RxDao(AbstractDao<T, K> dao, Scheduler scheduler) {
        this.dao = dao;
        this.scheduler = scheduler;
    }

    public Observable<List<T>> loadAll() {
        Observable<List<T>> observable = Observable.create(new OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(dao.loadAll());
                        subscriber.onCompleted();
                    }
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, subscriber);
                }
            }
        });
        return wrap(observable);
    }

    private <R> Observable<R> wrap(Observable<R> observable) {
        if(scheduler != null) {
            return observable.subscribeOn(scheduler);
        } else {
            return observable;
        }
    }

}
