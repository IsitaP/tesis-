package com.rebottle.app.ui.screens.fundacion.rutas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.location.Location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
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


@Composable
fun MapaParadas(
    puntos: List<PuntoMapa>,
    polyline: String = "",
    polylineActiva: String = "",
    mostrarMiUbicacion: Boolean = false,

    // Ubicación GPS que usaremos para ir
    // recortando la ruta ya recorrida.
    ubicacionActual: LatLng? = null,

    modifier: Modifier = Modifier
) {

    // ========================================================
    // RUTA COMPLETA ORIGINAL
    // ========================================================

    val trazoCompleto =
        remember(polyline) {

            decodificarPolyline(polyline)
                .map {

                    LatLng(
                        it.first,
                        it.second
                    )
                }
        }


    // ========================================================
    // TRAMO GPS -> PARADA ACTUAL
    // ========================================================

    val trazoActivo =
        remember(polylineActiva) {

            decodificarPolyline(polylineActiva)
                .map {

                    LatLng(
                        it.first,
                        it.second
                    )
                }
        }


    // ========================================================
    // ÍNDICE HASTA DONDE YA RECORRIMOS
    // ========================================================

    var indiceRecorrido by
    remember(polyline) {

        mutableIntStateOf(0)
    }


    // ========================================================
    // BUSCAR EL PUNTO DE LA RUTA MÁS CERCANO AL GPS
    //
    // Solo avanzamos el índice, nunca retrocedemos.
    // ========================================================

    LaunchedEffect(
        ubicacionActual,
        trazoCompleto
    ) {

        val ubicacion =
            ubicacionActual
                ?: return@LaunchedEffect


        if (
            trazoCompleto.isEmpty()
        ) {

            indiceRecorrido = 0
            return@LaunchedEffect
        }


        var indiceMasCercano =
            indiceRecorrido


        var distanciaMasCercana =
            Float.MAX_VALUE


        val resultado =
            FloatArray(1)


        trazoCompleto
            .forEachIndexed { indice, punto ->

                // Nunca permitimos volver hacia atrás.
                if (
                    indice < indiceRecorrido
                ) {

                    return@forEachIndexed
                }


                Location.distanceBetween(
                    ubicacion.latitude,
                    ubicacion.longitude,
                    punto.latitude,
                    punto.longitude,
                    resultado
                )


                if (
                    resultado[0] <
                    distanciaMasCercana
                ) {

                    distanciaMasCercana =
                        resultado[0]


                    indiceMasCercano =
                        indice
                }
            }


        /*
         * Solo recortamos si realmente estamos cerca
         * de la ruta.
         *
         * Esto evita que una ubicación lejana haga
         * desaparecer media ruta accidentalmente.
         */
        if (
            distanciaMasCercana <= 120f
        ) {

            indiceRecorrido =
                maxOf(
                    indiceRecorrido,
                    indiceMasCercano
                )
        }
    }


    // ========================================================
    // SOLO QUEDA LA PARTE NO RECORRIDA
    // ========================================================

    val trazoPendiente =
        remember(
            trazoCompleto,
            indiceRecorrido
        ) {

            if (
                trazoCompleto.isEmpty()
            ) {

                emptyList()

            } else {

                trazoCompleto
                    .drop(
                        indiceRecorrido
                    )
            }
        }


    // ========================================================
    // PUNTOS PARA AJUSTAR LA CÁMARA
    // ========================================================

    val todos =
        remember(
            trazoPendiente,
            trazoActivo,
            puntos
        ) {

            trazoPendiente +
                    trazoActivo +
                    puntos.map {

                        LatLng(
                            it.latitud,
                            it.longitud
                        )
                    }
        }


    // ========================================================
    // CÁMARA
    // ========================================================

    val camara =
        rememberCameraPositionState {

            position =
                CameraPosition.fromLatLngZoom(
                    LatLng(
                        4.7110,
                        -74.0721
                    ),
                    11f
                )
        }


    // ========================================================
    // MI UBICACIÓN
    // ========================================================

    val propiedadesMapa =
        MapProperties(
            isMyLocationEnabled =
                mostrarMiUbicacion
        )


    var mapaCargado by
    remember {

        mutableStateOf(false)
    }


    // ========================================================
    // AJUSTAR CÁMARA
    // ========================================================

    LaunchedEffect(
        todos,
        mapaCargado
    ) {

        if (
            !mapaCargado ||
            todos.isEmpty()
        ) {

            return@LaunchedEffect
        }


        if (
            todos.size == 1
        ) {

            camara.move(
                CameraUpdateFactory.newLatLngZoom(
                    todos.first(),
                    15f
                )
            )

            return@LaunchedEffect
        }


        val limites =
            LatLngBounds
                .builder()
                .apply {

                    todos.forEach {

                        include(it)
                    }
                }
                .build()


        camara.move(
            CameraUpdateFactory.newLatLngBounds(
                limites,
                100
            )
        )
    }


    // ========================================================
    // GOOGLE MAP
    // ========================================================

    GoogleMap(
        modifier =
            modifier,

        cameraPositionState =
            camara,

        properties =
            propiedadesMapa,

        uiSettings =
            MapUiSettings(
                zoomControlsEnabled =
                    false,

                mapToolbarEnabled =
                    false,

                myLocationButtonEnabled =
                    mostrarMiUbicacion
            ),

        onMapLoaded = {

            mapaCargado =
                true
        }

    ) {


        // ====================================================
        // RUTA QUE TODAVÍA FALTA - VERDE
        // ====================================================

        if (
            trazoPendiente.size >= 2
        ) {

            Polyline(
                points =
                    trazoPendiente,

                color =
                    RebottleColors.MainGreen,

                width =
                    12f
            )
        }


        // ====================================================
        // GPS -> SIGUIENTE PARADA - NARANJA
        // ====================================================

        if (
            trazoActivo.size >= 2
        ) {

            Polyline(
                points =
                    trazoActivo,

                color =
                    Color(0xFFD99B4B),

                width =
                    15f
            )
        }


        // ====================================================
        // MARCADORES
        // ====================================================

        puntos.forEach { punto ->

            key(
                punto.latitud,
                punto.longitud,
                punto.color
            ) {

                Marker(
                    state =
                        rememberMarkerState(
                            position =
                                LatLng(
                                    punto.latitud,
                                    punto.longitud
                                )
                        ),

                    // Ahora sí conserva gris,
                    // naranja, verde, etc.
                    icon =
                        crearMarcador(
                            punto.color
                        )
                )
            }
        }
    }
}


