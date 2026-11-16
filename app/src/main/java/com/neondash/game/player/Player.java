package com.neondash.game.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.ParticleSystem;
import com.neondash.game.powerups.PowerUpManager;
import com.neondash.game.utilities.Constants;
import com.neondash.game.utilities.SoundManager;
import com.neondash.game.utilities.VibrationHelper;

public class Player {

    public static final int STATE_RUNNING = 0;
    public static final int STATE_JUMPING = 1;
    public static final int STATE_DOUBLE_JUMPING = 2;
    public static final int STATE_SLIDING = 3;
    public static final int STATE_CRASHED = 4;

    private int state = STATE_RUNNING;
    public float x;
    public float y;
    public float vy = 0.0f;
    public float groundY;

    public float width = 56.0f;
    public float height = 80.0f;
    public float slideHeight = 36.0f;

    private CharacterModel character;
    private final RectF bounds = new RectF();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private long slideRemainingMs = 0;
    private float animCycle = 0.0f;
    private float shieldPulse = 0.0f;

    private final ParticleSystem particleSystem;
    private final SoundManager soundManager;
    private final Context context;

    public Player(Context context, CharacterModel character, ParticleSystem particleSystem, float startX, float groundY) {
        this.context = context;
        this.character = character;
        this.particleSystem = particleSystem;
        this.soundManager = SoundManager.getInstance(context);
        this.groundY = groundY;
        this.x = startX;
        this.y = groundY - height;
    }

    public void setCharacter(CharacterModel character) {
        this.character = character;
    }

    public void setGroundY(float groundY) {
        this.groundY = groundY;
        if (state == STATE_RUNNING || state == STATE_SLIDING) {
            this.y = groundY - getCurrentHeight();
        }
    }

    public float getCurrentHeight() {
        return (state == STATE_SLIDING) ? slideHeight : height;
    }

    public void jump(boolean allowDoubleJump) {
        if (state == STATE_CRASHED) return;

        if (state == STATE_RUNNING || state == STATE_SLIDING) {
            vy = Constants.BASE_JUMP_FORCE * character.getJumpMultiplier();
            state = STATE_JUMPING;
            soundManager.playJump();
            VibrationHelper.vibrateTap(context);
            particleSystem.emitThruster(x + width * 0.5f, y + getCurrentHeight(), character.getPrimaryColor());
        } else if (state == STATE_JUMPING && allowDoubleJump) {
            vy = Constants.BASE_DOUBLE_JUMP_FORCE * character.getJumpMultiplier();
            state = STATE_DOUBLE_JUMPING;
            soundManager.playDoubleJump();
            VibrationHelper.vibrateTap(context);
            particleSystem.emitThruster(x + width * 0.5f, y + getCurrentHeight(), Color.parseColor("#FF007F"));
        }
    }

    public void slide() {
        if (state == STATE_CRASHED) return;

        if (state == STATE_RUNNING) {
            state = STATE_SLIDING;
            slideRemainingMs = Constants.SLIDE_DURATION_MS;
            y = groundY - slideHeight;
            soundManager.playSlide();
            VibrationHelper.vibrateTap(context);
            particleSystem.emitSlideSparks(x + width * 0.8f, groundY - 2);
        } else if (state == STATE_JUMPING || state == STATE_DOUBLE_JUMPING) {
            // Dive down quickly
            vy = 26.0f;
            state = STATE_SLIDING;
            slideRemainingMs = Constants.SLIDE_DURATION_MS;
        }
    }

    public void update(long deltaMs, boolean powerUpDoubleJumpActive) {
        if (state == STATE_CRASHED) return;

        animCycle += 0.35f;
        shieldPulse += 0.1f;

        // Apply Gravity
        if (state == STATE_JUMPING || state == STATE_DOUBLE_JUMPING || (state == STATE_SLIDING && y + slideHeight < groundY)) {
            vy += Constants.GRAVITY;
            y += vy;

            // Thruster particle stream when airborne
            if (vy < 0) {
                particleSystem.emitThruster(x + width * 0.4f, y + getCurrentHeight(), character.getPrimaryColor());
            }

            // Landed on ground
            if (y + getCurrentHeight() >= groundY) {
                y = groundY - getCurrentHeight();
                vy = 0.0f;
                if (state == STATE_SLIDING) {
                    // keep sliding if timer remains
                } else {
                    state = STATE_RUNNING;
                }
            }
        }

        // Sliding countdown
        if (state == STATE_SLIDING) {
            slideRemainingMs -= deltaMs;
            particleSystem.emitSlideSparks(x + width * 0.7f, groundY - 2);
            if (slideRemainingMs <= 0) {
                state = STATE_RUNNING;
                y = groundY - height;
            }
        }
    }

