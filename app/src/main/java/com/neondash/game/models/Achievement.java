package com.neondash.game.models;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private int rewardCoins;
    private boolean isUnlocked;

    public Achievement(String id, String title, String description, int rewardCoins, boolean isUnlocked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rewardCoins = rewardCoins;
        this.isUnlocked = isUnlocked;
    }

    public static Achievement[] getInitialAchievements() {
        return new Achievement[]{
                new Achievement("a1", "Booting Up", "Complete your first run in the Cyber Grid", 50, false),
                new Achievement("a2", "Speed Demon", "Reach maximum running velocity", 150, false),
                new Achievement("a3", "Magnetic Pull", "Collect 30 coins using the Magnet power-up", 200, false),
                new Achievement("a4", "Invulnerable", "Absorb a lethal hit using the Energy Shield", 150, false),
                new Achievement("a5", "Double Trouble", "Perform 25 double jumps", 150, false),
                new Achievement("a6", "Cyber Millionaire", "Accumulate a bank of 1,000 coins", 500, false)
        };
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRewardCoins() { return rewardCoins; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
