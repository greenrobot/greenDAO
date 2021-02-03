package org.greenrobot.greendao.rx2;

import org.greenrobot.greendao.annotation.apihint.Internal;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Created by Zhang Tingkuo.
 * Date: 2017-04-28
 * Time: 14:09
 */

@Internal
class Rx2Utils {
    @Internal
    static <T> Observable<T> fromCallable(final Callable<T> callable) {
        return Observable.defer(new Callable<ObservableSource<T>>() {

            @Override
            public ObservableSource<T> call() {
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
