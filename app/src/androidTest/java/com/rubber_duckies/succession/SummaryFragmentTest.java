package com.rubber_duckies.succession;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.rubber_duckies.succession.ui.SummaryFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests covered:
 *   TC-11  SummaryFragment displays the correct week header
 *   TC-12  SummaryFragment displays the outcome text passed in via arguments
 *   TC-13  SummaryFragment displays the hint text passed in via arguments
 *   TC-14  SummaryFragment displays the Continue and Exit buttons
 *   TC-20  SummaryFragment displays "VIEW FINAL RESULTS" on the final week
 */
@RunWith(AndroidJUnit4.class)
public class SummaryFragmentTest {

    // -------------------------------------------------------------------------
    // Helper: builds a valid Bundle for SummaryFragment
    // -------------------------------------------------------------------------
    private Bundle buildSummaryArgs(
            String outcome,
            String hint,
            int week,
            int power,
            int loyalty,
            int heat,
            int prevPower,
            int prevLoyalty,
            int prevHeat,
            boolean isFinalWeek) {

        Bundle args = new Bundle();
        args.putString("outcome", outcome);
        args.putString("hint", hint);
        args.putInt("week", week);
        args.putInt("current_power", power);
        args.putInt("current_loyalty", loyalty);
        args.putInt("current_heat", heat);
        args.putInt("previous_power", prevPower);
        args.putInt("previous_loyalty", prevLoyalty);
        args.putInt("previous_heat", prevHeat);
        args.putBoolean("is_final_week", isFinalWeek);
        return args;
    }

    // -------------------------------------------------------------------------
    // TC-11: SummaryFragment displays the correct week header
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_summary_header (TextView) in fragment_summary.xml,
     *                           setupObservers() -> currentWeek LiveData observer
     *
     * Short Description:
     *   Verifies that the week header TextView is visible and displays the correct
     *   week number formatted as "Week X Summary". The header is set inside
     *   setupObservers() when the currentWeek LiveData posts its value.
     *   Covers SRS REQ-15 and UC-4 step 1.
     *
     * Input:
     *   - Launch SummaryFragment with week = 3 and standard stat values
     *
     * Expected Results:
     *   - tv_summary_header is displayed
     *   - tv_summary_header text == "Week 3 Summary"
     */
    @Test
    public void testSummaryFragmentShowsWeekHeader() {
        Bundle args = buildSummaryArgs(
                "You chose wisely.", "Watch your Heat.", 3,
                60, 55, 40, 50, 50, 50, false);

        FragmentScenario.launchInContainer(SummaryFragment.class, args);

        onView(withId(R.id.tv_summary_header))
                .check(matches(isDisplayed()))
                .check(matches(withText("Week 3 Summary")));
    }

    // -------------------------------------------------------------------------
    // TC-12: SummaryFragment displays the outcome text passed in via arguments
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_outcome_text (TextView) in fragment_summary.xml,
     *                           setupObservers() -> outcomeText LiveData observer
     *
     * Short Description:
     *   Verifies that the outcome text TextView is visible and displays the exact
     *   outcome string passed into the fragment via its Bundle arguments. The text
     *   is posted to the UI through the outcomeText LiveData in SummaryViewModel.
     *   Covers SRS REQ-14 and UC-4 step 2.
     *
     * Input:
     *   - Launch SummaryFragment with outcome = "Your allies took note of your move."
     *
     * Expected Results:
     *   - tv_outcome_text is displayed
     *   - tv_outcome_text text == "Your allies took note of your move."
     */
    @Test
    public void testSummaryFragmentShowsOutcomeText() {
        Bundle args = buildSummaryArgs(
                "Your allies took note of your move.",
                "Watch your Heat.", 2,
                60, 55, 40, 50, 50, 50, false);

        FragmentScenario.launchInContainer(SummaryFragment.class, args);

        onView(withId(R.id.tv_outcome_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("Your allies took note of your move.")));
    }

    // -------------------------------------------------------------------------
    // TC-13: SummaryFragment displays the hint text passed in via arguments
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_hint_text (TextView) in fragment_summary.xml,
     *                           setupObservers() -> hintText LiveData observer
     *
     * Short Description:
     *   Verifies that the hint TextView is visible and displays the hint string
     *   passed in via Bundle arguments, prefixed with "Hint for next week: ".
     *   The prefix is appended inside setupObservers() before the text is set.
     *   Covers SRS REQ-17 and UC-4 step 5.
     *
     * Input:
     *   - Launch SummaryFragment with hint = "Keep your Heat low."
     *
     * Expected Results:
     *   - tv_hint_text is displayed
     *   - tv_hint_text text == "Hint for next week: Keep your Heat low."
     */
    @Test
    public void testSummaryFragmentShowsHintText() {
        Bundle args = buildSummaryArgs(
                "You chose wisely.",
                "Keep your Heat low.", 2,
                60, 55, 40, 50, 50, 50, false);

        FragmentScenario.launchInContainer(SummaryFragment.class, args);

        onView(withId(R.id.tv_hint_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("Hint for next week: Keep your Heat low.")));
    }

    // -------------------------------------------------------------------------
    // TC-14: SummaryFragment displays the Continue and Exit buttons
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btn_continue and btnExit (Buttons)
     *                           in fragment_summary.xml
     *
     * Short Description:
     *   Verifies that both action buttons are visible on the Summary screen.
     *   btn_continue pops the back stack to return to CoreLoopActivity for the
     *   next week. btnExit navigates back to MainActivity with CLEAR_TOP flags.
     *   Covers SRS REQ-13, REQ-18, REQ-19, and UC-4 steps 6-7.
     *
     * Input:
     *   - Launch SummaryFragment with standard arguments and isFinalWeek = false
     *
     * Expected Results:
     *   - btn_continue is displayed
     *   - btnExit is displayed
     */
    @Test
    public void testSummaryFragmentShowsContinueAndExitButtons() {
        Bundle args = buildSummaryArgs(
                "You chose wisely.", "Watch your Heat.", 2,
                60, 55, 40, 50, 50, 50, false);

        FragmentScenario.launchInContainer(SummaryFragment.class, args);

        onView(withId(R.id.btn_continue)).check(matches(isDisplayed()));
        onView(withId(R.id.btnExit)).check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-20: SummaryFragment shows "VIEW FINAL RESULTS" on the final week
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btn_continue (Button) in fragment_summary.xml,
     *                           loadDataFromArguments() isFinalWeek branch
     *
     * Short Description:
     *   Verifies that when isFinalWeek = true, the Continue button label changes
     *   to "VIEW FINAL RESULTS" instead of the standard "CONTINUE TO WEEK X".
     *   This text change is applied inside loadDataFromArguments() when the
     *   isFinalWeek flag is true. Covers SRS REQ-18 and UC-5 extension 4a.
     *
     * Input:
     *   - Launch SummaryFragment with isFinalWeek = true and week = 10
     *
     * Expected Results:
     *   - btn_continue is displayed
     *   - btn_continue text == "VIEW FINAL RESULTS"
     */
    @Test
    public void testSummaryFragmentShowsViewFinalResultsOnFinalWeek() {
        Bundle args = buildSummaryArgs(
                "You chose wisely.", "Watch your Heat.", 10,
                60, 55, 40, 50, 50, 50, true);

        FragmentScenario.launchInContainer(SummaryFragment.class, args);

        onView(withId(R.id.btn_continue))
                .check(matches(isDisplayed()))
                .check(matches(withText("VIEW FINAL RESULTS")));
    }
}