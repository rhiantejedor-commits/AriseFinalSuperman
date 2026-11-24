package com.example.arise.ui;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arise.R;
import com.example.arise.data.local.HabitEntity;
import com.example.arise.databinding.ItemHabitBinding;
import com.example.arise.utils.GamificationEngine;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    public interface OnHabitActionListener {
        void onToggleComplete(HabitEntity habit);
        void onEdit(HabitEntity habit);
        void onDelete(HabitEntity habit);
    }

    private final List<HabitEntity> habits = new ArrayList<>();
    private final OnHabitActionListener listener;

    public HabitAdapter(OnHabitActionListener listener) {
        this.listener = listener;
    }

    public void setHabits(List<HabitEntity> newHabits) {
        habits.clear();
        if (newHabits != null) {
            habits.addAll(newHabits);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHabitBinding binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HabitViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        holder.bind(habits.get(position));
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    class HabitViewHolder extends RecyclerView.ViewHolder {
        private final ItemHabitBinding binding;

        public HabitViewHolder(ItemHabitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HabitEntity habit) {
            binding.tvHabitTitle.setText(habit.getTitle());
            binding.tvHabitTime.setText(habit.getTime());
            binding.tvHabitCategory.setText(habit.getCategory());
            binding.tvXpBadge.setText("+" + habit.getXpReward() + " XP");

            boolean isFixed = GamificationEngine.CAT_DAILY_FIXED.equalsIgnoreCase(habit.getCategory());
            if (isFixed && habit.getStreakCount() > 0) {
                binding.containerStreak.setVisibility(View.VISIBLE);
                binding.tvHabitStreak.setText(habit.getStreakCount() + "d");
            } else {
                binding.containerStreak.setVisibility(View.GONE);
            }

            if (habit.isCompleted()) {
                binding.btnCompleteCheckbox.setImageResource(R.drawable.ic_check_circle_filled);
                binding.tvHabitTitle.setPaintFlags(binding.tvHabitTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.btnCompleteCheckbox.setImageResource(R.drawable.ic_check);
                binding.tvHabitTitle.setPaintFlags(binding.tvHabitTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.getRoot().setAlpha(1.0f);
            }

            binding.btnCompleteCheckbox.setOnClickListener(v -> {
                if (listener != null) listener.onToggleComplete(habit);
            });

            binding.btnEditHabit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(habit);
            });

            binding.btnDeleteHabit.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(habit);
            });
        }
    }
}
