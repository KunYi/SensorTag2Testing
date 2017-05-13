package com.uti.sensors.bleshow;

import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by kunyi on 2017/5/9.
 */

public class SimpleKeyProfile extends GenericProfile {
    private final String TAG = "SimpleKeyProfile";
    private static final String GattService = "0000FFE0-0000-1000-8000-00805F9B34FB";
    private static final String GattData = "0000FFE1-0000-1000-8000-00805F9B34FB";

    public SimpleKeyProfile(@NonNull rx.Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattService),
                null,
                UUID.fromString(GattData),
                null,
                null);
    }

    public boolean registerNotification() {
        super.registerNotificationImp(bytes -> {
            Log.d(TAG, "Key value:" + bytes[0]);
        });
        return true;
    }

    public boolean configuration() {
        return true;
    }
}
