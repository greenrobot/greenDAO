package org.greenrobot.greendao.rx2;

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.annotation.apihint.Experimental;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by Zhang Tingkuo.
 * Date: 2017-04-28
 * Time: 14:16
 */
@Experimental
public class Rx2Transaction extends Rx2Base {
    private final AbstractDaoSession mDaoSession;

    public Rx2Transaction(AbstractDaoSession daoSession) {
        mDaoSession = daoSession;
    }

    public Rx2Transaction(AbstractDaoSession daoSession, Scheduler scheduler) {
        super(scheduler);
        mDaoSession = daoSession;
    }

    /**
     * Rx version of {@link AbstractDaoSession#runInTx(Runnable)} returning an Observable.
     */
    @Experimental
    public Observable<Void> run(final Runnable runnable) {
        return wrap(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mDaoSession.runInTx(runnable);
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
                return mDaoSession.callInTx(callable);
            }
        });
    }

    // Note: wrapping callInTxNoException does not make sense, because the Exception is handled by Rx anyway.


    @Experimental
    public AbstractDaoSession getDaoSession() {
        return mDaoSession;
    }
}
