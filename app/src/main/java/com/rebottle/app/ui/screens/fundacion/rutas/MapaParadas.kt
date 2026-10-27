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


// ============================================================
// MODELO DE PUNTO DEL MAPA
// ============================================================

data class PuntoMapa(
    val latitud: Double,
    val longitud: Double,
    val color: Color,

    // Se usa principalmente en el mapa del recolector.
    // Fundación puede seguir sin pasarlo.
    val solicitudId: String = ""
)


// ============================================================
// MAPA DE PARADAS
// ============================================================

@Composable
fun MapaParadas(
    puntos: List<PuntoMapa>,

    // Ruta completa creada por Google Routes.
    polyline: String = "",

    // Ruta desde el GPS del recolector
    // hasta la siguiente parada.
    polylineActiva: String = "",

    // Activa el punto azul de Google Maps.
    mostrarMiUbicacion: Boolean = false,

    // Ubicación actual del recolector.
    // Se usa para eliminar la parte ya recorrida.
    ubicacionActual: LatLng? = null,

    // Evento al tocar un marker.
    onPuntoClick: (PuntoMapa) -> Unit = {},

    modifier: Modifier = Modifier
) {

    // ========================================================
    // DECODIFICAR RUTA COMPLETA
    // ========================================================

    val trazoCompleto =
        remember(
            polyline
        ) {

            decodificarPolyline(
                polyline
            ).map { coordenada ->

                LatLng(
                    coordenada.first,
                    coordenada.second
                )
            }
        }


    // ========================================================
    // DECODIFICAR RUTA ACTIVA
    //
    // GPS -> SIGUIENTE PARADA
    // ========================================================

    val trazoActivo =
        remember(
            polylineActiva
        ) {

            decodificarPolyline(
                polylineActiva
            ).map { coordenada ->

                LatLng(
                    coordenada.first,
                    coordenada.second
                )
            }
        }


    // ========================================================
    // ÍNDICE DE LA RUTA YA RECORRIDA
    //
    // Este índice solo puede avanzar.
    // Nunca retrocede.
    // ========================================================

    var indiceRecorrido by
    remember(
        polyline
    ) {

        mutableIntStateOf(
            0
        )
    }


    // ========================================================
    // ACTUALIZAR PARTE RECORRIDA
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

            indiceRecorrido =
                0

            return@LaunchedEffect
        }


        var indiceMasCercano =
            indiceRecorrido


        var distanciaMasCercana =
            Float.MAX_VALUE


        val resultadoDistancia =
            FloatArray(
                1
            )


        trazoCompleto
            .forEachIndexed { indice, puntoRuta ->


                // =================================================
                // NO BUSCAMOS DETRÁS DE DONDE YA PASAMOS
                // =================================================

                if (
                    indice <
                    indiceRecorrido
                ) {

                    return@forEachIndexed
                }


                // =================================================
                // DISTANCIA GPS -> PUNTO DE LA POLYLINE
                // =================================================

                Location.distanceBetween(

                    ubicacion.latitude,
                    ubicacion.longitude,

                    puntoRuta.latitude,
                    puntoRuta.longitude,

                    resultadoDistancia
                )


                if (
                    resultadoDistancia[0] <
                    distanciaMasCercana
                ) {

                    distanciaMasCercana =
                        resultadoDistancia[0]


                    indiceMasCercano =
                        indice
                }
            }


        // ====================================================
        // SOLO RECORTAMOS SI EL RECOLECTOR ESTÁ CERCA
        // DE LA RUTA ORIGINAL.
        //
        // Esto evita eliminar partes de la ruta por errores
        // temporales del GPS.
        // ====================================================

        if (
            distanciaMasCercana <=
            120f
        ) {

            indiceRecorrido =
                maxOf(
                    indiceRecorrido,
                    indiceMasCercano
                )
        }
    }


    // ========================================================
    // PARTE DE LA RUTA QUE TODAVÍA FALTA
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
    // TODOS LOS ELEMENTOS PARA AJUSTAR LA CÁMARA
    // ========================================================

    val todos =
        remember(
            trazoPendiente,
            trazoActivo,
            puntos
        ) {

            trazoPendiente +
                    trazoActivo +
                    puntos.map { punto ->

                        LatLng(
                            punto.latitud,
                            punto.longitud
                        )
                    }
        }


    // ========================================================
    // CÁMARA
    // ========================================================

    val camara =
        rememberCameraPositionState {

            // Bogotá por defecto
            position =
                CameraPosition
                    .fromLatLngZoom(

                        LatLng(
                            4.7110,
                            -74.0721
                        ),

                        11f
                    )
        }


    // ========================================================
    // PROPIEDADES DEL MAPA
    // ========================================================

    val propiedadesMapa =
        MapProperties(

            isMyLocationEnabled =
                mostrarMiUbicacion
        )


    var mapaCargado by
    remember {

        mutableStateOf(
            false
        )
    }


    // ========================================================
    // AJUSTAR LA CÁMARA AUTOMÁTICAMENTE
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


        // ====================================================
        // SOLO HAY UN PUNTO
        // ====================================================

        if (
            todos.size ==
            1
        ) {

            camara.move(

                CameraUpdateFactory
                    .newLatLngZoom(

                        todos.first(),

                        15f
                    )
            )


            return@LaunchedEffect
        }


        // ====================================================
        // ENCUADRAR TODA LA RUTA
        // ====================================================

        val limites =
            LatLngBounds
                .builder()
                .apply {

                    todos.forEach { punto ->

                        include(
                            punto
                        )
                    }
                }
                .build()


        camara.move(

            CameraUpdateFactory
                .newLatLngBounds(

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
        // RUTA PENDIENTE
        //
        // COLOR VERDE
        // ====================================================

        if (
            trazoPendiente.size >=
            2
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
        // RUTA HACIA LA PARADA ACTUAL
        //
        // COLOR NARANJA
        // ====================================================

        if (
            trazoActivo.size >=
            2
        ) {

            Polyline(
                points =
                    trazoActivo,

                color =
                    Color(
                        0xFFD99B4B
                    ),

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
                punto.color,
                punto.solicitudId
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

                    // =========================================
                    // MARCADOR PERSONALIZADO
                    //
                    // Permite usar gris real.
                    // =========================================

                    icon =
                        crearMarcador(
                            punto.color
                        ),


                    // =========================================
                    // CLICK SOBRE EL MARKER
                    // =========================================

                    onClick = {

                        onPuntoClick(
                            punto
                        )


                        /*
                         * true indica que nosotros manejamos
                         * el click y evitamos el InfoWindow
                         * estándar de Google Maps.
                         */
                        true
                    }
                )
            }
        }
    }
}


