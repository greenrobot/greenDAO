package org.greenrobot.greendao.rx2;

import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.annotation.apihint.Internal;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by Zhang Tingkuo.
 * Date: 2017-04-28
 * Time: 14:14
 */
@Internal
class Rx2Base {

    protected final Scheduler mScheduler;

    /**
     * No default scheduler.
     */
    public Rx2Base() {
        mScheduler = null;
    }

    /**
     * Sets the default scheduler, which is used to configure returned observables with
     * {@link Observable#subscribeOn(Scheduler)}.
     */
    @Experimental
    Rx2Base(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    /**
     * The default scheduler (or null) used for wrapping.
     */
    @Experimental
    public Scheduler getScheduler() {
        return mScheduler;
    }

    protected <R> Observable<R> wrap(Callable<R> callable) {
        return wrap(Rx2Utils.fromCallable(callable));
    }

    protected <R> Observable<R> wrap(Observable<R> observable) {
        if (mScheduler != null) {
            return observable.subscribeOn(mScheduler);
        } else {
            return observable;
        }
    }

}
