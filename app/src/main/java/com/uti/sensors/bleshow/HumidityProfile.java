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

/**
 * Created by kunyi on 2017/5/19.
 */

public class HumidityProfile extends GenericProfile {
    private final static String GattServ = "F000AA20-0451-4000-B000-0000000000000";
    private final static String GattData = "F000AA21-0451-4000-B000-0000000000000";
    private final static String GattConf = "F000AA22-0451-4000-B000-0000000000000";
    private final static String GattPeri = "F000AA23-0451-4000-B000-0000000000000";
    private final static byte[] Bconf = new byte[]{(byte) 0x01};
    private final boolean DBG = false;
    private final String TAG = "BarometerProfile";
    protected GenericTabRow tr;
    protected Context context;
    protected float humidity;

    public HumidityProfile(@NonNull Observable<RxBleConnection> conn) {
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
        tr.setIcon("sensortag2", "humidity");
        tr.title.setText("Humidity Data");
        tr.uuidLabel.setText(GattData);
        tr.value.setText("0.0%rH");
        tr.periodBar.setProgress(100);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tabLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);
            tr.value.setText(String.format("%.1f %%rH", humidity));
            tr.sl1.addValue(humidity);
            if (DBG)
                Log.d(TAG, "Humidity:" + humidity);
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
        humidity = 100f * (i / 65535f);
    }
}
