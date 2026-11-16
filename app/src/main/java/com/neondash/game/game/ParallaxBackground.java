package com.neondash.game.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import com.neondash.game.models.GameEnvironment;

public class ParallaxBackground {
    private GameEnvironment environment;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float scrollFar = 0.0f;
    private float scrollMid = 0.0f;
    private float scrollNear = 0.0f;
    private float scrollGround = 0.0f;
    private float gridPulse = 0.0f;

    public ParallaxBackground(GameEnvironment environment) {
        this.environment = environment;
    }

    public void setEnvironment(GameEnvironment environment) {
        this.environment = environment;
    }

    public void update(float gameSpeed) {
        scrollFar = (scrollFar + gameSpeed * 0.12f) % 2000.0f;
        scrollMid = (scrollMid + gameSpeed * 0.35f) % 1800.0f;
        scrollNear = (scrollNear + gameSpeed * 0.65f) % 1500.0f;
        scrollGround = (scrollGround + gameSpeed) % 80.0f;
        gridPulse += 0.04f;
    }

    public void draw(Canvas canvas, float width, float height, float groundY) {
        if (environment == null) return;

        // 1. Sky Gradient
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(
                0, 0, 0, groundY,
                environment.getSkyColorTop(),
                environment.getSkyColorBottom(),
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, width, groundY, paint);
        paint.setShader(null);

        // 2. Far Layer (Distant Skylines / Dunes / Giant Trees / Monoliths)
        drawFarLayer(canvas, width, groundY);

        // 3. Mid Layer (Midground structures / billboards / cacti / mushrooms / holograms)
        drawMidLayer(canvas, width, groundY);

        // 4. Ground Foundation & Neon Cyber Grid
        drawGround(canvas, width, height, groundY);
    }

    private void drawFarLayer(Canvas canvas, float width, float groundY) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#121424"));
        paint.setAlpha(160);

        float spacing = 220.0f;
        float startX = -(scrollFar % spacing);

        for (float x = startX - spacing; x < width + spacing; x += spacing) {
            float bHeight = 160.0f + ((long) (x * 7) % 120);
            RectF bldg = new RectF(x, groundY - bHeight, x + spacing * 0.82f, groundY);
            canvas.drawRect(bldg, paint);

            // Subtle neon rooftop beacon
            paint.setColor(environment.getAccentGlowColor());
            paint.setAlpha(120);
            canvas.drawCircle(x + spacing * 0.41f, groundY - bHeight, 3.5f, paint);

            paint.setColor(Color.parseColor("#121424"));
            paint.setAlpha(160);
        }
    }

    private void drawMidLayer(Canvas canvas, float width, float groundY) {
        float spacing = 320.0f;
        float startX = -(scrollMid % spacing);

        for (float x = startX - spacing; x < width + spacing; x += spacing) {
            float mHeight = 110.0f + ((long) (x * 13) % 90);

            // Midground silhouette
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#1A1D36"));
            RectF tower = new RectF(x, groundY - mHeight, x + 85.0f, groundY);
            canvas.drawRoundRect(tower, 6, 6, paint);

            // Glowing neon sign / hologram window
            paint.setColor(environment.getAccentGlowColor());
            paint.setAlpha(180);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            canvas.drawRoundRect(new RectF(x + 15, groundY - mHeight + 20, x + 70, groundY - mHeight + 60), 4, 4, paint);

            // Windows
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#4DFFFFFF"));
            canvas.drawRect(x + 25, groundY - 40, x + 35, groundY - 25, paint);
            canvas.drawRect(x + 50, groundY - 40, x + 60, groundY - 25, paint);
        }
    }

    private void drawGround(Canvas canvas, float width, float height, float groundY) {
        // Deep dark ground base
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(environment.getGroundColor());
        canvas.drawRect(0, groundY, width, height, paint);

        // Ground upper glowing laser edge
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.5f);
        paint.setColor(environment.getGridLineColor());
        paint.setAlpha(255);
        canvas.drawLine(0, groundY, width, groundY, paint);

        // Second subtle lower horizontal line
        paint.setStrokeWidth(1.5f);
        paint.setAlpha(140);
        canvas.drawLine(0, groundY + 16, width, groundY + 16, paint);
        canvas.drawLine(0, groundY + 38, width, groundY + 38, paint);

        // Moving perspective grid vertical lines
        float lineSpacing = 70.0f;
        float startX = -(scrollGround % lineSpacing);
        paint.setStrokeWidth(2.0f);
        paint.setColor(environment.getGridLineColor());
        paint.setAlpha(120);

        for (float gx = startX - lineSpacing; gx < width + lineSpacing; gx += lineSpacing) {
            // Slight perspective angle
            canvas.drawLine(gx, groundY, gx - 28.0f, height, paint);
        }
    }
}
