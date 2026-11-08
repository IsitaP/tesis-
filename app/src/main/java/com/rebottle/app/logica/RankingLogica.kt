package com.rebottle.app.logica

import java.util.Calendar
import java.util.TimeZone

/*
 * Cálculos del ranking semanal e histórico (RF-GAM-004).
 * No dependen de Android ni de Firebase, por eso se prueban
 * con pruebas unitarias en app/src/test.
 */

data class PosicionRanking(
    val uid: String,
    val puntos: Int,
    val puesto: Int
)

/**
 * Ordena de mayor a menor puntaje. Si empatan comparten puesto
 * (ej: 1, 2, 2, 4) y se ordenan por nombre.
 */
fun calcularPosiciones(
    puntos: Map<String, Int>,
    nombreDe: (String) -> String?
): List<PosicionRanking> {
    val ordenados =
        puntos.entries.sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenBy { nombreDe(it.key)?.lowercase() }
        )

    var puestoAnterior = 0
    var puntosAnteriores: Int? = null

    return ordenados.mapIndexed { index, (uid, pts) ->
        val puesto =
            if (pts == puntosAnteriores) puestoAnterior else index + 1
        puestoAnterior = puesto
        puntosAnteriores = pts
        PosicionRanking(uid = uid, puntos = pts, puesto = puesto)
    }
}

/** Lunes de la semana de [ahoraMillis] a las 00:00 (hora del celular). */
fun inicioDeSemana(
    ahoraMillis: Long,
    zona: TimeZone = TimeZone.getDefault()
): Long =
    Calendar.getInstance(zona).apply {
        timeInMillis = ahoraMillis
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }.timeInMillis

/**
 * Suma los puntos de los módulos completados desde [desdeMillis].
 * Cada elemento es (fecha en milisegundos o null, puntos).
 */
fun puntosDesde(modulos: List<Pair<Long?, Int>>, desdeMillis: Long): Int =
    modulos
        .filter { (fecha, _) -> fecha != null && fecha >= desdeMillis }
        .sumOf { it.second }
