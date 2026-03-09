package com.rubber_duckies.succession;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.rubber_duckies.succession.ui.HelpFragment;
import com.rubber_duckies.succession.ui.MainMenuFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * MainMenuFragmentTest
 *
 * UI unit tests for MainMenuFragment and HelpFragment.
 * Uses FragmentScenario to launch each fragment in isolation (no Activity required).
 * Uses Espresso to inspect views and simulate user interactions.
 *
 * Place this file in:
 *   app/src/androidTest/java/com/rubber_duckies/succession/MainMenuFragmentTest.java
 *
 * Required dependencies in build.gradle:
 *   androidTestImplementation "androidx.fragment:fragment-testing:1.6.1"
 *   androidTestImplementation "androidx.test.espresso:espresso-core:3.5.1"
 *   androidTestImplementation "androidx.test.ext:junit:1.1.5"
 *
 * Tests covered:
 *   TC-1  Main menu displays exactly 3 option buttons
 *   TC-2  The 3 buttons have the correct text: START, HELP, EXIT
 *   TC-3  The title "THE SUCCESSION" is displayed at the top
 *   TC-4  The upside-down flag ImageView is present on screen
 *   TC-5  Tapping HELP navigates to HelpFragment and shows instructions
 *   TC-6  The HelpFragment has a "GO BACK" button
 *   TC-7  Tapping EXIT shows a confirmation dialog and closes the app on confirm
 */
