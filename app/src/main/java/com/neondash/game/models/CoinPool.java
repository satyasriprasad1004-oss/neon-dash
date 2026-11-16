package com.neondash.game.models;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.neondash.game.player.Player;

import java.util.Random;

public class CoinPool {
    private static final int MAX_COINS = 60;
    private final Coin[] pool = new Coin[MAX_COINS];
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    public CoinPool() {
        for (int i = 0; i < MAX_COINS; i++) {
            pool[i] = new Coin();
        }
    }

    public Coin obtain(float x, float y) {
        for (int i = 0; i < MAX_COINS; i++) {
            if (!pool[i].isActive) {
                pool[i].init(x, y);
                return pool[i];
            }
        }
        return null;
    }

    // Spawns lines or arcs of coins
    public void spawnLine(float startX, float y, int count, float spacing) {
        for (int i = 0; i < count; i++) {
            obtain(startX + i * spacing, y);
        }
    }

    public void spawnArc(float startX, float baseY, int count, float spacing, float arcHeight) {
        for (int i = 0; i < count; i++) {
            float progress = (float) i / (count - 1);
            float y = baseY - (float) Math.sin(progress * Math.PI) * arcHeight;
            obtain(startX + i * spacing, y);
        }
    }

    public void update(float gameSpeed, Player player, boolean isMagnetActive, float magnetRadius) {
        for (int i = 0; i < MAX_COINS; i++) {
            if (pool[i].isActive) {
                pool[i].update(gameSpeed, player, isMagnetActive, magnetRadius);
            }
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < MAX_COINS; i++) {
            if (pool[i].isActive) {
                pool[i].draw(canvas, paint);
            }
        }
    }

    public Coin[] getCoins() {
        return pool;
    }

    public void reset() {
        for (int i = 0; i < MAX_COINS; i++) {
            pool[i].isActive = false;
        }
    }
}
