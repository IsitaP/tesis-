package com.rebottle.app.integracion

import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.firestoreSettings
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Conecta la app al Firebase Emulator Suite durante las pruebas de
 * integración. Nada de lo que hacen estas pruebas llega a producción.
 *
 * Antes de correrlas hay que levantar los emuladores:
 *   cd functions && npm run emuladores
 *
 * Desde el emulador de Android el PC es 10.0.2.2. En un dispositivo
 * físico se usa `adb reverse tcp:8080 tcp:8080` y
 * `adb reverse tcp:9099 tcp:9099` y se pasa el host con:
 *   ./gradlew connectedDebugAndroidTest
 *       -Pandroid.testInstrumentationRunnerArguments.emuladorHost=127.0.0.1
 */
object EmuladorFirebase {

    const val PUERTO_FIRESTORE = 8080
    const val PUERTO_AUTH = 9099
    const val CONTRASENA_PRUEBA = "Prueba123*"

    private const val TIEMPO_ESPERA_SEG = 20L

    private var configurado = false

    val host: String
        get() = InstrumentationRegistry.getArguments()
            .getString("emuladorHost") ?: "10.0.2.2"

    private val proyecto: String
        get() = FirebaseApp.getInstance().options.projectId
            ?: error("google-services.json no tiene project_id")

    /** Debe llamarse antes de cualquier uso de Firestore o Auth. */
    @Synchronized
    fun configurar() {
        if (configurado) return

        val firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator(host, PUERTO_FIRESTORE)
        // Sin caché en disco: cada prueba lee lo que hay en el emulador.
        firestore.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {})
        }

        FirebaseAuth.getInstance().useEmulator(host, PUERTO_AUTH)

        configurado = true
    }

    /** Borra todos los documentos y usuarios de los emuladores. */
    fun limpiarDatos() {
        FirebaseAuth.getInstance().signOut()
        borrar(
            "http://$host:$PUERTO_FIRESTORE/emulator/v1/projects/" +
                "$proyecto/databases/(default)/documents"
        )
        borrar(
            "http://$host:$PUERTO_AUTH/emulator/v1/projects/" +
                "$proyecto/accounts"
        )
    }

    /**
     * Crea un usuario en el emulador de Auth con su documento en
     * `users/{uid}` y lo deja con la sesión iniciada.
     */
    fun crearUsuario(
        rol: String,
        nombre: String = "Usuario de prueba"
    ): UsuarioPrueba {
        val correo = "prueba-${UUID.randomUUID().toString().take(8)}@rebottle.test"

        val resultado = FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(correo, CONTRASENA_PRUEBA)
            .esperar()

        val uid = resultado.user!!.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(
                mapOf(
                    "name" to nombre,
                    "email" to correo,
                    "role" to rol
                )
            )
            .esperar()

        return UsuarioPrueba(uid, correo, CONTRASENA_PRUEBA)
    }

    private fun borrar(url: String) {
        val conexion = URL(url).openConnection() as HttpURLConnection
        try {
            conexion.requestMethod = "DELETE"
            val codigo = conexion.responseCode
            check(codigo in 200..299) {
                "No se pudo limpiar el emulador ($codigo) en $url. " +
                    "¿Están corriendo los emuladores de Firebase?"
            }
        } finally {
            conexion.disconnect()
        }
    }

    /** Bloquea el hilo de la prueba hasta que termine la tarea de Firebase. */
    fun <T> Task<T>.esperar(): T =
        Tasks.await(this, TIEMPO_ESPERA_SEG, TimeUnit.SECONDS)

    /**
     * Ejecuta una función de la app que responde por callbacks
     * (onSuccess / onError) y espera el resultado.
     */
    fun <T> esperarCallback(
        accion: (onSuccess: (T) -> Unit, onError: (String) -> Unit) -> Unit
    ): Resultado<T> {
        val latch = CountDownLatch(1)
        val resultado = AtomicReference<Resultado<T>>()

        accion(
            { valor -> resultado.set(Resultado.Exito(valor)); latch.countDown() },
            { mensaje -> resultado.set(Resultado.Error(mensaje)); latch.countDown() }
        )

        check(latch.await(TIEMPO_ESPERA_SEG, TimeUnit.SECONDS)) {
            "La operación no respondió en $TIEMPO_ESPERA_SEG s"
        }
        return resultado.get()
    }

    /** Repite la condición hasta que se cumpla o se acabe el tiempo. */
    fun esperarHasta(
        descripcion: String,
        condicion: () -> Boolean
    ) {
        val limite = System.currentTimeMillis() + TIEMPO_ESPERA_SEG * 1000
        while (System.currentTimeMillis() < limite) {
            if (condicion()) return
            Thread.sleep(100)
        }
        throw AssertionError("No se cumplió a tiempo: $descripcion")
    }
}

data class UsuarioPrueba(
    val uid: String,
    val correo: String,
    val contrasena: String
)

sealed class Resultado<out T> {
    data class Exito<T>(val valor: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}
