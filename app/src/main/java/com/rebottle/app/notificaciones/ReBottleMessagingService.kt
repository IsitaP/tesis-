
package com.rebottle.app.notificaciones

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReBottleMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "ReBottleFCM"
    }

    // Se ejecuta cuando el dispositivo queda registrado en FCM.
    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)

        Log.d(
            TAG,
            "Dispositivo registrado. FID: $installationId"
        )
    }

    // Recibe mensajes enviados desde Firebase.
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title ?: "ReBottle"
        val contenido = message.notification?.body ?: ""

        Log.d(TAG, "Notificación recibida")
        Log.d(TAG, "Título: $titulo")
        Log.d(TAG, "Mensaje: $contenido")
        Log.d(TAG, "Datos: ${message.data}")
    }
}
