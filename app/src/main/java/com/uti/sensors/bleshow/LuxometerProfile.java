package com.uti.sensors.bleshow;

import android.support.annotation.NonNull;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

import rx.Observable;

import static java.lang.StrictMath.pow;

/**
 * Created by kunyi on 2017/5/14.
 */

public class LuxometerProfile extends GenericProfile {
    public static final String TAG = "LuxometerProfile";
    private final static String GattServ = "F000AA70-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA71-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA72-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA73-0451-4000-B000-0000000000000";
    private final static byte[] Bconf =  new byte[] {(byte)0x01 };
    private double lux;

    public LuxometerProfile(@NonNull Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf);
    }

    @Override
    public boolean registerNotification() {
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            if (DBG)
                Log.d(TAG, "Lux:" + lux);
        });
        return false;
    }

    @Override
    public boolean configuration() {
        super.configurationImp(bytes -> {
            if (DBG)
                Log.d(TAG, "Configuration");
        });
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        Integer i = u16AtOffset(bytes, 0);
        int mantissa = i & 0x0FFF;
        int exponent = (i >> 12) & 0xFF;
        double magnitude = pow(2.0f, exponent);
        lux = (magnitude * mantissa) / 100.0F;
    }
}
