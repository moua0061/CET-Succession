package com.rubber_duckies.succession;

public class AiScenarioRequest {
    public int week;
    public int phase;
    public int power;
    public int loyalty;
    public int heat;
    public boolean onBlacklistedState;

    public AiScenarioRequest(
            int week,
            int phase,
            int power,
            int loyalty,
            int heat,
            boolean onBlacklistedState
    ) {
        this.week = week;
        this.phase = phase;
        this.power = power;
        this.loyalty = loyalty;
        this.heat = heat;
        this.onBlacklistedState = onBlacklistedState;
    }
}
