package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.polidea.rxandroidble.RxBleConnection;
import com.uti.Utils.GenericTabRow;

import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static java.lang.Math.pow;

/**
 * Created by kunyi on 2017/5/19.
 */

public class BarometerProfile extends GenericProfile {
    private final boolean DBG = true;
    private final String TAG = "BarometerProfile";
    private final static String GattServ = "F000AA40-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA41-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA42-0451-4000-B000-0000000000000";
    private final static String GattCali = "F000AA43-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA44-0451-4000-B000-0000000000000";
    private final static byte[] Bconf = new byte[]{(byte) 0x01};
    private final static byte[] Bcali = new byte[]{(byte) 0x02};
    private static final double PA_PER_METER = 12.0;
    protected GenericTabRow tr;
    protected Context context;
    protected float baro;

    public BarometerProfile(@NonNull Observable<RxBleConnection> conn) {
        super(conn,
                UUID.fromString(GattServ),
                UUID.fromString(GattConf),
                UUID.fromString(GattData),
                UUID.fromString(GattPeri),
                Bconf);
    }

    @Override
    public boolean registerNotification(Context con, View parenet, TableLayout tabLayout) {
        tr = new GenericTabRow(con);
        tr.sl1.autoScale = true;
        tr.sl1.autoScaleBounceBack = true;
        tr.sl1.setColor(255, 0, 150, 125);
        tr.sl1.maxVal = 100;
        tr.sl1.minVal = 0;
        tr.setIcon("sensortag2", "barometer");
        tr.title.setText("Barometer Data");
        tr.uuidLabel.setText(GattData);
        tr.value.setText("0.0mBar, 0.0m");
        tr.periodBar.setProgress(100);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tabLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            double h = ((baro/100.0)/PA_PER_METER - 70.0);
            tr.value.setText(String.format("%.1f mBar %.1f meter", baro/100.0, h));
            tr.sl1.addValue(baro);
            if (DBG)
                Log.d(TAG, "Baro:" + baro);
        });
        return false;
    }

    @Override
    public boolean configuration() {
        // Todo: current the function not yet to doing,
        //      becuase need to get Calibrate data first
        //      but current we don't know how to make a correct sequence
        if (false) {
            // first to read calibration data
            mConn.flatMap(rxBleConnection -> rxBleConnection
                    .writeCharacteristic(uuidConf, Bcali)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(writesbytes -> rxBleConnection.readCharacteristic(uuidData)
                            .doOnNext(new Action1<byte[]>() {
                                @Override
                                public void call(byte[] readbytes) {
                                    Log.d(TAG, "readbytes size:" + readbytes.length);
                                    rxBleConnection.writeCharacteristic(uuidConf, Bconf)
                                            .subscribe(writebytes -> {
                                                if (DBG)
                                                    Log.d(TAG, "Configuration complete");
                                            });
                                }
                            })));
        }
        super.configurationImp(bytes->{
            if (DBG)
            Log.d(TAG, "Configigration complete");
        });
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        if (bytes.length > 4) {
            Integer i = u24AtOffset(bytes, 2);
            baro = (i / 100.0f);
        } else {
            Integer i = u16AtOffset(bytes, 2);
            int mantissa = i & 0x0FFF;
            int exponent = (i >> 12) & 0xFF;

            double magnitude = pow(2.0f, exponent);
            baro = (float) (mantissa * magnitude) / 100.0f;
        }
    }
}
