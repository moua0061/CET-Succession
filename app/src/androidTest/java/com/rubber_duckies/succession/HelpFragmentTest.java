package com.rubber_duckies.succession;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.rubber_duckies.succession.ui.HelpFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests covered:
 *   TC-8   HelpFragment displays the "HOW TO PLAY" title
 *   TC-9   HelpFragment displays the scrollable instructions area
 *   TC-10  Tapping GO BACK in HelpFragment pops the fragment
 */
@RunWith(AndroidJUnit4.class)
public class HelpFragmentTest {

    // -------------------------------------------------------------------------
    // TC-8: HelpFragment displays the "HOW TO PLAY" title
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tv_help_title (TextView) in fragment_help.xml
     *
     * Short Description:
     *   Verifies that when HelpFragment is launched in isolation, the title
     *   TextView is visible and displays the correct heading text matching
     *   R.string.how2play ("HOW TO PLAY"). Covers SRS REQ-31 and UC-2 step 3.
     *
     * Input: Launch HelpFragment with no arguments.
     *
     * Expected Results:
     *   - tv_help_title is displayed
     *   - tv_help_title text == R.string.how2play ("HOW TO PLAY")
     */
    @Test
    public void testHelpFragmentShowsTitle() {
        FragmentScenario.launchInContainer(HelpFragment.class);

        onView(withId(R.id.tv_help_title))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.how2play)));
    }

    // -------------------------------------------------------------------------
    // TC-9: HelpFragment displays the scrollable instructions area
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: scroll_content (ScrollView) in fragment_help.xml
     *
     * Short Description:
     *   Verifies that the scrollable instructions area is visible when HelpFragment
     *   is launched. This area contains the game mechanics explanation required
     *   by SRS REQ-31, including Power, Loyalty, Heat stats, choice types, Phase 1
     *   victory goal, and Blacklisted State explanation.
     *
     * Input: Launch HelpFragment with no arguments.
     *
     * Expected Results:
     *   - scroll_content is displayed
     */
    @Test
    public void testHelpFragmentShowsInstructionsArea() {
        FragmentScenario.launchInContainer(HelpFragment.class);

        onView(withId(R.id.scroll_content))
                .check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-10: Tapping GO BACK in HelpFragment pops the fragment
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btn_back click listener in HelpFragment.java,
     *                           setupClickListeners() -> popBackStack()
     *
     * Short Description:
     *   Verifies that tapping the GO BACK button calls popBackStack() and the
     *   HelpFragment's host activity begins finishing as a result. The click
     *   listener in HelpFragment.setupClickListeners() calls:
     *     getActivity().getSupportFragmentManager().popBackStack()
     *   When launched in isolation via FragmentScenario with nothing on the back
     *   stack, popping causes the host activity to finish.
     *   Covers SRS REQ-32 and UC-2 steps 5-6.
     *
     * Input:
     *   - Launch HelpFragment in isolation with no arguments
     *   - Perform click on btn_back
     *
     * Expected Results:
     *   - btn_back is displayed with text matching R.string.go_back ("GO BACK")
     *   - After clicking, the host activity begins finishing
     */
    @Test
    public void testHelpFragmentGoBackButtonPopsFragment() {
        FragmentScenario<HelpFragment> scenario =
                FragmentScenario.launchInContainer(HelpFragment.class);

        onView(withId(R.id.btn_back))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.go_back)))
                .perform(click());

        scenario.onFragment(fragment ->
                assertTrue(
                        "Expected Activity to be finishing after tapping GO BACK",
                        fragment.requireActivity().isFinishing()
                )
        );
    }
}