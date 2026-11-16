package com.neondash.game.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.adapters.MissionAdapter;
import com.neondash.game.database.DatabaseHelper;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.models.Mission;
import com.neondash.game.utilities.SoundManager;

import java.util.List;

public class MissionsActivity extends AppCompatActivity implements MissionAdapter.OnClaimListener {

    private RecyclerView recyclerView;
    private TextView tvCoinBalance;
    private DatabaseHelper dbHelper;
    private PreferencesManager prefs;
    private SoundManager soundManager;
    private MissionAdapter adapter;
    private List<Mission> missionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        dbHelper = DatabaseHelper.getInstance(this);
        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        recyclerView = findViewById(R.id.recyclerMissions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        loadMissions();
    }

    private void loadMissions() {
        tvCoinBalance.setText(String.format("%,d", prefs.getTotalCoins()));
        missionList = dbHelper.getMissions();
        adapter = new MissionAdapter(this, missionList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClaim(Mission mission) {
        soundManager.playPowerUp();
        prefs.addCoins(mission.getRewardCoins());
        dbHelper.claimMission(mission.getId());
        Toast.makeText(this, String.format("+%d Coins Claimed!", mission.getRewardCoins()), Toast.LENGTH_SHORT).show();
        loadMissions();
    }
}
