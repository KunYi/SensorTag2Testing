package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.polidea.rxandroidble.RxBleConnection;

import java.util.UUID;

import rx.Observable;

import static java.lang.StrictMath.pow;

/**
 * Created by kunyi on 2017/5/14.
 */

public class TempertureProfile extends GenericProfile {
    private final boolean DBG = false;
    private final String TAG = "TempertureProfile";
    private final static String GattServ = "F000AA00-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA01-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA02-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA03-0451-4000-B000-0000000000000";
    private final static byte[] Bconf =  new byte[] {(byte)0x01 };
    private double ambient;
    private double target;

    public TempertureProfile(@NonNull Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf);
    }

    @Override
    public boolean registerNotification(Context con, View parenet, TableLayout tabLayout) {
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            if (DBG)
                Log.d(TAG, "Ambient value:" + ambient + ", Target:" + target);
        });
        return true;
    }

    @Override
    public boolean configuration() {
        super.configurationImp(bytes -> {
            if (DBG)
                Log.d(TAG, "configuration complete");
        });
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        final double SCALE_LSB = (0.03125f);
        // TI -- TMP007 sensor algorithm
        target = (int16AtOffset(bytes, 0)>>2)*SCALE_LSB;
        ambient = (int16AtOffset(bytes, 2)>>2)*SCALE_LSB;
    }
}
