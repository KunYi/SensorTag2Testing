package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.polidea.rxandroidble.RxBleConnection;
import com.uti.Utils.GenericTabRow;

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
    private float ambient;
    private float target;
    private final boolean enabledIR = false;
    protected GenericTabRow tr;
    protected GenericTabRow am;
    protected Context context;

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
        am = new GenericTabRow(con);
        am.sl1.autoScale = true;
        am.sl1.autoScale = true;
        am.sl1.autoScaleBounceBack = true;
        am.setIcon("sensortag2", "temperature");
        am.title.setText("Ambient Temperature Data");
        am.uuidLabel.setText(GattData);
        am.value.setText("0.0'C");
        am.periodMinVal = 200;
        am.periodBar.setMax(255 - (am.periodMinVal/10));
        am.periodBar.setProgress(100);
        am.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tabLayout.addView(am, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        if (enabledIR) {
            // due to APM/Benson ask to remove
            tr = new GenericTabRow(con);
            tr.sl1.autoScale = true;
            tr.sl1.autoScaleBounceBack = true;
            tr.setIcon("sensortag2", "irtemperature");
            tr.title.setText("IR Temperature Data");
            tr.uuidLabel.setText(GattData);
            tr.value.setText("0.0°C");
            tr.periodMinVal = 200;
            tr.periodBar.setMax(255 - (tr.periodMinVal / 10));
            tr.periodBar.setProgress(100);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tabLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }


        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            if (DBG)
                Log.d(TAG, "Ambient value:" + ambient + ", Target:" + target);
            am.value.setText(String.format("%.1f°C", (ambient-1)));
            am.sl1.addValue(ambient);
            if (enabledIR) {
                tr.value.setText(String.format("%.1f°C", target));
                tr.sl1.addValue(target);
            }
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
        final float SCALE_LSB = (0.03125f);
        // TI -- TMP007 sensor algorithm
        target = (int16AtOffset(bytes, 0)>>2)*SCALE_LSB;
        ambient = (int16AtOffset(bytes, 2)>>2)*SCALE_LSB;
    }
}
