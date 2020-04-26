package psimos.jacob.dashboard;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import psimos.jacob.XGATT.KnownUUIDs;

public class RadioActivity extends AppCompatActivity {

    private Button btnVolUp = null;
    private Button btnVolDown = null;
    private Button btnMute = null;
    private Button btnSource = null;
    private Button btnUp = null;
    private Button btnDown = null;
    private Button btnLeft = null;
    private Button btnRight = null;
    private Button btnInfo = null;

    final static int BUTTON_MUTE = 0x01;
    final static int BUTTON_VOL_DOWN = 0x02;
    final static int BUTTON_VOL_UP = 0x03;
    final static int BUTTON_RIGHT = 0x04;
    final static int BUTTON_LEFT = 0x05;
    final static int BUTTON_SOURCE = 0x06;
    final static int BUTTON_UP = 0x07;
    final static int BUTTON_UNKNOWN = 0x08;
    final static int BUTTON_DOWN = 0x09;
    final static int BUTTON_INFO = 0x0B;

    private boolean buttonDown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Shared.radioActivity = this;

        btnVolUp = (Button)findViewById(R.id.btnVolUp);
        btnVolUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        btnVolUp_onClick();
                        buttonDown = true;
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnVolDown = (Button)findViewById(R.id.btnVolDown);
        btnVolDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        buttonDown = true;
                        btnVolDown_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnMute = (Button)findViewById(R.id.btnMute);
        btnMute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown){
                        buttonDown = true;
                        btnMute_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnSource = (Button)findViewById(R.id.btnSource);
        btnSource.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        buttonDown = true;
                        btnSource_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnUp = (Button)findViewById(R.id.btnUp);
        btnUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        buttonDown = true;
                        btnUp_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnDown = (Button)findViewById(R.id.btnDown);
        btnDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        buttonDown = true;
                        btnDown_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown) {
                        buttonDown = true;
                        btnLeft_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnRight = (Button)findViewById(R.id.btnRight);
        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown){
                        buttonDown = true;
                        btnRight_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

        btnInfo = (Button)findViewById(R.id.btnInfo);
        btnInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!buttonDown){
                        buttonDown = true;
                        btnInfo_onClick();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    generic_button_onRelease();
                }
                return true;
            }
        });

    }

    private void generic_button_onRelease(){
        byte[] gattValue = new byte[1];
        gattValue[0] = 0;
        Shared.ble112.setValueForCharacteristic(KnownUUIDs.GATT_RADIO_BUTTON, gattValue);
        buttonDown = false;
    }

    private void btnVolUp_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_VOL_UP);
    }

    private void btnVolDown_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_VOL_DOWN);
    }

    private void btnMute_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_MUTE);
    }

    private void btnSource_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_SOURCE);
    }

    private void btnUp_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_UP);
    }

    private void btnDown_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_DOWN);
    }

    private void btnLeft_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_LEFT);
    }

    private void btnRight_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_RIGHT);
    }

    private void btnInfo_onClick(){
        Shared.bleReceiver.radioButtonCommand(BUTTON_INFO);
    }
}
