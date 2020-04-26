package psimos.jacob.dashboard;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import psimos.jacob.BLE112.BLECallbackAdapter;
import psimos.jacob.BLE112.BLEWriteCommand;
import psimos.jacob.XGATT.GattProfile;
import psimos.jacob.XGATT.KnownUUIDs;

/**
 * Created by JacobPsimos on 1/31/2017.
 */

public class BLEReceiver {

    private Handler delayHandler = new Handler();

    private int mph = 0;
    private int rpm = 0;
    private int fuel = 0;
    private double fuelGallons = 0.0;
    private double mpg = 0;
    private MPGAverager mpgAverager;
    private double range = 0.0;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private int elevation = 0;
    private int heading = 0;
    private int gear = 0;
    private int intakeAirTemperature = 0;
    private int manifoldPressure = 0;
    private int throttle = 0;
    private boolean leftBlinker = false;
    private boolean rightBlinker = false;
    private boolean highBeams = false;
    private int dimmer = 0;
    private int hvac = 0;
    private boolean driverDoorOpen = false;
    private boolean passengerDoorOpen = false;

    private int failedChecksums = 0;
    private int notificationMask = 0;

    private HashMap<String, GattProfile> profileMap = null;

    public BLEReceiver(){
        GattProfile engineProfile = new GattProfile(KnownUUIDs.GATT_ENGINE, 'n', 0x01, 1, 8, "Speed MPH, RPM, Gear, Throttle, Intake Temp F, Manifold pressure kPa");
        GattProfile fuelProfile = new GattProfile(KnownUUIDs.GATT_FUEL, 'b', 0x02, 1, 8, "Fuel 0-255, MPG");
        GattProfile gpsProfile = new GattProfile(KnownUUIDs.GATT_GPS, 'b', 0x04, 1, 12, "GPS Location degrees, Elevation FT, Heading degrees");
        GattProfile lightingProfile = new GattProfile(KnownUUIDs.GATT_LIGHTING, 'b', 0x08, 3, 8, "Dimmer 0-255, Blinkers, Headlights");
        GattProfile interiorProfile = new GattProfile(KnownUUIDs.GATT_INTERIOR, 'b', 0x10, 1, 8, "Door switch status, HVAC fan status");

        profileMap = new HashMap<>();
        profileMap.put(engineProfile.getUuid(), engineProfile);
        profileMap.put(fuelProfile.getUuid(), fuelProfile);
        profileMap.put(gpsProfile.getUuid(), gpsProfile);
        profileMap.put(lightingProfile.getUuid(), lightingProfile);
        profileMap.put(interiorProfile.getUuid(), interiorProfile);

        mpgAverager = new MPGAverager(1000);

        initBLECallbackAdapter();
    }

