package com.uti.sensors.bleshow.Devices;

import android.support.v4.view.ViewCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.polidea.rxandroidble.RxBleDevice;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DeviceContext {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DeviceItem> ITEMS = new ArrayList<DeviceItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DeviceItem> ITEM_MAP = new HashMap<String, DeviceItem>();

    private static final int COUNT = 1;


    public static boolean CheckDeviceExist(final String mac) {
        return (ITEM_MAP.get(mac) != null);
    }

    public static int AddorUpdateDevice(String mac, int rssi) {
        DeviceItem dev = ITEM_MAP.get(mac);
        if (dev == null) {
            dev = new DeviceItem(mac, rssi);
            dev.position = ITEMS.size();
            addItem(dev);
            return -1;
        }

        dev.nRSSI = rssi;
        return dev.position;
    }

    private static void addItem(DeviceItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.MAC, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DeviceItem {
        public final String MAC;
        public int nRSSI;
        public boolean bConnected;
        public int position;

        public DeviceItem(String mac, int rssi) {
            this.MAC = mac;
            this.nRSSI = rssi;
            this.bConnected = false;
            this.position = 0;
        }

        @Override
        public String toString() {
            return MAC;
        }
    }
}
