package com.neondash.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.models.GameEnvironment;

import java.util.List;
import java.util.Locale;

public class EnvironmentAdapter extends RecyclerView.Adapter<EnvironmentAdapter.ViewHolder> {

    public interface OnEnvironmentActionListener {
        void onSelect(GameEnvironment environment);
        void onUnlock(GameEnvironment environment);
    }

    private final Context context;
    private final List<GameEnvironment> environmentList;
    private final String selectedId;
    private final OnEnvironmentActionListener listener;

    public EnvironmentAdapter(Context context, List<GameEnvironment> environmentList, String selectedId, OnEnvironmentActionListener listener) {
        this.context = context;
        this.environmentList = environmentList;
        this.selectedId = selectedId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_environment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameEnvironment env = environmentList.get(position);

        holder.tvName.setText(env.getName());
        holder.tvDesc.setText(env.getDescription());

        // Preview Gradient Banner
        GradientDrawable banner = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{env.getSkyColorTop(), env.getGroundColor()}
        );
        banner.setCornerRadius(12.0f);
        holder.viewBanner.setBackground(banner);

        boolean isCurrent = env.getId().equals(selectedId);

        if (isCurrent) {
            holder.btnAction.setText("SELECTED");
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundResource(R.drawable.bg_badge_active);
            holder.btnAction.setTextColor(Color.parseColor("#00F0FF"));
        } else if (env.isUnlocked()) {
            holder.btnAction.setText("SELECT");
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_primary);
            holder.btnAction.setTextColor(Color.BLACK);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onSelect(env);
            });
        } else {
            holder.btnAction.setText(String.format(Locale.getDefault(), "UNLOCK (%d C)", env.getUnlockCoins()));
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_magenta);
            holder.btnAction.setTextColor(Color.WHITE);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onUnlock(env);
            });
        }
    }

    @Override
    public int getItemCount() {
        return environmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        View viewBanner;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEnvName);
            tvDesc = itemView.findViewById(R.id.tvEnvDesc);
            viewBanner = itemView.findViewById(R.id.viewEnvBanner);
            btnAction = itemView.findViewById(R.id.btnEnvAction);
        }
    }
}
