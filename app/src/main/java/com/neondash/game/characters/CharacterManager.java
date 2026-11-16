package com.neondash.game.characters;

import android.content.Context;

import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.CharacterModel;

import java.util.List;

public class CharacterManager {

    private static CharacterManager instance;
    private final DatabaseHelper dbHelper;
    private final PreferencesManager prefs;

    private CharacterManager(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        prefs = PreferencesManager.getInstance(context);
    }

    public static synchronized CharacterManager getInstance(Context context) {
        if (instance == null) {
            instance = new CharacterManager(context);
        }
        return instance;
    }

    public List<CharacterModel> getAllCharacters() {
        return dbHelper.getCharacters();
    }

    public CharacterModel getEquippedCharacter() {
        String equippedId = prefs.getSelectedCharacter();
        List<CharacterModel> list = getAllCharacters();
        for (CharacterModel c : list) {
            if (c.getId().equals(equippedId)) {
                return c;
            }
        }
        return list.isEmpty() ? CharacterModel.getInitialCharacters()[0] : list.get(0);
    }

    public boolean unlockCharacter(CharacterModel character) {
        if (character == null) return false;
        if (prefs.spendCoins(character.getUnlockCost())) {
            character.setUnlocked(true);
            dbHelper.unlockCharacter(character.getId());
            return true;
        }
        return false;
    }

    public void equipCharacter(CharacterModel character) {
        if (character == null || !character.isUnlocked()) return;
        prefs.setSelectedCharacter(character.getId());
        dbHelper.setEquippedCharacter(character.getId());
    }
}
