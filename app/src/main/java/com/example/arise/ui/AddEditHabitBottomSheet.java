package com.example.arise.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.arise.R;
import com.example.arise.data.local.HabitEntity;

import com.example.arise.databinding.DialogAddEditHabitBinding;
import com.example.arise.model.HabitCategory;
import com.example.arise.utils.DateUtils;
import com.example.arise.viewmodel.HabitViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditHabitBottomSheet extends BottomSheetDialogFragment {

    private DialogAddEditHabitBinding binding;
    private HabitViewModel habitViewModel;
    private HabitEntity habitToEdit;
    private String selectedTime = "07:00 AM";

    public static AddEditHabitBottomSheet newInstance(@Nullable HabitEntity habit) {
        AddEditHabitBottomSheet fragment = new AddEditHabitBottomSheet();
        fragment.habitToEdit = habit;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        // Setup Category Spinner
        List<String> categories = new ArrayList<>();
        for (HabitCategory cat : HabitCategory.values()) {
            categories.add(cat.getDisplayName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(adapter);

        // Time Picker setup
        binding.btnPickTime.setText(selectedTime);
        binding.btnPickTime.setOnClickListener(v -> showTimePicker());

        if (habitToEdit != null) {
            binding.tvDialogTitle.setText(R.string.edit_habit);
            binding.etHabitTitle.setText(habitToEdit.getTitle());
            if (habitToEdit.getTime() != null) {
                selectedTime = habitToEdit.getTime();
                binding.btnPickTime.setText(selectedTime);
            }
            int catPos = categories.indexOf(habitToEdit.getCategory());
            if (catPos >= 0) {
                binding.spinnerCategory.setSelection(catPos);
            }
            binding.switchRecurring.setChecked(habitToEdit.isRecurring());
        }

        binding.btnSaveHabit.setOnClickListener(v -> saveHabit());
        binding.btnCancelHabit.setOnClickListener(v -> dismiss());
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> {
            selectedTime = DateUtils.formatTime(hourOfDay, minuteOfHour);
            binding.btnPickTime.setText(selectedTime);
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void saveHabit() {
        String title = binding.etHabitTitle.getText() != null ? binding.etHabitTitle.getText().toString().trim() : "";
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a habit title", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = (String) binding.spinnerCategory.getSelectedItem();
        HabitCategory catEnum = HabitCategory.fromDisplayName(category);
        int xpReward = catEnum.getDefaultXp();

        List<String> daysList = new ArrayList<>();
        if (binding.chipMon.isChecked()) daysList.add("Mon");
        if (binding.chipTue.isChecked()) daysList.add("Tue");
        if (binding.chipWed.isChecked()) daysList.add("Wed");
        if (binding.chipThu.isChecked()) daysList.add("Thu");
        if (binding.chipFri.isChecked()) daysList.add("Fri");
        if (binding.chipSat.isChecked()) daysList.add("Sat");
        if (binding.chipSun.isChecked()) daysList.add("Sun");
        String repeatDays = String.join(",", daysList);

        boolean isRecurring = binding.switchRecurring.isChecked();

        if (habitToEdit != null) {
            habitToEdit.setTitle(title);
            habitToEdit.setCategory(category);
            habitToEdit.setTime(selectedTime);
            habitToEdit.setRepeatDays(repeatDays);
            habitToEdit.setRecurring(isRecurring);
            habitToEdit.setXpReward(xpReward);
            habitViewModel.update(habitToEdit);
            Toast.makeText(requireContext(), "Habit updated!", Toast.LENGTH_SHORT).show();
        } else {
            HabitEntity newHabit = new HabitEntity(title, category, selectedTime, repeatDays, isRecurring, xpReward);
            habitViewModel.insert(newHabit);
            Toast.makeText(requireContext(), "Habit added!", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
