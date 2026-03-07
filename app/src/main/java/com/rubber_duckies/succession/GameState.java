package com.rubber_duckies.succession;

public class GameState {

	public int power = 50;
	public int heat = 50;
	public int loyalty = 50;

	// ADDED: campaign phase vs presidency phase
	public int phase = 1;

	// CHANGED: true when heat is between 51-100
	public boolean onBlacklistedState = false;

	// ADDED: triggered when the presidency is secured
	public boolean presidencySecured = false;

	// ADDED: triggered if loyalty reaches re-election threshold
	public boolean reelectionWon = false;

	// ADDED: keeps stats in valid range
	public void clampStats() {
		power = Math.max(0, Math.min(100, power));
		heat = Math.max(0, Math.min(100, heat));
		loyalty = Math.max(0, Math.min(100, loyalty));
	}

	// ADDED: threshold rules
	public void updateThresholds() {

		if (phase == 1) {

			// CHANGED: Blacklisted State condition
			onBlacklistedState = (heat >= 51 && heat <= 100);

			// ADDED: Presidency secured condition
			if (power == 100 && loyalty == 100 && heat <= 50) {
				presidencySecured = true;
				phase = 2;
			}
		}

		// Phase 2 rule
		if (phase == 2 && loyalty >= 200) {
			reelectionWon = true;
		}
	}
}
