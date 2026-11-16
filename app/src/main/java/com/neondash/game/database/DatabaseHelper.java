package com.neondash.game.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neondash.game.models.Achievement;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.GameEnvironment;
import com.neondash.game.models.LeaderboardEntry;
import com.neondash.game.models.Mission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NeonDash.db";
    private static final int DATABASE_VERSION = 1;

    // Table: Scores
    private static final String TABLE_SCORES = "scores";
    private static final String COL_SCORE_ID = "id";
    private static final String COL_PLAYER_NAME = "player_name";
    private static final String COL_SCORE = "score";
    private static final String COL_DISTANCE = "distance";
    private static final String COL_COINS = "coins";
    private static final String COL_CHARACTER = "character";
    private static final String COL_MODE = "mode";
    private static final String COL_DATE = "date";

    // Table: Characters
    private static final String TABLE_CHARACTERS = "characters";
    private static final String COL_CHAR_ID = "id";
    private static final String COL_CHAR_UNLOCKED = "unlocked";
    private static final String COL_CHAR_EQUIPPED = "equipped";

    // Table: Environments
    private static final String TABLE_ENVIRONMENTS = "environments";
    private static final String COL_ENV_ID = "id";
    private static final String COL_ENV_UNLOCKED = "unlocked";

    // Table: Missions
    private static final String TABLE_MISSIONS = "missions";
    private static final String COL_MISSION_ID = "id";
    private static final String COL_MISSION_PROGRESS = "progress";
    private static final String COL_MISSION_COMPLETED = "completed";
    private static final String COL_MISSION_CLAIMED = "claimed";

    // Table: Achievements
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String COL_ACHIEVEMENT_ID = "id";
    private static final String COL_ACHIEVEMENT_UNLOCKED = "unlocked";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Scores Table
        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                COL_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PLAYER_NAME + " TEXT, " +
                COL_SCORE + " INTEGER, " +
                COL_DISTANCE + " INTEGER, " +
                COL_COINS + " INTEGER, " +
                COL_CHARACTER + " TEXT, " +
                COL_MODE + " TEXT, " +
                COL_DATE + " TEXT)");

        // Create Characters Table
        db.execSQL("CREATE TABLE " + TABLE_CHARACTERS + " (" +
                COL_CHAR_ID + " TEXT PRIMARY KEY, " +
                COL_CHAR_UNLOCKED + " INTEGER, " +
                COL_CHAR_EQUIPPED + " INTEGER)");

        // Create Environments Table
        db.execSQL("CREATE TABLE " + TABLE_ENVIRONMENTS + " (" +
                COL_ENV_ID + " TEXT PRIMARY KEY, " +
                COL_ENV_UNLOCKED + " INTEGER)");

        // Create Missions Table
        db.execSQL("CREATE TABLE " + TABLE_MISSIONS + " (" +
                COL_MISSION_ID + " TEXT PRIMARY KEY, " +
                COL_MISSION_PROGRESS + " INTEGER, " +
                COL_MISSION_COMPLETED + " INTEGER, " +
                COL_MISSION_CLAIMED + " INTEGER)");

        // Create Achievements Table
        db.execSQL("CREATE TABLE " + TABLE_ACHIEVEMENTS + " (" +
                COL_ACHIEVEMENT_ID + " TEXT PRIMARY KEY, " +
                COL_ACHIEVEMENT_UNLOCKED + " INTEGER)");

        // Seed initial data
        seedInitialData(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed characters
        for (CharacterModel c : CharacterModel.getInitialCharacters()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_CHAR_ID, c.getId());
            cv.put(COL_CHAR_UNLOCKED, c.isUnlocked() ? 1 : 0);
            cv.put(COL_CHAR_EQUIPPED, c.isEquipped() ? 1 : 0);
            db.insert(TABLE_CHARACTERS, null, cv);
        }

        // Seed environments
        for (GameEnvironment env : GameEnvironment.getAllEnvironments()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ENV_ID, env.getId());
            cv.put(COL_ENV_UNLOCKED, env.isUnlocked() ? 1 : 0);
            db.insert(TABLE_ENVIRONMENTS, null, cv);
        }

        // Seed missions
        for (Mission m : Mission.getInitialMissions()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_MISSION_ID, m.getId());
            cv.put(COL_MISSION_PROGRESS, 0);
            cv.put(COL_MISSION_COMPLETED, 0);
            cv.put(COL_MISSION_CLAIMED, 0);
            db.insert(TABLE_MISSIONS, null, cv);
        }

        // Seed achievements
        for (Achievement a : Achievement.getInitialAchievements()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ACHIEVEMENT_ID, a.getId());
            cv.put(COL_ACHIEVEMENT_UNLOCKED, 0);
            db.insert(TABLE_ACHIEVEMENTS, null, cv);
        }

        // Add a few baseline local high scores to populate leaderboard on first install
        insertSampleScore(db, "CYBER_ACE", 4850, 1200, 48, "Nova", "Classic");
        insertSampleScore(db, "NEO_RUNNER", 3120, 850, 32, "Shadow", "Classic");
        insertSampleScore(db, "GLITCH", 1950, 560, 18, "Nova", "Time Challenge");
    }

    private void insertSampleScore(SQLiteDatabase db, String player, int score, int distance, int coins, String character, String mode) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PLAYER_NAME, player);
        cv.put(COL_SCORE, score);
        cv.put(COL_DISTANCE, distance);
        cv.put(COL_COINS, coins);
        cv.put(COL_CHARACTER, character);
        cv.put(COL_MODE, mode);
        cv.put(COL_DATE, "Today");
        db.insert(TABLE_SCORES, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENVIRONMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        onCreate(db);
    }

    // Leaderboard Methods
    public void addScore(String playerName, int score, int distance, int coins, String character, String mode) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PLAYER_NAME, playerName);
        cv.put(COL_SCORE, score);
        cv.put(COL_DISTANCE, distance);
        cv.put(COL_COINS, coins);
        cv.put(COL_CHARACTER, character);
        cv.put(COL_MODE, mode);
        String dateStr = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        cv.put(COL_DATE, dateStr);
        db.insert(TABLE_SCORES, null, cv);
    }

    public List<LeaderboardEntry> getLeaderboard(String modeFilter) {
        List<LeaderboardEntry> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        if (modeFilter != null && !modeFilter.equalsIgnoreCase("All")) {
            selection = COL_MODE + " = ?";
            selectionArgs = new String[]{modeFilter};
        }
        Cursor c = db.query(TABLE_SCORES, null, selection, selectionArgs, null, null, COL_SCORE + " DESC", "50");
        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(COL_SCORE_ID));
                String name = c.getString(c.getColumnIndexOrThrow(COL_PLAYER_NAME));
                int score = c.getInt(c.getColumnIndexOrThrow(COL_SCORE));
                int dist = c.getInt(c.getColumnIndexOrThrow(COL_DISTANCE));
                int coins = c.getInt(c.getColumnIndexOrThrow(COL_COINS));
                String character = c.getString(c.getColumnIndexOrThrow(COL_CHARACTER));
                String mode = c.getString(c.getColumnIndexOrThrow(COL_MODE));
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                list.add(new LeaderboardEntry(id, name, score, dist, coins, character, mode, date));
            }
            c.close();
        }
        return list;
    }

    // Character Methods
    public List<CharacterModel> getCharacters() {
        List<CharacterModel> list = new ArrayList<>();
        CharacterModel[] defaults = CharacterModel.getInitialCharacters();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CHARACTERS, null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COL_CHAR_ID));
                boolean unlocked = c.getInt(c.getColumnIndexOrThrow(COL_CHAR_UNLOCKED)) == 1;
                boolean equipped = c.getInt(c.getColumnIndexOrThrow(COL_CHAR_EQUIPPED)) == 1;
                for (CharacterModel def : defaults) {
                    if (def.getId().equals(id)) {
                        def.setUnlocked(unlocked);
                        def.setEquipped(equipped);
                        list.add(def);
                        break;
                    }
                }
            }
            c.close();
        }
        return list;
    }

    public void unlockCharacter(String charId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CHAR_UNLOCKED, 1);
        db.update(TABLE_CHARACTERS, cv, COL_CHAR_ID + " = ?", new String[]{charId});
    }

    public void setEquippedCharacter(String charId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cvUnequip = new ContentValues();
        cvUnequip.put(COL_CHAR_EQUIPPED, 0);
        db.update(TABLE_CHARACTERS, cvUnequip, null, null);

        ContentValues cvEquip = new ContentValues();
        cvEquip.put(COL_CHAR_EQUIPPED, 1);
        db.update(TABLE_CHARACTERS, cvEquip, COL_CHAR_ID + " = ?", new String[]{charId});
    }

    // Environments Methods
    public List<GameEnvironment> getEnvironments() {
        List<GameEnvironment> list = new ArrayList<>();
        GameEnvironment[] defaults = GameEnvironment.getAllEnvironments();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ENVIRONMENTS, null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COL_ENV_ID));
                boolean unlocked = c.getInt(c.getColumnIndexOrThrow(COL_ENV_UNLOCKED)) == 1;
                for (GameEnvironment def : defaults) {
                    if (def.getId().equals(id)) {
                        def.setUnlocked(unlocked);
                        list.add(def);
                        break;
                    }
                }
            }
            c.close();
        }
        return list;
    }

    public void unlockEnvironment(String envId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ENV_UNLOCKED, 1);
        db.update(TABLE_ENVIRONMENTS, cv, COL_ENV_ID + " = ?", new String[]{envId});
    }

    // Missions Methods
    public List<Mission> getMissions() {
        List<Mission> list = new ArrayList<>();
        Mission[] defaults = Mission.getInitialMissions();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MISSIONS, null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COL_MISSION_ID));
                int progress = c.getInt(c.getColumnIndexOrThrow(COL_MISSION_PROGRESS));
                boolean completed = c.getInt(c.getColumnIndexOrThrow(COL_MISSION_COMPLETED)) == 1;
                boolean claimed = c.getInt(c.getColumnIndexOrThrow(COL_MISSION_CLAIMED)) == 1;
                for (Mission def : defaults) {
                    if (def.getId().equals(id)) {
                        def.setCurrentProgress(progress);
                        def.setCompleted(completed);
                        def.setClaimed(claimed);
                        list.add(def);
                        break;
                    }
                }
            }
            c.close();
        }
        return list;
    }

    public void updateMissionProgress(String type, int progressToAdd, boolean isAbsolute) {
        List<Mission> missions = getMissions();
        SQLiteDatabase db = getWritableDatabase();
        for (Mission m : missions) {
            if (m.getType().equals(type) && !m.isCompleted()) {
                int newProgress = isAbsolute ? progressToAdd : (m.getCurrentProgress() + progressToAdd);
                boolean completed = newProgress >= m.getTargetCount();
                ContentValues cv = new ContentValues();
                cv.put(COL_MISSION_PROGRESS, newProgress);
                cv.put(COL_MISSION_COMPLETED, completed ? 1 : 0);
                db.update(TABLE_MISSIONS, cv, COL_MISSION_ID + " = ?", new String[]{m.getId()});
            }
        }
    }

    public void claimMission(String missionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MISSION_CLAIMED, 1);
        db.update(TABLE_MISSIONS, cv, COL_MISSION_ID + " = ?", new String[]{missionId});
    }

    // Achievements Methods
    public List<Achievement> getAchievements() {
        List<Achievement> list = new ArrayList<>();
        Achievement[] defaults = Achievement.getInitialAchievements();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ACHIEVEMENTS, null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COL_ACHIEVEMENT_ID));
                boolean unlocked = c.getInt(c.getColumnIndexOrThrow(COL_ACHIEVEMENT_UNLOCKED)) == 1;
                for (Achievement def : defaults) {
                    if (def.getId().equals(id)) {
                        def.setUnlocked(unlocked);
                        list.add(def);
                        break;
                    }
                }
            }
            c.close();
        }
        return list;
    }

    public void unlockAchievement(String achievementId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ACHIEVEMENT_UNLOCKED, 1);
        db.update(TABLE_ACHIEVEMENTS, cv, COL_ACHIEVEMENT_ID + " = ?", new String[]{achievementId});
    }
}
