
package com.rebottle.app.notificaciones

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ReBottleMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "ReBottleFCM"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "Nuevo token FCM: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title ?: "ReBottle"
        val contenido = message.notification?.body ?: ""

        Log.d(TAG, "Notificación recibida")
        Log.d(TAG, "Título: $titulo")
        Log.d(TAG, "Mensaje: $contenido")
    }
}
