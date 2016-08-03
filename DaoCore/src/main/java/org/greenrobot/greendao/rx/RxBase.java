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
import org.greenrobot.greendao.annotation.apihint.Internal;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Scheduler;

/**
 * Base functionality for Rx, e.g. default scheduler.
 */
@Internal
class RxBase {

    protected final Scheduler scheduler;

    /**
     * No default scheduler.
     */
    RxBase() {
        scheduler = null;
    }

    /**
     * Sets the default scheduler, which is used to configure returned observables with
     * {@link Observable#subscribeOn(Scheduler)}.
     */
    @Experimental
    RxBase(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * The default scheduler (or null) used for wrapping.
     */
    @Experimental
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected <R> Observable<R> wrap(Callable<R> callable) {
        return wrap(RxUtils.fromCallable(callable));
    }

    protected <R> Observable<R> wrap(Observable<R> observable) {
        if (scheduler != null) {
            return observable.subscribeOn(scheduler);
        } else {
            return observable;
        }
    }

}
