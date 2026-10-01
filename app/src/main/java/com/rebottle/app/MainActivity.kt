package com.rebottle.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.libraries.places.api.Places
import com.rebottle.app.navigation.Navigation
import com.rebottle.app.ui.theme.RebottleTheme
import android.content.pm.PackageManager
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appInfo = packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )

        val apiKey = appInfo.metaData
            ?.getString("com.rebottle.app.PLACES_API_KEY")
            .orEmpty()

        if (apiKey.isBlank()) {
            Log.e("Places", "No se encontró la API Key de Places")
        } else {

            Places.initializeWithNewPlacesApiEnabled(
                applicationContext,
                apiKey
            )

            Places.createClient(this)

            Log.d(
                "Places",
                "Google Places inicializado correctamente"
            )
        }

        setContent {
            RebottleTheme {
                Navigation()
            }
        }
    }
}

