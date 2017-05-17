package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.polidea.rxandroidble.RxBleConnection;
import com.uti.Utils.Point3D;

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
    private Point3D gyroscope;
    private Point3D accelerometer;
    private Point3D magnetometer;

    public MovementProfile(@NonNull rx.Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf );
    }

    public boolean registerNotification(Context con, View parenet, TableLayout tabLayout) {
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            Log.d(TAG, "Gryoscope X:" + gyroscope.x +
                    ",Y:" + gyroscope.y +
                    ",Z:" + gyroscope.z + "\n" +
            "Accelermeter X:" + accelerometer.x +
                    ",Y:" + accelerometer.y +
                    ",Z:" + accelerometer.z + "\n" +
            "Magnetormeter X:" + magnetometer.x +
                    ",Y:" + magnetometer.y +
                    ",Z:" + magnetometer.z);
        });
        return true;
    }

    public boolean configuration() {
        super.configurationImp(bytes -> {
            Log.d(TAG, "Configuration complete!");
        });
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        final double scaleGyro =  (65536/500);
        final double scaleAcce = (32768/2);
        gyroscope = new Point3D(int16AtOffset(bytes, 0)/scaleGyro,
                int16AtOffset(bytes, 2)/scaleGyro,
                int16AtOffset(bytes, 4)/scaleGyro);
        accelerometer = new Point3D(int16AtOffset(bytes, 6)/scaleAcce,
                int16AtOffset(bytes, 8)/scaleAcce,
                int16AtOffset(bytes, 10)/scaleAcce);
        magnetometer = new Point3D(u16AtOffset(bytes, 12),
                u16AtOffset(bytes, 14),
                u16AtOffset(bytes, 16));
    }
}
