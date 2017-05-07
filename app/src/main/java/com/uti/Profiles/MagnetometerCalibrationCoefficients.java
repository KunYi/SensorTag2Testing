
package com.uti.Profiles;

import com.uti.Utils.Point3D;


/**
 * As a last-second hack i'm storing the barometer coefficients in a global.
 */
public enum MagnetometerCalibrationCoefficients {
    INSTANCE;
    Point3D val = new Point3D(0.0, 0.0, 0.0);
}
