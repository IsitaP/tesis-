package com.rebottle.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.screens.auth.AuthFlowScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RebottleTheme {
                AuthFlowScreen()
            }
        }
    }
}

