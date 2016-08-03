package com.runordie.rod.status;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

/**
 * Created by wsouza on 8/2/16.
 */
public class CircularProgressBarRun extends CircularProgressBar {
    Paint percentagePaint = new Paint();
    TextPaint textPaint = new TextPaint();
    private String runData = "00:00";

    public CircularProgressBarRun(Context context, AttributeSet attrs) {
        super(context, attrs);
        percentagePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        percentagePaint.setColor(getColor());
        percentagePaint.setTextSize(50);
        setColor(-10011977);
        setBackgroundColor(1298610871);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getColor());
        textPaint.setTextSize(50);
        Typeface tf = Typeface.create("", Typeface.BOLD);
        textPaint.setTypeface(tf);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas, textPaint, runData, percentagePaint);
    }

    private void drawText(Canvas canvas, Paint paint, String text, Paint percentagePaint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Rect percentageBounds = new Rect();
        percentagePaint.getTextBounds(".", 0, 1, percentageBounds);
        int x = (canvas.getWidth() / 2) - (bounds.width() / 2) - (percentageBounds.width() / 2);
        int y = (canvas.getHeight() / 2) + (bounds.height() / 2);
        canvas.drawText(text, x, y, paint);
    }

    public String getRunData() {
        return runData;
    }

    public void setRunData(String runData) {
        this.runData = runData;
    }

}
