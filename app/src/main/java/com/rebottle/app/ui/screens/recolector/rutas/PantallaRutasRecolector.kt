package com.rebottle.app.ui.screens.recolector.rutas

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

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
import androidx.compose.ui.text.font.FontWeight
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
import com.rebottle.app.ui.screens.recolector.solicitudes.aAsignacion
import com.rebottle.app.ui.screens.recolector.solicitudes.formatearFecha

import com.rebottle.app.ui.theme.RebottleColors


// ============================================================
// COLORES
// ============================================================

private val NaranjaParadaActiva =
    Color(0xFFD99B4B)

private val GrisParadaCompletada =
    Color(0xFF9E9E9E)


// ============================================================
// MAPA DE RUTA DEL RECOLECTOR
// ============================================================

@SuppressLint("MissingPermission")
@Composable
fun MapaRutaRecolectorScreen(
    controller: NavController,
    model: RutaProgresoViewModel = viewModel()
) {

    val context =
        LocalContext.current






    // ========================================================
    // ESTADO DEL VIEWMODEL
    // ========================================================

    val state by
    model.state.collectAsState()

    // ========================================================
    // FIREBASE FUNCTIONS
    // ========================================================

    val functions =
        remember {
            FirebaseFunctions.getInstance()
        }


    // ========================================================
    // MARKER SELECCIONADO
    // ========================================================

    var solicitudSeleccionadaId by
    remember {
        mutableStateOf<String?>(null)
    }


    // ========================================================
    // ESTADO DEL AVISO DE LLEGADA
    // ========================================================

    var llegadaEnProcesoId by remember {
        mutableStateOf<String?>(null)
    }

    var llegadasRegistradas by remember {
        mutableStateOf(setOf<String>())
    }

    var mensajesLlegada by remember {
        mutableStateOf(mapOf<String, String>())
    }


    // ========================================================
    // PERMISOS DE UBICACIÓN
    // ========================================================

    var permisoUbicacion by
    remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }


    val launcherPermisoUbicacion =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
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


    LaunchedEffect(Unit) {

        if (!permisoUbicacion) {

            launcherPermisoUbicacion.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    // ========================================================
    // ESCUCHAR RUTA DEL RECOLECTOR
    // ========================================================

    LaunchedEffect(Unit) {

        val recolectorId =
            FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid

        if (!recolectorId.isNullOrBlank()) {

            model.escucharRutaDe(
                recolectorId
            )
        }
    }


    // ========================================================
    // GPS
    // ========================================================

    val fusedLocationClient =
        remember {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }


    var ubicacionRecolector by
    remember {
        mutableStateOf<Location?>(null)
    }


    DisposableEffect(
        permisoUbicacion
    ) {

        val locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    result.lastLocation?.let { location ->

                        ubicacionRecolector =
                            location
                    }
                }
            }


        if (permisoUbicacion) {

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


            fusedLocationClient
                .lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {

                        ubicacionRecolector =
                            location
                    }
                }


            fusedLocationClient
                .requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
        }


        onDispose {

            fusedLocationClient
                .removeLocationUpdates(
                    locationCallback
                )
        }
    }


    // ========================================================
    // ORDEN DE SOLICITUDES
    // ========================================================

    val ordenSolicitudes =
        state.ruta
            ?.ordenSolicitudes
            .orEmpty()


    // ========================================================
    // ORDENAR PARADAS
    // ========================================================

    val paradasOrdenadas =
        remember(
            state.paradas,
            ordenSolicitudes
        ) {

            if (ordenSolicitudes.isEmpty()) {

                state.paradas

            } else {

                val ordenadas =
                    ordenSolicitudes
                        .mapNotNull { solicitudId ->

                            state.paradas
                                .find { parada ->

                                    parada.solicitudId ==
                                            solicitudId
                                }
                        }


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
    // SABER SI ESTÁ COMPLETADA
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
    // ========================================================

    val paradaActiva =
        paradasOrdenadas
            .firstOrNull { parada ->

                !estaCompletada(
                    parada.estado
                )
            }


    // ========================================================
    // PARADA SELECCIONADA AL TOCAR MARKER
    // ========================================================

    val paradaSeleccionada =
        paradasOrdenadas
            .firstOrNull { parada ->

                parada.solicitudId ==
                        solicitudSeleccionadaId
            }


    // ========================================================
    // ¿TERMINÓ TODA LA RUTA?
    // ========================================================

    val rutaFinalizada =
        paradasOrdenadas.isNotEmpty()
                &&
                paradasOrdenadas.all { parada ->

                    estaCompletada(
                        parada.estado
                    )
                }


    // ========================================================
    // MARCADORES
    // ========================================================

    val puntosMapa =
        remember(
            paradasOrdenadas,
            paradaActiva?.solicitudId,
            rutaFinalizada
        ) {

            if (rutaFinalizada) {

                emptyList()

            } else {

                paradasOrdenadas.map { parada ->

                    val colorParada =
                        when {

                            estaCompletada(
                                parada.estado
                            ) -> {

                                GrisParadaCompletada
                            }


                            parada.solicitudId ==
                                    paradaActiva?.solicitudId -> {

                                NaranjaParadaActiva
                            }


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
                            colorParada,

                        // MUY IMPORTANTE
                        solicitudId =
                            parada.solicitudId
                    )
                }
            }
        }


    // ========================================================
    // POLYLINE GPS -> PARADA ACTIVA
    // ========================================================

    var polylineHaciaParada by
    remember {
        mutableStateOf("")
    }


    var ultimaUbicacionCalculada by
    remember {
        mutableStateOf<Location?>(null)
    }


    var ultimoDestinoId by
    remember {
        mutableStateOf<String?>(null)
    }


    var calculandoTramo by
    remember {
        mutableStateOf(false)
    }


    // ========================================================
    // CALCULAR RUTA HACIA LA PARADA ACTUAL
    // ========================================================

    LaunchedEffect(
        ubicacionRecolector?.latitude,
        ubicacionRecolector?.longitude,
        paradaActiva?.solicitudId,
        rutaFinalizada
    ) {

        // ====================================================
        // RUTA TERMINADA
        // ====================================================

        if (rutaFinalizada) {

            polylineHaciaParada =
                ""

            ultimoDestinoId =
                null

            ultimaUbicacionCalculada =
                null

            solicitudSeleccionadaId =
                null

            return@LaunchedEffect
        }


        val ubicacion =
            ubicacionRecolector
                ?: return@LaunchedEffect


        val destino =
            paradaActiva
                ?: run {

                    polylineHaciaParada =
                        ""

                    ultimoDestinoId =
                        null

                    return@LaunchedEffect
                }


        if (calculandoTramo) {

            return@LaunchedEffect
        }


        // ====================================================
        // ¿CAMBIÓ LA PARADA?
        // ====================================================

        val cambioDestino =
            ultimoDestinoId !=
                    destino.solicitudId


        // ====================================================
        // ¿CUÁNTO SE MOVIÓ?
        // ====================================================

        val distanciaDesdeUltimoCalculo =
            ultimaUbicacionCalculada
                ?.distanceTo(
                    ubicacion
                )
                ?: Float.MAX_VALUE


        // ====================================================
        // SOLO RECALCULAMOS DESPUÉS DE 75 METROS
        // ====================================================

        if (
            !cambioDestino &&
            distanciaDesdeUltimoCalculo < 75f
        ) {

            return@LaunchedEffect
        }


        calculandoTramo =
            true


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


        functions
            .getHttpsCallable(
                "calcularRutaHaciaParada"
            )
            .call(
                datos
            )
            .addOnSuccessListener { resultado ->

                val respuesta =
                    resultado.data
                            as? Map<*, *>


                val nuevaPolyline =
                    respuesta
                        ?.get("polyline")
                            as? String


                if (!nuevaPolyline.isNullOrBlank()) {

                    polylineHaciaParada =
                        nuevaPolyline


                    ultimaUbicacionCalculada =
                        Location(
                            ubicacion
                        )


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
    // UBICACIÓN PARA RECORTAR RUTA VERDE
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
        // HAY RUTA
        // ====================================================

        if (state.ruta != null) {

            // =================================================
            // MAPA
            // =================================================

            MapaParadas(

                puntos =
                    if (rutaFinalizada) {
                        emptyList()
                    } else {
                        puntosMapa
                    },


                polyline =
                    if (rutaFinalizada) {

                        ""

                    } else {

                        state.ruta
                            ?.polyline
                            .orEmpty()
                    },


                polylineActiva =
                    if (rutaFinalizada) {

                        ""

                    } else {

                        polylineHaciaParada
                    },


                ubicacionActual =
                    if (rutaFinalizada) {

                        null

                    } else {

                        ubicacionMapa
                    },


                mostrarMiUbicacion =
                    permisoUbicacion,


                // =============================================
                // CLICK EN MARKER
                // =============================================

                onPuntoClick = { punto ->

                    solicitudSeleccionadaId =
                        punto.solicitudId
                },


                modifier =
                    Modifier.fillMaxSize()
            )


            // =================================================
            // CARD DEL MARKER SELECCIONADO
            // =================================================

            if (
                paradaSeleccionada != null &&
                !rutaFinalizada
            ) {

                val esCompletada =
                    estaCompletada(
                        paradaSeleccionada.estado
                    )


                val esParadaActual =
                    paradaSeleccionada.solicitudId ==
                            paradaActiva?.solicitudId


                Card(
                    modifier =
                        Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 32.dp
                            )
                            .fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                8.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {

                        // =====================================
                        // TÍTULO
                        // =====================================

                        Text(
                            text =
                                "Recolección ${paradaSeleccionada.solicitudId}",

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                RebottleColors.MainText
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    6.dp
                                )
                        )


                        // =====================================
                        // COMPLETADA
                        // =====================================

                        if (esCompletada) {

                            Text(
                                text =
                                    "✓ Recolección completada",

                                color =
                                    RebottleColors.MainGreen,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }


                        // =====================================
                        // PARADA ACTUAL
                        // =====================================

                        else if (esParadaActual) {

                            Text(
                                text =
                                    "Esta es tu parada actual",

                                color =
                                    NaranjaParadaActiva,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        14.dp
                                    )
                            )


                            // =====================================
                            // HE LLEGADO - AVISAR AL CIUDADANO
                            // =====================================

                            val solicitudId = paradaSeleccionada.solicitudId
                            val llegadaRegistrada =
                                solicitudId in llegadasRegistradas

                            Button(
                                onClick = {
                                    llegadaEnProcesoId = solicitudId
                                    mensajesLlegada = mensajesLlegada - solicitudId

                                    functions
                                        .getHttpsCallable("avisarLlegadaRecolector")
                                        .call(mapOf("solicitudId" to solicitudId))
                                        .addOnSuccessListener { resultado ->
                                            llegadaEnProcesoId = null
                                            val datos = resultado.data as? Map<*, *>

                                            if (datos?.get("registrada") == true) {
                                                llegadasRegistradas =
                                                    llegadasRegistradas + solicitudId

                                                val mensaje = when {
                                                    datos["yaRegistrada"] == true ->
                                                        "La llegada ya estaba registrada"
                                                    datos["notificacionEnviada"] == true ->
                                                        "Llegada registrada. Usuario notificado"
                                                    else ->
                                                        "Llegada registrada, pero no se envió el aviso"
                                                }
                                                mensajesLlegada =
                                                    mensajesLlegada + (solicitudId to mensaje)
                                            } else {
                                                mensajesLlegada = mensajesLlegada +
                                                        (solicitudId to "No se pudo registrar la llegada")
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            llegadaEnProcesoId = null
                                            mensajesLlegada = mensajesLlegada +
                                                    (solicitudId to (
                                                            error.localizedMessage
                                                                ?: "Error al registrar la llegada"
                                                            ))
                                        }
                                },
                                enabled =
                                    llegadaEnProcesoId == null &&
                                            !llegadaRegistrada &&
                                            state.resolviendoId == null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NaranjaParadaActiva
                                )
                            ) {
                                Text(
                                    text = when {
                                        llegadaEnProcesoId == solicitudId ->
                                            "Avisando llegada..."
                                        llegadaRegistrada ->
                                            "✓ Llegada registrada"
                                        else -> "He llegado"
                                    },
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            mensajesLlegada[solicitudId]?.let { mensaje ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = mensaje,
                                    fontSize = 13.sp,
                                    color = RebottleColors.MainText
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    model.completarParada(paradaSeleccionada.solicitudId)
                                },

                                enabled =
                                    state.resolviendoId ==
                                            null,

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(
                                            48.dp
                                        ),

                                shape =
                                    RoundedCornerShape(
                                        14.dp
                                    ),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            RebottleColors.MainGreen
                                    )
                            ) {

                                Text(
                                    text =
                                        "Marcar como finalizada",

                                    color =
                                        Color.White,

                                    fontSize =
                                        15.sp,

                                    fontWeight =
                                        FontWeight.Medium
                                )
                            }
                        }


                        // =====================================
                        // PARADA FUTURA
                        // =====================================

                        else {

                            Text(
                                text =
                                    "Completa primero la parada anterior",

                                color =
                                    Color(0xFF7F887A),

                                fontSize =
                                    14.sp
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )


                        // =====================================
                        // CERRAR AVISO
                        // =====================================

                        TextButton(
                            onClick = {

                                solicitudSeleccionadaId =
                                    null
                            },

                            modifier =
                                Modifier.align(
                                    Alignment.End
                                )
                        ) {

                            Text(
                                text =
                                    "Cerrar",

                                color =
                                    RebottleColors.MainGreen
                            )
                        }
                    }
                }
            }


            // =================================================
            // CARGANDO NUEVO TRAMO
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
                                top = 52.dp
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

                    state.isLoading -> {

                        CircularProgressIndicator(
                            color =
                                RebottleColors.MainGreen
                        )
                    }


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
                        start = 16.dp,
                        top = 48.dp
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

                    controller.popBackStack()
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
