package com.runordie.rod.status;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.runordie.rod.R;

/**
 * Created by wsouza on 8/2/16.
 */
public class CircularProgressBarPercent extends CircularProgressBar {
    Paint percentagePaint = new Paint();
    TextPaint textPaint = new TextPaint();
    private float realPercent = 0;

    public CircularProgressBarPercent(Context context, AttributeSet attrs) {
        super(context, attrs);
        percentagePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        percentagePaint.setColor(getColor());
        percentagePaint.setTextSize(100);

        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getColor());
        textPaint.setTextSize(100);
//        Typeface tf = Typeface.create("", Typeface.BOLD);
//        textPaint.setTypeface(tf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String percent = null;

        if(realPercent >  100 && getProgress() == 99.999f){
            percent = String.format("%.1f", realPercent);
        }else{
            percent = String.format("%.1f", getProgress());
        }
        drawText(canvas, textPaint, percent , percentagePaint);
    }

    private void drawText(Canvas canvas, Paint paint, String text, Paint percentagePaint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Rect percentageBounds = new Rect();
        percentagePaint.getTextBounds("%", 0, 1, percentageBounds);
        int x = (canvas.getWidth() / 2) - (bounds.width() / 2) - (percentageBounds.width() / 2);
        int y = (canvas.getHeight() / 2) + (bounds.height() / 2);
        canvas.drawText(text, x, y, paint);
        canvas.drawText("%", x + bounds.width() + percentageBounds.width() / 2, y - bounds.height() + percentageBounds.height(), percentagePaint);
    }

    public float getRealPercent() {
        return realPercent;
    }

    public void setRealPercent(float realPercent) {
        this.realPercent = realPercent;
    }

}
