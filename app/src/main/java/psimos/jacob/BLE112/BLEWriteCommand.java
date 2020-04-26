package psimos.jacob.BLE112;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by JacobPsimos on 2/8/2017.
 */

public class BLEWriteCommand extends BLECommand {
    private BluetoothGattCharacteristic characteristic = null;

    public BLEWriteCommand(BluetoothGattCharacteristic characteristic){
        this.characteristic = characteristic;
    }

    public void execute(BluetoothGatt gatt){
        gatt.writeCharacteristic(characteristic);
    }
}
