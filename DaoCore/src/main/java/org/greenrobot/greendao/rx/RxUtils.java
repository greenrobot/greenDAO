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

import org.greenrobot.greendao.annotation.apihint.Internal;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Func0;

@Internal
class RxUtils {
    /** As of RxJava 1.1.7, Observable.fromCallable is still @Beta, so just in case... */
    @Internal
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
