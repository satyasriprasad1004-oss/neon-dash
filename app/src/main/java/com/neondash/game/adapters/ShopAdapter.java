package com.neondash.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;

import java.util.List;
import java.util.Locale;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    public static class UpgradeItem {
        public String key;
        public String name;
        public String description;
        public int currentLevel; // 1 to 5
        public int baseCost;
        public int color;

        public UpgradeItem(String key, String name, String description, int currentLevel, int baseCost, int color) {
            this.key = key;
            this.name = name;
            this.description = description;
            this.currentLevel = currentLevel;
            this.baseCost = baseCost;
            this.color = color;
        }

        public int getUpgradeCost() {
            return baseCost * currentLevel;
        }

        public boolean isMaxLevel() {
            return currentLevel >= 5;
        }
    }

    public interface OnUpgradeListener {
        void onUpgrade(UpgradeItem item);
    }

    private final Context context;
    private final List<UpgradeItem> items;
    private final OnUpgradeListener listener;

    public ShopAdapter(Context context, List<UpgradeItem> items, OnUpgradeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpgradeItem item = items.get(position);

        holder.tvName.setText(item.name);
        holder.tvName.setTextColor(item.color);
        holder.tvDesc.setText(item.description);
        holder.tvLevel.setText(String.format(Locale.getDefault(), "Level %d / 5", item.currentLevel));

        if (item.isMaxLevel()) {
            holder.btnUpgrade.setText("MAX LEVEL");
            holder.btnUpgrade.setEnabled(false);
            holder.btnUpgrade.setBackgroundResource(R.drawable.bg_badge_active);
            holder.btnUpgrade.setTextColor(Color.parseColor("#39FF14"));
        } else {
            holder.btnUpgrade.setText(String.format(Locale.getDefault(), "UPGRADE (%d C)", item.getUpgradeCost()));
            holder.btnUpgrade.setEnabled(true);
            holder.btnUpgrade.setBackgroundResource(R.drawable.bg_btn_primary);
            holder.btnUpgrade.setTextColor(Color.BLACK);
            holder.btnUpgrade.setOnClickListener(v -> {
                if (listener != null) listener.onUpgrade(item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvLevel;
        Button btnUpgrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvShopItemName);
            tvDesc = itemView.findViewById(R.id.tvShopItemDesc);
            tvLevel = itemView.findViewById(R.id.tvShopItemLevel);
            btnUpgrade = itemView.findViewById(R.id.btnShopUpgrade);
        }
    }
}
