package com.neondash.game.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.Coin;
import com.neondash.game.models.CoinPool;
import com.neondash.game.models.GameEnvironment;
import com.neondash.game.models.Mission;
import com.neondash.game.models.ParticleSystem;
import com.neondash.game.obstacles.Obstacle;
import com.neondash.game.obstacles.ObstacleManager;
import com.neondash.game.player.Player;
import com.neondash.game.powerups.PowerUp;
import com.neondash.game.powerups.PowerUpManager;
import com.neondash.game.utilities.Constants;
import com.neondash.game.utilities.SoundManager;

public class GameManager {

    public interface GameEventListener {
        void onScoreUpdated(int score, int distance, int coins);
        void onGameOver(int finalScore, int distance, int coins, boolean isNewHighScore, boolean isVictory);
        void onPauseStateChanged(boolean isPaused);
    }

    public static final int STATE_READY = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_GAME_OVER = 3;

    private int state = STATE_PLAYING;
    private final Context context;
    private GameEventListener listener;

    private final Player player;
    private final ObstacleManager obstacleManager;
    private final CoinPool coinPool;
    private final PowerUpManager powerUpManager;
    private final ParticleSystem particleSystem;
    private final ParallaxBackground background;
    private final SoundManager soundManager;
    private final PreferencesManager prefs;
    private final DatabaseHelper dbHelper;

    private GameEnvironment currentEnvironment;
    private CharacterModel currentCharacter;
    private int gameMode;
    private int difficulty;
    private float difficultyMultiplier = 1.0f;

    // Run Stats
    private int score = 0;
    private int distance = 0;
    private int coinsCollected = 0;
    private int obstaclesAvoided = 0;
    private int coinsWithoutDeath = 0;
    private float currentSpeed = Constants.BASE_RUN_SPEED;
    private long timeRemainingMs = 60000;
    private long gameOverDelayMs = 0;
    private int hudThrottleCounter = 0;

    private float screenWidth = 1920.0f;
    private float screenHeight = 1080.0f;
    private float groundY = 820.0f;

    private final Paint hudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GameManager(Context context, GameEnvironment environment, CharacterModel character, int gameMode) {
        this.context = context;
        this.currentEnvironment = environment;
        this.currentCharacter = character;
        this.gameMode = gameMode;

        this.prefs = PreferencesManager.getInstance(context);
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.soundManager = SoundManager.getInstance(context);

        this.particleSystem = new ParticleSystem();
        this.coinPool = new CoinPool();
        this.powerUpManager = new PowerUpManager(context);
        this.obstacleManager = new ObstacleManager();
        this.background = new ParallaxBackground(environment);

        this.player = new Player(context, character, particleSystem, 180.0f, groundY);

        initDifficulty();
        initMode();
    }

    private void initDifficulty() {
        this.difficulty = prefs.getDifficulty();
        if (difficulty == 0) difficultyMultiplier = Constants.DIFFICULTY_EASY_MULT;
        else if (difficulty == 2) difficultyMultiplier = Constants.DIFFICULTY_HARD_MULT;
        else difficultyMultiplier = Constants.DIFFICULTY_MEDIUM_MULT;

        currentSpeed = Constants.BASE_RUN_SPEED * difficultyMultiplier * currentCharacter.getSpeedMultiplier();
    }

    private void initMode() {
        if (gameMode == Constants.MODE_TIME_CHALLENGE) {
            timeRemainingMs = 120000; // 2 minutes challenge
        } else if (gameMode == Constants.MODE_COIN_RUSH) {
            timeRemainingMs = 60000; // 60s coin rush
        }
    }

    public void setGameEventListener(GameEventListener listener) {
        this.listener = listener;
    }

    public void onSurfaceChanged(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.groundY = height * 0.72f;
        player.setPosition(width * 0.16f, groundY);
    }

