package com.neondash.game.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.adapters.LeaderboardAdapter;
import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.models.LeaderboardEntry;
import com.neondash.game.utilities.SoundManager;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private SoundManager soundManager;
    private Button btnAll, btnClassic, btnTime, btnCoins;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        dbHelper = DatabaseHelper.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        recyclerView = findViewById(R.id.recyclerLeaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        initFilters();
        loadLeaderboard();
    }

    private void initFilters() {
        btnAll = findViewById(R.id.btnFilterAll);
        btnClassic = findViewById(R.id.btnFilterClassic);
        btnTime = findViewById(R.id.btnFilterTime);
        btnCoins = findViewById(R.id.btnFilterCoins);

        btnAll.setOnClickListener(v -> {
            soundManager.playClick();
            currentFilter = "All";
            updateFilterButtons();
            loadLeaderboard();
        });

        btnClassic.setOnClickListener(v -> {
            soundManager.playClick();
            currentFilter = "Classic";
            updateFilterButtons();
            loadLeaderboard();
        });

        btnTime.setOnClickListener(v -> {
            soundManager.playClick();
            currentFilter = "Time Challenge";
            updateFilterButtons();
            loadLeaderboard();
        });

        btnCoins.setOnClickListener(v -> {
            soundManager.playClick();
            currentFilter = "Coin Rush";
            updateFilterButtons();
            loadLeaderboard();
        });
    }

    private void updateFilterButtons() {
        highlightButton(btnAll, currentFilter.equals("All"));
        highlightButton(btnClassic, currentFilter.equals("Classic"));
        highlightButton(btnTime, currentFilter.equals("Time Challenge"));
        highlightButton(btnCoins, currentFilter.equals("Coin Rush"));
    }

    private void highlightButton(Button btn, boolean isSelected) {
        btn.setBackgroundResource(isSelected ? R.drawable.bg_btn_primary : R.drawable.bg_btn_outline);
        btn.setTextColor(isSelected ? Color.BLACK : Color.WHITE);
    }

    private void loadLeaderboard() {
        List<LeaderboardEntry> list = dbHelper.getLeaderboard(currentFilter);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}
