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
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        configurarNotificaciones()
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


    private val permisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permitido ->

            if (permitido) {
                Log.d(
                    "ReBottleFCM",
                    "Permiso de notificaciones concedido"
                )
            } else {
                Log.d(
                    "ReBottleFCM",
                    "Permiso de notificaciones denegado"
                )
            }
        }

    private fun configurarNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permisoConcedido =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!permisoConcedido) {
                permisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        // Registrar el dispositivo en Firebase Cloud Messaging
        FirebaseMessaging.getInstance().register()
            .addOnSuccessListener {
                Log.d(
                    "ReBottleFCM",
                    "Registro de FCM completado"
                )
            }
            .addOnFailureListener { error ->
                Log.e(
                    "ReBottleFCM",
                    "Error al registrar el dispositivo",
                    error
                )
            }
    }


}

