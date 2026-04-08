package com.rubber_duckies.succession.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import android.view.View;
import com.rubber_duckies.succession.*;
import com.rubber_duckies.succession.R;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoreLoopActivity extends AppCompatActivity {
	private static final int MAX_WEEKS=10;
	private TextView tvPower, tvHeat, tvLoyalty, tvWeek;
	private TextView tvScenario, tvPresident, tvAdvisor, tvHint, tvAiLoading;
	private Button btn1, btn2, btn3;
	private GameState state = new GameState();
	private List<Scenario> scenarios;
	private int currentIndex = 0;
	private boolean showingFragment = false;
	// ADDED: AI service
	private AiApiService aiService;

	// ADDED: cache AI-generated scenarios by week index
	private HashMap<Integer, AiScenarioResponse> aiScenarioCache = new HashMap<>();

	// ADDED: stores current AI scenario metadata for summary
	private String currentAiHint = "";
	private String currentAiTone = "";
	private String[] currentAiOutcomes = new String[3];
	

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_core_loop);

		tvPower = findViewById(R.id.tv_power);
		tvHeat = findViewById(R.id.tv_heat);
		tvLoyalty = findViewById(R.id.tv_loyalty);
		tvScenario = findViewById(R.id.tv_scenario);
		tvPresident = findViewById(R.id.tv_president);
		tvAdvisor = findViewById(R.id.tv_advisor);
		tvHint = findViewById(R.id.tv_hint);
		tvAiLoading = findViewById(R.id.tv_ai_loading);
		tvWeek = findViewById(R.id.tv_week);
		btn1 = findViewById(R.id.btn_choice1);
		btn2 = findViewById(R.id.btn_choice2);
		btn3 = findViewById(R.id.btn_choice3);

		scenarios = makeStubScenarios();

		// ADDED: initialize AI service
		aiService = AiClient.create();

		// CHANGED: stats now start at the threshold-required default values (50/50/50)
		state.power = 50;
		state.heat = 50;
		state.loyalty = 50;

		// listen for when fragments are popped from back stack
		getSupportFragmentManager().addOnBackStackChangedListener(() -> {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				showingFragment = false;

				// Increment week after summary
				currentIndex++;

				// CHECK: Is the game over?
				if (currentIndex >= MAX_WEEKS) {
					// CHANGED: new threshold based ending
					if (state.presidencySecured || state.reelectionWon) {
						triggerGameOver(true);
					} else {
						triggerGameOver(false);
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
	/*private void render() {
		// don't render if we're showing a fragment
		if (showingFragment) {
			return;
		}

		//Scenario sc = scenarios.get(currentIndex);
		//tvText.setText(sc.text);
		Scenario sc = scenarios.get(currentIndex);

		// Show fallback static text first
		tvText.setText(sc.text);
		Toast.makeText(this, "Calling AI...", Toast.LENGTH_SHORT).show();
		// ADDED: call AI backend to enrich the dialogue


		loadAiDialogue(sc);

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
	}*/

	private void render() {
    if (showingFragment) {
        return;
    }

    tvWeek.setText("WEEK " + (currentIndex + 1));

    tvPower.setText(String.valueOf(state.power));
    tvHeat.setText(String.valueOf(state.heat));
    tvLoyalty.setText(String.valueOf(state.loyalty));

    updateStatColor(tvPower, state.power);
    updateStatColor(tvHeat, state.heat);
    updateStatColor(tvLoyalty, state.loyalty);

    // Clear previous content
    tvScenario.setText("Loading scenario...");
    tvPresident.setText("");
    tvAdvisor.setText("");
    tvHint.setText("");
    tvAiLoading.setVisibility(View.VISIBLE);

    loadAiScenario();
}

	// ADDED: requests AI-generated dialogue for the current scenario
	/*private void loadAiDialogue(Scenario sc) {
    if (aiService == null) {
        tvText.setText(sc.text + "\n\nAI ERROR: aiService is null");
        return;
    }

    tvText.setText(sc.text + "\n\nAI DEBUG: loadAiDialogue started");

    AiDialogueRequest request = new AiDialogueRequest(
            currentIndex + 1,
            state.phase,
            state.power,
            state.loyalty,
            state.heat,
            state.onBlacklistedState,
            sc.title,
            sc.text
    );

   		aiService.getDialogue(request).enqueue(new Callback<AiDialogueResponse>() {
    		@Override
    	public void onResponse(Call<AiDialogueResponse> call, Response<AiDialogueResponse> response) {
       		 	if (!response.isSuccessful() || response.body() == null) {
           		 tvText.setText(sc.text + "\n\nAI ERROR: response failed with code " + response.code());
           		 return;
        		}

        		AiDialogueResponse ai = response.body();

       			 String dynamicText =
                sc.text +
                "\n\nPresident: " + ai.president_line +
                "\nAdvisor: " + ai.advisor_line +
                "\n\nHint: " + ai.hint +
                "\nTone: " + ai.tone;

       		 		tvText.setText(dynamicText);
   			 }

    		@Override
    		public void onFailure(Call<AiDialogueResponse> call, Throwable t) {
       			 tvText.setText(sc.text + "\n\nAI ERROR: " + t.getMessage());
   			 }
		});
	}	*/


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
	/*private void onChoose(Choice c) {
		// store previous stats before applying choice
		int previousPower = state.power;
		int previousLoyalty = state.loyalty;
		int previousHeat = state.heat;

		applyChoice(c);

		// ADDED: presidency threshold
		if (state.presidencySecured) {
			triggerGameOver(true);
			return;
		}

		// ADDED: re-election threshold
		if (state.reelectionWon) {
			triggerGameOver(true);
			return;
		}

		// Keep original sudden-death handling
		if (isSuddenDeath()) {
			triggerGameOver(false);
			return;
		}

		boolean isFinalWeek = (currentIndex + 1) >= scenarios.size();

		// go to SummaryFragment instead of immediately continuing
		showSummary(previousPower, previousLoyalty, previousHeat, c.text, isFinalWeek);
	}*/

	/**
	 * Checks if any stat has breached the critical lower limits (<= 0)
	 */
	private boolean isSuddenDeath() {
		// CHANGED: removed old >=100 loss condition because 100 is now part of the win logic
		return state.power <= 0 ||
				state.heat <= 0 ||
				state.loyalty <= 0;
	}

	/**
	 * Handles the Game Over state.
	 * For Phase 1, we use a simple Alert Dialog.
	 * In Phase 2, this will launch the GameOverFragment.
	 */
	private void triggerGameOver(boolean victory) {
		showingFragment = true;

		String narrative;
		if (victory) {
			narrative = "Your rivals sat silent as the floor erupted. You balanced Power, Loyalty, and Heat long enough to secure victory.";
		} else {
			if (state.onBlacklistedState) {
				narrative = "Your Heat pushed you into the Blacklisted State. Without enough recovery, your path to the presidency collapsed.";
			} else {
				narrative = "The delegation moved on without your name. In politics, vanishing is the same as losing.";
			}
		}

		saveHighScore(currentIndex);

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
	/*private void showSummary(int previousPower, int previousLoyalty, int previousHeat, String chosenAction,
			boolean isFinalWeek) {
		showingFragment = true;

		String outcome = "You chose: \"" + chosenAction
				+ "\"\n\nThe political landscape shifts in response to your decision..."
				+ "Your allies and enemies take note of your actions.";

		String hint = generateHint();

		SummaryFragment summaryFragment = SummaryFragment.newInstance(
				outcome,
				hint,
				currentIndex + 1,
				state.power,
				state.loyalty,
				state.heat,
				previousPower,
				previousLoyalty,
				previousHeat,
				isFinalWeek);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, summaryFragment)
				.addToBackStack("summary")
				.commit();
	}*/

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

		if (state.presidencySecured) {
			return "You secured the presidency. Phase 2 begins.";
		}

		if (state.onBlacklistedState) {
			return "Your Heat is too high. You are now in the Blacklisted State and must recover your reputation.";
		}

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

		// ADDED: keep stats valid
		state.clampStats();

		// ADDED: evaluate thresholds
		state.updateThresholds();
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
	//add:helper method to convert AI scenario into your existing game model
	private Scenario convertAiScenario(AiScenarioResponse ai) {
		Scenario sc = new Scenario();
		sc.title = ai.title;
		sc.text = ai.brief;

		Choice[] convertedChoices = new Choice[3];

		for (int i = 0; i < ai.choices.length; i++) {
			Choice c = new Choice();
			c.text = ai.choices[i].text;

			Effects e = new Effects();
			e.power = ai.choices[i].effects.power;
			e.loyalty = ai.choices[i].effects.loyalty;
			e.heat = ai.choices[i].effects.heat;

			c.effects = e;
			convertedChoices[i] = c;
		}

		sc.choices = convertedChoices;
		return sc;
	}
	//ADD: new method to loadAi scenario()
		private void loadAiScenario() {
			if (aiService == null) {
				loadFallbackScenario();
				return;
			}

			// Use cached scenario if already generated for this week
			if (aiScenarioCache.containsKey(currentIndex)) {
				AiScenarioResponse cached = aiScenarioCache.get(currentIndex);
				applyAiScenarioToUi(cached);
				return;
			}

			AiScenarioRequest request = new AiScenarioRequest(
					currentIndex + 1,
					state.phase,
					state.power,
					state.loyalty,
					state.heat,
					state.onBlacklistedState
			);

			aiService.generateScenario(request).enqueue(new Callback<AiScenarioResponse>() {
				@Override
				public void onResponse(Call<AiScenarioResponse> call, Response<AiScenarioResponse> response) {
					if (!response.isSuccessful() || response.body() == null) {
						loadFallbackScenario();
						return;
					}

					AiScenarioResponse ai = response.body();

					// Cache it so we don't regenerate the same week
					aiScenarioCache.put(currentIndex, ai);

					applyAiScenarioToUi(ai);
				}

				@Override
				public void onFailure(Call<AiScenarioResponse> call, Throwable t) {
					loadFallbackScenario();
				}
			});
		}
		//Ui aply method
		private void applyAiScenarioToUi(AiScenarioResponse ai) {
			tvAiLoading.setVisibility(View.GONE);

			currentAiHint = ai.hint;
			currentAiTone = ai.tone;

			for (int i = 0; i < 3; i++) {
				currentAiOutcomes[i] = ai.choices[i].outcome;
			}

			Scenario sc = convertAiScenario(ai);

			tvScenario.setText(sc.text);
			tvPresident.setText("Scenario: " + ai.title);
			tvAdvisor.setText("Advisor: Tone = " + ai.tone);
			tvHint.setText("Hint: " + ai.hint);

			btn1.setText(sc.choices[0].text);
			btn2.setText(sc.choices[1].text);
			btn3.setText(sc.choices[2].text);

			btn1.setOnClickListener(v -> onChooseWithOutcome(sc.choices[0], currentAiOutcomes[0]));
			btn2.setOnClickListener(v -> onChooseWithOutcome(sc.choices[1], currentAiOutcomes[1]));
			btn3.setOnClickListener(v -> onChooseWithOutcome(sc.choices[2], currentAiOutcomes[2]));
		}
		// Fallback method
		private void loadFallbackScenario() {
			tvAiLoading.setVisibility(View.GONE);

			Scenario sc = scenarios.get(currentIndex);

			tvScenario.setText(sc.text);
			tvPresident.setText("President: Default scenario mode");
			tvAdvisor.setText("Advisor: AI unavailable");
			tvHint.setText("Hint: Using fallback content.");

			currentAiHint = "Using fallback content.";
			currentAiTone = "neutral";
			currentAiOutcomes[0] = "Your decision changes the political landscape.";
			currentAiOutcomes[1] = "Your decision changes the political landscape.";
			currentAiOutcomes[2] = "Your decision changes the political landscape.";

			btn1.setText(sc.choices[0].text);
			btn2.setText(sc.choices[1].text);
			btn3.setText(sc.choices[2].text);

			btn1.setOnClickListener(v -> onChooseWithOutcome(sc.choices[0], currentAiOutcomes[0]));
			btn2.setOnClickListener(v -> onChooseWithOutcome(sc.choices[1], currentAiOutcomes[1]));
			btn3.setOnClickListener(v -> onChooseWithOutcome(sc.choices[2], currentAiOutcomes[2]));
		}
		//Add a new choice handler with AI outcome
		private void onChooseWithOutcome(Choice c, String aiOutcome) {
			int previousPower = state.power;
			int previousLoyalty = state.loyalty;
			int previousHeat = state.heat;

			applyChoice(c);

			if (state.presidencySecured) {
				triggerGameOver(true);
				return;
			}

			if (state.reelectionWon) {
				triggerGameOver(true);
				return;
			}

			if (isSuddenDeath()) {
				triggerGameOver(false);
				return;
			}

			boolean isFinalWeek = (currentIndex + 1) >= MAX_WEEKS;

			showSummaryWithAiOutcome(
					previousPower,
					previousLoyalty,
					previousHeat,
					aiOutcome,
					currentAiHint,
					isFinalWeek
			);
		}
		//Add this new summary method
		private void showSummaryWithAiOutcome(
					int previousPower,
					int previousLoyalty,
					int previousHeat,
					String outcome,
					String hint,
					boolean isFinalWeek
			) {
				showingFragment = true;

				SummaryFragment summaryFragment = SummaryFragment.newInstance(
						outcome,
						hint,
						currentIndex + 1,
						state.power,
						state.loyalty,
						state.heat,
						previousPower,
						previousLoyalty,
						previousHeat,
						isFinalWeek
				);

				getSupportFragmentManager()
						.beginTransaction()
						.replace(android.R.id.content, summaryFragment)
						.addToBackStack("summary")
						.commit();
		}

}
