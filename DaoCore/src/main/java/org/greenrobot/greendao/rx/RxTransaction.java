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

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.annotation.apihint.Experimental;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Scheduler;

/**
 * Allows to do transactions using Rx Observable.
 */
@Experimental
public class RxTransaction extends RxBase {
    private final AbstractDaoSession daoSession;

    public RxTransaction(AbstractDaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public RxTransaction(AbstractDaoSession daoSession, Scheduler scheduler) {
        super(scheduler);
        this.daoSession = daoSession;
    }

    /**
     * Rx version of {@link AbstractDaoSession#runInTx(Runnable)} returning an Observable.
     */
    @Experimental
    public Observable<Void> run(final Runnable runnable) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                daoSession.runInTx(runnable);
                return null;
            }
        });
    }

    /**
     * Rx version of {@link AbstractDaoSession#callInTx(Callable)} returning an Observable.
     */
    @Experimental
    public <T> Observable<T> call(final Callable<T> callable) {
        return wrap(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return daoSession.callInTx(callable);
            }
        });
    }

    // Note: wrapping callInTxNoException does not make sense, because the Exception is handled by Rx anyway.


    @Experimental
    public AbstractDaoSession getDaoSession() {
        return daoSession;
    }
}
