package com.neondash.game.powerups;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.neondash.game.database.PreferencesManager;
import com.neondash.game.utilities.Constants;

import java.util.Random;

public class PowerUpManager {
    private static final int MAX_FIELD_POWERUPS = 5;
    private final PowerUp[] fieldPowerUps = new PowerUp[MAX_FIELD_POWERUPS];
    private final PreferencesManager prefs;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    // Active power-up remaining timers in milliseconds
    private long magnetRemainingMs = 0;
    private long magnetTotalMs = 0;

    private boolean shieldActive = false;

    private long speedBoostRemainingMs = 0;
    private long speedBoostTotalMs = 0;

    private long slowMotionRemainingMs = 0;
    private long slowMotionTotalMs = 0;

    private long doubleJumpRemainingMs = 0;
    private long doubleJumpTotalMs = 0;

    public PowerUpManager(Context context) {
        this.prefs = PreferencesManager.getInstance(context);
        for (int i = 0; i < MAX_FIELD_POWERUPS; i++) {
            fieldPowerUps[i] = new PowerUp();
        }
    }

    public PowerUp spawn(float x, float y) {
        for (int i = 0; i < MAX_FIELD_POWERUPS; i++) {
            if (!fieldPowerUps[i].isActive) {
                int type = random.nextInt(5);
                fieldPowerUps[i].init(type, x, y);
                return fieldPowerUps[i];
            }
        }
        return null;
    }

    public void activatePowerUp(int type) {
        float upgradeMultiplier = 1.0f;
        switch (type) {
            case PowerUp.TYPE_MAGNET:
                int magnetLvl = prefs.getUpgradeLevel(Constants.KEY_UPGRADE_MAGNET);
                upgradeMultiplier += (magnetLvl - 1) * 0.25f;
                magnetTotalMs = (long) (Constants.BASE_POWERUP_DURATION_MS * upgradeMultiplier);
                magnetRemainingMs = magnetTotalMs;
                break;

            case PowerUp.TYPE_SHIELD:
                shieldActive = true;
                break;

            case PowerUp.TYPE_SPEED_BOOST:
                int speedLvl = prefs.getUpgradeLevel(Constants.KEY_UPGRADE_SPEED);
                upgradeMultiplier += (speedLvl - 1) * 0.20f;
                speedBoostTotalMs = (long) (Constants.SPEED_BOOST_DURATION_MS * upgradeMultiplier);
                speedBoostRemainingMs = speedBoostTotalMs;
                break;

            case PowerUp.TYPE_SLOW_MOTION:
                int slowLvl = prefs.getUpgradeLevel(Constants.KEY_UPGRADE_SLOWMO);
                upgradeMultiplier += (slowLvl - 1) * 0.25f;
                slowMotionTotalMs = (long) (Constants.SLOW_MOTION_DURATION_MS * upgradeMultiplier);
                slowMotionRemainingMs = slowMotionTotalMs;
                break;

            case PowerUp.TYPE_DOUBLE_JUMP:
                int djLvl = prefs.getUpgradeLevel(Constants.KEY_UPGRADE_DOUBLEJUMP);
                upgradeMultiplier += (djLvl - 1) * 0.30f;
                doubleJumpTotalMs = (long) (Constants.BASE_POWERUP_DURATION_MS * 1.5f * upgradeMultiplier);
                doubleJumpRemainingMs = doubleJumpTotalMs;
                break;
        }
    }

    public void update(long deltaMs, float gameSpeed) {
        // Update field powerups
        for (int i = 0; i < MAX_FIELD_POWERUPS; i++) {
            if (fieldPowerUps[i].isActive) {
                fieldPowerUps[i].update(gameSpeed);
            }
        }

        // Decrement active timers
        if (magnetRemainingMs > 0) {
            magnetRemainingMs = Math.max(0, magnetRemainingMs - deltaMs);
        }
        if (speedBoostRemainingMs > 0) {
            speedBoostRemainingMs = Math.max(0, speedBoostRemainingMs - deltaMs);
        }
        if (slowMotionRemainingMs > 0) {
            slowMotionRemainingMs = Math.max(0, slowMotionRemainingMs - deltaMs);
        }
        if (doubleJumpRemainingMs > 0) {
            doubleJumpRemainingMs = Math.max(0, doubleJumpRemainingMs - deltaMs);
        }
    }

