package com.uti.sensors.bleshow;

import com.hwangjr.rxbus.Bus;

/**
 * Created by kunyi on 2017/5/17.
 */

public final class RxBus {
    private static Bus sBus;

    public static synchronized Bus get() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