    public void updatePinCode(String newPin){
        byte[] gattValue = new byte[20];
        byte[] pinValue = newPin.getBytes();
        memset(gattValue, (byte)0);
        System.arraycopy(pinValue, 0, gattValue, 0, pinValue.length);

        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_SET_PIN, gattValue);
    }

    public void requestPinAuthentication(String pin){
        byte[] gattValue = new byte[20];
        byte[] pinValue = pin.getBytes();
        memset(gattValue, (byte)0);
        System.arraycopy(pinValue, 0, gattValue, 0, pinValue.length);

        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_PIN, gattValue);
    }

    public void radioButtonCommand(int cmd){
        byte[] gattValue = new byte[1];
        gattValue[0] = (byte)cmd;
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_RADIO_BUTTON, gattValue);
    }

    //16 bit integer
    public void setBluetoothMask(final int mask){
        byte[] gattValue = new byte[2]; //16 MSB are transmitted and the 16 LSB are ignored
        gattValue[0] = (byte)((mask & 0xFF00) >> 8);
        gattValue[1] = (byte)(mask & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_TXC_MASK, gattValue);
    }

    public void pauseGattTransmissions(){
        byte[] gattValue = new byte[1];
        gattValue[0] = (byte)1;
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_TXC_PAUSE, gattValue);
    }

    public void handleGattRead(BluetoothGattCharacteristic chara){
        final String incommingUUID = chara.getUuid().toString();

        if(profileMap.containsKey(incommingUUID)){
            GattProfile localProfile = profileMap.get(incommingUUID);
            final int notificationBit = localProfile.getNotificationBit();
            final byte[] value = chara.getValue();

            if(determineIntegrity(value)) {
                notificationMask &= ~notificationBit;

                Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_TXC_NOTIFY, notificationMask);
                switchUuidForAnimation(chara);
            }
        }
    }

    public void handleGattNotify(BluetoothGattCharacteristic chara){
        final String uuid = chara.getUuid().toString();
        final byte[] value = chara.getValue();

        if(uuid.equals(KnownUUIDs.GATT_TXC_NOTIFY)){
            Integer rxMask = ((value[0] & 0xFF) << 8) + (value[1] & 0xFF);
            notificationMask |= rxMask;

            //Log.d("ble112", String.format("RX Notification: %04X", rxMask));

            for(String profileUuid : profileMap.keySet()){
                GattProfile gattProfile = profileMap.get(profileUuid);
                int profileKey = gattProfile.getNotificationBit();
                if((notificationMask & profileKey) == profileKey){
                    Shared.ble112.getValueFromCharacteristic(profileUuid);
                }
            }
            return;
        }

        if(!determineIntegrity(value)){
            failedChecksums++;
            return;
        }

        switchUuidForAnimation(chara);
    }

    private void switchUuidForAnimation(BluetoothGattCharacteristic chara){
        final String uuid = chara.getUuid().toString();
        final byte[] value = chara.getValue();

        try {
            switch (uuid) {
                case KnownUUIDs.GATT_ENGINE:
                    setEngine(value);
                    Shared.hudActivity.setMPHandRPM(mph, rpm, throttle, manifoldPressure, gear);
                    break;
                case KnownUUIDs.GATT_FUEL:
                    setFuel(value);
                    Shared.hudActivity.setFuel(fuelGallons, mpg, mpgAverager.getAverage());
                    break;
                case KnownUUIDs.GATT_GPS:
                    setGPS(value);
                    Shared.hudActivity.setCompass(elevation, heading);
                    break;
                case KnownUUIDs.GATT_LIGHTING:
                    setLighting(value);
                    Shared.hudActivity.setBlinkers(leftBlinker, rightBlinker);
                    break;
                case KnownUUIDs.GATT_INTERIOR:
                    setInterior(value);
                    break;
            }
        }catch(Exception exception){    }
    }

    private void setEngine(final byte[] value){
        mph = (int)value[0];
        rpm = ((value[1] & 0xFF) << 8) + (value[2] & 0xFF);
        gear = value[3] & 0xFF;
        throttle = value[4] & 0xFF;
        intakeAirTemperature = ((value[5] & 0xFF) << 8) + (value[6] & 0xFF);
        manifoldPressure = value[7] & 0xFF;
    }

    private void setFuel(final byte[] value){
        fuel = value[0] & 0xFF;
        fuelGallons = ((double)fuel / 255.0) * 26.0;
        range = fuelGallons * 15.0;

        int impg = ((value[4] & 0xFF) << 24) + ((value[3] & 0xFF) << 16) + ((value[2] & 0xFF) << 8) + (value[1] & 0xFF);
        mpg = Float.intBitsToFloat(impg);

        if(!mpgAverager.addDouble(mpg)){
            mpgAverager.reset();
            mpgAverager.addDouble(mpg);
        }
    }

    private void setGPS(final byte[] value){
        int ilat = ((value[0] & 0xFF) << 24) + ((value[1] & 0xFF) << 16) + ((value[2] & 0xFF) << 8) + (value[3] & 0xFF);
        int ilog = ((value[4] & 0xFF) << 24) + ((value[5] & 0xFF) << 16) + ((value[6] & 0xFF) << 8) + (value[7] & 0xFF);
        heading = ((value[8] & 0xFF) << 8) + (value[9] & 0xFF);
        elevation = ((value[10] & 0xFF) << 8) + (value[11] & 0xFF);
        latitude = (double)ilat / 3600000.0;
        longitude = ((double) ilog / 3600000.0) - 596.52325;
    }

    private void setLighting(final byte[] value){

        if((value[0] & 0xFF) != dimmer){
            dimmer = value[0] & 0xFF;
            Shared.mainActivity.setScreenBrightness(dimmer);
        }

        rightBlinker = ((value[1] & 0xFF) & 0x08) == 0x08;
        leftBlinker = ((value[1] & 0xFF) & 0x20) == 0x20;
        highBeams = ((value[2] & 0xFF) & 0x08) == 0x08;
    }

    private void setInterior(final byte[] value){
        driverDoorOpen = ((value[0] & 0xFF) & 0x80) == 0x80;
        passengerDoorOpen = ((value[0] & 0xFF) & 0x01) == 0x01;
        hvac = value[1] & 0xFF;
    }

    /***
     * Calculate the one byte checksum using the same algorithm on the transmitter device
     * The last byte in the array is the checksum transmitted with the payload, so the checksum
     * needs to be calculated on all bytes except the last, then compared to the last byte.
     * @param ptr
     * @return did the checksums match?
     */
    private boolean determineIntegrity(final byte[] ptr){
        int checksum = 0;
        int cmp = (ptr[ptr.length - 1] & 0xFF);
        for(int i = 0; i < ptr.length - 1; i++){
            int value = (ptr[i] & 0xFF);
            checksum += value;
        }
        checksum = ~checksum & 0xFF;
        return (checksum == cmp);
    }


    private void initBLECallbackAdapter(){
        Shared.ble112.setCallbackAdapter(new BLECallbackAdapter() {
            @Override
            public void onServicesDiscovered(HashMap<String, BluetoothGattService> services, HashMap<String, BluetoothGattCharacteristic> characteristics) {
                //Determine if the discovered characteristic UUID is worthy of notifications
                if(characteristics.containsKey(KnownUUIDs.GATT_TXC_NOTIFY)){
                    Shared.ble112.setDescriptorNotification(KnownUUIDs.GATT_TXC_NOTIFY, true);
                    Log.d("ble112", "TXC Notification BitMask Descriptor Updated");
                }

                for(String discoveredUuid : characteristics.keySet()){
                    if(profileMap.containsKey(discoveredUuid)){
                        GattProfile gattProfile = profileMap.get(discoveredUuid);
                        if(gattProfile.getRXType() == 'n'){
                            Shared.ble112.setDescriptorNotification(discoveredUuid, true);
                            Log.d("ble112", String.format("Notify: %s", gattProfile.getDescription()));
                        }
                    }
                }
            }

            @Override
            public void onConnectionStateChange(int state) {
                if(state == BluetoothGatt.STATE_CONNECTED){
                    Shared.mainActivity.LogOut("Connected", "BLE112");

                    delayHandler.postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            requestPinAuthentication("asflkja;sdf;kjrqow");
                            Shared.mainActivity.LogOut("Requesting Authentication...", "BLE112");
                        }
                    }, 1000);
                }

                if(state == BluetoothGatt.STATE_DISCONNECTED){
                    Shared.mainActivity.LogOut("Disconnected", "BLE112");
                    Shared.mainActivity.setScreenBrightness(0xF0);
                    Shared.mainActivity.setAutoScreenBrightness();
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGattCharacteristic characteristic) {
                BLEReceiver.this.handleGattRead(characteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
                final String uuid = characteristic.getUuid().toString();

                if(uuid.equals(KnownUUIDs.GATT_TXC_MASK)){
                    byte[] data = characteristic.getValue();
                    int notifyMask = ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);
                    Shared.mainActivity.LogOut(String.format("Updated notify mask: %04X", notifyMask), "BLE112");
                    return;
                }

                if(uuid.equals(KnownUUIDs.GATT_PIN)){
                    Shared.mainActivity.LogOut("Requested auth with pin", "BLE112");
                    return;
                }

                if(uuid.equals(KnownUUIDs.GATT_SET_PIN)){
                    Shared.mainActivity.LogOut("Set new pin", "BLE112");
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
                BLEReceiver.this.handleGattNotify(characteristic);
            }

            @Override
            public void onRemoteReadRssi(int rssi) {
                //Shared.mainActivity.LogOut(String.format("RSSI: %d", rssi), "BLE112");
            }
        });
    }

    @Override
    public String toString(){
        String smph = String.format("{\"mph\":\"%d\"", mph);
        String srpm = String.format("{\"rpm\":\"%d\"", rpm);
        String sfuel = String.format("{\"fuel\":\"%.1f\"", fuelGallons);
        String smpg = String.format("{\"mpg\":\"%d\"", mpg);
        String sgear = String.format("{\"gear\":\"%d\"", this.gear);
        String slatitude = String.format("{\"lat\":\"%.7f\"", latitude);
        String slongitude = String.format("{\"log\":\"%.7f\"", longitude);
        String selev = String.format("{\"elev\":\"%d\"", elevation);
        String shead = String.format("{\"head\":\"%d\"", heading);

        return String.format("[%s,%s,%s,%s,%s,%s,%s,%s,%s]", smph, srpm, sfuel, smpg, sgear, slatitude, slongitude, selev, shead);
    }

    public static final String bytesToASCII(byte[] array){
        byte[] resa = Arrays.copyOfRange(array, 0, array.length);
        return new String(resa);
    }

    public static final String bytesToStringF(byte[] array, char separator){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            if(i < array.length - 1) {
                result.append(String.format("%02X%c", array[i], separator));
            }else{
                result.append(String.format("%02X", array[i]));
            }
        }
        return result.toString();
    }

    public static void memset(byte[] dst, byte val){
        for(int i = 0; i < dst.length; i++){
            dst[i] = val;
        }
    }

}
