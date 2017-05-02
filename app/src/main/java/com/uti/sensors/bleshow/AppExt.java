package com.uti.sensors.bleshow;

import android.app.Application;
import android.content.Context;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by kunyi on 5/2/17.
 */


public class AppExt extends Application {
    private RxBleClient mRxBleClient;

    public static RxBleClient getRxBleClient(Context context) {
        AppExt app = (AppExt) context.getApplicationContext();
        return app.mRxBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.DEBUG);
    }
}
