package com.rebottle.app.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas unitarias del cálculo del aporte ambiental (RF-GAM-006). */
class AportesLogicaTest {

    @Test
    fun `U-23 clasifica materiales sin importar tildes ni mayusculas`() {
        assertEquals("Plástico", categoriaMaterial("Botellas PET"))
        assertEquals("Vidrio", categoriaMaterial("vidrio"))
        assertEquals("Papel", categoriaMaterial("Cartón"))
        assertEquals("Metal", categoriaMaterial("Latas de aluminio"))
    }

    @Test
    fun `U-24 un material desconocido queda en Otros`() {
        assertEquals("Otros", categoriaMaterial("Icopor"))
    }

    @Test
    fun `U-25 reparte el peso por igual entre los materiales`() {
        assertEquals(5.0, kgPorMaterial(pesoKg = 10, cantidadMateriales = 2), 0.0001)
        assertEquals(0.0, kgPorMaterial(pesoKg = 10, cantidadMateriales = 0), 0.0001)
    }

    @Test
    fun `U-26 suma los kg por categoria`() {
        val resultado = sumarKgPorCategoria(
            listOf("PET" to 2.0, "Plástico" to 3.0, "Vidrio" to 5.0)
        )
        assertEquals(5.0, resultado["Plástico"]!!, 0.0001)
        assertEquals(5.0, resultado["Vidrio"]!!, 0.0001)
    }

    @Test
    fun `U-27 calcula el porcentaje por material y 0 si no hay aportes`() {
        assertEquals(25f, porcentajeDe(kg = 2.5, total = 10.0), 0.001f)
        assertEquals(0f, porcentajeDe(kg = 0.0, total = 0.0), 0.001f)
    }

    @Test
    fun `U-28 el periodo de una semana empieza 7 dias antes`() {
        val ahora = 1_000_000_000_000L
        assertEquals(ahora - 7L * 24 * 60 * 60 * 1000, inicioDelPeriodo(ahora, 7))
        assertEquals(Long.MIN_VALUE, inicioDelPeriodo(ahora, null))
    }

    @Test
    fun `U-33 reconoce todas las variantes de cada categoria`() {
        assertEquals("Plástico", categoriaMaterial("PLÁSTICO"))
        assertEquals("Vidrio", categoriaMaterial("Botellas de vidrio"))
        assertEquals("Papel", categoriaMaterial("Papel periódico"))
        assertEquals("Papel", categoriaMaterial("CARTON"))
        assertEquals("Metal", categoriaMaterial("Metal"))
        assertEquals("Metal", categoriaMaterial("Latas"))
        assertEquals("Metal", categoriaMaterial("Alumínio"))
        assertEquals("Otros", categoriaMaterial(""))
    }

    @Test
    fun `U-34 sin aportes no hay categorias y lo desconocido suma en Otros`() {
        assertTrue(sumarKgPorCategoria(emptyList()).isEmpty())

        val resultado = sumarKgPorCategoria(listOf("Icopor" to 1.5, "Tela" to 2.0))
        assertEquals(setOf("Otros"), resultado.keys)
        assertEquals(3.5, resultado["Otros"]!!, 0.0001)
    }

    @Test
    fun `U-35 reparte pesos no exactos y no falla con cantidad negativa`() {
        assertEquals(10.0 / 3, kgPorMaterial(pesoKg = 10, cantidadMateriales = 3), 0.0001)
        assertEquals(0.0, kgPorMaterial(pesoKg = 10, cantidadMateriales = -1), 0.0001)
    }

    @Test
    fun `U-36 los porcentajes de todas las categorias suman 100`() {
        val kg = sumarKgPorCategoria(
            listOf("PET" to 3.0, "Vidrio" to 2.0, "Cartón" to 4.0, "Lata" to 1.0)
        )
        val total = kg.values.sum()
        val suma = kg.values.sumOf { porcentajeDe(it, total).toDouble() }
        assertEquals(100.0, suma, 0.01)
    }

    @Test
    fun `U-37 el periodo de un mes empieza 30 dias antes`() {
        val ahora = 1_000_000_000_000L
        assertEquals(ahora - 30L * 24 * 60 * 60 * 1000, inicioDelPeriodo(ahora, 30))
    }
}
