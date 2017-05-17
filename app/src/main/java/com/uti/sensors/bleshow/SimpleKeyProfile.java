package com.uti.sensors.bleshow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    private static int fReed = 0x04;
    private int keyState;
    protected SimpleKeyTabRow tr;
    protected Context context;


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
    public boolean registerNotification(Context con, View parenet, TableLayout tabLayout) {
        SimpleKeyTabRow tr = new SimpleKeyTabRow(con);
        tr.setId(parenet.generateViewId());
        tr.setIcon("sensortag2", "simplekeys");
        tr.title.setText("SimpleKeys");
        tr.uuidLabel.setText("0000FFE1-0000-1000-8000-00805F9B34FB");
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tabLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        super.registerNotificationImp(bytes -> {
            convertRaw(bytes);

            tr.leftKeyPressState.setImageResource(isLeftKey() ? R.drawable.leftkeyon_300 : R.drawable.leftkeyoff_300);
            tr.rightKeyPressState.setImageResource(isRightKey() ? R.drawable.rightkeyon_300 : R.drawable.rightkeyoff_300);
            tr.lastKeys = keyState;
            if (DBG)
                Log.d(TAG, "Left key:" + getKeyState(true) + ", " +
                        "Right key:" + getKeyState(false));
        });
        return true;
    }sta

    @Override
    public boolean configuration() {
        return true;
    }

    @Override
    protected void convertRaw(byte[] bytes) {
        keyState = bytes[0] & 0x00FF;
    }
}
