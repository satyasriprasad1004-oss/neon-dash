package com.neondash.game.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.neondash.game.R;
import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.utilities.SoundManager;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchSound, switchMusic, switchVibration;
    private Button btnDiffEasy, btnDiffMedium, btnDiffHard;
    private PreferencesManager prefs;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        initAudioToggles();
        initDifficultyButtons();
        initResetButton();
    }

    private void initAudioToggles() {
        switchSound = findViewById(R.id.switchSound);
        switchMusic = findViewById(R.id.switchMusic);
        switchVibration = findViewById(R.id.switchVibration);

        switchSound.setChecked(prefs.isSoundEnabled());
        switchMusic.setChecked(prefs.isMusicEnabled());
        switchVibration.setChecked(prefs.isVibrationEnabled());

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setSoundEnabled(isChecked);
            if (isChecked) soundManager.playClick();
        });

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setMusicEnabled(isChecked);
            if (isChecked) {
                soundManager.startMusic();
            } else {
                soundManager.stopMusic();
            }
        });

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setVibrationEnabled(isChecked);
        });
    }

    private void initDifficultyButtons() {
        btnDiffEasy = findViewById(R.id.btnDiffEasy);
        btnDiffMedium = findViewById(R.id.btnDiffMedium);
        btnDiffHard = findViewById(R.id.btnDiffHard);

        int currentDiff = prefs.getDifficulty();
        updateDifficultyUI(currentDiff);

        btnDiffEasy.setOnClickListener(v -> {
            soundManager.playClick();
            prefs.setDifficulty(0);
            updateDifficultyUI(0);
        });

        btnDiffMedium.setOnClickListener(v -> {
            soundManager.playClick();
            prefs.setDifficulty(1);
            updateDifficultyUI(1);
        });

        btnDiffHard.setOnClickListener(v -> {
            soundManager.playClick();
            prefs.setDifficulty(2);
            updateDifficultyUI(2);
        });
    }

    private void updateDifficultyUI(int diff) {
        setDiffButtonState(btnDiffEasy, diff == 0);
        setDiffButtonState(btnDiffMedium, diff == 1);
        setDiffButtonState(btnDiffHard, diff == 2);
    }

    private void setDiffButtonState(Button btn, boolean isSelected) {
        btn.setBackgroundResource(isSelected ? R.drawable.bg_btn_primary : R.drawable.bg_btn_outline);
        btn.setTextColor(isSelected ? Color.BLACK : Color.WHITE);
    }

    private void initResetButton() {
        findViewById(R.id.btnResetData).setOnClickListener(v -> {
            soundManager.playClick();
            new AlertDialog.Builder(this)
                    .setTitle("PURGE SYSTEM DATA")
                    .setMessage("Are you sure you want to reset all high scores, coins, unlocked characters, and mission progress? This cannot be undone.")
                    .setPositiveButton("PURGE DATA", (dialog, which) -> {
                        DatabaseHelper.getInstance(this).onUpgrade(
                                DatabaseHelper.getInstance(this).getWritableDatabase(), 1, 1
                        );
                        prefs.setHighScore(0);
                        prefs.spendCoins(prefs.getTotalCoins()); // reset coins to 0
                        soundManager.playCollision();
                        Toast.makeText(this, "System data has been reset.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        });
    }
}
