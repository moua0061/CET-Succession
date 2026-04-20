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

import com.rubber_duckies.succession.ui.GameOverFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests covered:
 *   TC-15  GameOverFragment displays VICTORY header on a victory outcome
 *   TC-16  GameOverFragment displays DEFEAT header on a defeat outcome
 *   TC-17  GameOverFragment displays the Play Again and Exit buttons
 *   TC-18  GameOverFragment displays the correct narrative text
 *   TC-19  GameOverFragment displays the correct final stat values
 */
@RunWith(AndroidJUnit4.class)
public class GameOverFragmentTest {

    // -------------------------------------------------------------------------
    // Helper: builds a valid Bundle for GameOverFragment
    // -------------------------------------------------------------------------
    private Bundle buildGameOverArgs(
            boolean isVictory,
            int power,
            int loyalty,
            int heat,
            String narrative) {

        Bundle args = new Bundle();
        args.putBoolean("victory", isVictory);
        args.putInt("power", power);
        args.putInt("loyalty", loyalty);
        args.putInt("heat", heat);
        args.putString("narrative", narrative);
        return args;
    }

    // -------------------------------------------------------------------------
    // TC-15: GameOverFragment displays VICTORY header on a victory outcome
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_header (TextView) in fragment_game_over.xml,
     *                           onViewCreated() isVictory == true branch
     *
     * Short Description:
     *   Verifies that when GameOverFragment is launched with isVictory = true,
     *   the header TextView displays "VICTORY". The header text is set inside
     *   onViewCreated() based on the isVictory flag read from the Bundle.
     *   Covers SRS REQ-35 and UC-4 extension 5b.
     *
     * Input:
     *   - Launch GameOverFragment with victory = true
     *   - power = 100, loyalty = 100, heat = 40
     *   - narrative = "You secured the presidency."
     *
     * Expected Results:
     *   - tv_header is displayed
     *   - tv_header text == "VICTORY"
     */
    @Test
    public void testGameOverFragmentShowsVictoryHeader() {
        Bundle args = buildGameOverArgs(
                true, 100, 100, 40,
                "You secured the presidency.");

        FragmentScenario.launchInContainer(GameOverFragment.class, args);

        onView(withId(R.id.tv_header))
                .check(matches(isDisplayed()))
                .check(matches(withText("VICTORY")));
    }

    // -------------------------------------------------------------------------
    // TC-16: GameOverFragment displays DEFEAT header on a defeat outcome
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_header (TextView) in fragment_game_over.xml,
     *                           onViewCreated() isVictory == false branch
     *
     * Short Description:
     *   Verifies that when GameOverFragment is launched with isVictory = false,
     *   the header TextView displays "DEFEAT". The header text is set inside
     *   onViewCreated() in the else branch of the isVictory check.
     *   Covers SRS REQ-35 and UC-4 extension 5a.
     *
     * Input:
     *   - Launch GameOverFragment with victory = false
     *   - power = 0, loyalty = 20, heat = 90
     *   - narrative = "The delegation moved on without your name."
     *
     * Expected Results:
     *   - tv_header is displayed
     *   - tv_header text == "DEFEAT"
     */
    @Test
    public void testGameOverFragmentShowsDefeatHeader() {
        Bundle args = buildGameOverArgs(
                false, 0, 20, 90,
                "The delegation moved on without your name.");

        FragmentScenario.launchInContainer(GameOverFragment.class, args);

        onView(withId(R.id.tv_header))
                .check(matches(isDisplayed()))
                .check(matches(withText("DEFEAT")));
    }

    // -------------------------------------------------------------------------
    // TC-17: GameOverFragment displays the Play Again and Exit buttons
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btn_play_again and btn_exit_menu (Buttons)
     *                           in fragment_game_over.xml, onViewCreated()
     *
     * Short Description:
     *   Verifies that both action buttons are visible on the Game Over screen
     *   regardless of outcome. btn_play_again launches a fresh CoreLoopActivity
     *   with CLEAR_TOP flags. btn_exit_menu navigates back to MainActivity.
     *   Covers SRS REQ-35 and UC-6.
     *
     * Input:
     *   - Launch GameOverFragment with victory = true and standard stat values
     *
     * Expected Results:
     *   - btn_play_again is displayed
     *   - btn_exit_menu is displayed
     */
    @Test
    public void testGameOverFragmentShowsPlayAgainAndExitButtons() {
        Bundle args = buildGameOverArgs(
                true, 100, 100, 40,
                "You secured the presidency.");

        FragmentScenario.launchInContainer(GameOverFragment.class, args);

        onView(withId(R.id.btn_play_again)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_exit_menu)).check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-18: GameOverFragment displays the correct narrative text
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_narrative (TextView) in fragment_game_over.xml,
     *                           onViewCreated() -> tvNarrative.setText(narrative)
     *
     * Short Description:
     *   Verifies that the narrative TextView is visible and displays the exact
     *   narrative string passed into the fragment via its Bundle arguments.
     *   The narrative text is set directly in onViewCreated() from the Bundle.
     *   Covers SRS REQ-35 and UC-4 extension 5a.
     *
     * Input:
     *   - Launch GameOverFragment with victory = false
     *   - narrative = "The delegation moved on without your name."
     *
     * Expected Results:
     *   - tv_narrative is displayed
     *   - tv_narrative text == "The delegation moved on without your name."
     */
    @Test
    public void testGameOverFragmentShowsNarrativeText() {
        Bundle args = buildGameOverArgs(
                false, 0, 20, 90,
                "The delegation moved on without your name.");

        FragmentScenario.launchInContainer(GameOverFragment.class, args);

        onView(withId(R.id.tv_narrative))
                .check(matches(isDisplayed()))
                .check(matches(withText("The delegation moved on without your name.")));
    }

    // -------------------------------------------------------------------------
    // TC-19: GameOverFragment displays the correct final stat values
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_final_power, tv_final_loyalty, tv_final_heat
     *                           (TextViews) in fragment_game_over.xml,
     *                           onViewCreated() -> tvPower/Loyalty/Heat.setText()
     *
     * Short Description:
     *   Verifies that all three final stat TextViews are visible and display the
     *   correct numeric values passed in via the Bundle arguments. The values are
     *   set directly in onViewCreated() using String.valueOf() on the int args.
     *   Covers SRS REQ-15 and UC-4 step 3.
     *
     * Input:
     *   - Launch GameOverFragment with power = 85, loyalty = 90, heat = 30
     *
     * Expected Results:
     *   - tv_final_power is displayed with text "85"
     *   - tv_final_loyalty is displayed with text "90"
     *   - tv_final_heat is displayed with text "30"
     */
    @Test
    public void testGameOverFragmentShowsCorrectFinalStats() {
        Bundle args = buildGameOverArgs(
                true, 85, 90, 30,
                "You secured the presidency.");

        FragmentScenario.launchInContainer(GameOverFragment.class, args);

        onView(withId(R.id.tv_final_power))
                .check(matches(isDisplayed()))
                .check(matches(withText("85")));

        onView(withId(R.id.tv_final_loyalty))
                .check(matches(isDisplayed()))
                .check(matches(withText("90")));

        onView(withId(R.id.tv_final_heat))
                .check(matches(isDisplayed()))
                .check(matches(withText("30")));
    }
}