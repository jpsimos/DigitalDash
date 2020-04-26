package psimos.jacob.dashboard;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import psimos.jacob.animations.BlinkerArrow;
import psimos.jacob.animations.CircularGauge;
import psimos.jacob.animations.Compass;
import psimos.jacob.animations.RA2Gauge;
import psimos.jacob.animations.RampingGauge;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HUD extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private boolean mVisible;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnLongClickListener mLongTouchListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            HUD.this.finish();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_hud);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();

                if(mVisible){
                    delayedHide(3000);
                }
            }
        });

        mContentView.setOnLongClickListener(mLongTouchListener);

        HUD_onCreate();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        HUD_onPostCreate();
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private CircularGauge mphGauge = null;
    private CircularGauge rpmGauge = null;
    private TextView lblFuelGallons = null;
    private TextView lblRange = null;
    private TextView lblMPG = null;
    private TextView lblAvgMPG = null;
    private TextView lblGear = null;
    private RA2Gauge throttleGauge = null;
    private RA2Gauge manifoldGauge = null;
    private Compass compass = null;
    private BlinkerArrow leftBlinker = null;
    private BlinkerArrow rightBlinker = null;


    private void HUD_onCreate(){
        Shared.hudActivity = this;

        mphGauge = (CircularGauge)findViewById(R.id.hud_mphGauge);
        rpmGauge = (CircularGauge)findViewById(R.id.hud_rpmGauge);
        throttleGauge = (RA2Gauge) findViewById(R.id.hud_throttleGauge);
        manifoldGauge = (RA2Gauge)findViewById(R.id.hud_manifoldGauge);
        compass = (Compass)findViewById(R.id.hud_Compass);
        lblFuelGallons = (TextView)findViewById(R.id.hud_lblFuelGallons);
        lblRange = (TextView)findViewById(R.id.hud_lblRange);
        lblMPG = (TextView)findViewById(R.id.hud_lblMPG);
        lblGear = (TextView)findViewById(R.id.hud_lblGear);
        lblAvgMPG = (TextView)findViewById(R.id.hud_lblAvgMPG);
        leftBlinker = (BlinkerArrow)findViewById(R.id.hud_leftBlinker);
        rightBlinker = (BlinkerArrow)findViewById(R.id.hud_rightBlinker);
    }

    private void HUD_onPostCreate(){
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                mphGauge.setMaximum(100);
                rpmGauge.setMaximum(5200);
                mphGauge.setUnits("mph", 3);
                rpmGauge.setUnits("rpm", 4);
                rpmGauge.setDrawColor(Color.rgb(0xba, 0xf4, 0xba));

                throttleGauge.setMaximum(255);
                manifoldGauge.setMaximum(101);

                lblRange.setMaxWidth(lblRange.getWidth());
                lblRange.setMinWidth(lblRange.getWidth());
                lblFuelGallons.setMaxWidth(lblFuelGallons.getWidth());
                lblFuelGallons.setMinWidth(lblFuelGallons.getWidth());
                lblMPG.setMaxWidth(lblMPG.getWidth());
                lblMPG.setMinWidth(lblMPG.getWidth());
                lblAvgMPG.setMaxWidth(lblAvgMPG.getWidth());
                lblAvgMPG.setMinWidth(lblAvgMPG.getWidth());

                rightBlinker.setType(BlinkerArrow.ARROW_RIGHT);
                leftBlinker.setType(BlinkerArrow.ARROW_LEFT);
            }
        });
    }


    public void setMPHandRPM(final int mph, final int rpm, final int throttle, final int manifold, final int gear){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                mphGauge.setValue(mph);
                rpmGauge.setValue(rpm);
                throttleGauge.setValue(throttle);
                manifoldGauge.setValue(101 - manifold >= 1 ? 101 - manifold : 1);
                lblGear.setText(Html.fromHtml(getGearString(gear)), TextView.BufferType.SPANNABLE);
            }
        });
    }


    public void setCompass(final int elevation, final int heading){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                compass.update(elevation, heading);
            }
        });
    }

    public void setBlinkers(final boolean left, final boolean right){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftBlinker.setFilled(left);
                rightBlinker.setFilled(right);
            }
        });
    }

    public void setFuel(final double fuelGallons, final double mpg, final double avgMpg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblFuelGallons.setText(String.format("Fuel: %-4.1f gal", fuelGallons));
                lblRange.setText(String.format("Range: %-5.1f miles", fuelGallons * 15.0));
                lblMPG.setText(String.format("MPG: %.1f", mpg));
                lblAvgMPG.setText(String.format("MPG: %.1f", avgMpg));
            }
        });
    }

    public void correctWidth(TextView textView, int desiredWidth) {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private String getGearString(int gear){
        String text = "P R N 1 2 3 4";
        //textView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        switch(gear){
            case 0xF0:
                text = text.replace("P", "<font color='red'>P</font>");
                break;
            case 0xE0:
                text = text.replace("R", "<font color='red'>R</font>");
                break;
            case 0xD0:
                text = text.replace("N", "<font color='red'>N</font>");
                break;
            case 0x10:
                text = text.replace("1", "<font color='red'>1</font>");
                break;
            case 0x20:
                text = text.replace("2", "<font color='red'>2</font>");
                break;
            case 0x30:
                text = text.replace("3", "<font color='red'>3</font>");
                break;
            case 0x40:
                text = text.replace("4", "<font color='red'>4</font>");
                break;
        }
        return text;
    }

}
