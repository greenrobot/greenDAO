package org.greenrobot.greendao.rx;

import java.util.concurrent.Callable;

import rx.Observable;

class RxUtils {
    /** As of RxJava 1.1.7, Observable.fromCallable is still @Beta, so just in case... */
    static <T> Observable<T> fromCallable(Callable<? extends T> callable) {
        return Observable.create(new OnSubscribeFromCallable<T>(callable));
    }
}
