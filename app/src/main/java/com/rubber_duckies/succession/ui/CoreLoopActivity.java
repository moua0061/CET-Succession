package com.rubber_duckies.succession.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;

import com.rubber_duckies.succession.*;
import com.rubber_duckies.succession.R;
import java.util.*;

public class CoreLoopActivity extends AppCompatActivity {

	private TextView tvPower, tvHeat, tvLoyalty, tvText, tvWeek;
	private Button btn1, btn2, btn3;
	private GameState state = new GameState();
	private List<Scenario> scenarios;
	private int currentIndex = 0;
	private boolean showingFragment = false;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_core_loop);

		tvPower = findViewById(R.id.tv_power);
		tvHeat = findViewById(R.id.tv_heat);
		tvLoyalty = findViewById(R.id.tv_loyalty);
		tvText = findViewById(R.id.tv_text);
		tvWeek = findViewById(R.id.tv_week);
		btn1 = findViewById(R.id.btn_choice1);
		btn2 = findViewById(R.id.btn_choice2);
		btn3 = findViewById(R.id.btn_choice3);

		scenarios = makeStubScenarios();

		Random rng = new Random();

		state.power = 40 + rng.nextInt(21);
		state.heat = 40 + rng.nextInt(21);
		state.loyalty = 40 + rng.nextInt(21);

		// listen for when fragments are popped from back stack
		getSupportFragmentManager().addOnBackStackChangedListener(() -> {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				showingFragment = false;

				// Increment week after summary
				currentIndex++;

				// CHECK: Is the game over?
				if (currentIndex >= scenarios.size()) {
					// NEW LOGIC: Win/Loss Condition
					// User Rule: "Heat higher than Power = Lose"
					if (state.heat > state.power) {
						triggerGameOver(false); // Defeat
					} else {
						triggerGameOver(true); // Victory
					}
				} else {
					// Game continues
					render();
				}
			}
		});

		render();
	}

	/**
	 * Renders the Summary Fragment
	 */
	private void render() {
		// don't render if we're showing a fragment
		if (showingFragment) {
			return;
		}

		Scenario sc = scenarios.get(currentIndex);
		tvText.setText(sc.text);
		tvWeek.setText("WEEK " + (currentIndex + 1));

		tvPower.setText(String.valueOf(state.power));
		tvHeat.setText(String.valueOf(state.heat));
		tvLoyalty.setText(String.valueOf(state.loyalty));

		updateStatColor(tvPower, state.power);
		updateStatColor(tvHeat, state.heat);
		updateStatColor(tvLoyalty, state.loyalty);

		btn1.setText(sc.choices[0].text);
		btn2.setText(sc.choices[1].text);
		btn3.setText(sc.choices[2].text);

		btn1.setOnClickListener(v -> onChoose(sc.choices[0]));
		btn2.setOnClickListener(v -> onChoose(sc.choices[1]));
		btn3.setOnClickListener(v -> onChoose(sc.choices[2]));
	}

	private void updateStatColor(TextView tv, int value) {
		if (value <= 20 || value >= 80) {
			tv.setTextColor(ContextCompat.getColor(this, R.color.stat_danger));
		} else if (value < 30 || value > 70) {
			tv.setTextColor(ContextCompat.getColor(this, R.color.stat_warning));
		} else {
			tv.setTextColor(ContextCompat.getColor(this, R.color.stat_safe));
		}
	}

	/**
	 * When selecting a choice from the briefing
	 * 
	 * @param c - Choice
	 */
	private void onChoose(Choice c) {
		// applyChoice(c);
		// Toast.makeText(this, c.text, Toast.LENGTH_SHORT).show();
		//
		// currentIndex++;
		// if (currentIndex >= scenarios.size()) {
		// Toast.makeText(this, "End of game.", Toast.LENGTH_LONG).show();
		// finish();
		// } else {
		// render();
		// }
		// store previous stats before applying choice
		int previousPower = state.power;
		int previousLoyalty = state.loyalty;
		int previousHeat = state.heat;

		applyChoice(c);
		// Toast.makeText(this, c.text, Toast.LENGTH_SHORT).show();
		// check if this is the last scenario
		if (isSuddenDeath()) {
			triggerGameOver(false); // False = Defeat
			return; // Stop execution, do not show summary
		}
		boolean isFinalWeek = (currentIndex + 1) >= scenarios.size();

		// go to SummaryFragment instead of immediately continuing
		showSummary(previousPower, previousLoyalty, previousHeat, c.text, isFinalWeek);

		// increment to next week
		// currentIndex++;

		// check if game is over
		// if (currentIndex >= scenarios.size()) {
		// Toast.makeText(this, "End of game.", Toast.LENGTH_LONG).show();
		// finish();
		// } else {
		// show summary fragment with the results
		// showSummary(previousPower, previousLoyalty, previousHeat, c.text);
		// }
	}

	/**
	 * Checks if any stat has breached the critical limits (<= 0 or >= 100)
	 */
	private boolean isSuddenDeath() {
		return state.power <= 0 || state.power >= 100 ||
				state.heat <= 0 || state.heat >= 100 ||
				state.loyalty <= 0 || state.loyalty >= 100;
	}

	/**
	 * Handles the Game Over state.
	 * For Phase 1, we use a simple Alert Dialog.
	 * In Phase 2, this will launch the GameOverFragment.
	 */
	private void triggerGameOver(boolean victory) {
		showingFragment = true; // Prevent render() from overwriting this

		String narrative;
		if (victory) {
			narrative = "Your rivals sat silent as the floor erupted. Heat had boiled, loyalty thinned—but Power held. And Power answers no one.";
		} else {
			// Customize text based on WHY they lost if you want
			if (state.heat > state.power) {
				narrative = "The media firestorm was too much. Without enough Power to quell the Heat, your administration collapsed.";
			} else {
				narrative = "The delegation moved on without your name. In politics, vanishing is the same as losing.";
			}
		}

		// Save High Score (Phase 2 logic)
		saveHighScore(currentIndex);

		// Show Game Over Screen
		GameOverFragment fragment = GameOverFragment.newInstance(
				victory,
				state.power,
				state.loyalty,
				state.heat,
				narrative);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, fragment)
				.commit();
	}

	// Helper to save high score
	private void saveHighScore(int week) {
		SharedPreferences prefs = getSharedPreferences("SuccessionPrefs", Context.MODE_PRIVATE);
		int currentHigh = prefs.getInt("HIGH_SCORE", 0);
		if (week > currentHigh) {
			prefs.edit().putInt("HIGH_SCORE", week).apply();
		}
	}

	/**
	 * Show the Summary Fragment that shows the outcome, hint, stats, trends, etc
	 * 
	 * @param previousPower   - int
	 * @param previousLoyalty - int
	 * @param previousHeat    - int
	 * @param chosenAction    - String
	 * @param isFinalWeek     - boolean
	 */
	private void showSummary(int previousPower, int previousLoyalty, int previousHeat, String chosenAction,
			boolean isFinalWeek) {
		// set showing fragment to true
		showingFragment = true;

		// outcome text based on choice
		String outcome = "You chose: \"" + chosenAction
				+ "\"\n\nThe political landscape shifts in response to your decision..."
				+ "Your allies and enemies take note of your actions.";

		// generate hint based on current stats
		String hint = generateHint();

		// create and show SummaryFragment
		SummaryFragment summaryFragment = SummaryFragment.newInstance(
				outcome,
				hint,
				currentIndex + 1, // current week
				state.power,
				state.loyalty,
				state.heat,
				previousPower,
				previousLoyalty,
				previousHeat,
				isFinalWeek);

		// replace the current view with the summary fragment
		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, summaryFragment)
				.addToBackStack("summary")
				.commit();
	}

	/**
	 * Shows an alert when the player reaches the final briefing for the day
	 */
	private void showFinalBriefingAlert() {
		new AlertDialog.Builder(this)
				.setTitle("End of Available Briefings")
				.setMessage("You have reached the maximum limit of briefings for today. " +
						"The President will provide more briefings tomorrow. " +
						"Your progress has been noted.")
				.setPositiveButton("Understood", (dialog, which) -> {
					dialog.dismiss();
				})
				.setCancelable(false)
				.show();
	}

	/**
	 * Generate a hint for next week
	 * 
	 * @return String
	 */
	private String generateHint() {
		if (state.power < 30) {
			return "You need to build more political influence. Consider choices that increase your power.";
		} else if (state.loyalty < 30) {
			return "Your inner circle is growing distant. Rebuild trust with those close to you.";
		} else if (state.heat > 70) {
			return "Too much attention can be dangerous. Consider laying low for a while.";
		} else if (state.power > 70 && state.loyalty > 70 && state.heat < 50) {
			return "You're in a strong position. Continue to balance your approach carefully.";
		} else {
			return "Maintain your current balance and stay vigilant. Every choice matters.";
		}
	}

	private float getMultiplier() {
		if (scenarios == null || scenarios.isEmpty())
			return 1.0f;
		return 1.0f + ((float) currentIndex / scenarios.size());
	}

	/**
	 * Apply the choice selected and calculates the stats for power, heat & loyalty
	 * 
	 * @param c - Choice
	 */
	private void applyChoice(Choice c) {
		float multiplier = getMultiplier();
		state.power += (int) (c.effects.power * multiplier);
		state.heat += (int) (c.effects.heat * multiplier);
		state.loyalty += (int) (c.effects.loyalty * multiplier);
	}

	/**
	 * Make the briefings/scenarios for each work
	 * 
	 * @return List<Scenario>
	 */
	private List<Scenario> makeStubScenarios() {
		List<Scenario> scenarioList = new ArrayList<>();

		// scenario 1:
		Scenario s1 = new Scenario();
		s1.title = "Factory Visit";
		s1.text = "Rumors swirl about your rising popularity with the Party base. The President asks you to cool off your media appearances.";

		Choice c1 = new Choice();
		c1.text = "“Of course, Mr. President. I’ll cancel appearances.”";
		c1.effects = new Effects();
		c1.effects.power = -1;
		c1.effects.heat = -2;
		c1.effects.loyalty = 2;

		Choice c2 = new Choice();
		c2.text = "“I’ve been loyal. But secrets go both ways.”";
		c2.effects = new Effects();
		c2.effects.power = 3;
		c2.effects.heat = 2;
		c2.effects.loyalty = -1;

		Choice c3 = new Choice();
		c3.text = "“Let’s1 not do anything rash. I’ll redirect quietly.”";
		c3.effects = new Effects();
		c3.effects.power = 1;
		c3.effects.heat = 0;
		c3.effects.loyalty = 1;

		s1.choices = new Choice[] { c1, c2, c3 };

		// repeat or add more for testing
		// return Arrays.asList(s1, s1, s1);
		scenarioList.add(s1);

		// scenario 2:
		Scenario s2 = new Scenario();
		s2.title = "Budget Crisis";
		s2.text = "A budget crisis looms. The Treasury Secretary privately asks for your support on an unpopular spending cut.";

		Choice c4 = new Choice();
		c4.text = "“I'll back you publicly. We need fiscal responsibility.“";
		c4.effects = new Effects();
		c4.effects.power = 2;
		c4.effects.heat = 3;
		c4.effects.loyalty = 0;

		Choice c5 = new Choice();
		c5.text = "“I can't support this. The people won't forgive it.“";
		c5.effects = new Effects();
		c5.effects.power = -1;
		c5.effects.heat = 1;
		c5.effects.loyalty = 2;

		Choice c6 = new Choice();
		c6.text = "“Let me propose a compromise behind closed doors.“";
		c6.effects = new Effects();
		c6.effects.power = 2;
		c6.effects.heat = -1;
		c6.effects.loyalty = 1;

		s2.choices = new Choice[] { c4, c5, c6 };
		scenarioList.add(s2);

		// scenario 3:
		Scenario s3 = new Scenario();
		s3.title = "Media Scandal";
		s3.text = "A journalist has discovered something about your past. They offer to kill the story in exchange for exclusive access.";

		Choice c7 = new Choice();
		c7.text = "“Do what you must. I have nothing to hide.“";
		c7.effects = new Effects();
		c7.effects.power = -2;
		c7.effects.heat = 4;
		c7.effects.loyalty = 1;

		Choice c8 = new Choice();
		c8.text = "“I'll give you the exclusive. Let's work together.“";
		c8.effects = new Effects();
		c8.effects.power = 1;
		c8.effects.heat = 1;
		c8.effects.loyalty = -1;

		Choice c9 = new Choice();
		c9.text = "“I know people who can make this go away.“";
		c9.effects = new Effects();
		c9.effects.power = 3;
		c9.effects.heat = -2;
		c9.effects.loyalty = -2;

		s3.choices = new Choice[] { c7, c8, c9 };
		scenarioList.add(s3);

		return scenarioList;
	}
}
