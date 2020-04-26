package psimos.jacob.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jacob on 1/28/2017.
 */

public class RampingGauge extends View {

    private int _width = 0;
    private int _height = 0;

    private double value = 0.0;
    private double maximum = 100.0;
    private double minimum = 0;
    private double changeValue = 0.0;

    public RampingGauge(Context context) {
        super(context);
    }

    public RampingGauge(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public RampingGauge(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    private void calculateDimensions(){
        _width = getWidth() - 3;
        _height = getHeight() - 3;
    }

    public void setMaximum(final double maximum){
        if(this.maximum != maximum) {
            this.maximum = maximum;
            this.invalidate();
        }
    }

    public void setMinimum(final double minimum){
        if(this.minimum != minimum) {
            this.minimum = minimum;
            this.invalidate();
        }
    }

    public void setChangeValue(final double changeValue){
        if(this.changeValue != changeValue){
            this.changeValue = changeValue;
            this.invalidate();
        }
    }

    public void setValue(final double value){
        if(this.value != value) {
            this.value = value > 0.0 ? (value <= maximum ? (value >= minimum ? value : minimum) : maximum) : 0.0;
            this.invalidate();
        }
    }

    private void drawOutline(Canvas canvas){
        Paint edgePaint = new Paint();
        edgePaint.setColor(Color.WHITE);
        edgePaint.setStyle(Paint.Style.STROKE);

        Path path = new Path();
        path.reset();

        path.moveTo(0, _height);
        path.lineTo(_width, 0);
        path.lineTo(_width, _height);
        path.lineTo(0, _height);

        canvas.drawPath(path, edgePaint);
    }

    private void fillValue(Canvas canvas){
        double fillTo = (double)_width / (100.0 / (value * 100.0 / maximum));
        double RightY = (double)_height - ((double)_height * (fillTo / (double)_width));
        double RightX = fillTo;

        Path fillPath = new Path();
        fillPath.moveTo(0F, (float)_height);
        fillPath.lineTo((float)RightX, (float)RightY);
        fillPath.lineTo((float)RightX, (float)_height);
        fillPath.lineTo(0F, (float)_height);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((value > changeValue) ? Color.RED : Color.GREEN);
        canvas.drawPath(fillPath, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDimensions();
        drawOutline(canvas);
        fillValue(canvas);
    }

    private double deg2rad(final double deg){
        return deg * (Math.PI / 180.0);
    }

    private double rad2deg(final double rad){
        return rad * (180.0 / Math.PI);
    }
}
