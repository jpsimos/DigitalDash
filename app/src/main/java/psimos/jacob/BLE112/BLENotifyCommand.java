package psimos.jacob.BLE112;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by JacobPsimos on 2/7/2017.
 */

public class BLENotifyCommand extends BLECommand {

    private final String CLIENT_CONFIG_ATTRIBUTE = "00002902-0000-1000-8000-00805f9b34fb";
    private BluetoothGattCharacteristic characteristic = null;
    private boolean enableNotify = false;

    public BLENotifyCommand(BluetoothGattCharacteristic characteristic, final boolean enableNotify){
        this.characteristic = characteristic;
        this.enableNotify = enableNotify;
    }

    public void execute(BluetoothGatt gatt){

        gatt.setCharacteristicNotification(characteristic, true);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CONFIG_ATTRIBUTE));

        if(enableNotify) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        }else{
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }

        gatt.writeDescriptor(descriptor);
    }
}
