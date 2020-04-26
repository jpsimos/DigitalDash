package psimos.jacob.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by JacobPsimos on 1/30/2017.
 */

public class BlinkerArrow extends View {

    public final static byte ARROW_LEFT = 1;
    public final static byte ARROW_RIGHT = 2;

    private int _width = 0;
    private int _height = 0;
    private byte type = 0;
    private boolean fill = false;


    public BlinkerArrow(Context context) {
        super(context);
    }

    public BlinkerArrow(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public BlinkerArrow(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setType(byte type){
        this.type = type;
        this.invalidate();
    }

    public void setFilled(boolean fill){
        this.fill = fill;
        this.invalidate();
    }

    private void calculateDimensions(){
        _width = getWidth() - 3;
        _height = getHeight() - 3;
    }

    private void drawArrow(Canvas canvas){
        Path path = new Path();

        int tipX = type == ARROW_LEFT ? 3 : _width - 3;
        int tipY = _height / 2;

        int topPointX = type == ARROW_LEFT ? _width / 3 : _width - (_width / 3);
        int topPointY = 3;

        int topRectX = topPointX;
        int topRectY = _height / 3;

        int sideRectX = type == ARROW_LEFT ? _width - 3 : 3;
        int sideRectY = _height / 3;

        int downRectX = sideRectX;
        int downRectY = _height - _height / 3;

        int bottomRectX = topRectX;
        int bottomRectY = _height - _height / 3;

        int bottomPointX = topPointX;
        int bottomPointY = _height;


        path.moveTo(tipX, tipY);
        path.lineTo(topPointX, topPointY);
        path.lineTo(topRectX, topRectY);
        path.lineTo(sideRectX, sideRectY);
        path.lineTo(downRectX, downRectY);
        path.lineTo(bottomRectX, bottomRectY);
        path.lineTo(bottomPointX, bottomPointY);
        path.lineTo(tipX, tipY);

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);

        if(!fill) {
            paint.setStrokeWidth(2.0f);
            paint.setStyle(Paint.Style.STROKE);
        }else{
            paint.setStyle(Paint.Style.FILL);
        }

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateDimensions();
        drawArrow(canvas);
    }
}
