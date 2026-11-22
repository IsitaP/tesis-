package com.rebottle.app.logica

import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * Progreso automático de una ruta de recolección.
 *
 * La ruta sale de la dirección de la fundación. Se estima cuánto tarda
 * una persona caminando hasta cada parada y se suman de 5 a 7 minutos
 * en cada parada. Cuando pasa la hora estimada de fin de una parada,
 * esa parada se marca como recolectada.
 *
 * No depende de Android ni de Firebase, por eso se prueba con pruebas
 * unitarias en app/src/test.
 */

// Velocidad promedio de una persona caminando: 4,5 km/h
const val METROS_POR_MINUTO_CAMINANDO = 4500.0 / 60.0

// Las calles no son línea recta: se aumenta la distancia un 30 %
const val FACTOR_CALLES = 1.3

const val MINUTOS_MIN_EN_PARADA = 5
const val MINUTOS_MAX_EN_PARADA = 7

private const val RADIO_TIERRA_METROS = 6_371_000.0
private const val MILIS_POR_MINUTO = 60_000L

data class PuntoRuta(
    val id: String,
    val latitud: Double,
    val longitud: Double
) {
    // Una parada sin coordenadas llega con 0,0
    val tieneCoordenadas: Boolean
        get() = latitud != 0.0 || longitud != 0.0
}

data class TiempoParada(
    val solicitudId: String,
    val llegadaMillis: Long,
    val finMillis: Long
)

/** Distancia en línea recta entre dos coordenadas (fórmula de Haversine). */
fun distanciaMetros(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a =
        sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)

    return 2 * RADIO_TIERRA_METROS * asin(sqrt(a))
}

/** Minutos que tarda una persona caminando entre dos puntos. */
fun minutosCaminando(desde: PuntoRuta?, hasta: PuntoRuta): Double {
    if (desde == null || !desde.tieneCoordenadas || !hasta.tieneCoordenadas) return 0.0

    val metros = distanciaMetros(desde.latitud, desde.longitud, hasta.latitud, hasta.longitud)

    return metros * FACTOR_CALLES / METROS_POR_MINUTO_CAMINANDO
}

/**
 * Minutos que se queda el recolector en una parada: entre 5 y 7.
 * Sale siempre igual para la misma solicitud, así todos los celulares
 * calculan la misma hora.
 */
fun minutosEnParada(solicitudId: String): Int {
    val rango = MINUTOS_MAX_EN_PARADA - MINUTOS_MIN_EN_PARADA + 1
    return MINUTOS_MIN_EN_PARADA + abs(solicitudId.hashCode() % rango)
}

/**
 * Calcula, en orden, la hora de llegada y la hora de fin de cada parada.
 *
 * @param inicioMillis hora de salida de la ruta desde la fundación
 * @param origen dirección de la fundación (null = se empieza en la primera parada)
 * @param paradas paradas en el orden de la ruta
 */
fun calcularTiemposParadas(
    inicioMillis: Long,
    origen: PuntoRuta?,
    paradas: List<PuntoRuta>
): List<TiempoParada> {

    var reloj = inicioMillis.toDouble()
    var anterior = origen

    return paradas.map { parada ->

        reloj += minutosCaminando(anterior, parada) * MILIS_POR_MINUTO
        val llegada = reloj.roundToLong()

        reloj += minutosEnParada(parada.id) * MILIS_POR_MINUTO
        val fin = reloj.roundToLong()

        if (parada.tieneCoordenadas) anterior = parada

        TiempoParada(
            solicitudId = parada.id,
            llegadaMillis = llegada,
            finMillis = fin
        )
    }
}

/**
 * Paradas pendientes que ya deberían estar terminadas a la hora [ahoraMillis],
 * en el orden de la ruta.
 */
fun paradasVencidas(
    tiempos: List<TiempoParada>,
    pendientes: Set<String>,
    ahoraMillis: Long
): List<TiempoParada> =
    tiempos.filter { it.solicitudId in pendientes && it.finMillis <= ahoraMillis }
