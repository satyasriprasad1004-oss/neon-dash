package com.neondash.game.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.neondash.game.utilities.Constants;

public class PreferencesManager {

    private static PreferencesManager instance;
    private final SharedPreferences prefs;

    private PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    // High Score
    public int getHighScore() {
        return prefs.getInt(Constants.KEY_HIGH_SCORE, 0);
    }

    public void setHighScore(int score) {
        if (score > getHighScore()) {
            prefs.edit().putInt(Constants.KEY_HIGH_SCORE, score).apply();
        }
    }

    // Coins
    public int getTotalCoins() {
        return prefs.getInt(Constants.KEY_TOTAL_COINS, 0);
    }

    public void addCoins(int amount) {
        int current = getTotalCoins();
        prefs.edit().putInt(Constants.KEY_TOTAL_COINS, Math.max(0, current + amount)).apply();
    }

    public boolean spendCoins(int amount) {
        int current = getTotalCoins();
        if (current >= amount) {
            prefs.edit().putInt(Constants.KEY_TOTAL_COINS, current - amount).apply();
            return true;
        }
        return false;
    }

    // Settings
    public boolean isSoundEnabled() {
        return prefs.getBoolean(Constants.KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.KEY_SOUND_ENABLED, enabled).apply();
    }

    public boolean isMusicEnabled() {
        return prefs.getBoolean(Constants.KEY_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.KEY_MUSIC_ENABLED, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return prefs.getBoolean(Constants.KEY_VIBRATION_ENABLED, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean(Constants.KEY_VIBRATION_ENABLED, enabled).apply();
    }

    public int getDifficulty() {
        return prefs.getInt(Constants.KEY_DIFFICULTY, 1); // 1 = Medium
    }

    public void setDifficulty(int difficulty) {
        prefs.edit().putInt(Constants.KEY_DIFFICULTY, difficulty).apply();
    }

    // Selections
    public String getSelectedCharacter() {
        return prefs.getString(Constants.KEY_SELECTED_CHARACTER, Constants.CHAR_NOVA);
    }

    public void setSelectedCharacter(String characterId) {
        prefs.edit().putString(Constants.KEY_SELECTED_CHARACTER, characterId).apply();
    }

    public String getSelectedEnvironment() {
        return prefs.getString(Constants.KEY_SELECTED_ENVIRONMENT, Constants.ENV_NEON_CITY);
    }

    public void setSelectedEnvironment(String envId) {
        prefs.edit().putString(Constants.KEY_SELECTED_ENVIRONMENT, envId).apply();
    }

    public int getSelectedMode() {
        return prefs.getInt(Constants.KEY_SELECTED_MODE, Constants.MODE_CLASSIC);
    }

    public void setSelectedMode(int mode) {
        prefs.edit().putInt(Constants.KEY_SELECTED_MODE, mode).apply();
    }

    // Power-up upgrades (Levels 1 to 5)
    public int getUpgradeLevel(String upgradeKey) {
        return prefs.getInt(upgradeKey, 1);
    }

    public void setUpgradeLevel(String upgradeKey, int level) {
        prefs.edit().putInt(upgradeKey, level).apply();
    }
}
