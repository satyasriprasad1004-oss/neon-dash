package com.neondash.game.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.adapters.EnvironmentAdapter;
import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.GameEnvironment;
import com.neondash.game.utilities.SoundManager;

import java.util.List;

public class EnvironmentSelectActivity extends AppCompatActivity implements EnvironmentAdapter.OnEnvironmentActionListener {

    private RecyclerView recyclerView;
    private TextView tvCoinBalance;
    private DatabaseHelper dbHelper;
    private PreferencesManager prefs;
    private SoundManager soundManager;
    private EnvironmentAdapter adapter;
    private List<GameEnvironment> environmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_select);

        dbHelper = DatabaseHelper.getInstance(this);
        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        recyclerView = findViewById(R.id.recyclerEnvironments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        loadEnvironments();
    }

    private void loadEnvironments() {
        tvCoinBalance.setText(String.format("%,d", prefs.getTotalCoins()));
        environmentList = dbHelper.getEnvironments();
        String selectedId = prefs.getSelectedEnvironment();
        adapter = new EnvironmentAdapter(this, environmentList, selectedId, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSelect(GameEnvironment environment) {
        soundManager.playClick();
        prefs.setSelectedEnvironment(environment.getId());
        Toast.makeText(this, environment.getName() + " Selected!", Toast.LENGTH_SHORT).show();
        loadEnvironments();
    }

    @Override
    public void onUnlock(GameEnvironment environment) {
        if (prefs.spendCoins(environment.getUnlockCoins())) {
            soundManager.playPowerUp();
            environment.setUnlocked(true);
            dbHelper.unlockEnvironment(environment.getId());
            prefs.setSelectedEnvironment(environment.getId());
            Toast.makeText(this, environment.getName() + " Unlocked & Selected!", Toast.LENGTH_SHORT).show();
            loadEnvironments();
        } else {
            soundManager.playCollision();
            Toast.makeText(this, "Insufficient coins! Need " + environment.getUnlockCoins() + " C", Toast.LENGTH_SHORT).show();
        }
    }
}
