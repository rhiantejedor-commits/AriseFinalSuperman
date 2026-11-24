package com.example.arise.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arise.R;
import com.example.arise.data.local.HabitEntity;
import com.example.arise.databinding.FragmentHabitsBinding;
import com.example.arise.utils.SoundHapticHelper;
import com.example.arise.viewmodel.HabitViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HabitsFragment extends Fragment implements HabitAdapter.OnHabitActionListener {

    private FragmentHabitsBinding binding;
    private HabitViewModel habitViewModel;
    private HabitAdapter habitAdapter;
    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        setupRecyclerView();
        setupCategoryChips();
        loadHabits();
    }

    private void setupRecyclerView() {
        habitAdapter = new HabitAdapter(this);
        binding.rvAllHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAllHabits.setAdapter(habitAdapter);
    }

    private void setupCategoryChips() {
        binding.cgCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    selectedCategory = chip.getText().toString();
                    loadHabits();
                }
            }
        });
    }

    private void loadHabits() {
        habitViewModel.getHabitsByCategory(selectedCategory).observe(getViewLifecycleOwner(), habits -> {
            habitAdapter.setHabits(habits);
            if (habits == null || habits.isEmpty()) {
                binding.llEmptyHabits.setVisibility(View.VISIBLE);
                binding.rvAllHabits.setVisibility(View.GONE);
            } else {
                binding.llEmptyHabits.setVisibility(View.GONE);
                binding.rvAllHabits.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onToggleComplete(HabitEntity habit) {
        habitViewModel.toggleCompleteHabit(habit, result -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    SoundHapticHelper.playCompletionFeedback(requireContext());
                    if (result.xpGained > 0) {
                        Toast.makeText(requireContext(), "+" + result.xpGained + " XP!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onEdit(HabitEntity habit) {
        AddEditHabitBottomSheet.newInstance(habit).show(getParentFragmentManager(), "AddEditHabitBottomSheet");
    }

    @Override
    public void onDelete(HabitEntity habit) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete '" + habit.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> habitViewModel.delete(habit))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
