package com.neondash.game.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.adapters.CharacterAdapter;
import com.neondash.game.characters.CharacterManager;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.utilities.SoundManager;

import java.util.List;

public class CharacterSelectActivity extends AppCompatActivity implements CharacterAdapter.OnCharacterActionListener {

    private RecyclerView recyclerView;
    private TextView tvCoinBalance;
    private CharacterManager characterManager;
    private PreferencesManager prefs;
    private SoundManager soundManager;
    private CharacterAdapter adapter;
    private List<CharacterModel> characterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        characterManager = CharacterManager.getInstance(this);
        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        recyclerView = findViewById(R.id.recyclerCharacters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        loadCharacters();
    }

    private void loadCharacters() {
        tvCoinBalance.setText(String.format("%,d", prefs.getTotalCoins()));
        characterList = characterManager.getAllCharacters();
        adapter = new CharacterAdapter(this, characterList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSelect(CharacterModel character) {
        soundManager.playClick();
        characterManager.equipCharacter(character);
        Toast.makeText(this, character.getName() + " Equipped!", Toast.LENGTH_SHORT).show();
        loadCharacters();
    }

    @Override
    public void onUnlock(CharacterModel character) {
        if (prefs.getTotalCoins() >= character.getUnlockCost()) {
            soundManager.playPowerUp();
            characterManager.unlockCharacter(character);
            characterManager.equipCharacter(character);
            Toast.makeText(this, character.getName() + " Unlocked & Equipped!", Toast.LENGTH_SHORT).show();
            loadCharacters();
        } else {
            soundManager.playCollision();
            Toast.makeText(this, "Insufficient coins! Need " + character.getUnlockCost() + " C", Toast.LENGTH_SHORT).show();
        }
    }
}
