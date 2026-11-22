package com.rebottle.app.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgresoRutaLogicaTest {

    private val minuto = 60_000L
    private val inicio = 1_000_000_000_000L

    // Dos puntos en Bogotá separados ~1,1 km (0,01° de latitud)
    private val fundacion = PuntoRuta("fundacion", 4.6000, -74.0800)
    private val parada1 = PuntoRuta("RB-0001", 4.6100, -74.0800)
    private val parada2 = PuntoRuta("RB-0002", 4.6100, -74.0800)

    @Test
    fun distanciaEntreDosPuntosEsAproximadamenteCorrecta() {
        val metros = distanciaMetros(4.6000, -74.0800, 4.6100, -74.0800)
        assertEquals(1112.0, metros, 5.0)
    }

    @Test
    fun tiempoEnParadaEstaEntreCincoYSieteMinutos() {
        (1..200).forEach { i ->
            val min = minutosEnParada("RB-%04d".format(i))
            assertTrue(min in MINUTOS_MIN_EN_PARADA..MINUTOS_MAX_EN_PARADA)
        }
    }

    @Test
    fun tiempoEnParadaEsSiempreIgualParaLaMismaSolicitud() {
        assertEquals(minutosEnParada("RB-0042"), minutosEnParada("RB-0042"))
    }

    @Test
    fun primeraParadaCuentaElCaminoDesdeLaFundacion() {
        val tiempos = calcularTiemposParadas(inicio, fundacion, listOf(parada1))

        val caminando = 1112.0 * FACTOR_CALLES / METROS_POR_MINUTO_CAMINANDO // ~19 min
        val llegadaEsperada = inicio + (caminando * minuto).toLong()

        assertEquals(llegadaEsperada.toDouble(), tiempos[0].llegadaMillis.toDouble(), 10_000.0)
        assertEquals(
            tiempos[0].llegadaMillis + minutosEnParada(parada1.id) * minuto,
            tiempos[0].finMillis
        )
    }

    @Test
    fun cadaParadaEmpiezaDespuesDeTerminarLaAnterior() {
        val tiempos = calcularTiemposParadas(inicio, fundacion, listOf(parada1, parada2))

        // Misma ubicación: no hay camino, llega justo al terminar la anterior
        assertEquals(tiempos[0].finMillis, tiempos[1].llegadaMillis)
        assertTrue(tiempos[1].finMillis > tiempos[0].finMillis)
    }

    @Test
    fun sinFundacionLaRutaEmpiezaEnLaPrimeraParada() {
        val tiempos = calcularTiemposParadas(inicio, null, listOf(parada1))
        assertEquals(inicio, tiempos[0].llegadaMillis)
    }

    @Test
    fun soloSeCompletanLasParadasPendientesCuyoTiempoYaPaso() {
        val tiempos = calcularTiemposParadas(inicio, fundacion, listOf(parada1, parada2))

        val justoDespuesDeLaPrimera = tiempos[0].finMillis

        val vencidas =
            paradasVencidas(tiempos, setOf(parada1.id, parada2.id), justoDespuesDeLaPrimera)

        assertEquals(listOf(parada1.id), vencidas.map { it.solicitudId })
    }

    @Test
    fun unaParadaYaResueltaNoSeVuelveACompletar() {
        val tiempos = calcularTiemposParadas(inicio, fundacion, listOf(parada1, parada2))

        val vencidas =
            paradasVencidas(tiempos, setOf(parada2.id), tiempos[1].finMillis)

        assertEquals(listOf(parada2.id), vencidas.map { it.solicitudId })
    }
}
