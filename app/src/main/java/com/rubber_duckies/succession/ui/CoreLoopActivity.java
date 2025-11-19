package com.rubber_duckies.succession.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

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
        //render();
        //listen for when fragments are popped from back stack
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                //fragment was popped, we're back to the activity
                showingFragment = false;

                //increment to next week AFTER returning from summary
                currentIndex++;

//                if (currentIndex < scenarios.size()) {
//                    render();
//                }
                //check if game is over
                if (currentIndex >= scenarios.size()) {
                    //Toast.makeText(this, "End of game. You've completed all weeks!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //render the next week
                    render();
                }
            }
        });

        render();
    }

    private void render() {
        //don't render if we're showing a fragment
        if (showingFragment) {
            return;
        }

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
        //store previous stats before applying choice
        int previousPower = state.power;
        int previousLoyalty = state.loyalty;
        int previousHeat = state.heat;

        applyChoice(c);
        //Toast.makeText(this, c.text, Toast.LENGTH_SHORT).show();

        //go to SummaryFragment instead of immediately continuing
        showSummary(previousPower, previousLoyalty, previousHeat, c.text);

        //increment to next week
        //currentIndex++;

        //check if game is over
        //if (currentIndex >= scenarios.size()) {
            //Toast.makeText(this, "End of game.", Toast.LENGTH_LONG).show();
            //finish();
        //} else {
            //show summary fragment with the results
            //showSummary(previousPower, previousLoyalty, previousHeat, c.text);
        //}
    }

    private void showSummary(int previousPower, int previousLoyalty, int previousHeat, String chosenAction) {
        //outcome text based on choice
        String outcome = "You chose: \"" + chosenAction + "\"\n\nThe political landscape shifts in response to your decision..." + "Your allies and enemies take note of your actions.";

        //generate hint based on current stats
        String hint = generateHint();

        //create and show SummaryFragment
        SummaryFragment summaryFragment = SummaryFragment.newInstance(
                outcome,
                hint,
                currentIndex, //current week
                state.power,
                state.loyalty,
                state.heat,
                previousPower,
                previousLoyalty,
                previousHeat
        );

        //replace the current view with the summary fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, summaryFragment)
                .addToBackStack("summary")
                .commit();
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
        List<Scenario> scenarioList = new ArrayList<>();

        //scenario 1:
        Scenario s1 = new Scenario();
        s1.title = "Factory Visit";
        s1.text = "Rumors swirl about your rising popularity with the Party base. The President asks you to cool off your media appearances.";

        Choice c1 = new Choice();
        c1.text = "“Of course, Mr. President. I’ll cancel appearances.”";
        c1.effects = new Effects(); c1.effects.power = -1; c1.effects.heat = -2; c1.effects.loyalty = 2;

        Choice c2 = new Choice();
        c2.text = "“I’ve been loyal. But secrets go both ways.”";
        c2.effects = new Effects(); c2.effects.power = 3; c2.effects.heat = 2; c2.effects.loyalty = -1;

        Choice c3 = new Choice();
        c3.text = "“Let’s1 not do anything rash. I’ll redirect quietly.”";
        c3.effects = new Effects(); c3.effects.power = 1; c3.effects.heat = 0; c3.effects.loyalty = 1;

        s1.choices = new Choice[] { c1, c2, c3 };

        // repeat or add more for testing
        //return Arrays.asList(s1, s1, s1);
        scenarioList.add(s1);

        //scenario 2:
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

        //scenario 3:
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