    public void drawFieldPowerUps(Canvas canvas) {
        for (int i = 0; i < MAX_FIELD_POWERUPS; i++) {
            if (fieldPowerUps[i].isActive) {
                fieldPowerUps[i].draw(canvas, paint);
            }
        }
    }

    public void drawActiveHUD(Canvas canvas, float startX, float startY) {
        float x = startX;
        float y = startY;
        float badgeWidth = 140.0f;
        float badgeHeight = 44.0f;
        float spacing = 12.0f;

        if (magnetRemainingMs > 0) {
            drawPowerUpBadge(canvas, "MAGNET", Color.parseColor("#00F0FF"), (float) magnetRemainingMs / magnetTotalMs, x, y, badgeWidth, badgeHeight);
            x += badgeWidth + spacing;
        }
        if (shieldActive) {
            drawPowerUpBadge(canvas, "SHIELD", Color.parseColor("#39FF14"), 1.0f, x, y, badgeWidth, badgeHeight);
            x += badgeWidth + spacing;
        }
        if (speedBoostRemainingMs > 0) {
            drawPowerUpBadge(canvas, "BOOST", Color.parseColor("#FFE600"), (float) speedBoostRemainingMs / speedBoostTotalMs, x, y, badgeWidth, badgeHeight);
            x += badgeWidth + spacing;
        }
        if (slowMotionRemainingMs > 0) {
            drawPowerUpBadge(canvas, "SLOW-MO", Color.parseColor("#0080FF"), (float) slowMotionRemainingMs / slowMotionTotalMs, x, y, badgeWidth, badgeHeight);
            x += badgeWidth + spacing;
        }
        if (doubleJumpRemainingMs > 0) {
            drawPowerUpBadge(canvas, "2X JUMP", Color.parseColor("#FF007F"), (float) doubleJumpRemainingMs / doubleJumpTotalMs, x, y, badgeWidth, badgeHeight);
            x += badgeWidth + spacing;
        }
    }

    private void drawPowerUpBadge(Canvas canvas, String title, int color, float progress, float x, float y, float w, float h) {
        RectF bg = new RectF(x, y, x + w, y + h);
        // Dark card background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E6131627"));
        canvas.drawRoundRect(bg, 10, 10, paint);

        // Border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(color);
        canvas.drawRoundRect(bg, 10, 10, paint);

        // Text
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(22.0f);
        paint.setFakeBoldText(true);
        canvas.drawText(title, x + 12, y + 24, paint);

        // Mini progress bar at bottom of badge
        if (progress < 1.0f || !title.equals("SHIELD")) {
            paint.setColor(Color.parseColor("#33FFFFFF"));
            canvas.drawRect(x + 10, y + h - 8, x + w - 10, y + h - 4, paint);

            paint.setColor(color);
            canvas.drawRect(x + 10, y + h - 8, x + 10 + (w - 20) * Math.max(0f, progress), y + h - 4, paint);
        }
    }

    // State queries
    public boolean isMagnetActive() { return magnetRemainingMs > 0; }
    public boolean isShieldActive() { return shieldActive; }
    public boolean consumeShield() {
        if (shieldActive) {
            shieldActive = false;
            return true;
        }
        return false;
    }
    public boolean isSpeedBoostActive() { return speedBoostRemainingMs > 0; }
    public boolean isSlowMotionActive() { return slowMotionRemainingMs > 0; }
    public boolean isDoubleJumpActive() { return doubleJumpRemainingMs > 0; }

    public PowerUp[] getFieldPowerUps() { return fieldPowerUps; }

    public void reset() {
        for (int i = 0; i < MAX_FIELD_POWERUPS; i++) {
            fieldPowerUps[i].isActive = false;
        }
        magnetRemainingMs = 0;
        shieldActive = false;
        speedBoostRemainingMs = 0;
        slowMotionRemainingMs = 0;
        doubleJumpRemainingMs = 0;
    }
}