    public void crash() {
        if (state == STATE_CRASHED) return;
        state = STATE_CRASHED;
        particleSystem.emitExplosion(getCenterX(), getCenterY());
        soundManager.playCollision();
        VibrationHelper.vibrateCollision(context);
    }

    public RectF getBounds() {
        float h = getCurrentHeight();
        // Give slightly generous hitboxes so collisions feel fair and tight
        float marginX = width * 0.15f;
        float marginY = h * 0.10f;
        bounds.set(x + marginX, y + marginY, x + width - marginX, y + h - marginY);
        return bounds;
    }

    public float getCenterX() {
        return x + width * 0.5f;
    }

    public float getCenterY() {
        return y + getCurrentHeight() * 0.5f;
    }

    public boolean isCrashed() {
        return state == STATE_CRASHED;
    }

    public int getState() {
        return state;
    }

    public void setPosition(float startX, float groundY) {
        this.x = startX;
        this.groundY = groundY;
        if (state == STATE_RUNNING || state == STATE_SLIDING) {
            this.y = groundY - getCurrentHeight();
        }
    }

    public void reset(float startX, float groundY) {
        this.groundY = groundY;
        this.x = startX;
        this.height = 80.0f;
        this.slideHeight = 36.0f;
        this.state = STATE_RUNNING;
        this.vy = 0.0f;
        this.y = groundY - height;
        this.slideRemainingMs = 0;
    }

    public void draw(Canvas canvas, PowerUpManager powerUpManager) {
        if (state == STATE_CRASHED) return;

        int primary = character.getPrimaryColor();
        int secondary = character.getSecondaryColor();
        int accent = character.getAccentColor();

        // Speed boost motion blur ghosts
        if (powerUpManager != null && powerUpManager.isSpeedBoostActive()) {
            drawGhost(canvas, x - 35, y, 90, primary);
            drawGhost(canvas, x - 65, y, 50, secondary);
        }

        canvas.save();

        if (state == STATE_SLIDING) {
            drawSlidingRunner(canvas, primary, secondary, accent);
        } else if (state == STATE_JUMPING || state == STATE_DOUBLE_JUMPING) {
            drawJumpingRunner(canvas, primary, secondary, accent);
        } else {
            drawRunningRunner(canvas, primary, secondary, accent);
        }

        // Energy Shield forcefield bubble
        if (powerUpManager != null && powerUpManager.isShieldActive()) {
            drawShield(canvas);
        }

        // Magnet Aura
        if (powerUpManager != null && powerUpManager.isMagnetActive()) {
            drawMagnetAura(canvas);
        }

        canvas.restore();
    }

    private void drawRunningRunner(Canvas canvas, int primary, int secondary, int accent) {
        float legSwing = (float) Math.sin(animCycle) * 22.0f;
        float armSwing = (float) Math.cos(animCycle) * 18.0f;
        float bob = (float) Math.abs(Math.sin(animCycle * 2.0f)) * 4.0f;

        float currY = y + bob;

        // Back Arm
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6.0f);
        paint.setColor(secondary);
        canvas.drawLine(x + 28, currY + 36, x + 28 - armSwing, currY + 54, paint);

        // Back Leg
        paint.setStrokeWidth(7.0f);
        paint.setColor(secondary);
        canvas.drawLine(x + 28, currY + 58, x + 28 - legSwing, currY + 76, paint);
        canvas.drawLine(x + 28 - legSwing, currY + 76, x + 28 - legSwing - 6, currY + height, paint);

        // Robotic Torso
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15182B"));
        RectF torso = new RectF(x + 18, currY + 24, x + 44, currY + 58);
        canvas.drawRoundRect(torso, 8, 8, paint);

        // Glowing Armor Chestplate
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setColor(primary);
        canvas.drawRoundRect(torso, 8, 8, paint);

        // Core Reactor Light
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(accent);
        canvas.drawCircle(x + 31, currY + 38, 5.0f, paint);

        // Robotic Head & Visor
        paint.setColor(Color.parseColor("#1F223D"));
        RectF head = new RectF(x + 24, currY + 6, x + 46, currY + 24);
        canvas.drawRoundRect(head, 6, 6, paint);

        // Neon Visor Strip
        paint.setColor(primary);
        canvas.drawRoundRect(new RectF(x + 34, currY + 11, x + 46, currY + 17), 3, 3, paint);

