package psimos.jacob.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jacob on 3/2/2017.
 */

public class CircularGauge extends View {

    private int value = 0;
    private int maximum = 0;
    private String strValueFormat = "%-3d %s";
    private String units = "";

    private float top;
    private float bottom;
    private float left;
    private float right;
    private float circleRadius;
    private float centerX;
    private float centerY;

    final int LIGHT_GREEN = Color.rgb(0, 255, 0);
    private int drawColor = LIGHT_GREEN;

    public CircularGauge(Context context) {
        super(context);
    }
    public CircularGauge(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public CircularGauge(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setDrawColor(final int color){
        this.drawColor = color;
    }

    public void setValue(int value){
        value = value >= maximum ? maximum : value;
        if(this.value != value){
            this.value = value;
            this.invalidate();
        }
    }

    public void setMaximum(final int maximum){
        if(this.maximum != maximum){
            this.maximum = maximum;
            this.invalidate();
        }
    }

    public void setUnits(final String units, final int padding){
        this.units = units;
        strValueFormat = "%-" + String.valueOf(padding) + "d %s";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDimensions();
        drawOutline(canvas);
        drawPointer(canvas);
    }

    private void drawPointer(Canvas canvas){
        double thetaDegrees = (double)value / (double)maximum * 180.0;
        double thetaRadians = Math.toRadians(-1.0 * thetaDegrees + 180.0);

        float edgeX = centerX + (float)(Math.cos(thetaRadians) * circleRadius);
        float edgeY = centerY - (float)(Math.sin(thetaRadians) * circleRadius);

        final float bulbDiameter = 40.0F;
        float bulbX = centerX - (bulbDiameter / 2.0F);
        float bulbY = centerY - (bulbDiameter / 2.0F);

        Paint paint = new Paint();
        paint.setColor(drawColor);
        paint.setStrokeWidth(10.0F);
        paint.setTextSize(52.0F);

        canvas.drawLine(centerX, centerY, edgeX, edgeY, paint);
        canvas.drawOval(bulbX, bulbY, bulbX + bulbDiameter, bulbY + bulbDiameter, paint);

        String strValue = String.format(strValueFormat, value, units);
        float strWidth = getTextWidth(strValue, paint);
        float textX = centerX - strWidth / 2.0F;
        float textY = centerY + 48.0F * 2.0F;

        //paint.setColor(value > maximum / 2 ? Color.RED : drawColor);
        paint.setColor(drawColor);

        canvas.drawText(strValue, textX, textY, paint);
    }

    private void drawOutline(Canvas canvas){
        final double longTick = 30.0;
        final double shortTick = 10.0;

        Paint paint = new Paint();

        for(int i = 0; i <= 180; i++){
            if(i % 5 != 0 && i % 30 != 0){ continue; }

            double tickLength = (i % 30 == 0) ? longTick : shortTick;
            double theta = Math.toRadians((double)i);
            float beginX = centerX + (float)(Math.cos(theta) * (circleRadius - tickLength));
            float beginY = centerY - (float)(Math.sin(theta) * (circleRadius - tickLength));
            float edgeX = centerX + (float)(Math.cos(theta) * circleRadius);
            float edgeY = centerY - (float)(Math.sin(theta) * circleRadius);


            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0F);

            canvas.drawLine(beginX, beginY, edgeX, edgeY, paint);
        }

        paint.setStrokeWidth(1.0F);
        canvas.drawOval(left, top, right, bottom, paint);
    }

    private void calculateDimensions(){
        top = 2.0F;
        bottom = (float)getHeight() - 2.0F;
        left = 0F;
        right = (float)getWidth() - 2.0F;
        centerX = (right - left) / 2.0F;
        centerY = ((bottom - top) / 2.0F) + top;
        circleRadius = ((right - left) / 2.0F) + left;
    }

    private float getTextWidth(final String string, final Paint paint){
        final int stringLength = string.length();
        float[] widths = new float[stringLength];
        paint.getTextWidths(string, widths);

        float total = 0.0F;
        for(int i = 0; i < stringLength; i++){
            total += widths[i];
        }
        return total;
    }
}
