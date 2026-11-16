package com.neondash.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.models.Mission;

import java.util.List;
import java.util.Locale;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

    public interface OnClaimListener {
        void onClaim(Mission mission);
    }

    private final Context context;
    private final List<Mission> missionList;
    private final OnClaimListener listener;

    public MissionAdapter(Context context, List<Mission> missionList, OnClaimListener listener) {
        this.context = context;
        this.missionList = missionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mission_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mission m = missionList.get(position);

        holder.tvTitle.setText(m.getTitle());
        holder.tvDesc.setText(m.getDescription());
        holder.tvReward.setText(String.format(Locale.getDefault(), "+%d COINS", m.getRewardCoins()));

        int progress = Math.min(m.getCurrentProgress(), m.getTargetCount());
        holder.progressBar.setMax(m.getTargetCount());
        holder.progressBar.setProgress(progress);
        holder.tvProgress.setText(String.format(Locale.getDefault(), "%d / %d", progress, m.getTargetCount()));

        if (m.isClaimed()) {
            holder.btnClaim.setText("CLAIMED");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setBackgroundResource(R.drawable.bg_badge_active);
            holder.btnClaim.setTextColor(Color.parseColor("#9DA5CD"));
        } else if (m.isCompleted()) {
            holder.btnClaim.setText("CLAIM");
            holder.btnClaim.setEnabled(true);
            holder.btnClaim.setBackgroundResource(R.drawable.bg_btn_primary);
            holder.btnClaim.setTextColor(Color.BLACK);
            holder.btnClaim.setOnClickListener(v -> {
                if (listener != null) listener.onClaim(m);
            });
        } else {
            holder.btnClaim.setText("IN PROGRESS");
            holder.btnClaim.setEnabled(false);
            holder.btnClaim.setBackgroundResource(R.drawable.bg_badge_active);
            holder.btnClaim.setTextColor(Color.parseColor("#FFE600"));
        }
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvReward, tvProgress;
        ProgressBar progressBar;
        Button btnClaim;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMissionTitle);
            tvDesc = itemView.findViewById(R.id.tvMissionDesc);
            tvReward = itemView.findViewById(R.id.tvMissionReward);
            tvProgress = itemView.findViewById(R.id.tvMissionProgress);
            progressBar = itemView.findViewById(R.id.progressMission);
            btnClaim = itemView.findViewById(R.id.btnMissionClaim);
        }
    }
}
