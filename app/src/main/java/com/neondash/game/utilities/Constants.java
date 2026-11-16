package com.neondash.game.utilities;

public class Constants {
    // Shared Preferences
    public static final String PREF_NAME = "NeonDashPrefs";
    public static final String KEY_HIGH_SCORE = "key_high_score";
    public static final String KEY_TOTAL_COINS = "key_total_coins";
    public static final String KEY_SOUND_ENABLED = "key_sound_enabled";
    public static final String KEY_MUSIC_ENABLED = "key_music_enabled";
    public static final String KEY_VIBRATION_ENABLED = "key_vibration_enabled";
    public static final String KEY_DIFFICULTY = "key_difficulty"; // 0: Easy, 1: Medium, 2: Hard
    public static final String KEY_SELECTED_CHARACTER = "key_selected_char";
    public static final String KEY_SELECTED_ENVIRONMENT = "key_selected_env";
    public static final String KEY_SELECTED_MODE = "key_selected_mode";

    // Power-up Upgrade Keys (Levels 1 to 5)
    public static final String KEY_UPGRADE_MAGNET = "key_up_magnet";
    public static final String KEY_UPGRADE_SHIELD = "key_up_shield";
    public static final String KEY_UPGRADE_SPEED = "key_up_speed";
    public static final String KEY_UPGRADE_SLOWMO = "key_up_slowmo";
    public static final String KEY_UPGRADE_DOUBLEJUMP = "key_up_doublejump";

    // Physics & Game Constants (Tuned for Portrait Mobile Vertical Runner)
    public static final float GRAVITY = 1.25f;
    public static final float BASE_JUMP_FORCE = -21.0f;
    public static final float BASE_DOUBLE_JUMP_FORCE = -19.0f;
    public static final float BASE_RUN_SPEED = 9.0f;
    public static final float MAX_RUN_SPEED = 18.0f;
    public static final float SPEED_ACCELERATION = 0.002f; // per frame

    // Durations (in milliseconds)
    public static final long SLIDE_DURATION_MS = 600;
    public static final long BASE_POWERUP_DURATION_MS = 8000;
    public static final long SPEED_BOOST_DURATION_MS = 6000;
    public static final long SLOW_MOTION_DURATION_MS = 7000;

    // Game Modes
    public static final int MODE_CLASSIC = 0;
    public static final int MODE_TIME_CHALLENGE = 1;
    public static final int MODE_COIN_RUSH = 2;

    // Difficulty Multipliers
    public static final float DIFFICULTY_EASY_MULT = 0.85f;
    public static final float DIFFICULTY_MEDIUM_MULT = 1.0f;
    public static final float DIFFICULTY_HARD_MULT = 1.25f;

    // Character IDs
    public static final String CHAR_NOVA = "char_nova";
    public static final String CHAR_SHADOW = "char_shadow";
    public static final String CHAR_BOLT = "char_bolt";
    public static final String CHAR_TITAN = "char_titan";

    // Environment IDs
    public static final String ENV_NEON_CITY = "env_neon_city";
    public static final String ENV_DESERT_RUN = "env_desert_run";
    public static final String ENV_JUNGLE_ESCAPE = "env_jungle_escape";
    public static final String ENV_CYBER_WORLD = "env_cyber_world";
}
