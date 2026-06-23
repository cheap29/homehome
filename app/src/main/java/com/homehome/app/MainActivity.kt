package com.homehome.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.homehome.app.navigation.AppNavGraph
import com.homehome.app.ui.theme.HomehomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as HomehomeApplication).repository
        setContent {
            HomehomeTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController, repository = repository)
            }
        }
    }
}
