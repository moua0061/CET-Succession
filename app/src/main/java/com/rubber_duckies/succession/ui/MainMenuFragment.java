package com.rubber_duckies.succession.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.rubber_duckies.succession.databinding.FragmentMainMenuBinding;

public class MainMenuFragment extends Fragment {

    private FragmentMainMenuBinding binding;
    private MainMenuViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainMenuViewModel.class);

        setupObservers();
        setupClickListeners();

        //check for saved game on launch
        viewModel.checkForSavedGame();
    }

    private void setupObservers() {
        //if a saved game exists, show/hide Continue button
        viewModel.getSaveGameExists().observe(getViewLifecycleOwner(), exists -> {
            //TODO: continue button is comment out for now until the functionality here works
//            if (exists != null) {
//                binding.btnContinue.setVisibility(exists ? View.VISIBLE : View.GONE);
//            }
        });
    }

    private void setupClickListeners() {
        //Start the game:
        binding.btnStart.setOnClickListener(v -> {
            viewModel.onStartNewGame();
            //go to BriefFragment for week 1
            // TODO: Add navigation action
        });

        //Continue where player left off:
        //TODO: Add navigation action for Continue

        //navigate to the HELP screen:
        binding.btnHelp.setOnClickListener(v -> {
            //go to HelpFragment
            // TODO: Add navigation action
        });

        //Exits out of the app
        binding.btnExit.setOnClickListener(v -> {
//            viewModel.onExitApp();
//            requireActivity().finish();
            new AlertDialog.Builder(requireContext())
                .setTitle("Exit The Succession")
                .setMessage("Are you sure you want to exit the game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.onExitApp();
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}