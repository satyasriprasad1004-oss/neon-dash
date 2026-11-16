package com.neondash.game.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.neondash.game.R;
import com.neondash.game.characters.CharacterManager;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.GameEnvironment;
import com.neondash.game.utilities.Constants;
import com.neondash.game.utilities.SoundManager;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvHighScore, tvCoins, tvCurrentChar, tvCurrentEnv;
    private Button btnModeClassic, btnModeTime, btnModeCoins;
    private PreferencesManager prefs;
    private SoundManager soundManager;
    private int selectedMode = Constants.MODE_CLASSIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        initViews();
        setupModeButtons();
        setupNavigation();
    }

    private void initViews() {
        tvHighScore = findViewById(R.id.tvMainHighScore);
        tvCoins = findViewById(R.id.tvMainCoins);
        tvCurrentChar = findViewById(R.id.tvCurrentChar);
        tvCurrentEnv = findViewById(R.id.tvCurrentEnv);

        btnModeClassic = findViewById(R.id.btnModeClassic);
        btnModeTime = findViewById(R.id.btnModeTime);
        btnModeCoins = findViewById(R.id.btnModeCoins);
    }

    private void setupModeButtons() {
        selectedMode = prefs.getSelectedMode();
        updateModeSelection(selectedMode);

        btnModeClassic.setOnClickListener(v -> {
            soundManager.playClick();
            selectedMode = Constants.MODE_CLASSIC;
            prefs.setSelectedMode(selectedMode);
            updateModeSelection(selectedMode);
        });

        btnModeTime.setOnClickListener(v -> {
            soundManager.playClick();
            selectedMode = Constants.MODE_TIME_CHALLENGE;
            prefs.setSelectedMode(selectedMode);
            updateModeSelection(selectedMode);
        });

        btnModeCoins.setOnClickListener(v -> {
            soundManager.playClick();
            selectedMode = Constants.MODE_COIN_RUSH;
            prefs.setSelectedMode(selectedMode);
            updateModeSelection(selectedMode);
        });
    }

    private void updateModeSelection(int mode) {
        btnModeClassic.setBackgroundResource(mode == Constants.MODE_CLASSIC ? R.drawable.bg_btn_primary : R.drawable.bg_btn_outline);
        btnModeClassic.setTextColor(mode == Constants.MODE_CLASSIC ? Color.BLACK : Color.WHITE);

        btnModeTime.setBackgroundResource(mode == Constants.MODE_TIME_CHALLENGE ? R.drawable.bg_btn_primary : R.drawable.bg_btn_outline);
        btnModeTime.setTextColor(mode == Constants.MODE_TIME_CHALLENGE ? Color.BLACK : Color.WHITE);

        btnModeCoins.setBackgroundResource(mode == Constants.MODE_COIN_RUSH ? R.drawable.bg_btn_primary : R.drawable.bg_btn_outline);
        btnModeCoins.setTextColor(mode == Constants.MODE_COIN_RUSH ? Color.BLACK : Color.WHITE);
    }

    private void setupNavigation() {
        findViewById(R.id.btnPlayGame).setOnClickListener(v -> {
            soundManager.playClick();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnNavCharacters).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, CharacterSelectActivity.class));
        });

        findViewById(R.id.btnNavEnvironments).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, EnvironmentSelectActivity.class));
        });

        findViewById(R.id.btnNavMissions).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, MissionsActivity.class));
        });

        findViewById(R.id.btnNavShop).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, ShopActivity.class));
        });

        findViewById(R.id.btnNavLeaderboard).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, LeaderboardActivity.class));
        });

        findViewById(R.id.btnNavSettings).setOnClickListener(v -> {
            soundManager.playClick();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh coins & high scores
        tvHighScore.setText(String.format(Locale.getDefault(), "%,d PTS", prefs.getHighScore()));
        tvCoins.setText(String.format(Locale.getDefault(), "%,d", prefs.getTotalCoins()));

        CharacterModel activeChar = CharacterManager.getInstance(this).getEquippedCharacter();
        tvCurrentChar.setText(activeChar.getName());

        GameEnvironment activeEnv = GameEnvironment.getById(prefs.getSelectedEnvironment());
        tvCurrentEnv.setText(activeEnv.getName());

        selectedMode = prefs.getSelectedMode();
        updateModeSelection(selectedMode);

        soundManager.startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.stopMusic();
    }
}
