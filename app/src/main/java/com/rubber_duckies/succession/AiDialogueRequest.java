package com.rubber_duckies.succession;

public class AiDialogueRequest {
    public int week;
    public int phase;
    public int power;
    public int loyalty;
    public int heat;
    public boolean onBlacklistedState;
    public String scenarioTitle;
    public String scenarioBrief;

    public AiDialogueRequest(
            int week,
            int phase,
            int power,
            int loyalty,
            int heat,
            boolean onBlacklistedState,
            String scenarioTitle,
            String scenarioBrief
    ) {
        this.week = week;
        this.phase = phase;
        this.power = power;
        this.loyalty = loyalty;
        this.heat = heat;
        this.onBlacklistedState = onBlacklistedState;
        this.scenarioTitle = scenarioTitle;
        this.scenarioBrief = scenarioBrief;
    }
}
