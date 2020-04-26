package psimos.jacob.dashboard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import psimos.jacob.BLE112.BLEConnection;
import psimos.jacob.XGATT.KnownUUIDs;


public class MainActivity extends AppCompatActivity {

    View viewMain = null;

    private Button btnConnectBT = null;
    private Button btnAbortBT = null;
    private Button btnDash = null;
    private Button btnXMRadio = null;
    private TextView txtBT = null;
    private Button btnDiagnostics = null;
    private Button btnDingDong = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Shared.mainActivity = this;
        Shared.appContext = getApplicationContext();
        Shared.ble112 = new BLEConnection(getApplicationContext());
        Shared.bleReceiver = new BLEReceiver();
        Shared.radioActivity = new RadioActivity();


        txtBT = (TextView)findViewById(R.id.txtBT);
        txtBT.setMovementMethod(new ScrollingMovementMethod());
        txtBT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Shared.ble112.debugDequeue();
                return true;
            }
        });

        btnAbortBT = (Button) findViewById(R.id.btnAbortBT);
        btnAbortBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAbortBT_onClick();
            }
        });

        btnConnectBT = (Button) findViewById(R.id.btnConnectBT);
        btnConnectBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConnectBT_onClick();
            }
        });

        btnDash = (Button)findViewById(R.id.btnDash);
        btnDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDash_onClick();
            }
        });

        btnXMRadio = (Button)findViewById(R.id.btnXMRadio);
        btnXMRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnXMRadio_onClick();
            }
        });

        btnDiagnostics = (Button)findViewById(R.id.btnDiagnostics);
        btnDiagnostics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDiagnostics_onClick();
            }
        });

        btnDingDong = (Button)findViewById(R.id.btnDingDong);
        btnDingDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDingDong_onClick();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                Shared.bleReceiver.setBluetoothMask(0);
                break;
        }
    }

    private void scrollLog(){
        int pos = txtBT.getScrollY();
        int height = txtBT.getHeight();
        int lineHeight = txtBT.getLineHeight();
        int lineCount = txtBT.getLineCount();
        int scrollLineIndex = ((pos / lineHeight) + 1) + (height / lineHeight);
        int linesHeight = lineHeight * (lineCount > 0 ? lineCount : lineCount - 1);
        boolean overflow = linesHeight > height;

        if(overflow && scrollLineIndex + 3 >= lineCount){
            //Autoscroll
            txtBT.setScrollY(linesHeight - height);
        }
    }

    public void LogOut(final String line, final String tag){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CharSequence data = txtBT.getText();
                CharSequence _line = String.format("[%s] %s%s", tag, line, System.getProperty("line.separator"));
                if(data.length() > 3048){
                    txtBT.setText(_line);
                    txtBT.setScrollY(0);
                }else {
                    txtBT.append(_line);
                }
                scrollLog();
            }
        });
    }

    public void enableAbortButton(final boolean enable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnAbortBT.setEnabled(enable);
            }
        });
    }

    public void enableConnectButton(final boolean enable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnConnectBT.setEnabled(enable);
            }
        });
    }

    private void btnAbortBT_onClick(){
        btnConnectBT.setEnabled(true);
        btnAbortBT.setEnabled(false);
        Shared.ble112.abortConnection();
    }

    private void btnConnectBT_onClick(){
        btnConnectBT.setEnabled(false);
        btnAbortBT.setEnabled(true);
        Shared.ble112.beginScan();
    }

    private void btnDash_onClick(){
        //startActivityForResult(new Intent(this, DashActivity.class), 1);
        Shared.bleReceiver.setBluetoothMask(0x00FF);
        startActivityForResult(new Intent(this, HUD.class), 1);
    }

    private void btnXMRadio_onClick(){
        Shared.bleReceiver.setBluetoothMask(0);
        startActivityForResult(new Intent(this, RadioActivity.class), 2);
    }

    private void btnDiagnostics_onClick(){
        Shared.bleReceiver.setBluetoothMask(0);
        startActivityForResult(new Intent(this, DiagnosticsActivity.class), 3);
    }

    private void btnDingDong_onClick(){

    }

    public void setScreenBrightness(int brightness){
        try {

            int mode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            if(mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);  //this will set the manual mode (set the automatic mode off)
            }

            if(brightness == 0){
                brightness = 0xFF;
            }

            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);

            //refreshes the screen
            int br = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = (float)br / 255.0f;
            getWindow().setAttributes(lp);

        } catch (Exception e) {Log.d("ble112", e.getMessage());}
    }

    public void setAutoScreenBrightness(){
        try {

            int mode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            if(mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  //this will set the manual mode (set the automatic mode off)
            }

        } catch (Exception e) {Log.d("ble112", e.getMessage());}
    }

}
