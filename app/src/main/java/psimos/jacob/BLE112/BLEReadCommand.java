package psimos.jacob.BLE112;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.util.UUID;

/**
 * Created by JacobPsimos on 2/8/2017.
 */

public class BLEReadCommand extends BLECommand {
    private BluetoothGattCharacteristic characteristic = null;

    public BLEReadCommand(BluetoothGattCharacteristic characteristic){
        this.characteristic = characteristic;
    }

    public void execute(BluetoothGatt gatt){
        gatt.readCharacteristic(characteristic);
    }
}
