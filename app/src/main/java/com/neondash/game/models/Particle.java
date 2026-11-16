package com.neondash.game.models;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Particle {
    public float x;
    public float y;
    public float vx;
    public float vy;
    public int color;
    public float size;
    public float life;
    public float maxLife;
    public boolean active = false;
    public String text = null;

    public void init(float x, float y, float vx, float vy, int color, float size, float life, String text) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.active = true;
        this.text = text;
    }

    public void update() {
        if (!active) return;
        x += vx;
        y += vy;
        life -= 1.0f;
        if (life <= 0) {
            active = false;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!active) return;
        float alpha = Math.max(0.0f, Math.min(1.0f, life / maxLife));
        int paintAlpha = (int) (alpha * 255);
        paint.setColor(color);
        paint.setAlpha(paintAlpha);

        if (text != null) {
            paint.setTextSize(size);
            paint.setStyle(Paint.Style.FILL);
            paint.setFakeBoldText(true);
            canvas.drawText(text, x, y, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, size * alpha, paint);
        }
    }
}
