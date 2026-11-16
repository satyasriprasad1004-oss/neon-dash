package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class SpikeObstacle extends Obstacle {
    private int spikeCount = 2; // 1, 2, or 3 spikes

    public SpikeObstacle() {
        this.type = TYPE_SPIKES;
        this.width = 55.0f;
        this.height = 42.0f;
    }

    public void init(float startX, float groundY, int count) {
        this.spikeCount = Math.max(1, Math.min(3, count));
        this.width = spikeCount * 26.0f;
        this.height = 44.0f;
        this.x = startX;
        this.y = groundY - height;
        this.isActive = true;
    }

    @Override
    public void init(float startX, float groundY) {
        init(startX, groundY, 2);
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;
        x -= gameSpeed;
        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        float spikeWidth = width / spikeCount;

        for (int i = 0; i < spikeCount; i++) {
            float sx = x + i * spikeWidth;

            // Spike body
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#15182B"));

            Path spike = new Path();
            spike.moveTo(sx, y + height);
            spike.lineTo(sx + spikeWidth * 0.5f, y);
            spike.lineTo(sx + spikeWidth, y + height);
            spike.close();
            canvas.drawPath(spike, paint);

            // Glowing neon edges
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.5f);
            paint.setColor(envTintColor);
            canvas.drawPath(spike, paint);

            // Glowing core line
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(1.2f);
            canvas.drawLine(sx + spikeWidth * 0.5f, y, sx + spikeWidth * 0.5f, y + height * 0.7f, paint);
        }
    }

    @Override
    public RectF getBounds() {
        bounds.set(x + 4, y + 8, x + width - 4, y + height);
        return bounds;
    }
}
