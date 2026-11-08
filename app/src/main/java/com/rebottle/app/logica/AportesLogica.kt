package com.rebottle.app.logica

import java.text.Normalizer

/*
 * Cálculos del aporte ambiental del ciudadano (RF-GAM-006).
 * No dependen de Android ni de Firebase, por eso se prueban
 * con pruebas unitarias en app/src/test.
 */

/** Lleva cualquier texto de material a una de las categorías de la pantalla. */
fun categoriaMaterial(raw: String): String {
    val t = Normalizer.normalize(raw.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")

    return when {
        "plast" in t || "pet" in t -> "Plástico"
        "vidri" in t -> "Vidrio"
        "papel" in t || "carton" in t -> "Papel"
        "metal" in t || "lata" in t || "alumin" in t -> "Metal"
        else -> "Otros"
    }
}

/**
 * La solicitud guarda un solo peso para todos sus materiales,
 * así que los kg se reparten por igual entre ellos.
 */
fun kgPorMaterial(pesoKg: Int, cantidadMateriales: Int): Double =
    if (cantidadMateriales <= 0) 0.0 else pesoKg.toDouble() / cantidadMateriales

/** Suma los kg por categoría. Recibe pares (material, kg). */
fun sumarKgPorCategoria(aportes: List<Pair<String, Double>>): Map<String, Double> =
    aportes
        .groupBy { categoriaMaterial(it.first) }
        .mapValues { (_, lista) -> lista.sumOf { it.second } }

/** Porcentaje que representa [kg] sobre [total]; 0 si no hay total. */
fun porcentajeDe(kg: Double, total: Double): Float =
    if (total > 0) (kg / total * 100).toFloat() else 0f

/** Fecha desde la que se cuentan los aportes; sin días = todo el historial. */
fun inicioDelPeriodo(ahoraMillis: Long, dias: Long?): Long =
    dias?.let { ahoraMillis - it * 24L * 60L * 60L * 1000L } ?: Long.MIN_VALUE
