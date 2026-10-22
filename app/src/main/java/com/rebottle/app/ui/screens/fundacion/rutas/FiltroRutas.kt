package com.rebottle.app.ui.screens.fundacion.rutas

import com.rebottle.app.model.RecolectorRuta


enum class RangoPeso(val etiqueta: String, val minKg: Int, val maxKg: Int?) {
    LIVIANO("Hasta 20 kg", 0, 20),
    MEDIO("21 a 50 kg", 21, 50),
    PESADO("Más de 50 kg", 51, null)
}

data class FiltroRutas(
    val recolectorId: String? = null,
    val fecha: String? = null,        // formato ISO: 2026-10-05
    val material: String? = null,     // nombre de MaterialType
    val peso: RangoPeso? = null       // peso total de la ruta
) {

    val cantidad: Int
        get() = listOfNotNull(recolectorId, fecha, material, peso).size

    fun aplica(r: RecolectorRuta): Boolean {

        if (recolectorId != null && r.uid != recolectorId) return false

        if (fecha != null && r.fecha != fecha) return false

        if (material != null && material !in r.materiales) return false

        if (peso != null) {
            if (r.rutaId == null) return false
            if (r.pesoKg < peso.minKg) return false
            if (peso.maxKg != null && r.pesoKg > peso.maxKg) return false
        }

        return true
    }
}
