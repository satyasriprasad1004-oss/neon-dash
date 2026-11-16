package com.neondash.game.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.neondash.game.models.CoinPool;
import com.neondash.game.powerups.PowerUpManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObstacleManager {
    private final List<Obstacle> pool = new ArrayList<>();
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float distanceSinceLastSpawn = 0.0f;
    private float nextSpawnDistance = 450.0f;
    private float distanceSinceLastPowerUp = 0.0f;
    private float powerUpInterval = 1800.0f;

    public ObstacleManager() {
        // Pre-allocate obstacle pools to prevent runtime object allocations
        for (int i = 0; i < 4; i++) pool.add(new SpikeObstacle());
        for (int i = 0; i < 4; i++) pool.add(new ElectricBarrierObstacle());
        for (int i = 0; i < 4; i++) pool.add(new FlyingDroneObstacle());
        for (int i = 0; i < 4; i++) pool.add(new MovingRobotObstacle());
        for (int i = 0; i < 4; i++) pool.add(new LaserObstacle());
        for (int i = 0; i < 3; i++) pool.add(new FallingObjectObstacle());
    }

    public void update(float gameSpeed, float screenWidth, float groundY,
                       CoinPool coinPool, PowerUpManager powerUpManager, int difficulty) {
        // Update existing obstacles
        for (int i = 0; i < pool.size(); i++) {
            Obstacle obs = pool.get(i);
            if (obs.isActive) {
                obs.update(gameSpeed);
            }
        }

        // Spawn logic
        distanceSinceLastSpawn += gameSpeed;
        distanceSinceLastPowerUp += gameSpeed;

        if (distanceSinceLastSpawn >= nextSpawnDistance) {
            spawnRandomObstacle(screenWidth + 60, groundY, coinPool, difficulty);
            distanceSinceLastSpawn = 0.0f;
            // Gap dynamically scales with speed so player always has time to react
            float baseGap = 460.0f + gameSpeed * 12.0f;
            nextSpawnDistance = baseGap + random.nextFloat() * 180.0f;
        }

        // Spawn field power-up periodically
        if (distanceSinceLastPowerUp >= powerUpInterval && powerUpManager != null) {
            float powerUpY = groundY - 60.0f - random.nextFloat() * 80.0f;
            powerUpManager.spawn(screenWidth + 100, powerUpY);
            distanceSinceLastPowerUp = 0.0f;
            powerUpInterval = 2200.0f + random.nextFloat() * 1200.0f;
        }
    }

    private void spawnRandomObstacle(float spawnX, float groundY, CoinPool coinPool, int difficulty) {
        int obsType;
        if (difficulty == 0) { // Easy
            int[] easyTypes = new int[]{Obstacle.TYPE_SPIKES, Obstacle.TYPE_ELECTRIC_BARRIER, Obstacle.TYPE_ROBOT};
            obsType = easyTypes[random.nextInt(easyTypes.length)];
        } else {
            obsType = random.nextInt(6);
        }

        Obstacle obs = getInactiveObstacle(obsType);
        if (obs == null) {
            obs = getAnyInactiveObstacle();
        }

        if (obs != null) {
            if (obs instanceof SpikeObstacle) {
                int count = (difficulty == 2) ? (random.nextInt(3) + 1) : (random.nextInt(2) + 1);
                ((SpikeObstacle) obs).init(spawnX, groundY, count);
                // Spawn coins in an arc over the spikes!
                if (coinPool != null && random.nextFloat() < 0.75f) {
                    coinPool.spawnArc(spawnX - 40, groundY - 20, 5, 30.0f, 130.0f);
                }
            } else if (obs instanceof ElectricBarrierObstacle) {
                boolean isHigh = random.nextBoolean();
                ((ElectricBarrierObstacle) obs).init(spawnX, groundY, isHigh);
                if (coinPool != null) {
                    if (isHigh) {
                        // Spawn coins low along ground to reward sliding!
                        coinPool.spawnLine(spawnX - 30, groundY - 20, 4, 32.0f);
                    } else {
                        // Spawn coins high to reward jumping!
                        coinPool.spawnArc(spawnX - 40, groundY - 20, 5, 30.0f, 140.0f);
                    }
                }
            } else if (obs instanceof FlyingDroneObstacle) {
                obs.init(spawnX, groundY);
                // Sliding reward coins underneath drone
                if (coinPool != null && random.nextFloat() < 0.8f) {
                    coinPool.spawnLine(spawnX - 20, groundY - 20, 4, 30.0f);
                }
            } else {
                obs.init(spawnX, groundY);
                if (coinPool != null && random.nextFloat() < 0.5f) {
                    coinPool.spawnArc(spawnX - 30, groundY - 20, 4, 30.0f, 120.0f);
                }
            }
        }
    }

    private Obstacle getInactiveObstacle(int type) {
        for (int i = 0; i < pool.size(); i++) {
            Obstacle o = pool.get(i);
            if (!o.isActive && o.type == type) {
                return o;
            }
        }
        return null;
    }

    private Obstacle getAnyInactiveObstacle() {
        for (int i = 0; i < pool.size(); i++) {
            Obstacle o = pool.get(i);
            if (!o.isActive) {
                return o;
            }
        }
        return null;
    }

    public Obstacle checkCollision(RectF playerBounds) {
        for (int i = 0; i < pool.size(); i++) {
            Obstacle obs = pool.get(i);
            if (obs.isActive && RectF.intersects(obs.getBounds(), playerBounds)) {
                return obs;
            }
        }
        return null;
    }

    public void draw(Canvas canvas, int envTintColor) {
        for (int i = 0; i < pool.size(); i++) {
            Obstacle obs = pool.get(i);
            if (obs.isActive) {
                obs.draw(canvas, paint, envTintColor);
            }
        }
    }

    public void reset() {
        for (int i = 0; i < pool.size(); i++) {
            pool.get(i).isActive = false;
        }
        distanceSinceLastSpawn = 0.0f;
        nextSpawnDistance = 450.0f;
        distanceSinceLastPowerUp = 0.0f;
    }
}
