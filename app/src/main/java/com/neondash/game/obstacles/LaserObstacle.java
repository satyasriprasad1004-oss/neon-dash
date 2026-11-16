package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class LaserObstacle extends Obstacle {
    private float pulse = 0.0f;
    public boolean isPulsing = false;
    private boolean beamOn = true;

    public LaserObstacle() {
        this.type = TYPE_LASER;
        this.width = 30.0f;
        this.height = 70.0f;
    }

    @Override
    public void init(float startX, float groundY) {
        this.x = startX;
        this.width = 32.0f;
        this.height = 74.0f;
        this.y = groundY - height;
        this.isActive = true;
        this.pulse = 0.0f;
        this.beamOn = true;
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;
        pulse += 0.2f;
        x -= gameSpeed;

        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        canvas.save();

        // Laser Base Generator at ground
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15182B"));
        RectF base = new RectF(x + 2, y + height - 14, x + width - 2, y + height);
        canvas.drawRoundRect(base, 4, 4, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(envTintColor);
        canvas.drawRoundRect(base, 4, 4, paint);

        // Top emitter node
        RectF topNode = new RectF(x + 4, y, x + width - 4, y + 12);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15182B"));
        canvas.drawRoundRect(topNode, 3, 3, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(envTintColor);
        canvas.drawRoundRect(topNode, 3, 3, paint);

        // Laser Beam
        float midX = x + width * 0.5f;
        float beamGlowWidth = 10.0f + (float) Math.sin(pulse) * 3.0f;

        // Outer glow
        paint.setColor(Color.parseColor("#FF007F"));
        paint.setAlpha(140);
        paint.setStrokeWidth(beamGlowWidth);
        canvas.drawLine(midX, y + 12, midX, y + height - 14, paint);

        // Intense core
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        paint.setStrokeWidth(3.0f);
        canvas.drawLine(midX, y + 12, midX, y + height - 14, paint);

        canvas.restore();
    }

    @Override
    public RectF getBounds() {
        bounds.set(x + 6, y + 4, x + width - 6, y + height);
        return bounds;
    }
}
