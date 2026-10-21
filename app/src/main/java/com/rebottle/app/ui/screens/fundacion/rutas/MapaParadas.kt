package com.rebottle.app.ui.screens.fundacion.rutas

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.rebottle.app.ui.theme.RebottleColors


data class PuntoMapa(
    val latitud: Double,
    val longitud: Double,
    val color: Color
)


/**
 * Mapa de Google con el recorrido (polyline) y las paradas.
 * Encuadra automáticamente todo lo que haya para mostrar.
 */
@Composable
fun MapaParadas(
    puntos: List<PuntoMapa>,
    polyline: String = "",
    modifier: Modifier = Modifier
) {

    val trazo = remember(polyline) {
        decodificarPolyline(polyline).map { LatLng(it.first, it.second) }
    }

    val todos = remember(trazo, puntos) {
        trazo + puntos.map { LatLng(it.latitud, it.longitud) }
    }

    // Bogotá por defecto, mientras no haya nada que mostrar
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.7110, -74.0721), 11f)
    }

    var mapaCargado by remember { mutableStateOf(false) }

    LaunchedEffect(todos, mapaCargado) {

        if (!mapaCargado || todos.isEmpty()) return@LaunchedEffect

        if (todos.size == 1) {
            camara.move(CameraUpdateFactory.newLatLngZoom(todos.first(), 15f))
            return@LaunchedEffect
        }

        val limites = LatLngBounds.builder().apply { todos.forEach { include(it) } }.build()

        camara.move(CameraUpdateFactory.newLatLngBounds(limites, 100))
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false
        ),
        onMapLoaded = { mapaCargado = true }
    ) {

        if (trazo.size >= 2) {
            Polyline(
                points = trazo,
                color = RebottleColors.MainGreen,
                width = 12f
            )
        }

        puntos.forEach { punto ->
            key(punto.latitud, punto.longitud, punto.color) {
                Marker(
                    state = rememberMarkerState(position = LatLng(punto.latitud, punto.longitud)),
                    icon = BitmapDescriptorFactory.defaultMarker(tonoDe(punto.color))
                )
            }
        }
    }
}


// Convierte un color de Compose al matiz que usan los marcadores de Google
private fun tonoDe(color: Color): Float {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(color.toArgb(), hsv)
    return hsv[0]
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