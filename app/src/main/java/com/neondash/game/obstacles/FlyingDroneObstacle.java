package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class FlyingDroneObstacle extends Obstacle {
    private float rotorAngle = 0.0f;
    private float hoverOffset = 0.0f;
    private float hoverTimer = 0.0f;

    public FlyingDroneObstacle() {
        this.type = TYPE_DRONE;
        this.width = 60.0f;
        this.height = 36.0f;
    }

    @Override
    public void init(float startX, float groundY) {
        this.x = startX;
        this.width = 60.0f;
        this.height = 36.0f;
        // Positioned overhead: bottom is at groundY - 56.0f, leaving clean clearance for sliding player!
        this.y = groundY - 92.0f;
        this.isActive = true;
        this.rotorAngle = 0.0f;
        this.hoverTimer = (float) (Math.random() * 10.0);
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;
        rotorAngle += 24.0f;
        hoverTimer += 0.12f;
        hoverOffset = (float) Math.sin(hoverTimer) * 4.0f;
        // Drones fly toward the player slightly faster than background
        x -= (gameSpeed + 1.2f);

        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        float currY = y + hoverOffset;

        canvas.save();

        // Downward spotlight cone (faint red/cyan)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15FF1744"));
        Path spot = new Path();
        spot.moveTo(x + width * 0.5f, currY + height);
        spot.lineTo(x - 16, currY + height + 40);
        spot.lineTo(x + width + 16, currY + height + 40);
        spot.close();
        canvas.drawPath(spot, paint);

        // Drone main fuselage
        paint.setColor(Color.parseColor("#171A2E"));
        RectF fuselage = new RectF(x + 12, currY + 10, x + width - 12, currY + 28);
        canvas.drawRoundRect(fuselage, 6, 6, paint);

        // Fuselage glowing outline
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(envTintColor);
        canvas.drawRoundRect(fuselage, 6, 6, paint);

        // Rotating rotor blades (Left & Right)
        float bladeSpan = 18.0f;
        float rScale = (float) Math.cos(Math.toRadians(rotorAngle));

        paint.setColor(Color.parseColor("#00F0FF"));
        paint.setStrokeWidth(2.5f);
        // Left rotor
        canvas.drawLine(x + 8 - bladeSpan * rScale, currY + 4, x + 8 + bladeSpan * rScale, currY + 4, paint);
        // Right rotor
        canvas.drawLine(x + width - 8 - bladeSpan * rScale, currY + 4, x + width - 8 + bladeSpan * rScale, currY + 4, paint);

        // Strobe beacon light
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FF1744"));
        canvas.drawCircle(x + width * 0.5f, currY + 19, 3.5f, paint);

        canvas.restore();
    }

    @Override
    public RectF getBounds() {
        float currY = y + hoverOffset;
        bounds.set(x + 6, currY + 4, x + width - 6, currY + height - 2);
        return bounds;
    }
}
