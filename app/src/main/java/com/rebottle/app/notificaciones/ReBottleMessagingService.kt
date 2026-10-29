
package com.rebottle.app.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rebottle.app.MainActivity

class ReBottleMessagingService : FirebaseMessagingService() {

    companion object {

        private const val TAG = "ReBottleFCM"

        const val CANAL_ID = "rebottle_rutas"

        // Crear el canal de notificaciones
        fun crearCanal(context: Context) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val canal = NotificationChannel(
                    CANAL_ID,
                    "Notificaciones de ReBottle",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description =
                        "Avisos sobre rutas y solicitudes de recolección"
                }

                val administrador =
                    context.getSystemService(
                        NotificationManager::class.java
                    )

                administrador.createNotificationChannel(canal)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        crearCanal(this)
    }

    // ---------- TOKEN FCM ----------

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "Nuevo token FCM: $token")
    }

    // ---------- RECIBIR NOTIFICACIONES ----------

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title
            ?: message.data["titulo"]
            ?: "ReBottle"

        val contenido = message.notification?.body
            ?: message.data["mensaje"]
            ?: "Tienes una nueva notificación"

        Log.d(TAG, "Notificación recibida")
        Log.d(TAG, "Título: $titulo")
        Log.d(TAG, "Mensaje: $contenido")

        mostrarNotificacion(titulo, contenido)
    }


    override fun onRegistered(installationId: String) {
        val usuario = FirebaseAuth.getInstance().currentUser ?: return

        val datos = hashMapOf<String, Any>(
            "installationId" to installationId,
            "actualizadoEn" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("fcmRegistros")
            .document(usuario.uid)
            .set(datos)
            .addOnSuccessListener {
                Log.d("ReBottleFCM", "Dispositivo registrado")
            }
            .addOnFailureListener { error ->
                Log.e("ReBottleFCM", "Error al registrar dispositivo", error)
            }
    }

    // ---------- MOSTRAR NOTIFICACIÓN ----------

    private fun mostrarNotificacion(
        titulo: String,
        contenido: String
    ) {

        // Comprobar permiso en Android 13+
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No hay permiso de notificaciones")
            return
        }

        val administrador = NotificationManagerCompat.from(this)

        if (!administrador.areNotificationsEnabled()) {
            Log.d(TAG, "Las notificaciones están desactivadas")
            return
        }

        // Abrir ReBottle al tocar la notificación
        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notificacion = NotificationCompat.Builder(
            this,
            CANAL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contenido)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Identificador de notificación
        val notificacionId = System.currentTimeMillis().toInt()

        administrador.notify(
            notificacionId,
            notificacion
        )

        Log.d(TAG, "Notificación mostrada correctamente")
    }
}
