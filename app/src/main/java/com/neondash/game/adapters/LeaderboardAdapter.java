package com.neondash.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neondash.game.R;
import com.neondash.game.models.LeaderboardEntry;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final Context context;
    private final List<LeaderboardEntry> list;

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardEntry entry = list.get(position);
        int rank = position + 1;

        holder.tvRank.setText(String.valueOf(rank));
        if (rank == 1) {
            holder.tvRank.setTextColor(Color.parseColor("#FFE600")); // Gold
        } else if (rank == 2) {
            holder.tvRank.setTextColor(Color.parseColor("#E0E0E0")); // Silver
        } else if (rank == 3) {
            holder.tvRank.setTextColor(Color.parseColor("#CD7F32")); // Bronze
        } else {
            holder.tvRank.setTextColor(Color.parseColor("#00F0FF"));
        }

        holder.tvPlayer.setText(entry.getPlayerName());
        holder.tvScore.setText(String.format("%,d PTS", entry.getScore()));
        holder.tvSub.setText(String.format("%s  •  %dm  •  %d coins  •  %s",
                entry.getCharacterName(), entry.getDistance(), entry.getCoins(), entry.getDateString()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvPlayer, tvScore, tvSub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvLeadRank);
            tvPlayer = itemView.findViewById(R.id.tvLeadPlayer);
            tvScore = itemView.findViewById(R.id.tvLeadScore);
            tvSub = itemView.findViewById(R.id.tvLeadSub);
        }
    }
}
