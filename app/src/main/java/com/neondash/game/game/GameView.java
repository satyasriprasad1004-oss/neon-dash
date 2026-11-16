package com.neondash.game.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.neondash.game.characters.CharacterManager;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.GameEnvironment;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;
    private GameManager gameManager;

    private float touchDownX = 0.0f;
    private float touchDownY = 0.0f;
    private boolean hasSlid = false;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);
        setFocusable(true);

        PreferencesManager prefs = PreferencesManager.getInstance(context);
        GameEnvironment environment = GameEnvironment.getById(prefs.getSelectedEnvironment());
        CharacterModel character = CharacterManager.getInstance(context).getEquippedCharacter();
        int mode = prefs.getSelectedMode();

        gameManager = new GameManager(context, environment, character, mode);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameManager == null) return super.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                hasSlid = false;

                // Zero-latency response on ACTION_DOWN:
                // Left 45% of screen taps = Slide, Right / center taps = Jump!
                float halfWidth = getWidth() * 0.45f;
                if (touchDownX < halfWidth) {
                    hasSlid = true;
                    gameManager.slide();
                } else {
                    gameManager.jump();
                }
                performClick();
                return true;

            case MotionEvent.ACTION_MOVE:
                float moveDy = event.getY() - touchDownY;
                float moveDx = event.getX() - touchDownX;
                if (!hasSlid && moveDy > 35.0f && moveDy > Math.abs(moveDx)) {
                    hasSlid = true;
                    gameManager.slide();
                } else if (moveDy < -35.0f && Math.abs(moveDy) > Math.abs(moveDx)) {
                    gameManager.jump();
                    touchDownY = event.getY(); // prevent repeat in same stroke
                }
                return true;

            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (gameLoop == null || !gameLoop.isAlive()) {
            gameLoop = new GameLoop(holder, gameManager);
            gameLoop.setRunning(true);
            gameLoop.start();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (gameManager != null) {
            gameManager.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        if (gameLoop != null) {
            gameLoop.setRunning(false);
            while (retry) {
                try {
                    gameLoop.join(500);
                    retry = false;
                } catch (InterruptedException ignored) {
                }
            }
            gameLoop = null;
        }
    }

    public void pauseGame() {
        if (gameManager != null) {
            gameManager.pause();
        }
        if (gameLoop != null) {
            gameLoop.setPaused(true);
        }
    }

    public void resumeGame() {
        if (gameManager != null) {
            gameManager.resume();
        }
        if (gameLoop != null) {
            gameLoop.setPaused(false);
        }
    }

    public void restartGame() {
        if (gameManager != null) {
            gameManager.restart();
        }
        if (gameLoop != null) {
            gameLoop.setPaused(false);
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
