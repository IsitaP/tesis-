package com.rebottle.app.logica

import com.rebottle.app.model.PuntosModulos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Pruebas unitarias de la asignación de puntos (RF-GAM-001). */
class PuntosModulosTest {

    @Test
    fun `U-29 cada modulo otorga los puntos definidos`() {
        assertEquals(100L, PuntosModulos.puntosDe(PuntosModulos.M1))
        assertEquals(200L, PuntosModulos.puntosDe(PuntosModulos.M4))
        assertEquals(800L, PuntosModulos.totalPosible)
    }

    @Test
    fun `U-30 suma puntos la primera vez que se completa un modulo`() {
        assertEquals(150L, PuntosModulos.puntosASumar(PuntosModulos.M2, emptySet()))
    }

    @Test
    fun `U-31 no suma puntos si el modulo ya estaba completado`() {
        assertEquals(0L, PuntosModulos.puntosASumar(PuntosModulos.M2, setOf("M1", "M2")))
    }

    @Test
    fun `U-32 un modulo inexistente no otorga puntos`() {
        assertNull(PuntosModulos.puntosASumar("M9", emptySet()))
    }

    @Test
    fun `U-43 los siete modulos tienen sus puntos definidos`() {
        val esperados = mapOf(
            "M1" to 100L, "M2" to 150L, "M3" to 50L, "M4" to 200L,
            "M5" to 100L, "M6" to 50L, "M7" to 150L
        )
        assertEquals(esperados, PuntosModulos.valores)
        assertNull(PuntosModulos.puntosDe("M0"))
        assertNull(PuntosModulos.puntosDe("m1")) // distingue mayúsculas
    }

    @Test
    fun `U-44 completar todos los modulos una vez da el total posible`() {
        val completados = mutableSetOf<String>()
        var total = 0L
        // Se completa cada módulo dos veces: la segunda no debe sumar.
        repeat(2) {
            PuntosModulos.valores.keys.forEach { id ->
                total += PuntosModulos.puntosASumar(id, completados)!!
                completados += id
            }
        }
        assertEquals(PuntosModulos.totalPosible, total)
    }

    @Test
    fun `U-45 un modulo inexistente no otorga puntos aunque aparezca como completado`() {
        assertNull(PuntosModulos.puntosASumar("M9", setOf("M9")))
    }
}
