package com.rebottle.app.model

/** Puntos que otorga cada módulo de aprendizaje (M1 a M7). */
object PuntosModulos {

    const val M1 = "M1"
    const val M2 = "M2"
    const val M3 = "M3"
    const val M4 = "M4"
    const val M5 = "M5"
    const val M6 = "M6"
    const val M7 = "M7"

    val valores: Map<String, Long> = mapOf(
        M1 to 100L,
        M2 to 150L,
        M3 to 50L,
        M4 to 200L,
        M5 to 100L,
        M6 to 50L,
        M7 to 150L
    )

    /** Puntos que otorga un módulo, o null si el módulo no existe. */
    fun puntosDe(moduloId: String): Long? = valores[moduloId]

    /**
     * Puntos que se deben sumar al completar un módulo.
     * Devuelve 0 si el módulo ya estaba completado (no se suma dos veces)
     * y null si el módulo no existe.
     */
    fun puntosASumar(moduloId: String, yaCompletados: Set<String>): Long? {
        val puntos = valores[moduloId] ?: return null
        return if (moduloId in yaCompletados) 0L else puntos
    }

    /** Total de puntos que se pueden ganar completando todos los módulos. */
    val totalPosible: Long get() = valores.values.sum()
}
