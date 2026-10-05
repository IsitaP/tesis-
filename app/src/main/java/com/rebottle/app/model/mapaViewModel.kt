package com.rebottle.app.model

import com.google.firebase.Timestamp

enum class EstadoRuta {
    PUBLICADA,
    ASIGNADA,
    EN_CURSO,
    COMPLETADA,
    CANCELADA
}

data class Ruta(

    val id: String = "",

    val solicitudesIds: List<String> =
        emptyList(),

    val estado: String =
        EstadoRuta.PUBLICADA.name,

    val recolectorId: String? =
        null,

    val ordenSolicitudes: List<String> =
        emptyList(),

    val distanciaTotalMetros: Long? =
        null,

    val duracionTotalSegundos: Long? =
        null,

    val polyline: String? =
        null,

    val creadaEn: Timestamp? =
        null
)

