package com.neondash.game.powerups;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class PowerUp {
    public static final int TYPE_MAGNET = 0;
    public static final int TYPE_SHIELD = 1;
    public static final int TYPE_SPEED_BOOST = 2;
    public static final int TYPE_SLOW_MOTION = 3;
    public static final int TYPE_DOUBLE_JUMP = 4;

    public int type;
    public float x;
    public float y;
    public float radius = 24.0f;
    public boolean isActive = false;
    private float bobAngle = 0.0f;
    private final RectF bounds = new RectF();

    public void init(int type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isActive = true;
        this.bobAngle = (float) (Math.random() * 360.0);
    }

    public void update(float gameSpeed) {
        if (!isActive) return;
        x -= gameSpeed;
        bobAngle += 5.0f;
        if (x + radius < -100) {
            isActive = false;
        }
    }

    public RectF getBounds() {
        float bobY = y + (float) Math.sin(Math.toRadians(bobAngle)) * 8.0f;
        bounds.set(x - radius, bobY - radius, x + radius, bobY + radius);
        return bounds;
    }

    public int getColor() {
        switch (type) {
            case TYPE_MAGNET: return Color.parseColor("#00F0FF"); // Cyan
            case TYPE_SHIELD: return Color.parseColor("#39FF14"); // Green
            case TYPE_SPEED_BOOST: return Color.parseColor("#FFE600"); // Yellow
            case TYPE_SLOW_MOTION: return Color.parseColor("#0080FF"); // Blue
            case TYPE_DOUBLE_JUMP: return Color.parseColor("#FF007F"); // Magenta
            default: return Color.WHITE;
        }
    }

    public String getName() {
        switch (type) {
            case TYPE_MAGNET: return "MAGNET";
            case TYPE_SHIELD: return "SHIELD";
            case TYPE_SPEED_BOOST: return "BOOST";
            case TYPE_SLOW_MOTION: return "SLOW-MO";
            case TYPE_DOUBLE_JUMP: return "2X JUMP";
            default: return "POWER";
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isActive) return;

        float bobY = y + (float) Math.sin(Math.toRadians(bobAngle)) * 8.0f;
        int color = getColor();

        canvas.save();
        canvas.translate(x, bobY);

        // Outer glowing diamond container
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.5f);
        paint.setColor(color);
        paint.setAlpha(255);

        Path diamond = new Path();
        float dSize = radius * 1.25f;
        diamond.moveTo(0, -dSize);
        diamond.lineTo(dSize, 0);
        diamond.lineTo(0, dSize);
        diamond.lineTo(-dSize, 0);
        diamond.close();
        canvas.drawPath(diamond, paint);

        // Inner semi-transparent fill
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(70);
        canvas.drawPath(diamond, paint);

        // Icon emblem inside
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
        paint.setColor(Color.WHITE);

        float s = radius * 0.6f;
        switch (type) {
            case TYPE_MAGNET:
                // U-shape horseshoe magnet
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5.0f);
                paint.setColor(color);
                RectF uRect = new RectF(-s * 0.7f, -s * 0.8f, s * 0.7f, s * 0.6f);
                canvas.drawArc(uRect, 0, 180, false, paint);
                canvas.drawLine(-s * 0.7f, -s * 0.8f, -s * 0.7f, -s * 0.2f, paint);
                canvas.drawLine(s * 0.7f, -s * 0.8f, s * 0.7f, -s * 0.2f, paint);
                break;

            case TYPE_SHIELD:
                // Shield crest
                Path crest = new Path();
                crest.moveTo(0, -s);
                crest.lineTo(s * 0.8f, -s * 0.4f);
                crest.lineTo(s * 0.6f, s * 0.7f);
                crest.lineTo(0, s);
                crest.lineTo(-s * 0.6f, s * 0.7f);
                crest.lineTo(-s * 0.8f, -s * 0.4f);
                crest.close();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(color);
                canvas.drawPath(crest, paint);
                break;

            case TYPE_SPEED_BOOST:
                // Lightning bolt
                Path bolt = new Path();
                bolt.moveTo(s * 0.2f, -s);
                bolt.lineTo(-s * 0.6f, 0);
                bolt.lineTo(0, 0);
                bolt.lineTo(-s * 0.2f, s);
                bolt.lineTo(s * 0.6f, -s * 0.1f);
                bolt.lineTo(0, -s * 0.1f);
                bolt.close();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(color);
                canvas.drawPath(bolt, paint);
                break;

            case TYPE_SLOW_MOTION:
                // Clock ring with hands
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3.0f);
                paint.setColor(color);
                canvas.drawCircle(0, 0, s * 0.85f, paint);
                canvas.drawLine(0, 0, 0, -s * 0.55f, paint);
                canvas.drawLine(0, 0, s * 0.4f, 0, paint);
                break;

            case TYPE_DOUBLE_JUMP:
                // Double up-chevrons
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3.5f);
                paint.setColor(color);
                Path arrow1 = new Path();
                arrow1.moveTo(-s * 0.6f, -s * 0.1f);
                arrow1.lineTo(0, -s * 0.7f);
                arrow1.lineTo(s * 0.6f, -s * 0.1f);
                canvas.drawPath(arrow1, paint);

                Path arrow2 = new Path();
                arrow2.moveTo(-s * 0.6f, s * 0.5f);
                arrow2.lineTo(0, -s * 0.1f);
                arrow2.lineTo(s * 0.6f, s * 0.5f);
                canvas.drawPath(arrow2, paint);
                break;
        }

        canvas.restore();
    }
}