// ============================================================
// MARCADOR PERSONALIZADO
//
// No usamos defaultMarker(hue) porque no permite representar
// correctamente colores sin saturación como gris.
// ============================================================

private fun crearMarcador(
    color: Color
): BitmapDescriptor {

    val ancho =
        56

    val alto =
        72


    val bitmap =
        Bitmap.createBitmap(
            ancho,
            alto,
            Bitmap.Config.ARGB_8888
        )


    val canvas =
        Canvas(bitmap)


    val pintura =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            this.color =
                color.toArgb()

            style =
                Paint.Style.FILL
        }


    val borde =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            this.color =
                android.graphics.Color.WHITE

            style =
                Paint.Style.STROKE

            strokeWidth =
                4f
        }


    // ========================================================
    // PUNTA DEL PIN
    // ========================================================

    val path =
        Path().apply {

            moveTo(
                28f,
                68f
            )

            lineTo(
                14f,
                38f
            )

            lineTo(
                42f,
                38f
            )

            close()
        }


    canvas.drawPath(
        path,
        pintura
    )


    canvas.drawPath(
        path,
        borde
    )


    // ========================================================
    // CABEZA DEL PIN
    // ========================================================

    canvas.drawCircle(
        28f,
        28f,
        20f,
        pintura
    )


    canvas.drawCircle(
        28f,
        28f,
        20f,
        borde
    )


    return BitmapDescriptorFactory
        .fromBitmap(
            bitmap
        )
}


// ============================================================
// DECODIFICAR POLYLINE
// ============================================================

private fun decodificarPolyline(
    encoded: String
): List<Pair<Double, Double>> {

    if (
        encoded.isBlank()
    ) {

        return emptyList()
    }


    return runCatching {

        val resultado =
            mutableListOf<Pair<Double, Double>>()


        var indice =
            0

        var lat =
            0

        var lng =
            0


        while (
            indice < encoded.length
        ) {

            var desplazamiento =
                0

            var acumulado =
                0

            var b: Int


            do {

                b =
                    encoded[indice++].code -
                            63


                acumulado =
                    acumulado or
                            (
                                    (b and 0x1f)
                                            shl desplazamiento
                                    )


                desplazamiento +=
                    5

            } while (
                b >= 0x20
            )


            lat +=
                if (
                    (acumulado and 1) != 0
                ) {

                    (acumulado shr 1)
                        .inv()

                } else {

                    acumulado shr 1
                }


            desplazamiento =
                0

            acumulado =
                0


            do {

                b =
                    encoded[indice++].code -
                            63


                acumulado =
                    acumulado or
                            (
                                    (b and 0x1f)
                                            shl desplazamiento
                                    )


                desplazamiento +=
                    5

            } while (
                b >= 0x20
            )


            lng +=
                if (
                    (acumulado and 1) != 0
                ) {

                    (acumulado shr 1)
                        .inv()

                } else {

                    acumulado shr 1
                }


            resultado.add(
                (lat / 1e5) to
                        (lng / 1e5)
            )
        }


        resultado

    }.getOrDefault(
        emptyList()
    )
}