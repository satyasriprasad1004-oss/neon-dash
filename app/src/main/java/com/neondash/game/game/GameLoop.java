package com.neondash.game.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS; // nanoseconds

    private final SurfaceHolder surfaceHolder;
    private final GameManager gameManager;
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;

    public GameLoop(SurfaceHolder surfaceHolder, GameManager gameManager) {
        this.surfaceHolder = surfaceHolder;
        this.gameManager = gameManager;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            long deltaMs = updateLength / 1000000;
            // Cap deltaMs to avoid huge physics steps on lag spikes
            deltaMs = Math.min(deltaMs, 50);

            if (!isPaused) {
                gameManager.update(deltaMs);
            }

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameManager.draw(canvas);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception ignored) {
                    }
                }
            }

            // Frame-rate throttle to maintain steady 60 FPS
            long frameTime = System.nanoTime() - now;
            long sleepTime = (OPTIMAL_TIME - frameTime) / 1000000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
