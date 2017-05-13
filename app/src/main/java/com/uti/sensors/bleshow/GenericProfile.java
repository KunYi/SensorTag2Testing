package com.uti.sensors.bleshow;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by kunyi on 2017/5/9.
 */

public abstract class GenericProfile {
    public static final String TAG = "GenericProfile";

    protected final rx.Observable<RxBleConnection> mConn;
    protected final UUID uuidServ;
    protected final UUID uuidConf;
    protected final UUID uuidData;
    protected final UUID uuidPeri;
    protected final byte[] baConf;

    public GenericProfile(@NonNull rx.Observable<RxBleConnection> conn,
                          @NonNull UUID serv, UUID conf,
                          @NonNull UUID data,UUID peri,
                                byte[] baConf) {
        this.mConn = conn;
        this.uuidServ = serv;
        this.uuidConf = conf;
        this.uuidData = data;
        this.uuidPeri = peri;
        this.baConf = baConf;
    }

    public abstract boolean registerNotification();
    public abstract boolean configuration();

    protected void configurationImp(@NonNull Action1<byte[]> action) {
        mConn.flatMap(rxBleConnection -> rxBleConnection
                .writeCharacteristic(uuidConf, baConf))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    protected void registerNotificationImp(Action1<byte[]> action) {
        mConn.flatMap(rxBleConnection -> rxBleConnection
                .setupNotification(uuidData))
                .doOnNext(notificationObservable -> {
                    //
                })
                .flatMap(notificationObservable -> notificationObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }
}
