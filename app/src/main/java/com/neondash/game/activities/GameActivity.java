package com.neondash.game.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.neondash.game.R;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.game.GameManager;
import com.neondash.game.game.GameView;
import com.neondash.game.utilities.SoundManager;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements GameManager.GameEventListener {

    private GameView gameView;
    private TextView tvHudScore, tvHudDistance, tvHudCoins;
    private ImageButton btnPause;
    private Button btnOnScreenJump, btnOnScreenSlide;

    // Pause Dialog
    private FrameLayout overlayPause;
    private Button btnResume, btnPauseRestart, btnPauseMainMenu;

    // Game Over Dialog
    private FrameLayout overlayGameOver;
    private TextView tvGameOverTitle, tvNewHighScoreBadge;
    private TextView tvGameOverScore, tvGameOverDistance, tvGameOverCoins, tvGameOverBestScore;
    private Button btnGameOverRestart, btnGameOverMainMenu;

    private SoundManager soundManager;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        soundManager = SoundManager.getInstance(this);
        prefs = PreferencesManager.getInstance(this);

        initViews();
        setupListeners();
        setupBackNavigation();
    }

    private void initViews() {
        gameView = findViewById(R.id.gameView);
        gameView.getGameManager().setGameEventListener(this);

        tvHudScore = findViewById(R.id.tvHudScore);
        tvHudDistance = findViewById(R.id.tvHudDistance);
        tvHudCoins = findViewById(R.id.tvHudCoins);
        btnPause = findViewById(R.id.btnPause);

        btnOnScreenJump = findViewById(R.id.btnOnScreenJump);
        btnOnScreenSlide = findViewById(R.id.btnOnScreenSlide);

        // Pause Overlay
        overlayPause = findViewById(R.id.overlayPause);
        btnResume = findViewById(R.id.btnResume);
        btnPauseRestart = findViewById(R.id.btnPauseRestart);
        btnPauseMainMenu = findViewById(R.id.btnPauseMainMenu);

        // Game Over Overlay
        overlayGameOver = findViewById(R.id.overlayGameOver);
        tvGameOverTitle = findViewById(R.id.tvGameOverTitle);
        tvNewHighScoreBadge = findViewById(R.id.tvNewHighScoreBadge);
        tvGameOverScore = findViewById(R.id.tvGameOverScore);
        tvGameOverDistance = findViewById(R.id.tvGameOverDistance);
        tvGameOverCoins = findViewById(R.id.tvGameOverCoins);
        tvGameOverBestScore = findViewById(R.id.tvGameOverBestScore);
        btnGameOverRestart = findViewById(R.id.btnGameOverRestart);
        btnGameOverMainMenu = findViewById(R.id.btnGameOverMainMenu);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        btnOnScreenJump.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.getGameManager().jump();
                v.setAlpha(0.65f);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setAlpha(1.0f);
            }
            return true;
        });

        btnOnScreenSlide.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.getGameManager().slide();
                v.setAlpha(0.65f);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setAlpha(1.0f);
            }
            return true;
        });

        btnPause.setOnClickListener(v -> {
            soundManager.playClick();
            gameView.pauseGame();
            overlayPause.setVisibility(View.VISIBLE);
        });

        btnResume.setOnClickListener(v -> {
            soundManager.playClick();
            overlayPause.setVisibility(View.GONE);
            gameView.resumeGame();
        });

        btnPauseRestart.setOnClickListener(v -> {
            soundManager.playClick();
            overlayPause.setVisibility(View.GONE);
            gameView.restartGame();
        });

        btnPauseMainMenu.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        btnGameOverRestart.setOnClickListener(v -> {
            soundManager.playClick();
            overlayGameOver.setVisibility(View.GONE);
            gameView.restartGame();
        });

        btnGameOverMainMenu.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (overlayGameOver.getVisibility() == View.VISIBLE) {
                    finish();
                } else if (overlayPause.getVisibility() == View.VISIBLE) {
                    overlayPause.setVisibility(View.GONE);
                    gameView.resumeGame();
                } else {
                    gameView.pauseGame();
                    overlayPause.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onScoreUpdated(int score, int distance, int coins) {
        runOnUiThread(() -> {
            tvHudScore.setText(String.format(Locale.getDefault(), "SCORE: %,d", score));
            tvHudDistance.setText(String.format(Locale.getDefault(), "%,dm", distance));
            tvHudCoins.setText(String.valueOf(coins));
        });
    }

    @Override
    public void onGameOver(int finalScore, int distance, int coins, boolean isNewHighScore, boolean isVictory) {
        runOnUiThread(() -> {
            overlayPause.setVisibility(View.GONE);
            overlayGameOver.setVisibility(View.VISIBLE);

            if (isVictory) {
                tvGameOverTitle.setText("CHALLENGE COMPLETE!");
                tvGameOverTitle.setTextColor(ContextCompat.getColor(this, R.color.neon_cyan));
            } else {
                tvGameOverTitle.setText("RUN TERMINATED");
                tvGameOverTitle.setTextColor(ContextCompat.getColor(this, R.color.neon_magenta));
            }

            tvNewHighScoreBadge.setVisibility(isNewHighScore ? View.VISIBLE : View.GONE);
            tvGameOverScore.setText(String.format(Locale.getDefault(), "%,d PTS", finalScore));
            tvGameOverDistance.setText(String.format(Locale.getDefault(), "%,dm", distance));
            tvGameOverCoins.setText(String.format(Locale.getDefault(), "+%d", coins));
            tvGameOverBestScore.setText(String.format(Locale.getDefault(), "%,d PTS", prefs.getHighScore()));
        });
    }

    @Override
    public void onPauseStateChanged(boolean isPaused) {
        runOnUiThread(() -> overlayPause.setVisibility(isPaused ? View.VISIBLE : View.GONE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
        }
        soundManager.stopMusic();
    }
}
