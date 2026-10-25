package com.rebottle.app.ui.screens.recolector.rutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.content.ContextCompat

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.model.LatLng

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions

import com.rebottle.app.model.EstadoParada
import com.rebottle.app.model.RutaProgresoViewModel

import com.rebottle.app.ui.screens.fundacion.rutas.MapaParadas
import com.rebottle.app.ui.screens.fundacion.rutas.PuntoMapa

import com.rebottle.app.ui.theme.RebottleColors


// ============================================================
// COLORES
// ============================================================

private val NaranjaParadaActiva =
    Color(0xFFD99B4B)

private val GrisParadaCompletada =
    Color(0xFF9E9E9E)


// ============================================================
// MAPA DE LA RUTA DEL RECOLECTOR
// ============================================================

@SuppressLint("MissingPermission")
@Composable
fun MapaRutaRecolectorScreen(
    controller: NavController,
    model: RutaProgresoViewModel = viewModel()
) {

    // ========================================================
    // CONTEXTO
    // ========================================================

    val context =
        LocalContext.current


    // ========================================================
    // ESTADO DEL VIEWMODEL
    // ========================================================

    val state by
    model
        .state
        .collectAsState()


    // ========================================================
    // FIREBASE FUNCTIONS
    // ========================================================

    val functions =
        remember {

            FirebaseFunctions
                .getInstance()
        }


    // ========================================================
    // PERMISO DE UBICACIÓN
    // ========================================================

    var permisoUbicacion by
    remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
                    ||

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }


    // ========================================================
    // SOLICITAR PERMISOS
    // ========================================================

    val launcherPermisoUbicacion =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts
                    .RequestMultiplePermissions()

        ) { permisos ->


            val permisoPreciso =
                permisos[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] ?: false


            val permisoAproximado =
                permisos[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] ?: false


            permisoUbicacion =
                permisoPreciso ||
                        permisoAproximado
        }


    // ========================================================
    // PEDIR PERMISO AL ENTRAR
    // ========================================================

    LaunchedEffect(Unit) {

        if (
            !permisoUbicacion
        ) {

            launcherPermisoUbicacion.launch(

                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    // ========================================================
    // CARGAR LA RUTA DEL RECOLECTOR
    // ========================================================

    LaunchedEffect(Unit) {

        val recolectorId =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid


        if (
            !recolectorId
                .isNullOrBlank()
        ) {

            model.escucharRutaDe(
                recolectorId
            )
        }
    }


    // ========================================================
    // CLIENTE DE UBICACIÓN
    // ========================================================

    val fusedLocationClient =
        remember {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }


    // ========================================================
    // UBICACIÓN ACTUAL DEL RECOLECTOR
    // ========================================================

    var ubicacionRecolector by
    remember {

        mutableStateOf<Location?>(
            null
        )
    }


    // ========================================================
    // ESCUCHAR GPS EN TIEMPO REAL
    // ========================================================

    DisposableEffect(
        permisoUbicacion
    ) {

        val locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    result
                        .lastLocation
                        ?.let { location ->

                            ubicacionRecolector =
                                location
                        }
                }
            }


        if (
            permisoUbicacion
        ) {

            val locationRequest =
                LocationRequest
                    .Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        5000L
                    )
                    .setMinUpdateDistanceMeters(
                        10f
                    )
                    .build()


            // =================================================
            // ÚLTIMA UBICACIÓN CONOCIDA
            // =================================================

            fusedLocationClient
                .lastLocation
                .addOnSuccessListener { location ->

                    if (
                        location != null
                    ) {

                        ubicacionRecolector =
                            location
                    }
                }


            // =================================================
            // ACTUALIZACIONES DE UBICACIÓN
            // =================================================

            fusedLocationClient
                .requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
        }


        // ====================================================
        // DEJAR DE ESCUCHAR AL SALIR DEL MAPA
        // ====================================================

        onDispose {

            fusedLocationClient
                .removeLocationUpdates(
                    locationCallback
                )
        }
    }


    // ========================================================
    // ORDEN GUARDADO EN LA RUTA
    // ========================================================

    val ordenSolicitudes =
        state.ruta
            ?.ordenSolicitudes
            .orEmpty()


    // ========================================================
    // ORDENAR LAS PARADAS
    //
    // solicitudId se compara con ordenSolicitudes.
    // ========================================================

    val paradasOrdenadas =
        remember(
            state.paradas,
            ordenSolicitudes
        ) {

            if (
                ordenSolicitudes.isEmpty()
            ) {

                state.paradas

            } else {


                // =============================================
                // PARADAS EN EL ORDEN ORIGINAL DE GOOGLE ROUTES
                // =============================================

                val ordenadas =
                    ordenSolicitudes
                        .mapNotNull { solicitudId ->

                            state.paradas
                                .find { parada ->

                                    parada.solicitudId ==
                                            solicitudId
                                }
                        }


                // =============================================
                // POR SEGURIDAD:
                // SI HAY ALGUNA PARADA NO PRESENTE EN EL ORDEN,
                // LA AGREGAMOS AL FINAL.
                // =============================================

                val restantes =
                    state.paradas
                        .filter { parada ->

                            !ordenSolicitudes.contains(
                                parada.solicitudId
                            )
                        }


                ordenadas +
                        restantes
            }
        }


    // ========================================================
    // SABER SI UNA PARADA TERMINÓ
    //
    // Uso estado.name para que no dependamos de que tu enum
    // tenga exactamente una única constante concreta.
    // ========================================================

    fun estaCompletada(
        estado: EstadoParada
    ): Boolean {

        return estado.name.equals(
            "COMPLETADA",
            ignoreCase = true
        ) ||
                estado.name.equals(
                    "RECOLECTADA",
                    ignoreCase = true
                )
    }


    // ========================================================
    // PARADA ACTIVA
    //
    // Es la primera que todavía no está completada.
    // ========================================================

    val paradaActiva =
        paradasOrdenadas
            .firstOrNull { parada ->

                !estaCompletada(
                    parada.estado
                )
            }


    // ========================================================
    // ¿YA TERMINÓ TODA LA RUTA?
    // ========================================================

    val rutaFinalizada =
        paradasOrdenadas
            .isNotEmpty()
                &&
                paradasOrdenadas
                    .all { parada ->

                        estaCompletada(
                            parada.estado
                        )
                    }


    // ========================================================
    // PUNTOS DEL MAPA
    //
    // COMPLETADA -> GRIS
    // ACTUAL     -> NARANJA
    // FUTURAS    -> VERDE
    //
    // Si toda la ruta terminó -> no mandamos marcadores.
    // ========================================================

    val puntosMapa =
        remember(
            paradasOrdenadas,
            paradaActiva?.solicitudId,
            rutaFinalizada
        ) {

            if (
                rutaFinalizada
            ) {

                emptyList()

            } else {

                paradasOrdenadas
                    .map { parada ->


                        val colorParada =
                            when {


                                // =================================
                                // COMPLETADA
                                // =================================

                                estaCompletada(
                                    parada.estado
                                ) -> {

                                    GrisParadaCompletada
                                }


                                // =================================
                                // ACTUAL
                                // =================================

                                parada.solicitudId ==
                                        paradaActiva
                                            ?.solicitudId -> {

                                    NaranjaParadaActiva
                                }


                                // =================================
                                // FUTURA
                                // =================================

                                else -> {

                                    RebottleColors.MainGreen
                                }
                            }


                        PuntoMapa(
                            latitud =
                                parada.latitud,

                            longitud =
                                parada.longitud,

                            color =
                                colorParada
                        )
                    }
            }
        }


    // ========================================================
    // POLYLINE:
    // GPS -> PARADA ACTUAL
    // ========================================================

    var polylineHaciaParada by
    remember {

        mutableStateOf("")
    }


    // ========================================================
    // ÚLTIMA UBICACIÓN DESDE LA QUE CALCULAMOS
    // ========================================================

    var ultimaUbicacionCalculada by
    remember {

        mutableStateOf<Location?>(
            null
        )
    }


    // ========================================================
    // ÚLTIMA PARADA CALCULADA
    // ========================================================

    var ultimoDestinoId by
    remember {

        mutableStateOf<String?>(
            null
        )
    }


    // ========================================================
    // INDICADOR DE CARGA
    // ========================================================

    var calculandoTramo by
    remember {

        mutableStateOf(
            false
        )
    }


    // ========================================================
    // CALCULAR TRAMO:
    //
    // GPS ACTUAL
    //     ↓
    // GOOGLE ROUTES
    //     ↓
    // PARADA ACTIVA
    // ========================================================

    LaunchedEffect(
        ubicacionRecolector?.latitude,
        ubicacionRecolector?.longitude,
        paradaActiva?.solicitudId,
        rutaFinalizada
    ) {

        // ====================================================
        // SI TODA LA RUTA TERMINÓ
        // LIMPIAMOS EL TRAMO ACTIVO.
        // ====================================================

        if (
            rutaFinalizada
        ) {

            polylineHaciaParada =
                ""


            ultimoDestinoId =
                null


            ultimaUbicacionCalculada =
                null


            return@LaunchedEffect
        }


        // ====================================================
        // NECESITAMOS UBICACIÓN GPS
        // ====================================================

        val ubicacion =
            ubicacionRecolector
                ?: return@LaunchedEffect


        // ====================================================
        // NECESITAMOS UNA PARADA ACTIVA
        // ====================================================

        val destino =
            paradaActiva
                ?: run {

                    polylineHaciaParada =
                        ""

                    ultimoDestinoId =
                        null

                    return@LaunchedEffect
                }


        // ====================================================
        // EVITAR CONSULTAS SIMULTÁNEAS
        // ====================================================

        if (
            calculandoTramo
        ) {

            return@LaunchedEffect
        }


        // ====================================================
        // ¿CAMBIÓ LA PARADA?
        // ====================================================

        val cambioDestino =
            ultimoDestinoId !=
                    destino.solicitudId


        // ====================================================
        // DISTANCIA RECORRIDA DESDE ÚLTIMO CÁLCULO
        // ====================================================

        val distanciaDesdeUltimoCalculo =
            ultimaUbicacionCalculada
                ?.distanceTo(
                    ubicacion
                )
                ?: Float.MAX_VALUE


        // ====================================================
        // NO LLAMAMOS GOOGLE ROUTES SI:
        //
        // - seguimos hacia la misma parada
        // - nos hemos movido menos de 75 metros
        // ====================================================

        if (
            !cambioDestino &&
            distanciaDesdeUltimoCalculo <
            75f
        ) {

            return@LaunchedEffect
        }


        // ====================================================
        // COMENZAR CÁLCULO
        // ====================================================

        calculandoTramo =
            true


        // ====================================================
        // DATOS PARA CLOUD FUNCTION
        // ====================================================

        val datos =
            hashMapOf<String, Any>(

                "origenLat" to
                        ubicacion.latitude,

                "origenLng" to
                        ubicacion.longitude,

                "destinoLat" to
                        destino.latitud,

                "destinoLng" to
                        destino.longitud
            )


        // ====================================================
        // LLAMAR CLOUD FUNCTION
        // ====================================================

        functions
            .getHttpsCallable(
                "calcularRutaHaciaParada"
            )
            .call(
                datos
            )
            .addOnSuccessListener { resultado ->


                // =============================================
                // RESPUESTA DE FIREBASE
                // =============================================

                val respuesta =
                    resultado.data
                            as? Map<*, *>


                val nuevaPolyline =
                    respuesta
                        ?.get(
                            "polyline"
                        )
                            as? String


                // =============================================
                // GUARDAR POLYLINE
                // =============================================

                if (
                    !nuevaPolyline
                        .isNullOrBlank()
                ) {

                    polylineHaciaParada =
                        nuevaPolyline


                    // =========================================
                    // GUARDAR UBICACIÓN DEL ÚLTIMO CÁLCULO
                    // =========================================

                    ultimaUbicacionCalculada =
                        Location(
                            ubicacion
                        )


                    // =========================================
                    // GUARDAR PARADA DESTINO
                    // =========================================

                    ultimoDestinoId =
                        destino.solicitudId
                }


                calculandoTramo =
                    false
            }
            .addOnFailureListener { error ->


                println(
                    "Error calculando ruta hacia parada: ${error.message}"
                )


                calculandoTramo =
                    false
            }
    }


    // ========================================================
    // UBICACIÓN PARA RECORTAR LA RUTA VERDE
    // ========================================================

    val ubicacionMapa =
        ubicacionRecolector
            ?.let { ubicacion ->

                LatLng(
                    ubicacion.latitude,
                    ubicacion.longitude
                )
            }


    // ========================================================
    // PANTALLA
    // ========================================================

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {


        // ====================================================
        // TENEMOS RUTA
        // ====================================================

        if (
            state.ruta != null
        ) {


            // =================================================
            // MAPA
            // =================================================

            MapaParadas(

                // =============================================
                // MARCADORES
                //
                // Después de terminar la última parada:
                // []
                // =============================================

                puntos =
                    if (
                        rutaFinalizada
                    ) {

                        emptyList()

                    } else {

                        puntosMapa
                    },


                // =============================================
                // POLYLINE VERDE
                //
                // Después de terminar:
                // ""
                // =============================================

                polyline =
                    if (
                        rutaFinalizada
                    ) {

                        ""

                    } else {

                        state.ruta
                            ?.polyline
                            .orEmpty()
                    },


                // =============================================
                // POLYLINE NARANJA
                //
                // GPS -> siguiente parada
                // =============================================

                polylineActiva =
                    if (
                        rutaFinalizada
                    ) {

                        ""

                    } else {

                        polylineHaciaParada
                    },


                // =============================================
                // GPS PARA RECORTAR LA PARTE YA RECORRIDA
                // =============================================

                ubicacionActual =
                    if (
                        rutaFinalizada
                    ) {

                        null

                    } else {

                        ubicacionMapa
                    },


                // =============================================
                // PUNTO AZUL DE GOOGLE MAPS
                // =============================================

                mostrarMiUbicacion =
                    permisoUbicacion,


                modifier =
                    Modifier.fillMaxSize()
            )


            // =================================================
            // INDICADOR MIENTRAS RECALCULA
            //
            // No aparece si ya terminó la ruta.
            // =================================================

            if (
                calculandoTramo &&
                !rutaFinalizada
            ) {

                Surface(
                    modifier =
                        Modifier
                            .align(
                                Alignment.TopCenter
                            )
                            .padding(
                                top =
                                    52.dp
                            ),

                    color =
                        Color.White,

                    shape =
                        CircleShape,

                    shadowElevation =
                        4.dp
                ) {


                    CircularProgressIndicator(
                        modifier =
                            Modifier.padding(
                                10.dp
                            ),

                        color =
                            RebottleColors.MainGreen
                    )
                }
            }

        } else {


            // =================================================
            // NO HAY RUTA
            // =================================================

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {


                when {


                    // =========================================
                    // CARGANDO
                    // =========================================

                    state.isLoading -> {

                        CircularProgressIndicator(
                            color =
                                RebottleColors.MainGreen
                        )
                    }


                    // =========================================
                    // ERROR
                    // =========================================

                    state.error != null -> {

                        Text(
                            text =
                                state.error
                                    ?: "Error al cargar la ruta",

                            color =
                                RebottleColors.MainText,

                            fontSize =
                                14.sp
                        )
                    }


                    // =========================================
                    // SIN RUTA
                    // =========================================

                    else -> {

                        Text(
                            text =
                                "No tienes una ruta asignada",

                            color =
                                RebottleColors.MainText,

                            fontSize =
                                14.sp
                        )
                    }
                }
            }
        }


        // ====================================================
        // BOTÓN VOLVER
        // ====================================================

        Surface(
            modifier =
                Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .padding(
                        start =
                            16.dp,

                        top =
                            48.dp
                    ),

            shape =
                CircleShape,

            color =
                Color.White,

            shadowElevation =
                6.dp
        ) {


            IconButton(
                onClick = {

                    controller
                        .popBackStack()
                }
            ) {


                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,

                    contentDescription =
                        "Volver",

                    tint =
                        RebottleColors.MainText
                )
            }
        }
    }
}