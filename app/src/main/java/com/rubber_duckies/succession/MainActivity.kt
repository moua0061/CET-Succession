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
    }
}