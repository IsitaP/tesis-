package com.rebottle.app.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

/** Pruebas unitarias del ranking semanal e histórico (RF-GAM-004). */
class RankingLogicaTest {

    private val bogota = TimeZone.getTimeZone("America/Bogota")

    private fun millis(fechaHora: String): Long =
        LocalDateTime.parse(fechaHora)
            .atZone(ZoneId.of("America/Bogota"))
            .toInstant()
            .toEpochMilli()

    @Test
    fun `U-18 ordena de mayor a menor puntaje`() {
        val posiciones = calcularPosiciones(
            mapOf("a" to 100, "b" to 300, "c" to 200)
        ) { null }
        assertEquals(listOf("b", "c", "a"), posiciones.map { it.uid })
        assertEquals(listOf(1, 2, 3), posiciones.map { it.puesto })
    }

    @Test
    fun `U-19 los empates comparten puesto y el siguiente se salta`() {
        val nombres = mapOf("a" to "Ana", "b" to "Beto", "c" to "Carla", "d" to "Dani")
        val posiciones = calcularPosiciones(
            mapOf("a" to 300, "b" to 200, "c" to 200, "d" to 100)
        ) { nombres[it] }
        assertEquals(listOf(1, 2, 2, 4), posiciones.map { it.puesto })
    }

    @Test
    fun `U-20 en empate ordena por nombre sin importar mayusculas`() {
        val nombres = mapOf("x" to "zoe", "y" to "Andrés")
        val posiciones = calcularPosiciones(mapOf("x" to 150, "y" to 150)) { nombres[it] }
        assertEquals(listOf("y", "x"), posiciones.map { it.uid })
    }

    @Test
    fun `U-21 la semana empieza el lunes a las 00 00`() {
        val lunes = millis("2026-11-02T00:00")
        assertEquals(lunes, inicioDeSemana(millis("2026-11-04T15:30"), bogota)) // miércoles
        assertEquals(lunes, inicioDeSemana(millis("2026-11-08T22:00"), bogota)) // domingo
        assertEquals(lunes, inicioDeSemana(lunes, bogota))                       // lunes
    }

    @Test
    fun `U-22 el ranking semanal solo suma modulos de esta semana`() {
        val lunes = millis("2026-11-02T00:00")
        val modulos = listOf(
            millis("2026-11-03T10:00") to 100,  // esta semana
            millis("2026-10-30T10:00") to 150,  // semana pasada
            null to 50                           // sin fecha
        )
        assertEquals(100, puntosDesde(modulos, lunes))
    }

    @Test
    fun `U-38 sin usuarios el ranking queda vacio`() {
        assertTrue(calcularPosiciones(emptyMap()) { null }.isEmpty())
    }

    @Test
    fun `U-39 si todos empatan todos quedan en el puesto 1`() {
        val nombres = mapOf("a" to "Carla", "b" to "ana", "c" to "Beto")
        val posiciones = calcularPosiciones(
            mapOf("a" to 50, "b" to 50, "c" to 50)
        ) { nombres[it] }
        assertEquals(listOf(1, 1, 1), posiciones.map { it.puesto })
        assertEquals(listOf("b", "c", "a"), posiciones.map { it.uid })
    }

    @Test
    fun `U-40 conserva los puntos de cada usuario en su posicion`() {
        val posiciones = calcularPosiciones(mapOf("a" to 0, "b" to 120)) { null }
        assertEquals(PosicionRanking(uid = "b", puntos = 120, puesto = 1), posiciones[0])
        assertEquals(PosicionRanking(uid = "a", puntos = 0, puesto = 2), posiciones[1])
    }

    @Test
    fun `U-41 la semana se calcula bien cuando cambia el mes`() {
        // Domingo 1 de noviembre pertenece a la semana del lunes 26 de octubre.
        assertEquals(
            millis("2026-10-26T00:00"),
            inicioDeSemana(millis("2026-11-01T23:59:59"), bogota)
        )
    }

    @Test
    fun `U-42 un modulo completado justo el lunes a las 00 00 cuenta en la semana`() {
        val lunes = millis("2026-11-02T00:00")
        val modulos = listOf(
            lunes to 200,                            // justo al inicio
            millis("2026-11-01T23:59:59") to 50      // un segundo antes
        )
        assertEquals(200, puntosDesde(modulos, lunes))
        assertEquals(0, puntosDesde(emptyList(), lunes))
    }
}
