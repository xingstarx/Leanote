package com.leanote.android.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by xiongxingxing on 17/3/5.
 */

public class RxBus {

    private static volatile RxBus rxBus;

    private RxBus() {
    }

    public static RxBus getInstance() {
        if (rxBus == null) {
            synchronized (RxBus.class) {
                if (rxBus == null) {
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }

    private final Subject<Object, Object> subject = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        subject.onNext(o);
    }

    public Observable<Object> toObservable() {
        return subject;
    }
}