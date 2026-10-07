package com.rebottle.app.ui.screens.fundacion.solicitudes

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.rebottle.app.R
import com.rebottle.app.model.EstadoRuta
import com.rebottle.app.model.MapaViewModel
import com.rebottle.app.model.Ruta
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme


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

    // ========================================================
    // ESTADO DEL VIEWMODEL
    // ========================================================

    val rutasState by
    mapaViewModel
        .rutasState
        .collectAsState()


    // ========================================================
    // ESCUCHAR RUTAS
    // ========================================================

    LaunchedEffect(Unit) {

        mapaViewModel
            .escucharRutasPublicadas()
    }


    // ========================================================
    // BUSCAR LA RUTA
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
                    color = PrimaryGreen
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
            ruta = ruta,
            navController = navController,
            innerPadding = innerPadding
        )
    }
}


// ============================================================
// CONTENIDO
// ============================================================

@Composable
private fun RecoleccionDetalleContent(
    ruta: Ruta,
    navController: NavController,
    innerPadding: PaddingValues = PaddingValues()
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                ),

        verticalArrangement =
            Arrangement.SpaceBetween
    ) {


        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // CARD DE DETALLE
            // =================================================

            RecoleccionDetailCard(
                ruta = ruta,
                controller = navController
            )


            Spacer(
                modifier =
                    Modifier.height(
                        28.dp
                    )
            )


            // =================================================
            // ASIGNAR RECOLECTOR
            // =================================================

            Button(
                onClick = {

                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            52.dp
                        ),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                PrimaryGreen
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
                        "Asignar recolector",

                    color =
                        Color.White,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // CANCELAR RUTA
            // =================================================

            OutlinedButton(
                onClick = {

                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            52.dp
                        ),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                border =
                    BorderStroke(
                        1.dp,
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

                Text(
                    text =
                        "Cancelar ruta",

                    color =
                        PrimaryGreen,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )
        }


        // ====================================================
        // PAISAJE
        // ====================================================

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.solicitudes_paisaje
                ),

            contentDescription =
                null,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        220.dp
                    ),

            contentScale =
                ContentScale.FillWidth
        )
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
                .padding(
                    top = 20.dp,
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
                null,

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
    modifier: Modifier = Modifier
) {

    // ========================================================
    // DATOS FORMATEADOS
    // ========================================================

    val distanciaKm =
        ruta.distanciaTotalMetros /
                1000.0


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


    val solicitudesOrdenadas =
        if (
            ruta.ordenSolicitudes.isNotEmpty()
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
            modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                28.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    LightOrange
            ),

        border =
            BorderStroke(
                1.dp,
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
                    20.dp
                )
        ) {


            // =================================================
            // ID Y ESTADO
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
                        20.sp,

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
                        16.dp
                    )
            )


            // =================================================
            // PUNTOS DE RECOLECCIÓN
            // =================================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable.ic_location_pin
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            20.dp
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
                        "${solicitudesOrdenadas.size} " +
                                "puntos de recolección",

                    fontSize =
                        14.sp,

                    color =
                        MainText
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // FECHA Y DURACIÓN
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        24.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // ---------------------------------------------
                // FECHA
                // ---------------------------------------------

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.ic_calendar
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                20.dp
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
                            14.sp,

                        color =
                            MainText
                    )
                }


                // ---------------------------------------------
                // DURACIÓN
                // ---------------------------------------------

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.ic_clock
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                20.dp
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
                            14.sp,

                        color =
                            MainText
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // SOLICITUDES
            // =================================================

            Text(
                text =
                    "Solicitudes de la ruta",

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    MainText
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // =================================================
            // ORDEN DE SOLICITUDES
            // =================================================

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {


                solicitudesOrdenadas
                    .forEachIndexed {
                            index,
                            solicitudId ->


                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {


                            Box(
                                modifier =
                                    Modifier
                                        .size(
                                            24.dp
                                        )
                                        .background(
                                            BrownIcon,
                                            RoundedCornerShape(
                                                50
                                            )
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {


                                Text(
                                    text =
                                        "${index + 1}",

                                    color =
                                        Color.White,

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        8.dp
                                    )
                            )


                            Text(
                                text =
                                    solicitudId,

                                fontSize =
                                    14.sp,

                                color =
                                    SecondaryText
                            )
                        }
                    }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // DISTANCIA Y ESTADO
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // ---------------------------------------------
                // DISTANCIA
                // ---------------------------------------------

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        painter =
                            painterResource(
                                id =
                                    R.drawable.icon_routes
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                36.dp
                            ),

                        tint =
                            BrownIcon
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                10.dp
                            )
                    )


                    Column {


                        Text(
                            text =
                                "Distancia estimada",

                            fontSize =
                                13.sp,

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
                                14.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                MainText
                        )
                    }
                }


                // ---------------------------------------------
                // ESTADO
                // ---------------------------------------------

                Column {


                    Text(
                        text =
                            "Estado",

                        fontSize =
                            13.sp,

                        color =
                            SecondaryText
                    )


                    Text(
                        text =
                            formatearEstadoRuta(
                                ruta.estado
                            ),

                        fontSize =
                            14.sp,

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
                        20.dp
                    )
            )


            // =================================================
            // BOTONES PEQUEÑOS
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {


                // ---------------------------------------------
                // VER SOLICITUDES
                // ---------------------------------------------

                OutlinedButton(
                    onClick = {

                        controller.navigate(AppScreens.solicitudesFundacion.name)
                    },

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
                            horizontal = 8.dp,
                            vertical = 10.dp
                        )
                ) {


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.Start
                    ) {


                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable.ic_box
                                ),

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(
                                    24.dp
                                ),

                            tint =
                                BrownIcon
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    6.dp
                                )
                        )


                        Text(
                            text =
                                "Ver solicitudes",

                            fontSize =
                                12.sp,

                            color =
                                ButtonText,

                            lineHeight =
                                14.sp,

                            textAlign =
                                TextAlign.Start
                        )
                    }
                }


                // ---------------------------------------------
                // VER RECORRIDO
                // ---------------------------------------------

                OutlinedButton(
                    onClick = {

                    },

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
                            horizontal = 8.dp,
                            vertical = 10.dp
                        )
                ) {


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.Start
                    ) {


                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable.ic_location_pin
                                ),

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(
                                    24.dp
                                ),

                            tint =
                                BrownIcon
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    6.dp
                                )
                        )


                        Text(
                            text =
                                "Ver recorrido",

                            fontSize =
                                12.sp,

                            color =
                                ButtonText,

                            lineHeight =
                                14.sp,

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
// BADGE DE ESTADO
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
                    MainOrange,
                    RoundedCornerShape(
                        8.dp
                    )
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 6.dp
                )
    ) {

        Text(
            text =
                text,

            color =
                Color.White,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// ============================================================
// FORMATEAR FECHA
// ============================================================

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


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = false
)
@Composable
private fun RutaDetalleScreenPreview() {

    RebottleTheme {

        val navController =
            rememberNavController()


        Scaffold(
            containerColor =
                BackgroundColor,

            topBar = {

                RecoleccionHeader(
                    rutaId =
                        "RT-0002",

                    onBackClick = {}
                )
            }

        ) { innerPadding ->


            RecoleccionDetalleContent(
                ruta =
                    Ruta(
                        id =
                            "RT-0002",

                        solicitudesIds =
                            listOf(
                                "RB-0029",
                                "RB-0028",
                                "RB-0027",
                                "RB-0030",
                                "RB-0026"
                            ),

                        fechaRecoleccion =
                            "2026-10-10",

                        estado =
                            EstadoRuta.PUBLICADA.name,

                        ordenSolicitudes =
                            listOf(
                                "RB-0027",
                                "RB-0029",
                                "RB-0028",
                                "RB-0030",
                                "RB-0026"
                            ),

                        distanciaTotalMetros =
                            5863,

                        duracionTotalSegundos =
                            780
                    ),

                navController =
                    navController,

                innerPadding =
                    innerPadding
            )
        }
    }
}