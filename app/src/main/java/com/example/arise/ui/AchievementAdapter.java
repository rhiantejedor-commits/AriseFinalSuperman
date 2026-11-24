package com.example.arise.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arise.databinding.ItemAchievementBinding;
import com.example.arise.model.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private final List<Achievement> achievements = new ArrayList<>();

    public void setAchievements(List<Achievement> newAchievements) {
        achievements.clear();
        if (newAchievements != null) {
            achievements.addAll(newAchievements);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAchievementBinding binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AchievementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        holder.bind(achievements.get(position));
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private final ItemAchievementBinding binding;

        public AchievementViewHolder(ItemAchievementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Achievement achievement) {
            binding.tvAchievementTitle.setText(achievement.getTitle());
            binding.tvAchievementDesc.setText(achievement.getDescription());
            binding.ivAchievementIcon.setImageResource(achievement.getIconResId());

            if (achievement.isUnlocked()) {
                binding.tvAchievementStatus.setText("Unlocked");
                binding.getRoot().setAlpha(1.0f);
            } else {
                binding.tvAchievementStatus.setText("Locked");
                binding.getRoot().setAlpha(0.4f);
            }
        }
    }
}
