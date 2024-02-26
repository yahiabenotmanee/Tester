package com.example.tester;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircularSeekBar extends View {

    private Paint circlePaint;
    private Paint arcPaint;
    private RectF rectF;
    private float angle = 0;

    private  int Blue_v1 = Color.parseColor("#4586D3");
    private  int Blue_v2 = Color.parseColor("#c2c8cf");



    public CircularSeekBar(Context context) {
        super(context);
        init();
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(Blue_v2);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);

        arcPaint = new Paint();
        //arcPaint.setColor(Color.BLUE);
        arcPaint.setColor(Blue_v1);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(20);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int diameter = Math.min(width, height); // Use the smaller dimension as the diameter
        int radius = diameter / 2;

        // Calculate the center coordinates considering padding
        int centerX = getPaddingLeft() + width / 2;
        int centerY = getPaddingTop() + height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw the outer circle with slightly scaled down radius
        canvas.drawCircle(centerX, centerY, radius * 0.9f, circlePaint);

        // Adjust RectF to ensure entire arc is visible
        rectF.set(centerX - radius * 0.9f, centerY - radius * 0.9f, centerX + radius * 0.9f, centerY + radius * 0.9f);

        // Draw the arc
        canvas.drawArc(rectF, -90, angle, false, arcPaint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
                if (angle < 0) {
                    angle += 360;
                }
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

}
