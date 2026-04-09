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
        //I will update SRS after Jose updates the SDS
        //add the design model to include AI in both SDS & SRS

        //TODO: for the summer/capstone
        //fix the loading when AI is thinking the next scenario
        //fix the word text in the options to fit
        //fix the week # to fit in the phone
        //fix the top to not go over the date, time, wifi/data/roam heading
        //try to give the AI a different prompt as this is a presidential campaign and we're trying to secure presidency
        //so no blacklist

    }
}