    public void update(long deltaMs) {
        if (state == STATE_PAUSED) return;

        if (state == STATE_GAME_OVER) {
            particleSystem.update();
            if (gameOverDelayMs > 0) {
                gameOverDelayMs -= deltaMs;
                if (gameOverDelayMs <= 0 && listener != null) {
                    boolean isNewHigh = score > prefs.getHighScore();
                    prefs.setHighScore(score);
                    prefs.addCoins(coinsCollected);
                    dbHelper.addScore("YOU", score, distance, coinsCollected, currentCharacter.getName(), getModeName());
                    updateMissionsOnGameOver();
                    listener.onGameOver(score, distance, coinsCollected, isNewHigh, false);
                }
            }
            return;
        }

        // Active Playing Loop
        boolean slowMo = powerUpManager.isSlowMotionActive();
        boolean boost = powerUpManager.isSpeedBoostActive();

        float effectiveSpeed = currentSpeed;
        if (slowMo) effectiveSpeed *= 0.55f;
        if (boost) effectiveSpeed *= 1.60f;

        // Accelerate speed gradually in Classic mode
        if (gameMode == Constants.MODE_CLASSIC && currentSpeed < Constants.MAX_RUN_SPEED) {
            currentSpeed += Constants.SPEED_ACCELERATION * difficultyMultiplier;
        }

        // Update Timers for Challenge modes
        if (gameMode == Constants.MODE_TIME_CHALLENGE || gameMode == Constants.MODE_COIN_RUSH) {
            timeRemainingMs -= deltaMs;
            if (timeRemainingMs <= 0) {
                timeRemainingMs = 0;
                handleVictoryOrTimeUp();
                return;
            }
        }

        // Update Distance & Score
        distance += (int) (effectiveSpeed * 0.12f);
        int scoreIncrement = (int) (effectiveSpeed * 0.22f);
        if (boost) scoreIncrement *= 2;
        score += scoreIncrement;

        hudThrottleCounter++;
        if (hudThrottleCounter % 4 == 0 && listener != null) {
            listener.onScoreUpdated(score, distance, coinsCollected);
        }

        // Update sub-systems
        boolean allowDoubleJump = powerUpManager.isDoubleJumpActive() ||
                (prefs.getUpgradeLevel(Constants.KEY_UPGRADE_DOUBLEJUMP) >= 5);
        player.update(deltaMs, allowDoubleJump);
        background.update(effectiveSpeed);
        obstacleManager.update(effectiveSpeed, screenWidth, groundY, coinPool, powerUpManager, difficulty);
        coinPool.update(effectiveSpeed, player, powerUpManager.isMagnetActive(), 420.0f * currentCharacter.getMagnetMultiplier());
        powerUpManager.update(deltaMs, effectiveSpeed);
        particleSystem.update();

        // 1. Collision with Obstacles
        Obstacle hitObstacle = obstacleManager.checkCollision(player.getBounds());
        if (hitObstacle != null) {
            if (powerUpManager.consumeShield()) {
                // Shield absorbs damage!
                hitObstacle.isActive = false;
                particleSystem.emitShieldHit(hitObstacle.x, hitObstacle.y);
                soundManager.playShieldBreak();
            } else if (boost) {
                // Ramming invincibility
                hitObstacle.isActive = false;
                particleSystem.emitExplosion(hitObstacle.x, hitObstacle.y);
                score += 250;
            } else {
                // Lethal hit
                handlePlayerDeath();
                return;
            }
        }

        // 2. Collision with Coins
        RectF pBounds = player.getBounds();
        Coin[] coins = coinPool.getCoins();
        for (Coin c : coins) {
            if (c.isActive && !c.isCollected && RectF.intersects(c.getBounds(), pBounds)) {
                c.isCollected = true;
                c.isActive = false;
                coinsCollected++;
                coinsWithoutDeath++;
                score += 25;
                particleSystem.emitCoinSparkles(c.x, c.y);
                soundManager.playCoin();
                if (listener != null) {
                    listener.onScoreUpdated(score, distance, coinsCollected);
                }
            }
        }

        // 3. Collision with PowerUps
        PowerUp[] fieldPups = powerUpManager.getFieldPowerUps();
        for (PowerUp pu : fieldPups) {
            if (pu.isActive && RectF.intersects(pu.getBounds(), pBounds)) {
                pu.isActive = false;
                powerUpManager.activatePowerUp(pu.type);
                particleSystem.emitCoinSparkles(pu.x, pu.y);
                soundManager.playPowerUp();
            }
        }
    }

    private void handlePlayerDeath() {
        state = STATE_GAME_OVER;
        player.crash();
        gameOverDelayMs = 750; // brief delay to watch explosion before opening GameOver screen
    }

    private void handleVictoryOrTimeUp() {
        state = STATE_GAME_OVER;
        boolean isVictory = (gameMode == Constants.MODE_TIME_CHALLENGE);
        if (isVictory) {
            score += 2000; // completion bonus!
            coinsCollected += 50;
        }
        boolean isNewHigh = score > prefs.getHighScore();
        prefs.setHighScore(score);
        prefs.addCoins(coinsCollected);
        dbHelper.addScore("YOU", score, distance, coinsCollected, currentCharacter.getName(), getModeName());
        updateMissionsOnGameOver();

        if (listener != null) {
            listener.onGameOver(score, distance, coinsCollected, isNewHigh, isVictory);
        }
    }

