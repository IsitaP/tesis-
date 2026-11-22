package com.rebottle.app.ui.screens.fundacion.solicitudes

import android.app.TimePickerDialog
import android.widget.Toast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import com.rebottle.app.R
import com.rebottle.app.model.DesasignacionRutaState
import com.rebottle.app.model.EstadoRuta
import com.rebottle.app.model.EstadoSolicitud
import com.rebottle.app.model.MaterialType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rebottle.app.model.MapaViewModel
import com.rebottle.app.model.Ruta
import com.rebottle.app.navigation.AppScreens


// ============================================================
// COLORES
// ============================================================

private val BackgroundColor =
    Color(0xFFFBF8F2)

private val LightOrange =
    Color(0xFFFEF3D7)

private val MainOrange =
    Color(0xFFD99B4B)

private val MainText =
    Color(0xFF34382F)

private val SecondaryText =
    Color(0xFF7F887A)

private val BorderOrange =
    Color(0xFFF5E1B8)

private val BrownIcon =
    Color(0xFFA55E37)

private val ButtonBorder =
    Color(0xFF7F887A)

private val ButtonText =
    Color(0xFF5A6255)

private val PrimaryGreen =
    Color(0xFF657753)


// ============================================================
// PANTALLA DETALLE DE RUTA
// ============================================================

@Composable
fun RutaDetalleScreen(
    navController: NavController,
    rutaId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {
        navController.popBackStack()
    },
    mapaViewModel: MapaViewModel = viewModel()
) {

    val context =
        LocalContext.current


    // ========================================================
    // ESTADO DE RUTAS
    // ========================================================

    val rutasState by
    mapaViewModel
        .rutasState
        .collectAsState()


    // ========================================================
    // ESTADO DE DESASIGNACIÓN
    // ========================================================

    val desasignacionState by
    mapaViewModel
        .desasignacionState
        .collectAsState()


    // ========================================================
    // ESCUCHAR RUTAS
    // ========================================================

    LaunchedEffect(Unit) {

        mapaViewModel
            .escucharRutasPublicadas()
    }


    // ========================================================
    // RESULTADO DE DESASIGNACIÓN
    // ========================================================

    LaunchedEffect(
        desasignacionState.desasignacionExitosa,
        desasignacionState.error
    ) {

        if (
            desasignacionState
                .desasignacionExitosa
        ) {

            Toast.makeText(
                context,
                "Recolector desasignado correctamente",
                Toast.LENGTH_SHORT
            ).show()


            mapaViewModel
                .limpiarEstadoDesasignacion()
        }

        else if (
            desasignacionState.error != null
        ) {

            Toast.makeText(
                context,
                desasignacionState.error
                    ?: "No se pudo desasignar el recolector",
                Toast.LENGTH_LONG
            ).show()


            mapaViewModel
                .limpiarEstadoDesasignacion()
        }
    }


    // ========================================================
    // BUSCAR RUTA
    // ========================================================

    val ruta =
        rutasState
            .rutasPublicadas
            .find {

                it.id == rutaId
            }


    // ========================================================
    // SCAFFOLD
    // ========================================================

    Scaffold(
        modifier =
            modifier.fillMaxSize(),

        containerColor =
            BackgroundColor,

        topBar = {

            RecoleccionHeader(
                rutaId = rutaId,
                onBackClick = onBackClick
            )
        }

    ) { innerPadding ->


        // ====================================================
        // CARGANDO
        // ====================================================

        if (
            rutasState.isLoading &&
            ruta == null
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        PrimaryGreen
                )
            }


            return@Scaffold
        }


        // ====================================================
        // ERROR
        // ====================================================

        if (
            rutasState.error != null &&
            ruta == null
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        rutasState.error
                            ?: "Error al cargar la ruta",

                    color =
                        MainText,

                    fontSize =
                        14.sp
                )
            }


            return@Scaffold
        }


        // ====================================================
        // RUTA NO ENCONTRADA
        // ====================================================

        if (ruta == null) {

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "No se encontró la ruta $rutaId",

                    color =
                        SecondaryText,

                    fontSize =
                        14.sp
                )
            }


            return@Scaffold
        }


        // ====================================================
        // CONTENIDO
        // ====================================================

        RecoleccionDetalleContent(
            controller =
                navController,

            ruta =
                ruta,

            mapaViewModel =
                mapaViewModel,

            desasignacionState =
                desasignacionState,

            innerPadding =
                innerPadding
        )
    }
}


