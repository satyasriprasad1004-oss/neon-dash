package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class FallingObjectObstacle extends Obstacle {
    private float groundY;
    private float fallVelocity = 0.0f;
    private float fallGravity = 1.4f;
    private boolean isLanded = false;
    private float warningTimer = 35.0f; // frames of warning before falling

    public FallingObjectObstacle() {
        this.type = TYPE_FALLING;
        this.width = 46.0f;
        this.height = 46.0f;
    }

    @Override
    public void init(float startX, float groundY) {
        this.x = startX;
        this.groundY = groundY;
        this.y = -80.0f; // start off-screen at top
        this.fallVelocity = 0.0f;
        this.isLanded = false;
        this.warningTimer = 35.0f;
        this.isActive = true;
    }

    @Override
    public RectF getBounds() {
        if (warningTimer > 0 || (!isLanded && y < 0)) {
            bounds.set(-9999, -9999, -9999, -9999);
            return bounds;
        }
        return super.getBounds();
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;

        x -= gameSpeed;

        if (warningTimer > 0) {
            warningTimer -= 1.0f;
        } else if (!isLanded) {
            fallVelocity += fallGravity;
            y += fallVelocity;
            if (y + height >= groundY) {
                y = groundY - height;
                isLanded = true;
            }
        }

        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        canvas.save();

        // Warning target beam projected onto ground before falling
        if (warningTimer > 0 && !isLanded) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            paint.setColor(Color.parseColor("#FF1744"));
            // Target exclamation indicator on ground
            canvas.drawCircle(x + width * 0.5f, groundY - 10, 16.0f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("!", x + width * 0.5f - 4, groundY - 2, paint);
        }

        // Falling crate / debris object
        if (y + height > 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#1F223D"));
            RectF crate = new RectF(x, y, x + width, y + height);
            canvas.drawRoundRect(crate, 6, 6, paint);

            // Glowing cyber hazard borders
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.5f);
            paint.setColor(Color.parseColor("#FFE600"));
            canvas.drawRoundRect(crate, 6, 6, paint);

            // Hazard diagonal stripe
            canvas.drawLine(x + 6, y + 6, x + width - 6, y + height - 6, paint);
            canvas.drawLine(x + 6, y + height - 6, x + width - 6, y + 6, paint);
        }

        canvas.restore();
    }
}