// ============================================================
// CREAR MARKER PERSONALIZADO
//
// Usamos un bitmap porque defaultMarker(hue)
// no puede representar correctamente el gris.
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
        Canvas(
            bitmap
        )


    // ========================================================
    // COLOR DEL PIN
    // ========================================================

    val pintura =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            this.color =
                color.toArgb()


            style =
                Paint.Style.FILL
        }


    // ========================================================
    // BORDE BLANCO
    // ========================================================

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
// DECODIFICAR POLYLINE DE GOOGLE
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
            indice <
            encoded.length
        ) {

            // =================================================
            // LATITUD
            // =================================================

            var desplazamiento =
                0


            var acumulado =
                0


            var b: Int


            do {

                b =
                    encoded[
                        indice++
                    ].code -
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
                b >=
                0x20
            )


            lat +=
                if (
                    (acumulado and 1) !=
                    0
                ) {

                    (acumulado shr 1)
                        .inv()

                } else {

                    acumulado shr 1
                }


            // =================================================
            // LONGITUD
            // =================================================

            desplazamiento =
                0


            acumulado =
                0


            do {

                b =
                    encoded[
                        indice++
                    ].code -
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
                b >=
                0x20
            )


            lng +=
                if (
                    (acumulado and 1) !=
                    0
                ) {

                    (acumulado shr 1)
                        .inv()

                } else {

                    acumulado shr 1
                }


            // =================================================
            // AGREGAR PUNTO
            // =================================================

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