package psimos.jacob.BLE112;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.HashMap;

/**
 * Created by JacobPsimos on 2/8/2017.
 */

public interface BLECallbackAdapter {
    void onServicesDiscovered(HashMap<String, BluetoothGattService> services, HashMap<String, BluetoothGattCharacteristic> characteristics);
    void onConnectionStateChange(final int state);
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic);
    void onCharacteristicWrite(BluetoothGattCharacteristic characteristic);
    void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);
    void onRemoteReadRssi(final int rssi);
}
