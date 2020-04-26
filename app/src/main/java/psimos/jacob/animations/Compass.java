package psimos.jacob.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jacob on 3/1/2017.
 */

public class Compass extends View {

    private float left;
    private float right;
    private float top;
    private float bottom;
    private float circleDiameter;

    /* Compass Values */
    private int elevation = 0;
    private int degrees = 0;
    private String heading = "";

    public Compass(Context context) {
        super(context);
    }
    public Compass(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public Compass(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void update(final int elevation, final int degrees){
        if(this.elevation != elevation || this.degrees != degrees){
            this.elevation = elevation;
            this.degrees = degrees;
            updateDirection();
            this.invalidate();
        }
    }

    private void calculateDimensions(){
        left = 2.0F;
        right = (float)getWidth() - 2.0F;
        top = 2.0F;
        bottom = (float)getHeight() - 2.0F;
    }

    private void drawOutline(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawOval(left, top, bottom, bottom, paint);
    }

    private void drawStrings(Canvas canvas){

        float fontHeight = 40.0F;

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(fontHeight);

        String strElevation = String.format("%-3d ft", elevation);
        String strDegrees = String.format("%d° %s", degrees, heading);

        float widthOfFirstLine = getTextWidth(strElevation, paint);

        float textCenterX = (float)right / 2.0F;
        float textCenterY = (float)bottom / 2.0F;
        textCenterX -= widthOfFirstLine / 2.0F;

        canvas.drawText(strElevation, textCenterX, textCenterY, paint);
        canvas.drawText(strDegrees, textCenterX, textCenterY + fontHeight, paint);
    }

    private void drawPointer(Canvas canvas){
        float tickLength = 40.0F;
        float centerX = right / 2.0F;
        float centerY = bottom / 2.0F;
        double circleRadius = ((double)bottom - (double)top) / 2.0;
        double thetaDegrees = 360.0 - (double)degrees - 270.0;
        double thetaRadians = Math.toRadians(thetaDegrees);

        float edgeX = (float)(Math.cos(thetaRadians) * circleRadius);
        float edgeY = (float)(Math.sin(thetaRadians) * circleRadius);
        float beginX = (float)(Math.cos(thetaRadians) * (circleRadius - tickLength));
        float beginY = (float)(Math.sin(thetaRadians) * (circleRadius - tickLength));

        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStrokeWidth(10.0F);

        canvas.drawLine(centerX + beginX, centerY - beginY, centerX + edgeX, centerY - edgeY, paint);
    }

    private void drawTickMarks(Canvas canvas){
        float littleTickLength = 10.0F;
        float bigTickLength = 30.0F;
        float centerX = right / 2.0F;
        float centerY = bottom / 2.0F;
        double circleRadius = ((double)bottom - (double)top) / 2.0;

        for(int i = 0; i < 360; i++){
            if(i % 45 != 0 && i % 5 != 0){
                continue;
            }

            double tickLength = (i % 45 == 0) ? bigTickLength : littleTickLength;

            double thetaDegrees = 360.0 - (double)i - 270.0;
            double thetaRadians = Math.toRadians(thetaDegrees);
            float edgeX = (float)(Math.cos(thetaRadians) * circleRadius);
            float edgeY = (float)(Math.sin(thetaRadians) * circleRadius);
            float beginX = (float)(Math.cos(thetaRadians) * (circleRadius - tickLength));
            float beginY = (float)(Math.sin(thetaRadians) * (circleRadius - tickLength));

            Paint paint = new Paint();
            paint.setColor(Color.rgb(0, 0xF2, 0xFD));

            canvas.drawLine(centerX + beginX, centerY - beginY, centerX + edgeX, centerY - edgeY, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDimensions();
        drawTickMarks(canvas);
        drawPointer(canvas);
        drawStrings(canvas);
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

    private void updateDirection(){
        if((degrees >= 340 && degrees <= 360) || (degrees >= 0 && degrees <= 20)){
            heading = "N";
        }else if(degrees >= 21 && degrees <= 70){
            heading = "NE";
        }else if(degrees >= 71 && degrees <= 110){
            heading = "E";
        }else if(degrees >= 111 && degrees <= 160){
            heading = "SE";
        }else if(degrees >= 161 && degrees <= 200){
            heading = "S";
        }else if(degrees >= 201 && degrees <= 250){
            heading = "SW";
        }else if(degrees >= 251 && degrees <= 290){
            heading = "W";
        }else if(degrees >= 291 && degrees <= 339) {
            heading = "NW";
        }
    }
}
