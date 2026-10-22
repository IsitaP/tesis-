package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


// ============================================================
// ESTADO DEL HOME DE FUNDACIÓN
// ============================================================

data class ProximaRutaHome(
    val rutaId: String,
    val lugar: String,
    val cuando: String,
    val usuario: String,
    val recolector: String,
    val enCurso: Boolean
)

data class HomeFundacionState(

    // Resumen de solicitudes
    val nuevas: Int = 0,
    val aceptadas: Int = 0,
    val completadasHoy: Int = 0,

    // Próxima ruta (null = no hay ninguna pendiente)
    val proximaRuta: ProximaRutaHome? = null,

    // Resumen del día
    val kgRecicladosHoy: Double = 0.0,
    val kgPorRecolectarHoy: Double = 0.0,

    val isLoading: Boolean = true
)


// ============================================================
// VIEWMODEL
// ============================================================

// Escucha en tiempo real:
//   solicitudes -> conteos y kg del día
//   rutas (pendientes) -> la próxima que sigue según fecha/hora
//   users -> nombre del usuario y del recolector de esa ruta
class HomeFundacionViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(HomeFundacionState())
    val state: StateFlow<HomeFundacionState> = _state.asStateFlow()

    private var solicitudesListener: ListenerRegistration? = null
    private var rutasListener: ListenerRegistration? = null

    private var solicitudesDocs: Map<String, DocumentSnapshot> = emptyMap()
    private var rutasDocs: List<DocumentSnapshot> = emptyList()

    // uid -> nombre, para no pedir el mismo usuario varias veces
    private val nombres = mutableMapOf<String, String>()
    private val nombresPedidos = mutableSetOf<String>()


    init {
        escuchar()
    }


    fun escuchar() {

        solicitudesListener?.remove()
        rutasListener?.remove()

        solicitudesListener =
            firestore
                .collection("solicitudes")
                .addSnapshotListener { snapshot, _ ->
                    solicitudesDocs = snapshot?.documents.orEmpty().associateBy { it.id }
                    recalcular()
                }

        rutasListener =
            firestore
                .collection("rutas")
                .whereIn(
                    "estado",
                    listOf(
                        EstadoRuta.PUBLICADA.name,
                        EstadoRuta.ASIGNADA.name,
                        EstadoRuta.EN_CURSO.name
                    )
                )
                .addSnapshotListener { snapshot, _ ->
                    rutasDocs = snapshot?.documents.orEmpty()
                    recalcular()
                }
    }


    private fun recalcular() {

        val hoy = LocalDate.now()
        val solicitudes = solicitudesDocs.values

        fun estado(doc: DocumentSnapshot) = doc.getString("estado").orEmpty()
        fun kg(doc: DocumentSnapshot) = (doc.getLong("pesoKg") ?: 0L).toDouble()

        // Nuevas: publicadas que todavía no están en ninguna ruta
        val nuevas = solicitudes.count {
            estado(it) == EstadoSolicitud.PUBLICADA.name && it.getString("rutaId") == null
        }

        // Aceptadas: ya metidas en una ruta pero sin recolectar
        val aceptadas = solicitudes.count {
            estado(it) == EstadoSolicitud.EN_RUTA.name ||
                    (estado(it) == EstadoSolicitud.PUBLICADA.name && it.getString("rutaId") != null)
        }

        val recolectadasHoy = solicitudes.filter {
            estado(it) == EstadoSolicitud.RECOLECTADA.name &&
                    it.getTimestamp("completadaEn")?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())?.toLocalDate() == hoy
        }

        // Lo que falta recoger hoy (programado para hoy y sin recolectar)
        val pendientesHoy = solicitudes.filter {
            it.getString("fechaRecoleccion") == hoy.toString() &&
                    (estado(it) == EstadoSolicitud.PUBLICADA.name ||
                            estado(it) == EstadoSolicitud.EN_RUTA.name)
        }

        _state.value =
            _state.value.copy(
                nuevas = nuevas,
                aceptadas = aceptadas,
                completadasHoy = recolectadasHoy.size,
                kgRecicladosHoy = recolectadasHoy.sumOf { kg(it) },
                kgPorRecolectarHoy = pendientesHoy.sumOf { kg(it) },
                proximaRuta = proximaRuta(),
                isLoading = false
            )
    }


    // ========================================================
    // PRÓXIMA RUTA
    // ========================================================

    // Si hay una ruta en curso, esa es la que sigue. Si no, la
    // primera que todavía no ha pasado: ej. rutas a las 9, 10 y 11
    // y son las 8 -> sale la de las 9.
    private fun proximaRuta(): ProximaRutaHome? {

        val ahora = LocalDateTime.now()

        val candidata =
            rutasDocs.firstOrNull { it.getString("estado") == EstadoRuta.EN_CURSO.name }
                ?: rutasDocs
                    .mapNotNull { doc -> momentoDe(doc)?.let { doc to it } }
                    .filter { (_, momento) -> !momento.isBefore(ahora) }
                    // Mismo momento (ej. sin hora): primero la creada antes
                    .sortedWith(
                        compareBy<Pair<DocumentSnapshot, LocalDateTime>> { it.second }
                            .thenBy { it.first.getTimestamp("creadaEn")?.seconds ?: 0L }
                    )
                    .firstOrNull()
                    ?.first
                ?: return null

        val paradas = idsDe(candidata)
        val primera = paradas.firstNotNullOfOrNull { solicitudesDocs[it] }

        val usuarioId = primera?.getString("usuarioId")
        val recolectorId = candidata.getString("recolectorId")

        listOfNotNull(usuarioId, recolectorId).forEach { pedirNombre(it) }

        val usuario = usuarioId?.let { nombres[it] } ?: "—"
        val extra = (paradas.size - 1).takeIf { it > 0 }?.let { " y $it más" }.orEmpty()

        return ProximaRutaHome(
            rutaId = candidata.id,
            lugar = primera?.getString("nombreLugar")?.ifBlank { null }
                ?: primera?.getString("direccion")?.ifBlank { null }
                ?: "Sin dirección",
            cuando = textoCuando(candidata),
            usuario = usuario + extra,
            recolector = recolectorId?.let { nombres[it] } ?: "Sin asignar",
            enCurso = candidata.getString("estado") == EstadoRuta.EN_CURSO.name
        )
    }


    // Fecha + hora de la ruta. Hoy solo se guarda la fecha; si la
    // ruta trae un campo "hora" también se tiene en cuenta. Sin
    // hora, la ruta cuenta hasta el final de su día.
    private fun momentoDe(ruta: DocumentSnapshot): LocalDateTime? {

        val fecha = runCatching {
            LocalDate.parse(ruta.getString("fechaRecoleccion"))
        }.getOrNull() ?: return null

        val hora = horaDe(ruta) ?: LocalTime.MAX

        return fecha.atTime(hora)
    }


    private fun horaDe(ruta: DocumentSnapshot): LocalTime? {

        val texto = ruta.getString("hora")?.trim()?.uppercase(Locale.US) ?: return null

        val formatos = listOf("H:mm", "HH:mm", "h:mm a", "h:mma", "h a")

        return formatos.firstNotNullOfOrNull { patron ->
            runCatching {
                LocalTime.parse(texto, DateTimeFormatter.ofPattern(patron, Locale.US))
            }.getOrNull()
        }
    }


    private fun textoCuando(ruta: DocumentSnapshot): String {

        if (ruta.getString("estado") == EstadoRuta.EN_CURSO.name) return "En curso"

        val fecha = runCatching {
            LocalDate.parse(ruta.getString("fechaRecoleccion"))
        }.getOrNull()

        val dia = when (fecha) {
            null -> "Sin fecha"
            LocalDate.now() -> "Hoy"
            LocalDate.now().plusDays(1) -> "Mañana"
            else -> fecha.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "CO")))
        }

        val hora = horaDe(ruta)?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))

        return if (hora != null) "$dia, $hora" else dia
    }


    private fun pedirNombre(uid: String) {

        if (!nombresPedidos.add(uid)) return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                nombres[uid] = doc.getString("name")?.ifBlank { null } ?: "—"
                _state.value = _state.value.copy(proximaRuta = proximaRuta())
            }
            .addOnFailureListener { nombresPedidos.remove(uid) }
    }


    private fun idsDe(ruta: DocumentSnapshot): List<String> {

        fun lista(campo: String): List<String> =
            (ruta.get(campo) as? List<*>)?.mapNotNull { it as? String }.orEmpty()

        return lista("ordenSolicitudes").ifEmpty { lista("solicitudesIds") }
    }


    override fun onCleared() {
        solicitudesListener?.remove()
        rutasListener?.remove()
        super.onCleared()
    }
}
