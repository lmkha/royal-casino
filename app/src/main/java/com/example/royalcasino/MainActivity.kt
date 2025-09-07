package com.example.royalcasino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.royalcasino.ui.screens.ThirteenGameScreen
import com.example.royalcasino.ui.theme.RoyalCasinoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoyalCasinoTheme {
                ThirteenGameScreen()
            }
        }
    }
}
