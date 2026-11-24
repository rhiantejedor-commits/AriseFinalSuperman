package com.example.arise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.databinding.FragmentProfileBinding;
import com.example.arise.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        observeUserStats();

        binding.btnSyncNow.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Syncing data to Cloud...", Toast.LENGTH_SHORT).show();
            userViewModel.syncNow(new FirebaseManager.SyncCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Cloud Sync Successful!", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Sync Failed: " + errorMessage, Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        });

        binding.btnEditName.setOnClickListener(v -> showEditNameDialog());

        binding.btnLogout.setOnClickListener(v -> {
            userViewModel.logout();
            startActivity(new Intent(requireContext(), AuthActivity.class));
            requireActivity().finish();
        });
    }

    private void showEditNameDialog() {
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setSingleLine(true);
        input.setHint("Display Name");

        String currentName = binding.tvProfileName.getText().toString();
        if (!currentName.isEmpty()) {
            input.setText(currentName);
        }

        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        int margin = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(margin, margin, margin, 0);
        container.addView(input);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Display Name")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        String email = binding.tvProfileEmail.getText().toString();
                        userViewModel.updateUserProfile(newName, email);
                        Toast.makeText(requireContext(), "Name updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeUserStats() {
        userViewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null && binding != null) {
                String name = stats.getDisplayName();
                binding.tvProfileName.setText((name != null && !name.trim().isEmpty()) ? name.trim() : "User");
                binding.tvProfileEmail.setText(stats.getEmail() != null ? stats.getEmail() : "");
                binding.tvProfileLevel.setText("Level " + stats.getCurrentLevel() + " Explorer");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