// ============================================================
// CONTENIDO
// ============================================================

@Composable
private fun RecoleccionDetalleContent(
    controller: NavController,
    ruta: Ruta,
    mapaViewModel: MapaViewModel,
    desasignacionState: DesasignacionRutaState,
    innerPadding: PaddingValues
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
    ) {


        // ====================================================
        // CONTENIDO PRINCIPAL
        // ====================================================

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // =================================================
            // CARD DETALLE
            // =================================================

            val context =
                LocalContext.current

            RecoleccionDetailCard(
                ruta =
                    ruta,

                controller =
                    controller,

                onCambiarHora = { hora ->
                    mapaViewModel.actualizarHoraRuta(
                        rutaId = ruta.id,
                        hora = hora,
                        onError = { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // =================================================
            // ASIGNAR RECOLECTOR
            // =================================================

            Button(
                onClick = {

                    controller.navigate(
                        "${AppScreens.asignarRecolector.name}/${ruta.id}"
                    )
                },

                enabled =
                    ruta.estado ==
                            EstadoRuta.PUBLICADA.name,

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
                    ButtonDefaults
                        .buttonColors(

                            containerColor =
                                PrimaryGreen,

                            disabledContainerColor =
                                Color(0xFFD3D3D3),

                            disabledContentColor =
                                Color(0xFF777777)
                        ),

                elevation =
                    ButtonDefaults
                        .buttonElevation(

                            defaultElevation =
                                2.dp
                        )
            ) {


                Text(
                    text =

                        if (
                            ruta.estado ==
                            EstadoRuta.PUBLICADA.name
                        ) {

                            "Asignar recolector"

                        } else if (
                            ruta.estado ==
                            EstadoRuta.CANCELADA.name
                        ) {

                            "Ruta cancelada"

                        } else {

                            "Recolector asignado"
                        },

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            // Explica por qué el botón está deshabilitado
            val motivoSinAsignar =
                when (ruta.estado) {
                    EstadoRuta.ASIGNADA.name ->
                        "Esta ruta ya tiene recolector. Para cambiarlo, primero desasígnalo."
                    EstadoRuta.EN_CURSO.name ->
                        "La ruta está en curso, por eso no se puede cambiar el recolector."
                    EstadoRuta.COMPLETADA.name ->
                        "La ruta ya fue completada."
                    EstadoRuta.CANCELADA.name ->
                        "La ruta fue cancelada, por eso no se puede asignar un recolector."
                    else -> null
                }

            if (motivoSinAsignar != null) {

                Text(
                    text = motivoSinAsignar,
                    fontSize = 13.sp,
                    color = Color(0xFF777777),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                )
            }


            // =================================================
            // DESASIGNAR RECOLECTOR
            // =================================================

            if (
                ruta.estado ==
                EstadoRuta.ASIGNADA.name
            ) {


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        mapaViewModel
                            .desasignarRecolectorDeRuta(

                                rutaId =
                                    ruta.id
                            )
                    },

                    enabled =
                        !desasignacionState
                            .isLoading,

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

                    border =
                        BorderStroke(
                            width =
                                1.dp,

                            color =
                                PrimaryGreen
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(

                                containerColor =
                                    Color.White,

                                contentColor =
                                    PrimaryGreen
                            ),

                    elevation =
                        ButtonDefaults
                            .buttonElevation(

                                defaultElevation =
                                    2.dp
                            )
                ) {


                    if (
                        desasignacionState
                            .isLoading
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    18.dp
                                ),

                            color =
                                PrimaryGreen,

                            strokeWidth =
                                2.dp
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Text(
                            text =
                                "Desasignando...",

                            color =
                                PrimaryGreen,

                            fontSize =
                                15.sp
                        )

                    } else {

                        Text(
                            text =
                                "Desasignar recolector",

                            color =
                                PrimaryGreen,

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )


        // ====================================================
        // PAISAJE
        //
        // weight(1f) hace que use solamente el espacio
        // que quede disponible en la pantalla; heightIn
        // lo limita para que no ocupe tanto.
        // ====================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),

            contentAlignment =
                Alignment.BottomCenter
        ) {

            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .solicitudes_paisaje
                    ),

                contentDescription =
                    null,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp),

                contentScale =
                    ContentScale.Crop,

                alignment =
                    Alignment.BottomCenter
            )
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun RecoleccionHeader(
    rutaId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    top = 16.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 12.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Icon(
            painter =
                painterResource(
                    id =
                        R.drawable.ic_arrow_back
                ),

            contentDescription =
                "Volver",

            modifier =
                Modifier
                    .size(
                        24.dp
                    )
                    .clickable {

                        onBackClick()
                    },

            tint =
                MainText
        )


        Spacer(
            modifier =
                Modifier.width(
                    16.dp
                )
        )


        Text(
            text =
                "Ruta $rutaId",

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                MainText
        )
    }
}


// ============================================================
// CARD DETALLE DE RUTA
// ============================================================

@Composable
private fun RecoleccionDetailCard(
    ruta: Ruta,
    controller: NavController,
    onCambiarHora: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context =
        LocalContext.current

    // Reloj para elegir la hora de salida de la ruta
    val abrirReloj = {
        val actual = horaDeTexto(ruta.hora)
        TimePickerDialog(
            context,
            { _, h, m -> onCambiarHora("%02d:%02d".format(h, m)) },
            actual?.first ?: 8,
            actual?.second ?: 0,
            false
        ).show()
    }

    // ========================================================
    // DISTANCIA
    // ========================================================

    val distanciaKm =
        ruta.distanciaTotalMetros /
                1000.0


    // ========================================================
    // DURACIÓN
    // ========================================================

    val duracionMinutos =

        if (
            ruta.duracionTotalSegundos <= 0
        ) {

            0

        } else {

            (
                    ruta.duracionTotalSegundos +
                            59
                    ) / 60
        }


    // ========================================================
    // ORDEN DE SOLICITUDES
    // ========================================================

    val solicitudesOrdenadas =

        if (
            ruta.ordenSolicitudes
                .isNotEmpty()
        ) {

            ruta.ordenSolicitudes

        } else {

            ruta.solicitudesIds
        }


    // ========================================================
    // CARD
    // ========================================================

    Card(
        modifier =
            modifier
                .fillMaxWidth(),

        shape =
            RoundedCornerShape(
                28.dp
            ),

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        LightOrange
                ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    BorderOrange
            ),

        elevation =
            CardDefaults
                .cardElevation(

                    defaultElevation =
                        0.dp
                )
    ) {


        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {


            // =================================================
            // ID + ESTADO
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Text(
                    text =
                        "#${ruta.id}",

                    fontSize =
                        19.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainText
                )


                StatusBadge(
                    text =
                        formatearEstadoRuta(
                            ruta.estado
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // PUNTOS
            // =================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .ic_location_pin
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            19.dp
                        ),

                    tint =
                        BrownIcon
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Text(
                    text =
                        "${solicitudesOrdenadas.size} puntos de recolección",

                    fontSize =
                        13.sp,

                    color =
                        MainText
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        9.dp
                    )
            )


            // =================================================
            // FECHA + DURACIÓN
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        20.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // =============================================
                // FECHA
                // =============================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .ic_calendar
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                19.dp
                            ),

                        tint =
                            BrownIcon
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            formatearFecha(
                                ruta.fechaRecoleccion
                            ),

                        fontSize =
                            13.sp,

                        color =
                            MainText
                    )
                }


                // =============================================
                // DURACIÓN
                // =============================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .ic_clock
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                19.dp
                            ),

                        tint =
                            BrownIcon
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "$duracionMinutos min",

                        fontSize =
                            13.sp,

                        color =
                            MainText
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        9.dp
                    )
            )


            // =================================================
            // HORA DE LA RUTA
            // =================================================

            Row(
                modifier =
                    Modifier.clickable { abrirReloj() },

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    painter =
                        painterResource(
                            id = R.drawable.ic_clock
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(19.dp),

                    tint =
                        BrownIcon
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        formatearHora(ruta.hora)
                            ?.let { "Hora: $it" }
                            ?: "Sin hora definida",

                    fontSize =
                        13.sp,

                    color =
                        MainText
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        if (ruta.hora.isBlank()) "Definir hora" else "Cambiar",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        PrimaryGreen
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // SOLICITUDES
            // =================================================

            Text(
                text =
                    "Solicitudes de la ruta",

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    MainText
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        5.dp
                    )
            ) {


                solicitudesOrdenadas
                    .forEachIndexed {
                            index,
                            solicitudId ->


                        SolicitudDeRutaItem(
                            numero = index + 1,
                            solicitudId = solicitudId
                        )
                    }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // DISTANCIA + ESTADO
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // =============================================
                // DISTANCIA
                // =============================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .icon_routes
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                32.dp
                            ),

                        tint =
                            BrownIcon
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    Column {


                        Text(
                            text =
                                "Distancia estimada",

                            fontSize =
                                12.sp,

                            color =
                                SecondaryText
                        )


                        Text(
                            text =
                                String.format(
                                    "%.1f KM",
                                    distanciaKm
                                ),

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                MainText
                        )
                    }
                }


                // =============================================
                // ESTADO
                // =============================================

                Column {


                    Text(
                        text =
                            "Estado",

                        fontSize =
                            12.sp,

                        color =
                            SecondaryText
                    )


                    Text(
                        text =
                            formatearEstadoRuta(
                                ruta.estado
                            ),

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            MainText
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // =================================================
            // BOTONES DEL CARD
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {


                // =============================================
                // VER RECORRIDO
                // =============================================

                // Abre el mapa y detalle del recolector asignado a esta ruta
                OutlinedButton(
                    onClick = {

                        ruta.recolectorId
                            ?.takeIf { it.isNotBlank() }
                            ?.let { recolectorId ->

                                controller.navigate(
                                    "${AppScreens.detalleRecolectorRuta.name}/$recolectorId"
                                )
                            }
                    },

                    enabled =
                        !ruta.recolectorId.isNullOrBlank(),

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            ButtonBorder
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(

                                containerColor =
                                    Color.White,

                                contentColor =
                                    ButtonText
                            ),

                    contentPadding =
                        PaddingValues(
                            horizontal =
                                6.dp,

                            vertical =
                                8.dp
                        )
                ) {


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable
                                            .ic_location_pin
                                ),

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(
                                    21.dp
                                ),

                            tint =
                                BrownIcon
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    5.dp
                                )
                        )


                        Text(
                            text =
                                "Ver recorrido",

                            fontSize =
                                11.sp,

                            color =
                                ButtonText,

                            lineHeight =
                                13.sp,

                            textAlign =
                                TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// SOLICITUD DE LA RUTA (se despliega al tocarla)
// ============================================================

private data class InfoSolicitudRuta(
    val direccion: String,
    val materiales: String,
    val pesoKg: Long,
    val estado: String
)

@Composable
private fun SolicitudDeRutaItem(
    numero: Int,
    solicitudId: String
) {

    var expandida by remember(solicitudId) { mutableStateOf(false) }
    var info by remember(solicitudId) { mutableStateOf<InfoSolicitudRuta?>(null) }
    var error by remember(solicitudId) { mutableStateOf(false) }

    // Solo se consulta la solicitud la primera vez que se abre
    LaunchedEffect(expandida, solicitudId) {
        if (expandida && info == null) {
            error = false
            Firebase.firestore.collection("solicitudes").document(solicitudId).get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        error = true
                        return@addOnSuccessListener
                    }
                    val direccion =
                        doc.getString("nombreLugar")?.ifBlank { null }
                            ?.let { lugar ->
                                doc.getString("direccion")?.ifBlank { null }
                                    ?.let { "$lugar · $it" } ?: lugar
                            }
                            ?: doc.getString("direccion")?.ifBlank { null }
                            ?: "Sin dirección"
                    val materiales =
                        (doc.get("materiales") as? List<*>)
                            ?.mapNotNull { it as? String }
                            ?.joinToString(", ") { nombre ->
                                MaterialType.values().firstOrNull { it.name == nombre }?.label ?: nombre
                            }
                            ?.ifBlank { null }
                            ?: "Sin materiales"
                    info = InfoSolicitudRuta(
                        direccion = direccion,
                        materiales = materiales,
                        pesoKg = doc.getLong("pesoKg") ?: 0L,
                        estado = estadoSolicitudLegible(doc.getString("estado"))
                    )
                }
                .addOnFailureListener { error = true }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { expandida = !expandida }
                .padding(vertical = 2.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier
                        .size(22.dp)
                        .background(color = BrownIcon, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$numero",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = solicitudId,
                fontSize = 13.sp,
                color = SecondaryText,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (expandida) "Ocultar ▲" else "Ver detalle ▼",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BrownIcon
            )
        }

        if (expandida) {

            Column(
                modifier = Modifier.padding(start = 30.dp, top = 4.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val datos = info
                when {
                    error ->
                        Text("No se pudo cargar la solicitud", fontSize = 12.sp, color = SecondaryText)
                    datos == null ->
                        Text("Cargando...", fontSize = 12.sp, color = SecondaryText)
                    else -> {
                        Text("Dirección: ${datos.direccion}", fontSize = 12.sp, color = MainText)
                        Text("Material: ${datos.materiales}", fontSize = 12.sp, color = MainText)
                        Text("Peso: ${datos.pesoKg} kg", fontSize = 12.sp, color = MainText)
                        Text("Estado: ${datos.estado}", fontSize = 12.sp, color = MainText)
                    }
                }
            }
        }
    }
}

private fun estadoSolicitudLegible(estado: String?): String =
    when (estado) {
        EstadoSolicitud.PUBLICADA.name -> "Publicada"
        EstadoSolicitud.EN_RUTA.name -> "En ruta"
        EstadoSolicitud.RECOLECTADA.name -> "Recolectada"
        EstadoSolicitud.CANCELADA.name -> "Cancelada"
        null -> "Sin estado"
        else -> estado
    }


// ============================================================
// BADGE
// ============================================================

@Composable
private fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier
) {

    Box(
        modifier =
            modifier
                .background(
                    color =
                        MainOrange,

                    shape =
                        RoundedCornerShape(
                            8.dp
                        )
                )
                .padding(
                    horizontal =
                        12.dp,

                    vertical =
                        5.dp
                )
    ) {


        Text(
            text =
                text,

            color =
                Color.White,

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// ============================================================
// FORMATEAR FECHA
// ============================================================

// "14:30" -> (14, 30). null si no hay hora válida.
private fun horaDeTexto(
    hora: String
): Pair<Int, Int>? {

    val partes = hora.split(":")

    val h = partes.getOrNull(0)?.trim()?.toIntOrNull() ?: return null
    val m = partes.getOrNull(1)?.trim()?.toIntOrNull() ?: return null

    return if (h in 0..23 && m in 0..59) h to m else null
}


// "14:30" -> "2:30 PM"
private fun formatearHora(
    hora: String
): String? {

    val (h, m) = horaDeTexto(hora) ?: return null

    val sufijo = if (h < 12) "AM" else "PM"
    val h12 = if (h % 12 == 0) 12 else h % 12

    return "%d:%02d %s".format(h12, m, sufijo)
}


private fun formatearFecha(
    fecha: String
): String {

    val partes =
        fecha.split("-")


    return if (
        partes.size == 3
    ) {

        "${partes[2]}/${partes[1]}/${partes[0]}"

    } else {

        fecha
    }
}


// ============================================================
// FORMATEAR ESTADO
// ============================================================

private fun formatearEstadoRuta(
    estado: String
): String {

    return when (estado) {

        EstadoRuta.PUBLICADA.name ->
            "Publicada"

        EstadoRuta.ASIGNADA.name ->
            "Asignada"

        EstadoRuta.EN_CURSO.name ->
            "En curso"

        EstadoRuta.COMPLETADA.name ->
            "Completada"

        EstadoRuta.CANCELADA.name ->
            "Cancelada"

        else ->
            estado
    }
}