    private void updateMissionsOnGameOver() {
        dbHelper.updateMissionProgress(Mission.TYPE_COINS, coinsCollected, false);
        dbHelper.updateMissionProgress(Mission.TYPE_DISTANCE, distance, false);
        dbHelper.updateMissionProgress(Mission.TYPE_COINS_SINGLE_RUN, coinsWithoutDeath, true);
        dbHelper.unlockAchievement("a1"); // Booting Up: completed first run
        if (coinsCollected >= 30) dbHelper.unlockAchievement("a3");
    }

    public void draw(Canvas canvas) {
        if (canvas == null) return;

        // 1. Parallax Layers & Ground
        background.draw(canvas, screenWidth, screenHeight, groundY);

        // 2. Obstacles
        obstacleManager.draw(canvas, currentEnvironment.getObstacleTintColor());

        // 3. Coins & Field PowerUps
        coinPool.draw(canvas);
        powerUpManager.drawFieldPowerUps(canvas);

        // 4. Player & Active PowerUp Overlays
        player.draw(canvas, powerUpManager);

        // 5. Particle System
        particleSystem.draw(canvas);

        // 6. Active PowerUp HUD Badges
        powerUpManager.drawActiveHUD(canvas, 30.0f, 30.0f);

        // 7. Timer HUD for Time Challenge & Coin Rush
        if (gameMode == Constants.MODE_TIME_CHALLENGE || gameMode == Constants.MODE_COIN_RUSH) {
            drawTimerHUD(canvas);
        }
    }

    private void drawTimerHUD(Canvas canvas) {
        hudPaint.setColor(Color.WHITE);
        hudPaint.setTextSize(36.0f);
        hudPaint.setFakeBoldText(true);
        int seconds = (int) (timeRemainingMs / 1000);
        String timeStr = "TIME: " + seconds + "s";
        float textWidth = hudPaint.measureText(timeStr);
        float tx = (screenWidth - textWidth) * 0.5f;

        // Draw timer pill
        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setColor(Color.parseColor("#CC15182D"));
        RectF pill = new RectF(tx - 20, 20, tx + textWidth + 20, 72);
        canvas.drawRoundRect(pill, 14, 14, hudPaint);

        hudPaint.setStyle(Paint.Style.STROKE);
        hudPaint.setStrokeWidth(2.0f);
        hudPaint.setColor(Color.parseColor("#FFE600"));
        canvas.drawRoundRect(pill, 14, 14, hudPaint);

        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setColor(Color.parseColor("#FFE600"));
        canvas.drawText(timeStr, tx, 58, hudPaint);
    }

    // Input Actions
    public void jump() {
        if (state == STATE_PLAYING) {
            boolean allowDouble = powerUpManager.isDoubleJumpActive() ||
                    (prefs.getUpgradeLevel(Constants.KEY_UPGRADE_DOUBLEJUMP) >= 5);
            player.jump(allowDouble);
        }
    }

    public void slide() {
        if (state == STATE_PLAYING) {
            player.slide();
        }
    }

    public void pause() {
        if (state == STATE_PLAYING) {
            state = STATE_PAUSED;
            if (listener != null) listener.onPauseStateChanged(true);
        }
    }

    public void resume() {
        if (state == STATE_PAUSED) {
            state = STATE_PLAYING;
            if (listener != null) listener.onPauseStateChanged(false);
        }
    }

    public void restart() {
        score = 0;
        distance = 0;
        coinsCollected = 0;
        coinsWithoutDeath = 0;
        obstaclesAvoided = 0;
        gameOverDelayMs = 0;
        initDifficulty();
        initMode();

        player.reset(screenWidth * 0.16f, groundY);
        obstacleManager.reset();
        coinPool.reset();
        powerUpManager.reset();
        state = STATE_PLAYING;

        if (listener != null) {
            listener.onScoreUpdated(score, distance, coinsCollected);
            listener.onPauseStateChanged(false);
        }
    }

    public int getState() { return state; }
    public int getScore() { return score; }
    public int getDistance() { return distance; }
    public int getCoinsCollected() { return coinsCollected; }

    public String getModeName() {
        switch (gameMode) {
            case Constants.MODE_TIME_CHALLENGE: return "Time Challenge";
            case Constants.MODE_COIN_RUSH: return "Coin Rush";
            default: return "Classic";
        }
    }
}
