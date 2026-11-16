package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.Random;

public class ElectricBarrierObstacle extends Obstacle {
    public boolean isHigh = false; // if true, hovering overhead (slide under)
    private final Random random = new Random();
    private float pulse = 0.0f;

    public ElectricBarrierObstacle() {
        this.type = TYPE_ELECTRIC_BARRIER;
        this.width = 38.0f;
        this.height = 68.0f;
    }

    public void init(float startX, float groundY, boolean isHigh) {
        this.isHigh = isHigh;
        this.x = startX;
        this.isActive = true;
        this.pulse = 0.0f;

        if (isHigh) {
            this.width = 44.0f;
            this.height = 54.0f;
            // Positioned overhead: bottom is at groundY - 56.0f, leaving clean clearance for 36.0f slide!
            this.y = groundY - 110.0f;
        } else {
            this.width = 38.0f;
            this.height = 68.0f;
            this.y = groundY - height;
        }
    }

    @Override
    public void init(float startX, float groundY) {
        init(startX, groundY, false);
    }

    @Override
    public void update(float gameSpeed) {
        if (!isActive) return;
        pulse += 0.35f;
        x -= gameSpeed;

        if (x + width < -100) {
            isActive = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int envTintColor) {
        if (!isActive) return;

        canvas.save();

        // Metallic Pylons (top & bottom emitters)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#1A1D36"));

        // Left & Right / Top & Bottom emitters
        RectF topPylon = new RectF(x + 4, y, x + width - 4, y + 14);
        RectF bottomPylon = new RectF(x + 4, y + height - 14, x + width - 4, y + height);
        canvas.drawRoundRect(topPylon, 4, 4, paint);
        canvas.drawRoundRect(bottomPylon, 4, 4, paint);

        // Neon Glow border on emitters
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(envTintColor);
        canvas.drawRoundRect(topPylon, 4, 4, paint);
        canvas.drawRoundRect(bottomPylon, 4, 4, paint);

        // Crackling electric lightning arcs
        paint.setColor(Color.parseColor("#00F0FF"));
        paint.setStrokeWidth(3.0f);

        Path lightning = new Path();
        float midX = x + width * 0.5f;
        lightning.moveTo(midX, y + 14);

        int segments = 5;
        float segHeight = (height - 28) / segments;
        for (int i = 1; i < segments; i++) {
            float jitter = (random.nextFloat() - 0.5f) * 16.0f;
            lightning.lineTo(midX + jitter, y + 14 + i * segHeight);
        }
        lightning.lineTo(midX, y + height - 14);
        canvas.drawPath(lightning, paint);

        // Second lighter core arc
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1.5f);
        canvas.drawPath(lightning, paint);

        canvas.restore();
    }
}
