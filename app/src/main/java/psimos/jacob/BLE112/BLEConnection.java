package psimos.jacob.BLE112;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;

import psimos.jacob.XGATT.KnownUUIDs;
import psimos.jacob.dashboard.Shared;

/**
 * Created by JacobPsimos on 2/7/2017.
 */

public class BLEConnection {

    /* Devices Connectable */
    private final String CBT_ORANGE_ADDRESS = "00:07:80:12:F1:14";
    private final String CBT_SILVER_ADDRESS = "00:07:80:12:EA:67";

    private Handler delayHandler = new Handler();

    /* Status Flags */
    protected static final int flagAdapterReady = 0x00000001;
    protected static final int flagConnecting = 0x00000002;
    protected static final int flagConnected = 0x00000004;
    protected static final int flagReconnect = 0x00000008;
    protected static final int flagServicesDescovered = 0x00000010;
    protected static final int flagDiscoveringServices = 0x00000020;
    protected static final int flagScanning = 0x00000080;

    private int flags = 0;
    private BLECommandQueue commandQueue = null;
    private Class<? extends BLECommand> currentCommandType = null;
    protected BLECallbackAdapter callbackAdapter = null;

    protected Context context = null;
    protected BluetoothManager manager = null;
    protected BluetoothAdapter adapter = null;
    protected BluetoothLeScanner scanner = null;
    protected ScanCallback scanCallback = null;
    protected BluetoothGattCallback gattCallback = null;
    protected BluetoothDevice device = null;
    protected BluetoothGatt gatt = null;

    protected HashMap<String, BluetoothGattCharacteristic> characteristicMap = null;
    protected HashMap<String, BluetoothGattService> serviceMap = null;

    public BLEConnection(Context context) {
        this.context = context;
        this.commandQueue = new BLECommandQueue(this);
        this.characteristicMap = new HashMap<>();
        this.serviceMap = new HashMap<>();

        initAndroidBluetoothLE();
        initScanCallback();
        initGattCallback();
    }

    public void beginScan() {
        if (!getFlag(flagAdapterReady)) {
            return;
        }

        if(device != null){
            gatt = device.connectGatt(context, false, gattCallback);
            return;
        }

        setFlag(flagDiscoveringServices | flagServicesDescovered, false);
        if (!getFlag(flagConnected) && !getFlag(flagConnecting) && !getFlag(flagScanning)) {
            setFlag(flagScanning, true);
            scanner.startScan(scanCallback);
        }
    }

    public void abortConnection() {
        if (getFlag(flagScanning)) {
            setFlag(flagScanning, false);
            scanner.stopScan(scanCallback);
            return;
        }

        if (getFlag(flagConnected)) {
            setFlag(flagConnected | flagScanning | flagConnecting | flagServicesDescovered | flagDiscoveringServices | flagReconnect, false);

            closeGatt();

            if(callbackAdapter != null){
                callbackAdapter.onConnectionStateChange(BluetoothAdapter.STATE_DISCONNECTED);
            }
        }
    }

    public final boolean isConnected(){
        return getFlag(flagConnected);
    }

    public void setCallbackAdapter(BLECallbackAdapter callbackAdapter){
        this.callbackAdapter = callbackAdapter;
    }

