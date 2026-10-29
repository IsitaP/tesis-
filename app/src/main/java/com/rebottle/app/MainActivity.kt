
package com.rebottle.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging
import com.rebottle.app.navigation.Navigation
import com.rebottle.app.notificaciones.ReBottleMessagingService
import com.rebottle.app.ui.theme.RebottleTheme

class MainActivity : ComponentActivity() {

    // ---------- PERMISO DE NOTIFICACIONES ----------

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

    // ---------- ON CREATE ----------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Crear canal de notificaciones
        ReBottleMessagingService.crearCanal(this)

        // Solicitar permisos y obtener token FCM
        configurarNotificaciones()

        // ---------- GOOGLE PLACES ----------

        val appInfo = packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )

        val apiKey = appInfo.metaData
            ?.getString("com.rebottle.app.PLACES_API_KEY")
            .orEmpty()

        if (apiKey.isBlank()) {

            Log.e(
                "Places",
                "No se encontró la API Key de Places"
            )

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

        // ---------- JETPACK COMPOSE ----------

        setContent {
            RebottleTheme {
                Navigation()
            }
        }
    }

    // ---------- CONFIGURAR NOTIFICACIONES ----------

    private fun configurarNotificaciones() {

        // Solicitar permiso en Android 13 o superior
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

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

        // ---------- OBTENER TOKEN FCM ----------

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { tarea ->

                if (!tarea.isSuccessful) {

                    Log.e(
                        "ReBottleFCM",
                        "Error obteniendo token FCM",
                        tarea.exception
                    )

                    return@addOnCompleteListener
                }

                val token = tarea.result

                Log.d(
                    "ReBottleFCM",
                    "Token FCM obtenido: $token"
                )
            }
    }
}
