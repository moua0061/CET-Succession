package com.rubber_duckies.succession.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.rubber_duckies.succession.GameState;

public class SummaryViewModel extends ViewModel {
    private final MutableLiveData<GameState> gameState = new MutableLiveData<>();
    private final MutableLiveData<StatTrends> statTrends = new MutableLiveData<>();
    private final MutableLiveData<String> outcomeText = new MutableLiveData<>();
    private final MutableLiveData<String> hintText = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentWeek = new MutableLiveData<>();

    public LiveData<GameState> getGameState() {
        return gameState;
    }

    public LiveData<StatTrends> getStatTrends() {
        return statTrends;
    }

    public LiveData<String> getOutcomeText() {
        return outcomeText;
    }

    public LiveData<String> getHintText() {
        return hintText;
    }

    public LiveData<Integer> getCurrentWeek() {
        return currentWeek;
    }

    /**
     * Sets the summary data to display
     */
    public void setSummaryData(GameState state, int previousPower, int previousLoyalty, int previousHeat, String outcome, String hint, int week) {
        gameState.setValue(state);
        outcomeText.setValue(outcome);
        hintText.setValue(hint);
        currentWeek.setValue(week);

        // Calculate trends
        StatTrends trends = calculateTrends(previousPower, state.power, previousLoyalty, state.loyalty, previousHeat, state.heat);
        statTrends.setValue(trends);
    }

    /**
     * Calculates stat trends for display
     */
    private StatTrends calculateTrends(int oldPower, int newPower, int oldLoyalty, int newLoyalty, int oldHeat, int newHeat) {
        StatTrends trends = new StatTrends();

        // Power trend
        trends.powerDiff = newPower - oldPower;
        trends.powerArrow = getTrendArrow(trends.powerDiff);

        // Loyalty trend
        trends.loyaltyDiff = newLoyalty - oldLoyalty;
        trends.loyaltyArrow = getTrendArrow(trends.loyaltyDiff);

        // Heat trend
        trends.heatDiff = newHeat - oldHeat;
        trends.heatArrow = getTrendArrow(trends.heatDiff);

        return trends;
    }

    /**
     * Returns arrow symbol based on difference
     */
    private String getTrendArrow(int diff) {
        if (diff > 0) return "↑";
        if (diff < 0) return "↓";
        return "→";
    }

    /**
     * Triggers navigation to next week's briefing
     */
    public void onContinue() {
        // This will be handled by the Fragment
    }

    /**
     * Inner class to hold stat trend data
     */
    public static class StatTrends {
        public int powerDiff;
        public String powerArrow;
        public int loyaltyDiff;
        public String loyaltyArrow;
        public int heatDiff;
        public String heatArrow;
    }
}
