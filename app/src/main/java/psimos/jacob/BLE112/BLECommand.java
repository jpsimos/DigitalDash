package psimos.jacob.BLE112;

import android.bluetooth.BluetoothGatt;

/**
 * Created by JacobPsimos on 2/7/2017.
 */

public abstract class BLECommand {
    public void execute(BluetoothGatt gatt) {}
}
