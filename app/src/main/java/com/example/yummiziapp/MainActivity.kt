package com.example.yummiziapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.yummiziapp.core.designsystem.theme.YummiziTheme
import com.example.yummiziapp.ui.theme.YummiziAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YummiziAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // 🛡️ Automatically detects top status bar, bottom navigation pill, and camera notches!
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    // Hook your centralized router engine up to the structural padding coordinates
                    YummiziNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // 🚀 This shields EVERY screen in your app automatically!
                            .background(YummiziTheme.colors.yummiziWhite)
                    )
                }
            }
        }
    }
}