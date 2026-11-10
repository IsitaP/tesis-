package com.rebottle.app.integracion

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rebottle.app.integracion.EmuladorFirebase.esperar
import com.rebottle.app.model.EstadoSolicitud
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.ui.screens.auth.UserRole
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Pruebas de integración del ciclo de vida de una solicitud:
 * SolicitudViewModel + Firebase Auth + Cloud Firestore (emuladores).
 */
@RunWith(AndroidJUnit4::class)
class SolicitudIntegracionTest {

    private val firestore get() = FirebaseFirestore.getInstance()

    private val store = ViewModelStore()

    private lateinit var ciudadano: UsuarioPrueba

    @Before
    fun prepararEmulador() {
        EmuladorFirebase.configurar()
        EmuladorFirebase.limpiarDatos()
        ciudadano = EmuladorFirebase.crearUsuario(UserRole.CIUDADANO.firestoreValue)
    }

    @After
    fun liberar() {
        // Llama a onCleared() y quita el listener de Firestore.
        store.clear()
        FirebaseAuth.getInstance().signOut()
    }

    // El ViewModel se crea después de iniciar sesión porque en su
    // init empieza a escuchar las solicitudes del usuario actual.
    private fun nuevoViewModel(): SolicitudViewModel =
        ViewModelProvider(store, ViewModelProvider.NewInstanceFactory())[SolicitudViewModel::class.java]

    private fun SolicitudViewModel.llenarFormulario(
        pesoKg: Int = 20,
        fecha: LocalDate = LocalDate.now().plusDays(3)
    ) {
        updateLocationFromPlaces(
            nombreLugar = "Parque de prueba",
            direccion = "Calle 100 # 15-20, Bogotá",
            placeId = "place-prueba-1",
            latitud = 4.6867,
            longitud = -74.0480
        )
        toggleMaterial(MaterialType.PLASTICO)
        toggleMaterial(MaterialType.VIDRIO)
        selectDate(fecha)
        // El peso inicial es 20 kg; se ajusta con los botones +/-.
        while (state.value.weightKg < pesoKg) incrementWeight()
        while (state.value.weightKg > pesoKg) decrementWeight()
    }

    private fun SolicitudViewModel.crearYEsperar(): String {
        val resultado = EmuladorFirebase.esperarCallback<String> { ok, error ->
            crearSolicitud(onSuccess = ok, onError = error)
        }
        check(resultado is Resultado.Exito) { "crearSolicitud falló: $resultado" }
        return resultado.valor
    }

    private fun leerSolicitud(id: String) =
        firestore.collection("solicitudes").document(id).get().esperar()

    @Test
    fun I01_crear_solicitud_la_guarda_publicada_en_firestore() {
        val viewModel = nuevoViewModel()
        val fecha = LocalDate.now().plusDays(3)
        viewModel.llenarFormulario(pesoKg = 20, fecha = fecha)

        val id = viewModel.crearYEsperar()

        val doc = leerSolicitud(id)
        assertTrue("La solicitud debe existir en Firestore", doc.exists())
        assertEquals("RB-0001", id)
        assertEquals(ciudadano.uid, doc.getString("usuarioId"))
        assertEquals(EstadoSolicitud.PUBLICADA.name, doc.getString("estado"))
        assertEquals(20L, doc.getLong("pesoKg"))
        assertEquals(fecha.toString(), doc.getString("fechaRecoleccion"))
        assertEquals("place-prueba-1", doc.getString("placeId"))
        assertEquals(
            setOf("PLASTICO", "VIDRIO"),
            (doc.get("materiales") as List<*>).toSet()
        )
        assertTrue("rutaId debe empezar en null", doc.contains("rutaId"))
        assertEquals(null, doc.get("rutaId"))
        assertNotNull("creadaEn lo pone el servidor", doc.getTimestamp("creadaEn"))
        assertFalse("El formulario deja de cargar", viewModel.state.value.isLoading)
    }

    @Test
    fun I02_los_ids_de_solicitud_son_consecutivos() {
        val viewModel = nuevoViewModel()
        viewModel.llenarFormulario()

        val primero = viewModel.crearYEsperar()
        val segundo = viewModel.crearYEsperar()

        assertEquals("RB-0001", primero)
        assertEquals("RB-0002", segundo)
        val contador = firestore.collection("contadores")
            .document("solicitudes").get().esperar()
        assertEquals(2L, contador.getLong("ultimoId"))
    }

    @Test
    fun I03_sin_sesion_iniciada_no_se_crea_la_solicitud() {
        FirebaseAuth.getInstance().signOut()
        val viewModel = nuevoViewModel()
        viewModel.llenarFormulario()

        val resultado = EmuladorFirebase.esperarCallback<String> { ok, error ->
            viewModel.crearSolicitud(onSuccess = ok, onError = error)
        }

        assertEquals(Resultado.Error("No hay un usuario autenticado"), resultado)
        // Se vuelve a entrar solo para poder leer la colección.
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(ciudadano.correo, ciudadano.contrasena)
            .esperar()
        val solicitudes = firestore.collection("solicitudes").get().esperar()
        assertTrue("No debe haber solicitudes", solicitudes.isEmpty)
    }

    @Test
    fun I04_la_solicitud_creada_aparece_en_el_historial_en_tiempo_real() {
        val viewModel = nuevoViewModel()
        viewModel.llenarFormulario()

        val id = viewModel.crearYEsperar()

        EmuladorFirebase.esperarHasta("el historial muestre $id") {
            viewModel.state.value.solicitudes.any { it.id == id }
        }
        val enHistorial = viewModel.state.value.solicitudes.first { it.id == id }
        assertEquals(EstadoSolicitud.PUBLICADA.name, enHistorial.estado)
        assertFalse(viewModel.state.value.isHistoryLoading)
    }

    @Test
    fun I05_cancelar_solicitud_cambia_su_estado_a_cancelada() {
        val viewModel = nuevoViewModel()
        viewModel.llenarFormulario()
        val id = viewModel.crearYEsperar()

        val resultado = EmuladorFirebase.esperarCallback<Unit> { ok, error ->
            viewModel.cancelarSolicitud(id, onSuccess = { ok(Unit) }, onError = error)
        }

        assertEquals(Resultado.Exito(Unit), resultado)
        assertEquals(EstadoSolicitud.CANCELADA.name, leerSolicitud(id).getString("estado"))
    }

    @Test
    fun I06_marcar_recolectada_otorga_puntos_una_sola_vez() {
        val viewModel = nuevoViewModel()
        viewModel.llenarFormulario(pesoKg = 12)
        val id = viewModel.crearYEsperar()

        repeat(2) {
            val resultado = EmuladorFirebase.esperarCallback<Unit> { ok, error ->
                viewModel.marcarRecolectada(id, onSuccess = { ok(Unit) }, onError = error)
            }
            assertEquals(Resultado.Exito(Unit), resultado)
        }

        val solicitud = leerSolicitud(id)
        assertEquals(EstadoSolicitud.RECOLECTADA.name, solicitud.getString("estado"))
        assertEquals(true, solicitud.getBoolean("puntosOtorgados"))
        assertEquals(60L, solicitud.getLong("puntosGanados")) // 12 kg x 5
        assertNotNull(solicitud.getTimestamp("completadaEn"))

        val progreso = firestore.collection("progresoAprendizaje")
            .document(ciudadano.uid).get().esperar()
        assertEquals(
            "La segunda llamada no debe volver a sumar",
            60L,
            progreso.getLong("puntosTotales")
        )
    }
}
