package com.rubber_duckies.succession.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.rubber_duckies.succession.databinding.FragmentSummaryBinding;
import com.rubber_duckies.succession.GameState;
import android.content.Intent;

public class SummaryFragment extends Fragment {

    private FragmentSummaryBinding binding;
    private SummaryViewModel viewModel;

    // Arguments keys
    private static final String ARG_OUTCOME = "outcome";
    private static final String ARG_HINT = "hint";
    private static final String ARG_WEEK = "week";
    private static final String ARG_CURRENT_POWER = "current_power";
    private static final String ARG_CURRENT_LOYALTY = "current_loyalty";
    private static final String ARG_CURRENT_HEAT = "current_heat";
    private static final String ARG_PREVIOUS_POWER = "previous_power";
    private static final String ARG_PREVIOUS_LOYALTY = "previous_loyalty";
    private static final String ARG_PREVIOUS_HEAT = "previous_heat";
    private static final String ARG_IS_FINAL_WEEK = "is_final_week";

    /**
     * Factory method to create SummaryFragment with data
     */
    public static SummaryFragment newInstance(
            String outcome,
            String hint,
            int week,
            int currentPower,
            int currentLoyalty,
            int currentHeat,
            int previousPower,
            int previousLoyalty,
            int previousHeat,
            boolean isFinalWeek
    ) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OUTCOME, outcome);
        args.putString(ARG_HINT, hint);
        args.putInt(ARG_WEEK, week);
        args.putInt(ARG_CURRENT_POWER, currentPower);
        args.putInt(ARG_CURRENT_LOYALTY, currentLoyalty);
        args.putInt(ARG_CURRENT_HEAT, currentHeat);
        args.putInt(ARG_PREVIOUS_POWER, previousPower);
        args.putInt(ARG_PREVIOUS_LOYALTY, previousLoyalty);
        args.putInt(ARG_PREVIOUS_HEAT, previousHeat);
        args.putBoolean(ARG_IS_FINAL_WEEK, isFinalWeek);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SummaryViewModel.class);

        loadDataFromArguments();
        setupObservers();
        setupClickListeners();
    }

    private void loadDataFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            String outcome = args.getString(ARG_OUTCOME, "");
            String hint = args.getString(ARG_HINT, "");
            int week = args.getInt(ARG_WEEK, 1);
            boolean isFinalWeek = args.getBoolean(ARG_IS_FINAL_WEEK, false);

            //create GameState from current values
            GameState state = new GameState();
            state.power = args.getInt(ARG_CURRENT_POWER, 0);
            state.loyalty = args.getInt(ARG_CURRENT_LOYALTY, 50);
            state.heat = args.getInt(ARG_CURRENT_HEAT, 0);

            int previousPower = args.getInt(ARG_PREVIOUS_POWER, 0);
            int previousLoyalty = args.getInt(ARG_PREVIOUS_LOYALTY, 50);
            int previousHeat = args.getInt(ARG_PREVIOUS_HEAT, 0);

            viewModel.setSummaryData(state, previousPower, previousLoyalty, previousHeat, outcome, hint, week);

            //hide Continue button if this is the final week
            if (isFinalWeek) {
                binding.btnContinue.setVisibility(View.GONE);
            }
        }
    }

    private void setupObservers() {
        //observe game state
        viewModel.getGameState().observe(getViewLifecycleOwner(), gameState -> {
            if (gameState != null) {
                binding.tvCurrentPower.setText(String.valueOf(gameState.power));
                binding.tvCurrentLoyalty.setText(String.valueOf(gameState.loyalty));
                binding.tvCurrentHeat.setText(String.valueOf(gameState.heat));
            }
        });

        //observe stat trends
        viewModel.getStatTrends().observe(getViewLifecycleOwner(), trends -> {
            if (trends != null) {
                //power trend
                String powerText = trends.powerArrow + " " + formatDiff(trends.powerDiff);
                binding.tvPowerTrend.setText(powerText);
                binding.tvPowerTrend.setTextColor(getTrendColor(trends.powerDiff));

                //loyalty trend
                String loyaltyText = trends.loyaltyArrow + " " + formatDiff(trends.loyaltyDiff);
                binding.tvLoyaltyTrend.setText(loyaltyText);
                binding.tvLoyaltyTrend.setTextColor(getTrendColor(trends.loyaltyDiff));

                //heat trend
                String heatText = trends.heatArrow + " " + formatDiff(trends.heatDiff);
                binding.tvHeatTrend.setText(heatText);
                binding.tvHeatTrend.setTextColor(getTrendColor(trends.heatDiff));
            }
        });

        //observe outcome text
        viewModel.getOutcomeText().observe(getViewLifecycleOwner(), outcome -> {
            if (outcome != null) {
                binding.tvOutcomeText.setText(outcome);
            }
        });

        //observe hint text
        viewModel.getHintText().observe(getViewLifecycleOwner(), hint -> {
            if (hint != null) {
                binding.tvHintText.setText("Hint for next week: " + hint);
            }
        });

        //observe current week
        viewModel.getCurrentWeek().observe(getViewLifecycleOwner(), week -> {
            if (week != null) {
                binding.tvSummaryHeader.setText("Week " + week + " Summary");
                binding.btnContinue.setText("CONTINUE TO WEEK " + (week + 1));
            }
        });
    }

    private void setupClickListeners() {
        //continue button - go back to CoreLoopActivity for next week
        binding.btnContinue.setOnClickListener(v -> {
            //pop the fragment and return to CoreLoopActivity
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //exit button - return to main menu
        binding.btnExit.setOnClickListener(v -> {
            if (getActivity() != null) {
                //go back to MainActivity/MainMenuFragment
                Intent intent = new Intent(requireContext(), com.rubber_duckies.succession.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    /**
     * Formats difference value with + sign for positive numbers
     */
    private String formatDiff(int diff) {
        if (diff > 0) {
            return "+" + diff;
        }
        return String.valueOf(diff);
    }

    /**
     * Returns color based on trend direction
     */
    private int getTrendColor(int diff) {
        if (diff > 0) {
            return Color.parseColor("#22C55E"); // Green
        } else if (diff < 0) {
            return Color.parseColor("#EF4444"); // Red
        } else {
            return Color.parseColor("#64748B"); // Gray
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}