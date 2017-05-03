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

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDeviceItem(i));
        }
    }

    public static boolean CheckDeviceExist(final String mac) {
        return (ITEM_MAP.get(mac) != null);
    }

    public static void UpdateRssi(final String mac, final int rssi) {
        final DeviceItem dev = ITEM_MAP.get(mac);
        if (dev != null)
            dev.setRssi(rssi);
    }

    private static void addItem(DeviceItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DeviceItem createDeviceItem(int position) {
        return new DeviceItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DeviceItem {
        public final String id;
        public final String content;
        public final String details;
        public RxBleDevice dev;
        public int rssi;

        public DeviceItem(RxBleDevice dev, int rssi) {
            this.dev = dev;
            this.rssi = rssi;
            this.id = "123";
            this.content = "test";
            this.details = "dummy";
        }

        public DeviceItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;

        }

        public String getMac() {
            return dev.getMacAddress();
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
