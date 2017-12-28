package com.uti.sensors.bleshow.compass;

import android.util.Log;

import com.uti.Utils.Point3D;
import com.uti.sensors.bleshow.BuildConfig;

/**
 * Continuously calibrate raw magnetic 3d axes by
 * 1. Calculating hard iron correction offset by finding min and max values and averaging single axis on origin and
 * 2. Soft iron correction scaling by averaging axis length on all three axes.
 * <p>
 * To calibrate your compass:
 * Wave the magnetometer slowly in three dimensional figure eight (8) pattern in front of you
 * by flicking you wrist on all three axes.
 * Alternatively rotate the magnetometer slowly over all three axes one by one.
 * <p>
 * Thanks for Kris Winer. See:
 * https://github.com/kriswiner/MPU6050/wiki/Simple-and-Effective-Magnetometer-Calibration
 * <p>
 * Created by oerjanti on 30/11/17.
 */

public class Compass {
    private static String TAG = "Compass";

    private static int sSampleCount = 0;
    private static final int SAMPLE_MINIMUM = 150;

    private static final int MAX_DEFAULT_VALUE = 32767;
    private static final int MIN_DEFAULT_VALUE = -32767;

    // Start scaling from opposite direction
    private static int[] sMagneticMaxValues = {MIN_DEFAULT_VALUE, MIN_DEFAULT_VALUE, MIN_DEFAULT_VALUE};
    private static int[] sMagneticMinValues = {MAX_DEFAULT_VALUE, MAX_DEFAULT_VALUE, MAX_DEFAULT_VALUE};

    private static float[] sHardIronOffsets = {0f, 0f, 0f};
    private static float[] sSoftIronScales = {0f, 0f, 0f};

    public static Point3D calibrate(Point3D rawPoint) {

        Point3D result;

        int[] magTemp = {0, 0, 0};

        magTemp[0] = (int) rawPoint.x;
        magTemp[1] = (int) rawPoint.y;
        magTemp[2] = (int) rawPoint.z;

        // Add min and max value for three axes if they change
        // Skip first sample as it always seems to be 0,0,0 for SensorTag CC2650
        if (sSampleCount != 0) {
            for (int i = 0; i < 3; i++) {
                if (magTemp[i] > sMagneticMaxValues[i]) sMagneticMaxValues[i] = magTemp[i];
                if (magTemp[i] < sMagneticMinValues[i]) sMagneticMinValues[i] = magTemp[i];
            }
        }

        if(BuildConfig.DEBUG) {
            Log.i(TAG, "##########");
            Log.i(TAG, "X max/min: " + sMagneticMaxValues[0] + " " + sMagneticMinValues[0]);
            Log.i(TAG, "Y max/min: " + sMagneticMaxValues[1] + " " + sMagneticMinValues[1]);
            Log.i(TAG, "Z max/min: " + sMagneticMaxValues[2] + " " + sMagneticMinValues[2]);
        }

        // Calculate calibration on the flow after minimum samples collected
        if (sSampleCount > SAMPLE_MINIMUM) {
            // Get hard iron correction for off center bias
            sHardIronOffsets[0] = (sMagneticMaxValues[0] + sMagneticMinValues[0]) / 2;  // get average x mag bias
            sHardIronOffsets[1] = (sMagneticMaxValues[1] + sMagneticMinValues[1]) / 2;  // get average y mag bias
            sHardIronOffsets[2] = (sMagneticMaxValues[2] + sMagneticMinValues[2]) / 2;  // get average z mag bias

            // Get soft iron correction estimate for response sensitivity
            float[] magneticScales = {0f, 0f, 0f};
            magneticScales[0] = (sMagneticMaxValues[0] - sMagneticMinValues[0]) / 2;  // get average x axis max chord length
            magneticScales[1] = (sMagneticMaxValues[1] - sMagneticMinValues[1]) / 2;  // get average y axis max chord length
            magneticScales[2] = (sMagneticMaxValues[2] - sMagneticMinValues[2]) / 2;  // get average z axis max chord length

            float avg_rad = magneticScales[0] + magneticScales[1] + magneticScales[2];
            avg_rad /= 3.0;

            sSoftIronScales[0] = avg_rad / magneticScales[0];
            sSoftIronScales[1] = avg_rad / magneticScales[1];
            sSoftIronScales[2] = avg_rad / magneticScales[2];

            result = new Point3D(
                    sSoftIronScales[0] * (rawPoint.x - sHardIronOffsets[0]),
                    sSoftIronScales[1] * (rawPoint.y - sHardIronOffsets[1]),
                    sSoftIronScales[2] * (rawPoint.z - sHardIronOffsets[2]));

            if(BuildConfig.DEBUG) {
                Log.i(TAG, "Original  point: " + rawPoint.x + " " + rawPoint.y + " " + rawPoint.z);
                Log.i(TAG, "Calibrate point: " + result.x + " " + result.y + " " + result.z);
            }

        } else {
            // Collect minimum number of samples before starting calibration
            result = rawPoint;
            sSampleCount++;
        }

        return result;
    }
}
