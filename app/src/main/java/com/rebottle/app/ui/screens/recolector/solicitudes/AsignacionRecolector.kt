package com.rebottle.app.ui.screens.recolector.solicitudes

import com.rebottle.app.model.EstadoParada
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.ParadaRuta
data class AsignacionRecolector(
    val id: String,
    val codigo: String,
    val estado: EstadoAsignacion,
    val direccion: String,
    val fecha: String,
    val hora: String,
    val materiales: List<String>,
    val pesoKg: Int,
    val usuario: String
)

enum class EstadoAsignacion {
    PENDIENTE,
    COMPLETADA,
    CANCELADA
}

// Datos de ejemplo — cuando conectes Firestore, esta lista
// se reemplaza por lo que traiga el ViewModel/repositorio.
val asignacionesEjemplo = listOf(
    AsignacionRecolector(
        id = "1",
        codigo = "#RB-20261",
        estado = EstadoAsignacion.COMPLETADA,
        direccion = "Cll 131a # 53- 31",
        fecha = "Septiembre 12",
        hora = "9:00 AM",
        materiales = listOf("Metal", "Vidrio"),
        pesoKg = 3,
        usuario = "Juan Pérez"
    ),
    AsignacionRecolector(
        id = "2",
        codigo = "#RB-20262",
        estado = EstadoAsignacion.PENDIENTE,
        direccion = "Cll 114 # 60-47",
        fecha = "Septiembre 12",
        hora = "9:30 AM",
        materiales = listOf("Metal", "Vidrio"),
        pesoKg = 8,
        usuario = "Ana Ríos"
    ),
    AsignacionRecolector(
        id = "3",
        codigo = "#RB-20263",
        estado = EstadoAsignacion.PENDIENTE,
        direccion = "Cll 94a # 50-15",
        fecha = "Septiembre 12",
        hora = "10:00 AM",
        materiales = listOf("Plástico"),
        pesoKg = 3,
        usuario = "Carlos Vera"
    )
)

// ----------------------------------------------------
// ADAPTADOR: parada real (Firestore) -> modelo de la UI
// ----------------------------------------------------

fun ParadaRuta.aAsignacion(fecha: String): AsignacionRecolector =
    AsignacionRecolector(
        id = solicitudId,
        codigo = "#$solicitudId",
        estado = when (estado) {
            EstadoParada.COMPLETADA -> EstadoAsignacion.COMPLETADA
            EstadoParada.CANCELADA -> EstadoAsignacion.CANCELADA
            EstadoParada.PENDIENTE -> EstadoAsignacion.PENDIENTE
        },
        direccion = direccion,
        fecha = fecha,
        hora = "",          // todavía no se guarda la hora por parada
        materiales = materiales.map { nombre ->
            MaterialType.values().firstOrNull { it.name == nombre }?.label ?: nombre
        },
        pesoKg = pesoKg,
        usuario = ""
    )

fun formatearFecha(iso: String): String =
    runCatching {
        val fecha = java.time.LocalDate.parse(iso)
        val mes = fecha.month.getDisplayName(
            java.time.format.TextStyle.FULL,
            java.util.Locale.forLanguageTag("es-CO")
        )
        "${mes.replaceFirstChar { it.uppercase() }} ${fecha.dayOfMonth}"
    }.getOrDefault(iso)