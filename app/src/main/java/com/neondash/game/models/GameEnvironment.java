package com.neondash.game.models;

import com.neondash.game.utilities.Constants;

public class GameEnvironment {
    private String id;
    private String name;
    private String description;
    private int unlockDistance;
    private int unlockCoins;
    private boolean isUnlocked;

    // Visual Theme Colors
    private int skyColorTop;
    private int skyColorBottom;
    private int groundColor;
    private int gridLineColor;
    private int accentGlowColor;
    private int obstacleTintColor;

    public GameEnvironment(String id, String name, String description,
                           int unlockDistance, int unlockCoins, boolean isUnlocked,
                           int skyColorTop, int skyColorBottom, int groundColor,
                           int gridLineColor, int accentGlowColor, int obstacleTintColor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlockDistance = unlockDistance;
        this.unlockCoins = unlockCoins;
        this.isUnlocked = isUnlocked;
        this.skyColorTop = skyColorTop;
        this.skyColorBottom = skyColorBottom;
        this.groundColor = groundColor;
        this.gridLineColor = gridLineColor;
        this.accentGlowColor = accentGlowColor;
        this.obstacleTintColor = obstacleTintColor;
    }

    public static GameEnvironment[] getAllEnvironments() {
        return new GameEnvironment[]{
                new GameEnvironment(
                        Constants.ENV_NEON_CITY,
                        "Neon City",
                        "Futuristic city with glowing holographic skyscrapers and cyber grid roads.",
                        0,
                        0,
                        true,
                        (int) 0xFF080914L,
                        (int) 0xFF151838L,
                        (int) 0xFF0C0E1EL,
                        (int) 0xFF00F0FFL,
                        (int) 0xFF00F0FFL,
                        (int) 0xFFFF007FL
                ),
                new GameEnvironment(
                        Constants.ENV_DESERT_RUN,
                        "Desert Run",
                        "Radioactive wasteland with sandstorms, digital dunes, and neon cacti.",
                        1500,
                        300,
                        false,
                        (int) 0xFF1A0B05L,
                        (int) 0xFF38170DL,
                        (int) 0xFF241008L,
                        (int) 0xFFFFE600L,
                        (int) 0xFFFF6600L,
                        (int) 0xFFFFD700L
                ),
                new GameEnvironment(
                        Constants.ENV_JUNGLE_ESCAPE,
                        "Jungle Escape",
                        "Bioluminescent alien canopy with glowing vines, spores, and cyber flora.",
                        3500,
                        600,
                        false,
                        (int) 0xFF04140DL,
                        (int) 0xFF0B2A1CL,
                        (int) 0xFF071F14L,
                        (int) 0xFF39FF14L,
                        (int) 0xFF00FF88L,
                        (int) 0xFF39FF14L
                ),
                new GameEnvironment(
                        Constants.ENV_CYBER_WORLD,
                        "Cyber World",
                        "Raw digital cyberspace filled with matrix data streams and laser platforms.",
                        6000,
                        1000,
                        false,
                        (int) 0xFF0E051AL,
                        (int) 0xFF260E45L,
                        (int) 0xFF160829L,
                        (int) 0xFFFF007FL,
                        (int) 0xFFA020F0L,
                        (int) 0xFF00F0FFL
                )
        };
    }

    public static GameEnvironment getById(String id) {
        for (GameEnvironment env : getAllEnvironments()) {
            if (env.getId().equals(id)) return env;
        }
        return getAllEnvironments()[0];
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getUnlockDistance() { return unlockDistance; }
    public int getUnlockCoins() { return unlockCoins; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    public int getSkyColorTop() { return skyColorTop; }
    public int getSkyColorBottom() { return skyColorBottom; }
    public int getGroundColor() { return groundColor; }
    public int getGridLineColor() { return gridLineColor; }
    public int getAccentGlowColor() { return accentGlowColor; }
    public int getObstacleTintColor() { return obstacleTintColor; }
}
