package com.kaora.anunciosapp.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.facebook.drawee.drawable.ProgressBarDrawable;

public class ImageLoadProgressBar extends ProgressBarDrawable {

    private float level;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF oval = new RectF();

    public ImageLoadProgressBar(){
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onLevelChange(int level) {
        this.level = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        int radius = 5;
        oval.set(canvas.getWidth() / 2 - radius, canvas.getHeight() / 2 - radius,
                canvas.getWidth() / 2 + radius, canvas.getHeight() / 2 + radius);

        int color = android.R.color.darker_gray;
        drawCircle(canvas, level, color);
    }


    private void drawCircle(Canvas canvas, float level, int color) {
        paint.setColor(color);
        float angle;
        angle = 360 / 1f;
        angle = level * angle;
        canvas.drawArc(oval, 0, Math.round(angle), false, paint);
    }

}