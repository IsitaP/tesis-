package com.rebottle.app.model

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


object PerfilCache {

    private var uidActual: String? = null

    var nombre by mutableStateOf<String?>(null)
        private set

    var foto by mutableStateOf<ImageBitmap?>(null)
        private set

    fun limpiar() {
        uidActual = null
        nombre = null
        foto = null
    }

    fun actualizarFoto(nueva: ImageBitmap) {
        foto = nueva
    }

    fun cargar(uid: String) {
        if (uidActual == uid && nombre != null) return

        if (uidActual != uid) limpiar()
        uidActual = uid

        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (uidActual != uid) return@addOnSuccessListener

                nombre = doc.getString("name")

                val url = doc.getString("fotoUrl")
                val role = doc.getString("role")

                // Perfil público: lo lee el ranking
                if (!role.isNullOrEmpty()) {
                    Firebase.firestore.collection("perfilesPublicos").document(uid)
                        .set(
                            mapOf(
                                "name" to (nombre ?: ""),
                                "fotoUrl" to (url ?: ""),
                                "role" to role
                            ),
                            SetOptions.merge()
                        )
                }

                if (!url.isNullOrEmpty() && foto == null) {
                    runCatching { Firebase.storage.getReferenceFromUrl(url) }
                        .getOrNull()
                        ?.getBytes(2 * 1024 * 1024)
                        ?.addOnSuccessListener { bytes ->
                            if (uidActual != uid) return@addOnSuccessListener
                            foto = BitmapFactory
                                .decodeByteArray(bytes, 0, bytes.size)
                                .asImageBitmap()
                        }
                }
            }
    }
}