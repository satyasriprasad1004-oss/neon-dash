package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class MovingRobotObstacle extends Obstacle {
    private float animTimer = 0.0f;
    private float patrolOffset = 0.0f;
    private float patrolSpeed = 2.5f;

    public MovingRobotObstacle() {
        this.type = TYPE_ROBOT;
        this.width = 68.0f;
        this.height = 62.0f;
    }

    @Override
    public void init(float startX, float groundY) {
        this.x = startX;
        this.y = groundY - height;
        this.isActive = true;
        this.animTimer = (float) (Math.random() * 10.0);
        this.patrolOffset = 0.0f;
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;
        animTimer += 0.25f;
        patrolOffset = (float) Math.sin(animTimer) * 14.0f;
        // Moves forward relative to background
        x -= (gameSpeed + patrolSpeed);

        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        float drawX = x + patrolOffset;
        float legCycle = (float) Math.sin(animTimer * 2.0f) * 10.0f;

        canvas.save();

        // 4 Spider/hound robotic legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);
        paint.setColor(Color.parseColor("#3A3E60"));

        // Back legs
        canvas.drawLine(drawX + 16, y + 36, drawX + 6 - legCycle, y + height, paint);
        canvas.drawLine(drawX + 48, y + 36, drawX + 58 + legCycle, y + height, paint);

        // Robotic chassis body
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#171A2E"));
        RectF chassis = new RectF(drawX + 12, y + 14, drawX + width - 12, y + 44);
        canvas.drawRoundRect(chassis, 8, 8, paint);

        // Armor outline
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setColor(envTintColor);
        canvas.drawRoundRect(chassis, 8, 8, paint);

        // Front legs
        paint.setColor(envTintColor);
        paint.setStrokeWidth(5.0f);
        canvas.drawLine(drawX + 22, y + 36, drawX + 12 + legCycle, y + height, paint);
        canvas.drawLine(drawX + 42, y + 36, drawX + 52 - legCycle, y + height, paint);

        // Red pulsating scanner eye
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FF1744"));
        float eyeSweep = (float) Math.sin(animTimer * 1.5f) * 8.0f;
        canvas.drawCircle(drawX + 34 + eyeSweep, y + 26, 4.5f, paint);

        canvas.restore();
    }

    @Override
    public RectF getBounds() {
        float drawX = x + patrolOffset;
        bounds.set(drawX + 8, y + 10, drawX + width - 8, y + height);
        return bounds;
    }
}
