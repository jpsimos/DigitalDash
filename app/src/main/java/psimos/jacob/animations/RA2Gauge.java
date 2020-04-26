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

public class RA2Gauge extends View {

    private int value = 0;
    private int maximum = 0;

    private float top;
    private float bottom;
    private float left;
    private float right;

    public RA2Gauge(Context context) {
        super(context);
    }
    public RA2Gauge(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public RA2Gauge(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setValue(final int value){
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDimensions();
        drawOutline(canvas);
        drawPieces(canvas);
    }

    private void drawPieces(Canvas canvas){
        final int numBoxes = 30;
        final float marginWidth = 5.0F;
        final float boxWidth = (right - left) / (float)numBoxes;
        final int fillBoxes = (int)(((float)value / (float)maximum) * (float)numBoxes);

        Paint paint = new Paint();

        for(int i = 0; i < fillBoxes; i++){
            float boxTop = top + 4.0F;
            float boxBottom = bottom - 4.0F;
            float boxLeft = (float)i * boxWidth + marginWidth;
            float boxRight = boxLeft + boxWidth - marginWidth;

            if(i <= numBoxes / 3){
                paint.setColor(Color.GREEN);
            }else if(i > numBoxes / 3 && i < ((numBoxes / 3) * 2)){
                paint.setColor(Color.YELLOW);
            }else{
                paint.setColor(Color.RED);
            }

            canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, paint);
        }
    }

    private void drawOutline(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void calculateDimensions(){
        top = 2.0F;
        bottom = (float)getHeight() - 2.0F;
        left = 2.0F;
        right = (float)getWidth() - 2.0F;
    }
}
