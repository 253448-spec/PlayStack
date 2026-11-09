package com.upchiapas.videojuegos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.upchiapas.videojuegos.navigation.AppNavHost
import com.upchiapas.videojuegos.ui.theme.VideojuegosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as KtApplication
        val isLoggedIn = app.container.sessionManager.isLoggedIn()

        setContent {
            VideojuegosTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }
}
