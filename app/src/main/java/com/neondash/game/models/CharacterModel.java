package com.neondash.game.models;

import com.neondash.game.utilities.Constants;

public class CharacterModel {
    private String id;
    private String name;
    private String title;
    private String description;
    private int primaryColor;
    private int secondaryColor;
    private int accentColor;
    private int unlockCost;
    private boolean isUnlocked;
    private boolean isEquipped;

    // Attributes (1.0 = baseline)
    private float speedMultiplier;
    private float jumpMultiplier;
    private float magnetMultiplier;

    public CharacterModel(String id, String name, String title, String description,
                          int primaryColor, int secondaryColor, int accentColor,
                          int unlockCost, boolean isUnlocked, boolean isEquipped,
                          float speedMultiplier, float jumpMultiplier, float magnetMultiplier) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.accentColor = accentColor;
        this.unlockCost = unlockCost;
        this.isUnlocked = isUnlocked;
        this.isEquipped = isEquipped;
        this.speedMultiplier = speedMultiplier;
        this.jumpMultiplier = jumpMultiplier;
        this.magnetMultiplier = magnetMultiplier;
    }

    public static CharacterModel[] getInitialCharacters() {
        return new CharacterModel[]{
                new CharacterModel(
                        Constants.CHAR_NOVA,
                        "Nova",
                        "Standard Cyber Runner",
                        "Balanced agile prototype built for high-speed cyber navigation.",
                        (int) 0xFF00F0FFL, // Cyan
                        (int) 0xFF0080FFL,
                        (int) 0xFFFFFFFFL,
                        0,
                        true,
                        true,
                        1.0f,
                        1.0f,
                        1.0f
                ),
                new CharacterModel(
                        Constants.CHAR_SHADOW,
                        "Shadow",
                        "Ninja Phantom Unit",
                        "Stealth-oriented cyber shinobi with augmented jump thrusters and phase shielding.",
                        (int) 0xFFA020F0L, // Purple
                        (int) 0xFF4A0E80L,
                        (int) 0xFFFF007FL,
                        250,
                        false,
                        false,
                        1.05f,
                        1.15f,
                        1.1f
                ),
                new CharacterModel(
                        Constants.CHAR_BOLT,
                        "Bolt",
                        "Hyper-Velocity Android",
                        "Overclocked kinetic reactor. Fastest ground accelerator in the grid.",
                        (int) 0xFFFFE600L, // Electric Yellow
                        (int) 0xFFFF8800L,
                        (int) 0xFFFFFFFFL,
                        500,
                        false,
                        false,
                        1.20f,
                        1.05f,
                        1.2f
                ),
                new CharacterModel(
                        Constants.CHAR_TITAN,
                        "Titan",
                        "Armored Dreadnought",
                        "Heavy titanium alloy plating with powerful magnetic collection coils.",
                        (int) 0xFFFF1744L, // Crimson
                        (int) 0xFF800A1FL,
                        (int) 0xFFFF6600L,
                        1000,
                        false,
                        false,
                        0.95f,
                        1.10f,
                        1.5f
                )
        };
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrimaryColor() { return primaryColor; }
    public int getSecondaryColor() { return secondaryColor; }
    public int getAccentColor() { return accentColor; }
    public int getUnlockCost() { return unlockCost; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    public float getSpeedMultiplier() { return speedMultiplier; }
    public float getJumpMultiplier() { return jumpMultiplier; }
    public float getMagnetMultiplier() { return magnetMultiplier; }
}
