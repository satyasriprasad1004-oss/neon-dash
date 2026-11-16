package com.neondash.game.models;

public class Mission {
    public static final String TYPE_COINS = "coins";
    public static final String TYPE_SURVIVE = "survive";
    public static final String TYPE_DISTANCE = "distance";
    public static final String TYPE_AVOID = "avoid";
    public static final String TYPE_COINS_SINGLE_RUN = "coins_single";

    private String id;
    private String title;
    private String description;
    private int targetCount;
    private int currentProgress;
    private int rewardCoins;
    private boolean isCompleted;
    private boolean isClaimed;
    private String type;

    public Mission(String id, String title, String description, int targetCount,
                   int currentProgress, int rewardCoins, boolean isCompleted,
                   boolean isClaimed, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetCount = targetCount;
        this.currentProgress = currentProgress;
        this.rewardCoins = rewardCoins;
        this.isCompleted = isCompleted;
        this.isClaimed = isClaimed;
        this.type = type;
    }

    public static Mission[] getInitialMissions() {
        return new Mission[]{
                new Mission("m1", "Coin Collector", "Collect a total of 100 cyber coins", 100, 0, 150, false, false, TYPE_COINS),
                new Mission("m2", "Cyber Survivor", "Survive for 120 seconds in a single run", 120, 0, 200, false, false, TYPE_SURVIVE),
                new Mission("m3", "Grid Marathon", "Travel 5,000 meters across any runs", 5000, 0, 350, false, false, TYPE_DISTANCE),
                new Mission("m4", "Acrobat", "Successfully avoid 50 obstacles", 50, 0, 200, false, false, TYPE_AVOID),
                new Mission("m5", "Flawless Grab", "Collect 20 coins in a single run without crashing", 20, 0, 250, false, false, TYPE_COINS_SINGLE_RUN)
        };
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTargetCount() { return targetCount; }
    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }
    public int getRewardCoins() { return rewardCoins; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isClaimed() { return isClaimed; }
    public void setClaimed(boolean claimed) { isClaimed = claimed; }
    public String getType() { return type; }
}
