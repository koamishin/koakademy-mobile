package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.auth.InMemoryAppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val preferences = SharedPreferencesAppPreferences(applicationContext)

        setContent {
            App(preferences)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val preferences = remember { InMemoryAppPreferences(hasCompletedOnboarding = true) }
    App(preferences)
}
