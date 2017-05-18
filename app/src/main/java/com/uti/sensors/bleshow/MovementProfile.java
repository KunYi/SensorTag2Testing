package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.polidea.rxandroidble.RxBleConnection;
import com.uti.Utils.Point3D;

import java.util.UUID;

/**
 * Created by kunyi on 2017/5/9.
 */

public class MovementProfile extends GenericProfile {
    private final boolean DBG = false;
    private final String TAG = "MovementProfile";
    private final static String GattServ = "F000AA80-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA81-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA82-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA83-0451-4000-B000-0000000000000";
    private final static byte[] Bconf = new byte[]{(byte) 0x7F, (byte) 0x00};
    protected MovementTabRow tr;
    private Point3D gyroscope;
    private Point3D accelerometer;
    private Point3D magnetometer;

    public MovementProfile(@NonNull rx.Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf);
    }

    public boolean registerNotification(Context con, View parenet, TableLayout tabLayout) {
        tr = new MovementTabRow(con);
        tr.setIcon("sensortag2", "motion");
        tr.title.setText("Motion Data");
        tr.uuidLabel.setText(GattData);
        tr.value.setText("X:0.00G, Y:0.00G, Z:0.00G");
        tr.gyroValue.setText("X:0.00'/s, Y:0.00'/s, Z:0.00'/s");
        tr.magValue.setText("X:0.00mT, Y:0.00mT, Z:0.00mT");
        tr.periodBar.setProgress(100);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tabLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            tr.value.setText(Html.fromHtml(String.format(
                    "<font color=#FF0000>X:%.2fG</font>, <font color=#00967D>Y:%.2fG</font>, <font color=#00000>Z:%.2fG</font>",
                    accelerometer.x, accelerometer.y, accelerometer.z)));
            tr.gyroValue.setText(Html.fromHtml(String.format(
                    "<font color=#FF0000>X:%.2f°/s</font>, <font color=#00967D>Y:%.2f°/s</font>, <font color=#00000>Z:%.2f°/s</font>",
                    gyroscope.x, gyroscope.y, gyroscope.z)));
            tr.magValue.setText(Html.fromHtml(String.format(
                    "<font color=#FF0000>X:%.2fuT</font>, <font color=#00967D>Y:%.2fuT</font>, <font color=#00000>Z:%.2fuT</font>",
                    magnetometer.x, magnetometer.y, magnetometer.z)));

            tr.sl1.addValue((float) accelerometer.x);
            tr.sl2.addValue((float) accelerometer.y);
            tr.sl3.addValue((float) accelerometer.z);
            tr.sl4.addValue((float) gyroscope.x);
            tr.sl5.addValue((float) gyroscope.y);
            tr.sl6.addValue((float) gyroscope.z);
            tr.sl7.addValue((float) magnetometer.x);
            tr.sl8.addValue((float) magnetometer.y);
            tr.sl9.addValue((float) magnetometer.z);

            if (DBG)
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
            if (DBG)
                Log.d(TAG, "Configuration complete!");
        });
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        final double scaleGyro = (128.0);
        final double scaleAcce = (4096.0);
        final double scaleMag = (32768 / 4912);
        gyroscope = new Point3D(int16AtOffset(bytes, 0) / scaleGyro,
                int16AtOffset(bytes, 2) / scaleGyro,
                int16AtOffset(bytes, 4) / scaleGyro);
        accelerometer = new Point3D(int16AtOffset(bytes, 6) / scaleAcce,
                int16AtOffset(bytes, 8) / scaleAcce,
                int16AtOffset(bytes, 10) / scaleAcce);
        magnetometer = new Point3D(int16AtOffset(bytes, 12) / scaleMag,
                int16AtOffset(bytes, 14) / scaleMag,
                int16AtOffset(bytes, 16) / scaleMag);
    }
}
