package com.uti.sensors.bleshow;

import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

/**
 * Created by kunyi on 2017/5/9.
 */

public class SimpleKeyProfile extends GenericProfile {
    private final String TAG = "SimpleKeyProfile";
    private static final String GattService = "0000FFE0-0000-1000-8000-00805F9B34FB";
    private static final String GattData = "0000FFE1-0000-1000-8000-00805F9B34FB";
    private static int fLeft = 0x01;
    private static int fRight = 0x02;
    private int keyState;


    public SimpleKeyProfile(@NonNull rx.Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattService),
                null,
                UUID.fromString(GattData),
                null,
                null);
    }

    public boolean isLeftKey() {
        return ((keyState & fLeft) != 0) ? true : false;
    }

    public boolean isRightKey() {
        return ((keyState & fRight) != 0) ? true : false;
    }

    private String getKeyState(boolean key) {
        return (key) ?
                isLeftKey() ? "press" : "release" :
                isRightKey() ? "press" : "release";
    }

    @Override
    public boolean registerNotification() {
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            if (DBG)
                Log.d(TAG, "Left key:" + getKeyState(true) + ", " +
                        "Right key:" + getKeyState(false));
        });
        return true;
    }

    @Override
    public boolean configuration() {
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        keyState = bytes[0] & 0x00FF;
    }
}
