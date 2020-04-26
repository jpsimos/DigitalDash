package psimos.jacob.dashboard;


import android.content.Context;

import psimos.jacob.BLE112.BLEConnection;

/**
 * Created by jacob on 1/28/2017.
 */

public class Shared {
    public static MainActivity mainActivity = null;
    public static HUD hudActivity = null;
    public static RadioActivity radioActivity = null;
    public static Context appContext = null;
    public static BLEConnection ble112 = null;
    public static BLEReceiver bleReceiver = null;
}
