package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class Obstacle {
    public static final int TYPE_ROBOT = 0;
    public static final int TYPE_ELECTRIC_BARRIER = 1;
    public static final int TYPE_SPIKES = 2;
    public static final int TYPE_DRONE = 3;
    public static final int TYPE_LASER = 4;
    public static final int TYPE_FALLING = 5;

    public float x;
    public float y;
    public float width;
    public float height;
    public boolean isActive = false;
    public int type;

    protected final RectF bounds = new RectF();

    public abstract void init(float x, float groundY);
    public abstract void update(float gameSpeed);
    public abstract void draw(Canvas canvas, Paint paint, int envTintColor);

    public RectF getBounds() {
        // Tight hitbox for fair player dodge mechanics
        float padX = width * 0.12f;
        float padY = height * 0.10f;
        bounds.set(x + padX, y + padY, x + width - padX, y + height - padY);
        return bounds;
    }
}
