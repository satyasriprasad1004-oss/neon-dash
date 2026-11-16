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
import com.neondash.game.models.CharacterModel;

import java.util.List;
import java.util.Locale;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

    public interface OnCharacterActionListener {
        void onSelect(CharacterModel character);
        void onUnlock(CharacterModel character);
    }

    private final Context context;
    private final List<CharacterModel> characterList;
    private final OnCharacterActionListener listener;

    public CharacterAdapter(Context context, List<CharacterModel> characterList, OnCharacterActionListener listener) {
        this.context = context;
        this.characterList = characterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_character_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterModel c = characterList.get(position);

        holder.tvName.setText(c.getName());
        holder.tvTitle.setText(c.getTitle());
        holder.tvDesc.setText(c.getDescription());
        holder.tvStats.setText(String.format(Locale.getDefault(), "Speed: %.1fx  |  Jump: %.1fx  |  Magnet: %.1fx",
                c.getSpeedMultiplier(), c.getJumpMultiplier(), c.getMagnetMultiplier()));

        // Color Swatch
        GradientDrawable swatch = new GradientDrawable();
        swatch.setShape(GradientDrawable.OVAL);
        swatch.setColor(c.getPrimaryColor());
        swatch.setStroke(3, Color.WHITE);
        holder.viewColor.setBackground(swatch);

        if (c.isEquipped()) {
            holder.btnAction.setText("EQUIPPED");
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundResource(R.drawable.bg_badge_active);
            holder.btnAction.setTextColor(Color.parseColor("#00F0FF"));
        } else if (c.isUnlocked()) {
            holder.btnAction.setText("SELECT");
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_primary);
            holder.btnAction.setTextColor(Color.BLACK);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onSelect(c);
            });
        } else {
            holder.btnAction.setText(String.format(Locale.getDefault(), "UNLOCK (%d C)", c.getUnlockCost()));
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundResource(R.drawable.bg_btn_magenta);
            holder.btnAction.setTextColor(Color.WHITE);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onUnlock(c);
            });
        }
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTitle, tvDesc, tvStats;
        View viewColor;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCharName);
            tvTitle = itemView.findViewById(R.id.tvCharTitle);
            tvDesc = itemView.findViewById(R.id.tvCharDesc);
            tvStats = itemView.findViewById(R.id.tvCharStats);
            viewColor = itemView.findViewById(R.id.viewCharColor);
            btnAction = itemView.findViewById(R.id.btnCharAction);
        }
    }
}
