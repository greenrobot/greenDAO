package org.greenrobot.greendao.rx2;

import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.exceptions.Exceptions;

/**
 * Created by Zhang Tingkuo.
 * Date: 2017-04-28
 * Time: 14:20
 */
@Experimental
public class Rx2Query<T> extends Rx2Base {
    private final Query<T> mQuery;

    public Rx2Query(Query<T> query) {
        mQuery = query;
    }

    public Rx2Query(Query<T> query, Scheduler scheduler) {
        super(scheduler);
        mQuery = query;
    }

    /**
     * Rx version of {@link Query#list()} returning an Observable.
     */
    @Experimental
    public Observable<List<T>> list() {
        return wrap(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return mQuery.forCurrentThread().list();
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
                return mQuery.forCurrentThread().unique();
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
        Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    LazyList<T> lazyList = mQuery.forCurrentThread().listLazyUncached();
                    try {
                        for (T entity : lazyList) {
                            if (emitter.isDisposed()) {
                                break;
                            }
                            emitter.onNext(entity);
                        }
                    } finally {
                        lazyList.close();
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    emitter.onError(t);
                }
            }
        });
        return wrap(observable);
    }

//    @Experimental
//    public Query<T> getQuery() {
//        return mQuery;
//    }
}
