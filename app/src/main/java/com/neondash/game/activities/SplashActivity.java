package com.neondash.game.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.neondash.game.R;
import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.utilities.SoundManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tvLogoTitle);
        TextView tvSubtitle = findViewById(R.id.tvLogoSubtitle);

        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1200);
        tvTitle.startAnimation(fadeIn);
        tvSubtitle.startAnimation(fadeIn);

        // Pre-warm local DB & preferences asynchronously
        new Thread(() -> {
            PreferencesManager.getInstance(this);
            DatabaseHelper.getInstance(this).getReadableDatabase();
            SoundManager.getInstance(this);
        }).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 2200);
    }
}
