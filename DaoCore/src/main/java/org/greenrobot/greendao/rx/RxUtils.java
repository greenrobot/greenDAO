package org.greenrobot.greendao.rx;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func0;

class RxUtils {
    /** As of RxJava 1.1.7, Observable.fromCallable is still @Beta, so just in case... */
    static <T> Observable<T> fromCallable(final Callable<T> callable) {
        return Observable.defer(new Func0<Observable<T>>() {

            @Override
            public Observable<T> call() {
                T result;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    return Observable.error(e);
                }
                return Observable.just(result);
            }
        });
    }
}
