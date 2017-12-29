package com.uti.sensors.bleshow;

import android.content.Context;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.exceptions.BleException;
import com.polidea.rxandroidble.exceptions.BleGattException;

import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by kunyi on 2017/5/9.
 */

public abstract class GenericProfile {
    protected static boolean DBG = true;
    public static final String TAG = "GenericProfile";
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

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

    public abstract boolean registerNotification(Context con, View parenet, TableLayout tabLayout);
    public abstract boolean configuration();
    protected abstract void convertRaw(byte[] bytes);

    protected void configurationImp(@NonNull Action1<byte[]> action) {
        mConn.flatMap(rxBleConnection -> rxBleConnection
                .writeCharacteristic(uuidConf, baConf))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, this::onConnectionFailure);

        // Speed up period for compass calibration
        // For some reason temperature sensor does not work at this speed
        if (!uuidConf.equals(UUID.fromString(TempertureProfile.GattConf))) {
            // Period 0x14 = 200ms
            byte[] periodConf = new byte[]{(byte) 0x14};
            mConn.flatMap(rxBleConnection -> rxBleConnection
                    .writeCharacteristic(uuidPeri, periodConf))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action, this::onConnectionFailure);
        }
    }

    protected void registerNotificationImp(Action1<byte[]> action) {
        mConn.flatMap(rxBleConnection -> rxBleConnection
                .setupNotification(uuidData))
                .doOnNext(notificationObservable -> {
                    //
                })
                .flatMap(notificationObservable -> notificationObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, this::onConnectionFailure);
    }

    /*
     * for convert helper function
     */
    protected static Integer int16AtOffset(byte[] b, int offset) {
        Integer lB = (int) b[offset] & 0xFF;
        Integer hB = (int) b[offset + 1];   // Interpret MSB as signed
        return (hB << 8) + lB;
    }

    protected static Integer u16AtOffset(byte[] b, int offset) {
        Integer lB = (int) b[offset] & 0xFF;
        Integer hB = (int) b[offset + 1] & 0xFF;
        return (hB << 8) + lB;
    }

    protected static Integer u24AtOffset(byte[] b, int offset) {
        Integer lB = (int) b[offset] & 0xFF;
        Integer mB = (int) b[offset + 1] & 0xFF;
        Integer hB = (int) b[offset + 2] & 0xFF;
        return (hB << 16) + (mB << 8) + lB;
    }

    private void onConnectionFailure(Throwable throwable) {
        Log.d(TAG, "Throwable:" + throwable);
    }
    /*
     *  maybe need to implement the 2902 client description for service notificaiton enabled
     *  but RxAndroidBle say, default have to implement the function
     *  just to use setupNotification
     *  ref. http://programtalk.com/vs/RxAndroidBle/mockrxandroidble/src/main/java/com/polidea/rxandroidble/mockrxandroidble/RxBleConnectionMock.java
     */
    protected void setupServiceNotification(boolean enabled)
    {
/*
            mConn.flatMap(rxBleConnection -> rxBleConnection
                    .writeCharacteristic(CLIENT_CHARACTERISTIC_CONFIG_UUID, new byte[]{(byte) (enabled ? 1 : 0)})
                .subscribe();
*/
    }
}
