package com.rebottle.app.ui.screens.fundacion.rutas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.ui.theme.RebottleColors
import kotlin.math.cos


data class PuntoMapa(
    val latitud: Double,
    val longitud: Double,
    val color: Color
)


/**
 * Mapa esquemático: dibuja el recorrido y las paradas sin calles.
 * Cuando haya Google Maps se reemplaza por uno real con la misma firma.
 */
@Composable
fun MapaParadas(
    puntos: List<PuntoMapa>,
    polyline: String = "",
    modifier: Modifier = Modifier
) {

    val trazo = remember(polyline) { decodificarPolyline(polyline) }

    Box(
        modifier = modifier.background(Color(0xFFE9EDE3)),
        contentAlignment = Alignment.Center
    ) {

        if (puntos.isEmpty() && trazo.isEmpty()) {
            Text(
                text = "Sin paradas para mostrar",
                fontSize = 13.sp,
                color = RebottleColors.SecondaryText
            )
            return@Box
        }

        Canvas(modifier = Modifier.matchParentSize()) {

            // Cuadrícula suave de fondo
            val paso = 36.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(Color(0x14000000), Offset(x, 0f), Offset(x, size.height), 1f)
                x += paso
            }
            var y = 0f
            while (y < size.height) {
                drawLine(Color(0x14000000), Offset(0f, y), Offset(size.width, y), 1f)
                y += paso
            }

            val todos = trazo + puntos.map { it.latitud to it.longitud }

            val minLat = todos.minOf { it.first }
            val maxLat = todos.maxOf { it.first }
            val minLng = todos.minOf { it.second }
            val maxLng = todos.maxOf { it.second }

            val kx = cos(Math.toRadians((minLat + maxLat) / 2.0))
            val anchoGeo = ((maxLng - minLng) * kx).coerceAtLeast(1e-6)
            val altoGeo = (maxLat - minLat).coerceAtLeast(1e-6)

            val margen = 28.dp.toPx().toDouble()
            val ancho = size.width.toDouble()
            val alto = size.height.toDouble()

            val escala = minOf(
                (ancho - 2 * margen) / anchoGeo,
                (alto - 2 * margen) / altoGeo
            )

            val offX = (ancho - anchoGeo * escala) / 2.0
            val offY = (alto - altoGeo * escala) / 2.0

            fun proyectar(lat: Double, lng: Double) = Offset(
                (offX + (lng - minLng) * kx * escala).toFloat(),
                (offY + (maxLat - lat) * escala).toFloat()
            )

            if (trazo.size >= 2) {
                val path = Path()
                trazo.forEachIndexed { i, (lat, lng) ->
                    val p = proyectar(lat, lng)
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                drawPath(
                    path = path,
                    color = RebottleColors.MainGreen,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            puntos.forEach { punto ->
                val c = proyectar(punto.latitud, punto.longitud)
                drawCircle(Color.White, radius = 11.dp.toPx(), center = c)
                drawCircle(punto.color, radius = 8.dp.toPx(), center = c)
                drawCircle(Color.White, radius = 3.dp.toPx(), center = c)
            }
        }
    }
}


// Decodifica el formato "encoded polyline" de Google
private fun decodificarPolyline(encoded: String): List<Pair<Double, Double>> {

    if (encoded.isBlank()) return emptyList()

    return runCatching {

        val resultado = mutableListOf<Pair<Double, Double>>()
        var indice = 0
        var lat = 0
        var lng = 0

        while (indice < encoded.length) {

            var desplazamiento = 0
            var acumulado = 0
            var b: Int

            do {
                b = encoded[indice++].code - 63
                acumulado = acumulado or ((b and 0x1f) shl desplazamiento)
                desplazamiento += 5
            } while (b >= 0x20)

            lat += if ((acumulado and 1) != 0) (acumulado shr 1).inv() else (acumulado shr 1)

            desplazamiento = 0
            acumulado = 0

            do {
                b = encoded[indice++].code - 63
                acumulado = acumulado or ((b and 0x1f) shl desplazamiento)
                desplazamiento += 5
            } while (b >= 0x20)

            lng += if ((acumulado and 1) != 0) (acumulado shr 1).inv() else (acumulado shr 1)

            resultado.add((lat / 1e5) to (lng / 1e5))
        }

        resultado
    }.getOrDefault(emptyList())
}