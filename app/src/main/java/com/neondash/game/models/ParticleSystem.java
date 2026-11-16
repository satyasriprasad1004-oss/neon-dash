package com.neondash.game.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class ParticleSystem {
    private static final int MAX_PARTICLES = 200;
    private final Particle[] particles = new Particle[MAX_PARTICLES];
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ParticleSystem() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i] = new Particle();
        }
    }

    private Particle getAvailableParticle() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (!particles[i].active) {
                return particles[i];
            }
        }
        return particles[0]; // fallback overwrite oldest
    }

    public void emitThruster(float x, float y, int neonColor) {
        for (int i = 0; i < 3; i++) {
            Particle p = getAvailableParticle();
            float vx = -random.nextFloat() * 4.0f - 2.0f;
            float vy = random.nextFloat() * 5.0f + 2.0f;
            int col = (random.nextBoolean()) ? neonColor : Color.WHITE;
            p.init(x, y, vx, vy, col, random.nextFloat() * 8.0f + 4.0f, 18.0f, null);
        }
    }

    public void emitSlideSparks(float x, float y) {
        for (int i = 0; i < 4; i++) {
            Particle p = getAvailableParticle();
            float vx = -random.nextFloat() * 8.0f - 3.0f;
            float vy = -random.nextFloat() * 4.0f - 1.0f;
            int col = (random.nextBoolean()) ? Color.parseColor("#FFE600") : Color.parseColor("#FF6600");
            p.init(x, y, vx, vy, col, random.nextFloat() * 6.0f + 3.0f, 15.0f, null);
        }
    }

    public void emitCoinSparkles(float x, float y) {
        for (int i = 0; i < 10; i++) {
            Particle p = getAvailableParticle();
            double angle = random.nextDouble() * 2.0 * Math.PI;
            float speed = random.nextFloat() * 6.0f + 2.0f;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            int col = (random.nextBoolean()) ? Color.parseColor("#FFE600") : Color.parseColor("#00F0FF");
            p.init(x, y, vx, vy, col, random.nextFloat() * 8.0f + 4.0f, 25.0f, null);
        }
        // Floating text
        Particle textP = getAvailableParticle();
        textP.init(x, y - 10, 0, -2.5f, Color.parseColor("#FFE600"), 36.0f, 40.0f, "+1");
    }

    public void emitExplosion(float x, float y) {
        for (int i = 0; i < 35; i++) {
            Particle p = getAvailableParticle();
            double angle = random.nextDouble() * 2.0 * Math.PI;
            float speed = random.nextFloat() * 12.0f + 3.0f;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            int[] cols = new int[]{
                    Color.parseColor("#FF007F"),
                    Color.parseColor("#00F0FF"),
                    Color.parseColor("#FFE600"),
                    Color.WHITE
            };
            int col = cols[random.nextInt(cols.length)];
            p.init(x, y, vx, vy, col, random.nextFloat() * 14.0f + 6.0f, 35.0f, null);
        }
    }

    public void emitShieldHit(float x, float y) {
        for (int i = 0; i < 20; i++) {
            Particle p = getAvailableParticle();
            double angle = random.nextDouble() * 2.0 * Math.PI;
            float speed = random.nextFloat() * 8.0f + 2.0f;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            p.init(x, y, vx, vy, Color.parseColor("#39FF14"), random.nextFloat() * 10.0f + 4.0f, 25.0f, null);
        }
    }

    public void update() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i].update();
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i].draw(canvas, paint);
        }
    }
}
