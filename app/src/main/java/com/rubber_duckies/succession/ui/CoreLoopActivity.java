package com.rubber_duckies.succession.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.rubber_duckies.succession.*;
import com.rubber_duckies.succession.R;
import java.util.*;

public class CoreLoopActivity extends AppCompatActivity {

    private TextView tvPower, tvHeat, tvLoyalty, tvText, tvWeek;
    private Button btn1, btn2, btn3;
    private GameState state = new GameState();
    private List<Scenario> scenarios;
    private int currentIndex = 0;

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
        render();
    }

    private void render() {
        Scenario sc = scenarios.get(currentIndex);
        tvText.setText(sc.text);
        tvWeek.setText("WEEK " + (currentIndex + 1));

        tvPower.setText(String.valueOf(state.power));
        tvHeat.setText(String.valueOf(state.heat));
        tvLoyalty.setText(String.valueOf(state.loyalty));

        btn1.setText(sc.choices[0].text);
        btn2.setText(sc.choices[1].text);
        btn3.setText(sc.choices[2].text);

        btn1.setOnClickListener(v -> onChoose(sc.choices[0]));
        btn2.setOnClickListener(v -> onChoose(sc.choices[1]));
        btn3.setOnClickListener(v -> onChoose(sc.choices[2]));
    }

    private void onChoose(Choice c) {
//        applyChoice(c);
//        Toast.makeText(this, c.text, Toast.LENGTH_SHORT).show();
//
//        currentIndex++;
//        if (currentIndex >= scenarios.size()) {
//            Toast.makeText(this, "End of game.", Toast.LENGTH_LONG).show();
//            finish();
//        } else {
//            render();
//        }
        // Store previous stats before applying choice
        int previousPower = state.power;
        int previousLoyalty = state.loyalty;
        int previousHeat = state.heat;

        applyChoice(c);
        Toast.makeText(this, c.text, Toast.LENGTH_SHORT).show();

        // Navigate to SummaryFragment instead of immediately continuing
        showSummary(previousPower, previousLoyalty, previousHeat, c.text);

        // Increment to next week
        currentIndex++;

        // Check if game is over
        if (currentIndex >= scenarios.size()) {
            Toast.makeText(this, "End of game.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            // Show summary fragment with the results
            showSummary(previousPower, previousLoyalty, previousHeat, c.text);
        }
    }

    private void showSummary(int previousPower, int previousLoyalty, int previousHeat, String chosenAction) {
        // Create outcome text based on choice
        String outcome = "You chose: \"" + chosenAction + "\"\n\nThe political landscape shifts in response to your decision..." + "Your allies and enemies take note of your actions.";

        // Generate hint based on current stats
        String hint = generateHint();

        // Create and show SummaryFragment
        SummaryFragment summaryFragment = SummaryFragment.newInstance(
                outcome,
                hint,
                currentIndex, // current week
                state.power,
                state.loyalty,
                state.heat,
                previousPower,
                previousLoyalty,
                previousHeat
        );

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(android.R.id.content, summaryFragment)
//                .addToBackStack("summary")
//                .commit();
        // Replace the current view with the summary fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, summaryFragment);
        transaction.addToBackStack("summary");
        transaction.commit();
    }

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

    private void applyChoice(Choice c) {
        state.power += c.effects.power;
        state.heat += c.effects.heat;
        state.loyalty = Math.max(0, Math.min(100, state.loyalty + c.effects.loyalty));
    }

    private List<Scenario> makeStubScenarios() {
        Scenario s = new Scenario();
        s.title = "Factory Visit";
        s.text = "Rumors swirl about your rising popularity with the Party base. The President asks you to cool off your media appearances.";

        Choice c1 = new Choice();
        c1.text = "“Of course, Mr. President. I’ll cancel appearances.”";
        c1.effects = new Effects(); c1.effects.power = -1; c1.effects.heat = -2; c1.effects.loyalty = 2;

        Choice c2 = new Choice();
        c2.text = "“I’ve been loyal. But secrets go both ways.”";
        c2.effects = new Effects(); c2.effects.power = 3; c2.effects.heat = 2; c2.effects.loyalty = -1;

        Choice c3 = new Choice();
        c3.text = "“Let’s not do anything rash. I’ll redirect quietly.”";
        c3.effects = new Effects(); c3.effects.power = 1; c3.effects.heat = 0; c3.effects.loyalty = 1;

        s.choices = new Choice[] { c1, c2, c3 };

        // repeat or add more for testing
        return Arrays.asList(s, s, s);
    }
}
