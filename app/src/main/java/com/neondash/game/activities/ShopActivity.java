package com.neondash.game.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.adapters.ShopAdapter;
import com.neondash.game.database.PreferencesManager;
import com.neondash.game.utilities.Constants;
import com.neondash.game.utilities.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements ShopAdapter.OnUpgradeListener {

    private RecyclerView recyclerView;
    private TextView tvCoinBalance;
    private PreferencesManager prefs;
    private SoundManager soundManager;
    private ShopAdapter adapter;
    private List<ShopAdapter.UpgradeItem> upgradeItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        prefs = PreferencesManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        recyclerView = findViewById(R.id.recyclerShop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClick();
            finish();
        });

        loadShopItems();
    }

    private void loadShopItems() {
        tvCoinBalance.setText(String.format("%,d", prefs.getTotalCoins()));

        upgradeItems = new ArrayList<>();
        upgradeItems.add(new ShopAdapter.UpgradeItem(
                Constants.KEY_UPGRADE_MAGNET,
                "Magnetic Pull Coils",
                "Expands coin magnetic attraction field radius and extends duration by +25% per level.",
                prefs.getUpgradeLevel(Constants.KEY_UPGRADE_MAGNET),
                150,
                Color.parseColor("#00F0FF")
        ));
        upgradeItems.add(new ShopAdapter.UpgradeItem(
                Constants.KEY_UPGRADE_SHIELD,
                "Energy Forcefield Matrix",
                "Hardens cyber shield and increases durability absorption protection.",
                prefs.getUpgradeLevel(Constants.KEY_UPGRADE_SHIELD),
                200,
                Color.parseColor("#39FF14")
        ));
        upgradeItems.add(new ShopAdapter.UpgradeItem(
                Constants.KEY_UPGRADE_SPEED,
                "Warp Speed Thrusters",
                "Increases hyper-velocity speed boost duration and score multiplier by +20% per level.",
                prefs.getUpgradeLevel(Constants.KEY_UPGRADE_SPEED),
                175,
                Color.parseColor("#FFE600")
        ));
        upgradeItems.add(new ShopAdapter.UpgradeItem(
                Constants.KEY_UPGRADE_SLOWMO,
                "Chrono-Dilation Matrix",
                "Extends slow-motion time distortion duration by +25% per level.",
                prefs.getUpgradeLevel(Constants.KEY_UPGRADE_SLOWMO),
                180,
                Color.parseColor("#0080FF")
        ));
        upgradeItems.add(new ShopAdapter.UpgradeItem(
                Constants.KEY_UPGRADE_DOUBLEJUMP,
                "Agile Thruster Boots (2X Jump)",
                "Extends mid-air double jump duration and permanently unlocks permanent double jumping at Level 5!",
                prefs.getUpgradeLevel(Constants.KEY_UPGRADE_DOUBLEJUMP),
                250,
                Color.parseColor("#FF007F")
        ));

        adapter = new ShopAdapter(this, upgradeItems, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onUpgrade(ShopAdapter.UpgradeItem item) {
        int cost = item.getUpgradeCost();
        if (prefs.spendCoins(cost)) {
            soundManager.playPowerUp();
            int newLevel = item.currentLevel + 1;
            prefs.setUpgradeLevel(item.key, newLevel);
            Toast.makeText(this, item.name + " upgraded to Level " + newLevel + "!", Toast.LENGTH_SHORT).show();
            loadShopItems();
        } else {
            soundManager.playCollision();
            Toast.makeText(this, "Insufficient coins! Need " + cost + " C", Toast.LENGTH_SHORT).show();
        }
    }
}
