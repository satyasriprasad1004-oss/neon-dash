package com.neondash.game.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.neondash.game.player.Player;

public class Coin {
    public float x;
    public float y;
    public float radius = 20.0f;
    public boolean isCollected = false;
    public boolean isActive = false;
    private float rotationAngle = 0.0f;
    private final RectF bounds = new RectF();

    public void init(float x, float y) {
        this.x = x;
        this.y = y;
        this.isCollected = false;
        this.isActive = true;
        this.rotationAngle = (float) (Math.random() * 360.0);
    }

    public void update(float gameSpeed, Player player, boolean isMagnetActive, float magnetRadius) {
        if (!isActive) return;

        // Magnet attraction physics
        if (isMagnetActive && player != null && !player.isCrashed()) {
            float dx = player.getCenterX() - x;
            float dy = player.getCenterY() - y;
            float distSq = dx * dx + dy * dy;

            if (distSq < magnetRadius * magnetRadius) {
                float dist = (float) Math.sqrt(distSq);
                if (dist > 1.0f) {
                    float pullSpeed = 22.0f;
                    x += (dx / dist) * pullSpeed;
                    y += (dy / dist) * pullSpeed;
                }
            } else {
                x -= gameSpeed;
            }
        } else {
            x -= gameSpeed;
        }

        rotationAngle += 6.0f;
        if (x + radius < -100) {
            isActive = false;
        }
    }

    public RectF getBounds() {
        bounds.set(x - radius, y - radius, x + radius, y + radius);
        return bounds;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isActive || isCollected) return;

        canvas.save();
        canvas.translate(x, y);

        // 3D rotation effect by scaling X with cosine
        float scaleX = (float) Math.abs(Math.cos(Math.toRadians(rotationAngle)));
        scaleX = Math.max(0.18f, scaleX);
        canvas.scale(scaleX, 1.0f);

        // Outer glow
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        paint.setColor(Color.parseColor("#FFE600"));
        canvas.drawCircle(0, 0, radius, paint);

        // Inner fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FFB700"));
        canvas.drawCircle(0, 0, radius - 2, paint);

        // Core cyber diamond/hexagon
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FFF380"));
        Path core = new Path();
        float rCore = radius * 0.55f;
        core.moveTo(0, -rCore);
        core.lineTo(rCore * 0.8f, 0);
        core.lineTo(0, rCore);
        core.lineTo(-rCore * 0.8f, 0);
        core.close();
        canvas.drawPath(core, paint);

        canvas.restore();
    }
}
