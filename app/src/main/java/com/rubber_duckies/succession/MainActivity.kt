package com.rubber_duckies.succession

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.rubber_duckies.succession.ui.MainMenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create FrameLayout programmatically
        val container = FrameLayout(this)
        container.id = android.R.id.content
        setContentView(container)

        // Load MainMenuFragment on app start
        if (savedInstanceState == null) {
            supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, MainMenuFragment())
            .commit()
        }

        //TODO: for final
        //add more unit tests to include the scenario, options, icon, heat/loyalty/power, points, arrows following
        //increase/deltas,
        //jose will update SDS to include the AI
        //raquel will do assignment 4
        //I will update SRS after Jose updates the SDS
        //add the design model to include AI in both SDS & SRS
        //update the README
        //i will add more unit tests for the scenarios

        //TODO: for the summer/capstone
        //fix the loading when AI is thinking the next scenario
        //fix the word text in the options to fit
        //fix the week # to fit in the phone
        //fix the top to not go over the date, time, wifi/data/roam heading
        //try to give the AI a different prompt as this is a presidential campaign and we're trying to secure presidency
        //so no blacklist
        //look into threshold for the blacklist -- make the window smaller? Heat
        //loyalty & power = 80 for presidency
        //heat <= 50
        //blacklist <= 70

    }
}