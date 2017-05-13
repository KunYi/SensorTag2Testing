package com.uti.sensors.bleshow;

import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by kunyi on 2017/5/9.
 */

public class MovementProfile extends GenericProfile {

    private final String TAG = "MovementProfile";
    private final static String GattServ = "F000AA80-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA81-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA82-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA83-0451-4000-B000-0000000000000";
    private final static byte[] Bconf =  new byte[] {(byte)0x7F,(byte)0x00};

    public MovementProfile(@NonNull rx.Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf );
    }

    public boolean registerNotification() {
        super.registerNotificationImp(bytes -> {
            Log.d(TAG, "Bytes size:"+ bytes.length + " get value:" + bytes[0] + ", " + bytes[1]);
        });
        return true;
    }

    public boolean configuration() {
        super.configurationImp(bytes -> {
            Log.d(TAG, "Configuration complete!");
        });
        return true;
    }
}
