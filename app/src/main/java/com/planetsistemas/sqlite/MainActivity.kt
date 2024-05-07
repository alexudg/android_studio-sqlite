package com.planetsistemas.sqlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.planetsistemas.sqlite.ui.screen.MainScreen
import com.planetsistemas.sqlite.ui.theme.GlobalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash Screen
        var isKeepSplashScreen = false

        installSplashScreen().setKeepOnScreenCondition { isKeepSplashScreen }

        setContent {
            GlobalTheme {
                MainScreen(this)
            }
        }
    }
}