        // Front Leg
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7.0f);
        paint.setColor(primary);
        canvas.drawLine(x + 32, currY + 58, x + 32 + legSwing, currY + 76, paint);
        canvas.drawLine(x + 32 + legSwing, currY + 76, x + 32 + legSwing + 8, currY + height, paint);

        // Front Arm
        paint.setStrokeWidth(6.0f);
        paint.setColor(primary);
        canvas.drawLine(x + 32, currY + 36, x + 32 + armSwing, currY + 54, paint);
    }

    private void drawJumpingRunner(Canvas canvas, int primary, int secondary, int accent) {
        // Tucked streamlined aerodynamic jump pose
        float currY = y;

        // Robotic Torso angled
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15182B"));
        RectF torso = new RectF(x + 18, currY + 22, x + 44, currY + 54);
        canvas.drawRoundRect(torso, 8, 8, paint);

        // Armor Plate
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setColor(primary);
        canvas.drawRoundRect(torso, 8, 8, paint);

        // Core
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(accent);
        canvas.drawCircle(x + 31, currY + 36, 5.0f, paint);

        // Head looking forward-up
        paint.setColor(Color.parseColor("#1F223D"));
        RectF head = new RectF(x + 26, currY + 4, x + 48, currY + 22);
        canvas.drawRoundRect(head, 6, 6, paint);

        paint.setColor(primary);
        canvas.drawRoundRect(new RectF(x + 36, currY + 9, x + 48, currY + 15), 3, 3, paint);

        // Tucked bent legs with thruster boots firing downward
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7.0f);
        paint.setColor(primary);
        // Left bent leg
        canvas.drawLine(x + 24, currY + 54, x + 16, currY + 68, paint);
        canvas.drawLine(x + 16, currY + 68, x + 28, currY + 78, paint);
        // Right bent leg
        paint.setColor(secondary);
        canvas.drawLine(x + 34, currY + 54, x + 26, currY + 68, paint);
        canvas.drawLine(x + 26, currY + 68, x + 38, currY + 78, paint);

        // Thruster flame cone at boots
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(primary);
        Path flame = new Path();
        flame.moveTo(x + 22, currY + 78);
        flame.lineTo(x + 36, currY + 78);
        flame.lineTo(x + 29, currY + 98);
        flame.close();
        canvas.drawPath(flame, paint);
    }

    private void drawSlidingRunner(Canvas canvas, int primary, int secondary, int accent) {
        // Low streamlined sliding wedge
        float currY = y;

        // Torso crouched low
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#15182B"));
        RectF torso = new RectF(x + 10, currY + 14, x + 52, currY + 38);
        canvas.drawRoundRect(torso, 8, 8, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setColor(primary);
        canvas.drawRoundRect(torso, 8, 8, paint);

        // Head tucked low
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#1F223D"));
        RectF head = new RectF(x + 34, currY + 6, x + 58, currY + 22);
        canvas.drawRoundRect(head, 6, 6, paint);

        paint.setColor(primary);
        canvas.drawRoundRect(new RectF(x + 46, currY + 11, x + 58, currY + 17), 3, 3, paint);

        // Legs extended forward
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7.0f);
        paint.setColor(primary);
        canvas.drawLine(x + 12, currY + 32, x + 40, currY + 40, paint);
        canvas.drawLine(x + 40, currY + 40, x + 66, currY + 42, paint);
    }

    private void drawGhost(Canvas canvas, float gx, float gy, int alpha, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawRoundRect(new RectF(gx + 16, gy + 10, gx + 46, gy + getCurrentHeight() - 4), 10, 10, paint);
    }

    private void drawShield(Canvas canvas) {
        float cx = getCenterX();
        float cy = getCenterY();
        float radius = Math.max(width, getCurrentHeight()) * 0.72f;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.5f);
        paint.setColor(Color.parseColor("#39FF14"));
        paint.setAlpha(180 + (int) (Math.sin(shieldPulse) * 60));
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(35);
        canvas.drawCircle(cx, cy, radius, paint);

        // Hexagon or rotating shield rings
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.WHITE);
        paint.setAlpha(120);
        RectF shieldOval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        canvas.drawArc(shieldOval, (shieldPulse * 50) % 360, 90, false, paint);
        canvas.drawArc(shieldOval, (shieldPulse * 50 + 180) % 360, 90, false, paint);
    }

    private void drawMagnetAura(Canvas canvas) {
        float cx = getCenterX();
        float cy = getCenterY();
        float r = 55.0f + (float) Math.sin(shieldPulse * 1.5f) * 6.0f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.5f);
        paint.setColor(Color.parseColor("#00F0FF"));
        paint.setAlpha(130);
        canvas.drawCircle(cx, cy, r, paint);
    }
}
