package com.example.yummiziapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Testing your design system access pattern:
                    Text(
                        text = "Welcome to Yummizi!",
                        style = YummiziTheme.typography.teacherFontVar2,
                        color = YummiziTheme.colors.yummziziDarkOrange
                    )
                }
            }
        }
    }
}