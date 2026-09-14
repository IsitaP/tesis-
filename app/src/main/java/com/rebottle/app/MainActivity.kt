package com.rebottle.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.rebottle.app.navigation.Navigation
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.screens.HomeUserScreen
import com.rebottle.app.ui.screens.auth.AuthFlowScreen
import com.rebottle.app.ui.screens.auth.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RebottleTheme {
                Navigation()
            }
        }
    }
}