@RunWith(AndroidJUnit4.class)
public class MainMenuFragmentTest {
    // -------------------------------------------------------------------------
    // TC-1: Main menu shows exactly 3 option buttons
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btnStart, btnHelp, btnExit in fragment_main_menu.xml
     *
     * Short Description:
     *   Verifies that all three action buttons (START, HELP, EXIT) are visible
     *   on the main menu screen simultaneously. The SRS requires exactly 3 options
     *   to be displayed (REQ-2).
     *
     * Input: Launch MainMenuFragment with no arguments.
     *
     * Expected Results:
     *   - btnStart is displayed
     *   - btnHelp  is displayed
     *   - btnExit  is displayed
     */
    @Test
    public void testMainMenuShowsThreeButtons() {
        FragmentScenario.launchInContainer(MainMenuFragment.class);

        onView(withId(R.id.btnStart)).check(matches(isDisplayed()));
        onView(withId(R.id.btnHelp)).check(matches(isDisplayed()));
        onView(withId(R.id.btnExit)).check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-2: The 3 buttons display the correct text: START, HELP, EXIT
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btnStart, btnHelp, btnExit text values
     *
     * Short Description:
     *   Verifies that each of the three buttons shows the exact label text
     *   defined in strings.xml — "START", "HELP", and "EXIT" — as required
     *   by the SRS (REQ-2). Text is pulled from R.string references to match
     *   whatever locale/value is defined in strings.xml.
     *
     * Input: Launch MainMenuFragment with no arguments.
     *
     * Expected Results:
     *   - btnStart text == R.string.start  ("START")
     *   - btnHelp  text == R.string.help   ("HELP")
     *   - btnExit  text == R.string.exit   ("EXIT")
     */
    @Test
    public void testMainMenuButtonsHaveCorrectText() {
        FragmentScenario.launchInContainer(MainMenuFragment.class);

        onView(withId(R.id.btnStart)).check(matches(withText(R.string.start)));
        onView(withId(R.id.btnHelp)).check(matches(withText(R.string.help)));
        onView(withId(R.id.btnExit)).check(matches(withText(R.string.exit)));
    }

    // -------------------------------------------------------------------------
    // TC-3: Main menu shows the title "THE SUCCESSION" at the top
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: tvTitle (TextView) in fragment_main_menu.xml
     *
     * Short Description:
     *   Verifies that the game title TextView with id tvTitle is visible and
     *   contains the text matching R.string.the_succession ("THE SUCCESSION").
     *   The title must appear at the top of the screen above the flag image.
     *
     * Input: Launch MainMenuFragment with no arguments.
     *
     * Expected Results:
     *   - tvTitle is displayed
     *   - tvTitle text == R.string.the_succession ("THE SUCCESSION")
     */
    @Test
    public void testMainMenuShowsTitle() {
        FragmentScenario.launchInContainer(MainMenuFragment.class);

        onView(withId(R.id.tvTitle))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.the_succession)));
    }

    // -------------------------------------------------------------------------
    // TC-4: Main menu shows the upside-down flag image in the middle
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: ImageView with src @drawable/flag and rotation=180
     *                           in fragment_main_menu.xml
     *
     * Short Description:
     *   Verifies that the flag ImageView is present and visible on the main menu.
     *   The image is rendered with android:rotation="180" in the XML (upside-down).
     *   We identify it via its contentDescription attribute which is set to
     *   @string/flag in the layout.
     *
     * Input: Launch MainMenuFragment with no arguments.
     *
     * Expected Results:
     *   - The ImageView with contentDescription R.string.flag is displayed
     */
    @Test
    public void testMainMenuShowsUpsideDownFlag() {
        FragmentScenario.launchInContainer(MainMenuFragment.class);

        onView(withContentDescription(R.string.flag))
                .check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-5: Tapping HELP navigates to HelpFragment and shows instructions
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btnHelp click listener in MainMenuFragment.java,
     *                           tv_help_title and scroll_content in fragment_help.xml
     *
     * Short Description:
     *   Simulates a tap on the HELP button and verifies that HelpFragment is loaded.
     *   The click listener in MainMenuFragment calls:
     *     getSupportFragmentManager().beginTransaction()
     *       .replace(android.R.id.content, new HelpFragment())
     *       .addToBackStack("help").commit()
     *   We check that the help title and scrollable instructions area are visible.
     *   Covers SRS REQ-4 and UC-2.
     *
     * Input:
     *   - Launch MainMenuFragment
     *   - Perform click on btnHelp
     *
     * Expected Results:
     *   - tv_help_title is displayed with text matching R.string.how2play
     *   - scroll_content (instructions area) is displayed
     */
    @Test
    public void testHelpButtonNavigatesToHelpScreen() {
        FragmentScenario.launchInContainer(MainMenuFragment.class);

        onView(withId(R.id.btnHelp)).perform(click());

        // HelpFragment is now in the container — verify title and instructions are shown
        onView(withId(R.id.tv_help_title))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.how2play)));

        onView(withId(R.id.scroll_content))
                .check(matches(isDisplayed()));
    }

    // -------------------------------------------------------------------------
    // TC-6: HelpFragment has a "GO BACK" button
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btn_back (Button) in fragment_help.xml,
     *                           setupClickListeners() in HelpFragment.java
     *
     * Short Description:
     *   Verifies that the HelpFragment contains a visible button labelled "GO BACK"
     *   (matching R.string.go_back). The button's click listener is wired to
     *   getSupportFragmentManager().popBackStack() in HelpFragment.setupClickListeners().
     *   Covers SRS REQ-32 and UC-2 step 5.
     *
     * Input: Launch HelpFragment directly in isolation.
     *
     * Expected Results:
     *   - btn_back is displayed
     *   - btn_back text == R.string.go_back ("GO BACK")
     */
    @Test
    public void testHelpFragmentHasGoBackButton() {
        FragmentScenario.launchInContainer(HelpFragment.class);

        onView(withId(R.id.btn_back))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.go_back)));
    }

    // -------------------------------------------------------------------------
    // TC-7: Tapping EXIT shows confirmation dialog and closes the app
    // -------------------------------------------------------------------------
    /**
     * Method/View being tested: btnExit click listener in MainMenuFragment.java,
     *                           AlertDialog "Exit The Succession"
     *
     * Short Description:
     *   Simulates a tap on the EXIT button and verifies that a confirmation
     *   AlertDialog appears with the title "Exit The Succession". The dialog has
     *   a "Yes" button that calls viewModel.onExitApp() and requireActivity().finish().
     *   We verify:
     *     (a) The dialog appears with the correct title after the tap
     *     (b) The "Yes" confirm button is present and clickable
     *     (c) After clicking "Yes", the Activity is finishing
     *   Covers SRS REQ-5 and UC-6.
     *
     * Input:
     *   - Launch MainMenuFragment
     *   - Perform click on btnExit
     *   - Perform click on dialog "Yes" button
     *
     * Expected Results:
     *   - AlertDialog with title "Exit The Succession" is displayed
     *   - "Yes" button is present in the dialog
     *   - Activity.isFinishing() returns true after confirming
     */
    @Test
    public void testExitButtonShowsConfirmationDialogAndClosesApp() {
        FragmentScenario<MainMenuFragment> scenario = FragmentScenario.launchInContainer(MainMenuFragment.class);

        // Tap EXIT — this triggers the AlertDialog in MainMenuFragment
        onView(withId(R.id.btnExit)).perform(click());

        // Verify the dialog title is shown
        onView(withText("Exit The Succession"))
                .check(matches(isDisplayed()));

        // Verify the "Yes" confirm button is shown
        onView(withText("Yes"))
                .check(matches(isDisplayed()));

        // Tap "Yes" to confirm exit
        onView(withText("Yes")).perform(click());

        // Verify the Activity is finishing after confirming
        scenario.onFragment(fragment ->
                assertTrue(
                        "Expected Activity to be finishing after tapping Yes on exit dialog",
                        fragment.requireActivity().isFinishing()
                )
        );
    }
}
