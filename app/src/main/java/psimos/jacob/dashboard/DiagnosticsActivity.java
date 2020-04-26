package psimos.jacob.dashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import psimos.jacob.XGATT.KnownUUIDs;

public class DiagnosticsActivity extends AppCompatActivity {

    private ToggleButton toggleLF = null;
    private ToggleButton toggleLR = null;
    private ToggleButton toggleRF = null;
    private ToggleButton toggleRR = null;
    private Switch toggleDiagnostics = null;
    private Switch toggleLowBeams = null;
    private Switch toggleHighBeams = null;
    private Switch toggleWipers = null;
    private Switch toggleWasher = null;
    private Switch toggleHorn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);

        toggleLF = (ToggleButton)findViewById(R.id.toggleLF);
        toggleLR = (ToggleButton)findViewById(R.id.toggleLR);
        toggleRF = (ToggleButton)findViewById(R.id.toggleRF);
        toggleRR = (ToggleButton)findViewById(R.id.toggleRR);
        toggleDiagnostics = (Switch)findViewById(R.id.toggleDiagnostics);
        toggleLowBeams = (Switch)findViewById(R.id.toggleLowBeams);
        toggleHighBeams = (Switch)findViewById(R.id.toggleHighBeams);
        toggleWipers = (Switch)findViewById(R.id.toggleWipers);
        toggleWasher = (Switch)findViewById(R.id.toggleWasher);
        toggleHorn = (Switch)findViewById(R.id.toggleHorn);

        toggleDiagnostics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleDiagnostics_onCheckedChanged(isChecked);
            }
        });
        toggleLowBeams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleLowBeams_onCheckedChanged(isChecked);
            }
        });
        toggleHighBeams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleHighBeams_onCheckedChanged(isChecked);
            }
        });
        toggleWipers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleWipers_onCheckedChanged(isChecked);
            }
        });
        toggleWasher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleWasher_onCheckedChanged(isChecked);
            }
        });
        toggleHorn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleHorn_onCheckedChanged(isChecked);
            }
        });
        toggleLF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleLF_onCheckedChanged(isChecked);
            }
        });
        toggleRF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleRF_onCheckedChanged(isChecked);
            }
        });
        toggleLR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleLR_onCheckedChanged(isChecked);
            }
        });
        toggleRR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setToggleRR_onCheckedChanged(isChecked);
            }
        });
    }

    private void setToggleDiagnostics_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[1];
        gattValue[0] = checked ? (byte)1 : (byte)0;
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_DIAGNOSTICS, gattValue);
    }

    private void setToggleLowBeams_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)2;
        gattValue[1] = (byte)((0x01 | (checked ? (0x01 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleHighBeams_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)2;
        gattValue[1] = (byte)((0x04 | (checked ? (0x04 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleWipers_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)3;
        gattValue[1] = (byte)((0x01 | (checked ? (0x01 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleWasher_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)3;
        gattValue[1] = (byte)((0x04 | (checked ? (0x04 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleHorn_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)4;
        gattValue[1] = checked ? (byte)1 : (byte)0;
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleLF_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)1;
        gattValue[1] = (byte)((0x01 | (checked ? (0x01 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleRF_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)1;
        gattValue[1] = (byte)((0x04 | (checked ? (0x04 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleRR_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)1;
        gattValue[1] = (byte)((0x40 | (checked ? (0x40 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }

    private void setToggleLR_onCheckedChanged(boolean checked){
        byte[] gattValue = new byte[4];
        gattValue[0] = (byte)1;
        gattValue[1] = (byte)((0x10 | (checked ? (0x10 << 1) : 0x00)) & 0xFF);
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_BCM, gattValue);
    }
}