    private void processScanResult(ScanResult result) {
        final String mac = result.getDevice().getAddress();

        if (mac.equals(CBT_ORANGE_ADDRESS) || mac.equals(CBT_SILVER_ADDRESS)) {
            setFlag(flagConnected | flagReconnect | flagScanning | flagServicesDescovered | flagDiscoveringServices, false);
            setFlag(flagConnecting, true);
            scanner.stopScan(scanCallback);
            device = result.getDevice();

            closeGatt();

            delayHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    gatt = device.connectGatt(context, false, gattCallback);
                }
            }, 500);

        }
    }

    private void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            setFlag(flagConnected | flagReconnect, true);
            setFlag(flagScanning | flagConnecting, false);

            if (!getFlag(flagServicesDescovered)) {
                setFlag(flagDiscoveringServices, true);
                gatt.discoverServices();
            }
        }

        if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            setFlag(flagConnected | flagScanning | flagConnecting | flagServicesDescovered | flagDiscoveringServices, false);

            gatt.close();
            if (getFlag(flagReconnect)) {
                setFlag(flagConnecting, true);
                openGatt();
            }
        }

        if(callbackAdapter != null){
            callbackAdapter.onConnectionStateChange(newState);
        }
    }

    private void onServicesDiscovered(BluetoothGatt gatt, int status){
        for(BluetoothGattService service : gatt.getServices()){
            serviceMap.put(service.getUuid().toString(), service);

            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                characteristicMap.put(characteristic.getUuid().toString(), characteristic);
            }
        }

        setFlag(flagDiscoveringServices, false);
        setFlag(flagServicesDescovered, true);

        if(callbackAdapter != null){
            HashMap<String, BluetoothGattService> servicesCopy = (HashMap<String, BluetoothGattService>)serviceMap.clone();
            HashMap<String, BluetoothGattCharacteristic> characteristicsCopy = (HashMap<String, BluetoothGattCharacteristic>)characteristicMap.clone();
            callbackAdapter.onServicesDiscovered(servicesCopy, characteristicsCopy);
        }
    }

    private void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        commandQueue.dequeueCommand();
        if(callbackAdapter != null){
            callbackAdapter.onCharacteristicWrite(characteristic);
        }
    }

    private void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        commandQueue.dequeueCommand();
        if(callbackAdapter != null){
            callbackAdapter.onCharacteristicRead(characteristic);
        }
    }

    private void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        commandQueue.dequeueCommand();
        if(callbackAdapter != null){
            callbackAdapter.onCharacteristicChanged(characteristic);
        }
    }

    private void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        commandQueue.dequeueCommand();
    }

    private void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if(callbackAdapter != null){
            callbackAdapter.onRemoteReadRssi(rssi);
        }
    }

    public boolean setDescriptorNotification(final String uuid, final boolean enabled){
        if(characteristicMap.containsKey(uuid)){
            BLENotifyCommand notify = new BLENotifyCommand(characteristicMap.get(uuid), enabled);
            commandQueue.queueCommand(notify);
            return true;
        }
        return false;
    }

    public boolean setCharacteristicNotification(final String uuid, final boolean enabled){
        if(getFlag(flagConnected)) {
            if (characteristicMap.containsKey(uuid)) {
                gatt.setCharacteristicNotification(characteristicMap.get(uuid), enabled);
                return true;
            }
        }
        return false;
    }

    public boolean setValueForCharacteristic(final String uuid, final byte[] value){
        if(getFlag(flagConnected)) {
            if (characteristicMap.containsKey(uuid)) {
                BluetoothGattCharacteristic characteristic = characteristicMap.get(uuid);
                characteristic.setValue(value);

                BLEWriteCommand writeCommand = new BLEWriteCommand(characteristic);
                commandQueue.queueCommand(writeCommand);
                return true;
            }
        }
        return false;
    }

    public boolean setValueForCharacteristic(final String uuid, final int value){
        byte[] valueOut = new byte[4];
        valueOut[0] = (byte)(value >> 24);
        valueOut[1] = (byte)(value >> 16);
        valueOut[2] = (byte)(value >> 8);
        valueOut[3] = (byte)(value & 0xFF);
        return setValueForCharacteristic(uuid, valueOut);
    }

    public boolean getValueFromCharacteristic(final String uuid){
        if(characteristicMap.containsKey(uuid)){
            BLEReadCommand readCommand = new BLEReadCommand(characteristicMap.get(uuid));
            commandQueue.queueCommand(readCommand);
            return true;
        }
        return false;
    }

    private void closeGatt(){
        synchronized(device) {
            try {
                if (gatt != null) {
                    gatt.close();
                }
            } catch (Exception bluetoothException) {
                Log.d("ble112", bluetoothException.getMessage());
            }
        }
    }

    private void openGatt(){
        synchronized (device) {
            try {
                if (device != null) {
                    gatt = device.connectGatt(context, false, gattCallback);
                }
            } catch (Exception bluetoothException) {
                Log.d("ble112", "openGatt() " + bluetoothException.getMessage());
            }
        }
    }

    private void initAndroidBluetoothLE() {
        if (getFlag(flagAdapterReady)) {
            return;
        }

        if (manager == null) {
            manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (adapter == null) {
            adapter = manager.getAdapter();
        }

        if (adapter != null && adapter.isEnabled()) {
            try {
                if (scanner == null) {
                    scanner = adapter.getBluetoothLeScanner();
                }
                setFlag(flagAdapterReady, true);
            } catch (Exception btex) {
            }
        }
    }

    private void initScanCallback() {
        if (scanCallback == null) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BLEConnection.this.processScanResult(result);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    for (ScanResult result : results) {
                        BLEConnection.this.processScanResult(result);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }
    }

    private void initGattCallback() {
        if (gattCallback == null) {
            gattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    BLEConnection.this.onConnectionStateChange(gatt, status, newState);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    BLEConnection.this.onServicesDiscovered(gatt, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    BLEConnection.this.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    BLEConnection.this.onCharacteristicRead(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    BLEConnection.this.onCharacteristicChanged(gatt, characteristic);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                    BLEConnection.this.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                    BLEConnection.this.onReadRemoteRssi(gatt, rssi, status);
                }
            };
        }
    }

    protected void setFlag(final int flags, final boolean enable) {
        if (enable) {
            this.flags |= flags;
        } else {
            this.flags &= ~flags;
        }
    }

    protected final boolean getFlag(final int flags) {
        if ((this.flags & flags) == flags) {
            return true;
        }
        return false;
    }

    public static String bytesToStringF(final byte[] array){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            sb.append(String.format("%02x", array[i]));
        }
        return sb.toString();
    }

    private void delay(final long ms){
        try{
            Thread.currentThread().wait(ms);
        }catch(Exception e){    }
    }
}