package com.rebottle.app.ui.screens.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.rebottle.app.model.Solicitud
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.navigation.AppScreens


// ============================================================
// COLORES
// ============================================================

private val BackgroundColor = Color(0xFFFBF8F2)

private val MainGreen = Color(0xFF536D45)

private val DarkGreen = Color(0xFF394C31)

private val SecondaryText = Color(0xFF888E85)

private val PendingColor = Color(0xFFDCA94D)

private val AcceptedColor = Color(0xFF6F9555)

private val CancelledColor = Color(0xFFC9564D)

private val CompletedColor = Color(0xFF4F8A5B)


// ============================================================
// PANTALLA
// ============================================================

@Composable
fun SolicitudesUsuarioScreen(
    controller: NavController,
    model: SolicitudViewModel = viewModel()
) {

    val state by model.state.collectAsState()


    // ========================================================
    // ESCUCHAR SOLICITUDES DEL USUARIO
    // ========================================================

    LaunchedEffect(Unit) {

        model.escucharSolicitudesUsuario()
    }


    // ========================================================
    // CALCULAR SOLICITUDES COMPLETADAS
    // ========================================================

    val solicitudesCompletadas =
        state.solicitudes.filter { solicitud ->

            solicitud.estado.equals(
                "COMPLETADA",
                ignoreCase = true
            )
        }


    // ========================================================
    // TOTAL DE KILOS RECICLADOS
    // ========================================================

    val totalKilos =
        solicitudesCompletadas.sumOf { solicitud ->

            solicitud.pesoKg
        }


    // ========================================================
    // TOTAL DE RECOLECCIONES COMPLETADAS
    // ========================================================

    val totalRecolecciones =
        solicitudesCompletadas.size


    // ========================================================
    // CONTENIDO
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(
                horizontal = 20.dp
            )
    ) {

        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =====================================================
        // HEADER
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {

                    controller.popBackStack()
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Volver",

                    tint =
                        DarkGreen
                )
            }


            Text(
                text = "Mis recolecciones",

                fontSize = 22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DarkGreen
            )
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // =====================================================
        // BOTÓN NUEVA SOLICITUD
        // =====================================================

        Button(
            onClick = {

                controller.navigate(
                    AppScreens.crearSolicitud.name
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),

            shape =
                RoundedCornerShape(12.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MainGreen
                )
        ) {

            Text(
                text = "Nueva solicitud",

                color =
                    Color.White,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Medium
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =====================================================
        // LOADING
        // =====================================================

        if (state.isHistoryLoading) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        MainGreen
                )
            }

        } else if (state.historyError.isNotEmpty()) {


            // =================================================
            // ERROR
            // =================================================

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        state.historyError,

                    color =
                        Color.Red,

                    fontSize =
                        14.sp
                )
            }

        } else {


            // =================================================
            // RESUMEN
            // =================================================

            ResumenRecolecciones(
                totalKilos =
                    totalKilos,

                totalRecolecciones =
                    totalRecolecciones
            )


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // =================================================
            // HISTORIAL
            // =================================================

            Text(
                text =
                    "Historial",

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    DarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // =================================================
            // NO HAY SOLICITUDES
            // =================================================

            if (state.solicitudes.isEmpty()) {

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "Todavía no tienes solicitudes de recolección.",

                        color =
                            SecondaryText,

                        fontSize =
                            15.sp
                    )
                }

            } else {


                // =============================================
                // LISTA DE SOLICITUDES
                // =============================================

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            12.dp
                        )

                ) {

                    items(
                        items =
                            state.solicitudes,

                        key = {
                            it.id
                        }
                    ) { solicitud ->

                        SolicitudCard(
                            solicitud =
                                solicitud
                        )
                    }


                    item {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    20.dp
                                )
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// RESUMEN DEL USUARIO
// ============================================================

@Composable
private fun ResumenRecolecciones(
    totalKilos: Int,
    totalRecolecciones: Int
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {


        // =====================================================
        // TOTAL RECICLADO
        // =====================================================

        Card(
            modifier = Modifier
                .weight(1f)
                .height(78.dp),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        3.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        "Total reciclado",

                    fontSize =
                        11.sp,

                    color =
                        SecondaryText
                )


                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )


                Text(
                    text =
                        "$totalKilos KG",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainGreen
                )
            }
        }


        // =====================================================
        // TOTAL RECOLECCIONES
        // =====================================================

        Card(
            modifier = Modifier
                .weight(1f)
                .height(78.dp),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        3.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        "Recolecciones",

                    fontSize =
                        11.sp,

                    color =
                        SecondaryText
                )


                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )


                Text(
                    text =
                        totalRecolecciones
                            .toString(),

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainGreen
                )
            }
        }
    }
}


// ============================================================
// CARD DE CADA SOLICITUD
// ============================================================

@Composable
private fun SolicitudCard(
    solicitud: Solicitud
) {

    // ========================================================
    // COLOR SEGÚN ESTADO
    // ========================================================

    val statusColor =
        when (
            solicitud.estado.uppercase()
        ) {

            "PENDIENTE" ->

                PendingColor


            "ACEPTADA" ->

                AcceptedColor


            "COMPLETADA" ->

                CompletedColor


            "CANCELADA" ->

                CancelledColor


            else ->

                Color.Gray
        }


    // ========================================================
    // COLOR DE FONDO SEGÚN ESTADO
    // ========================================================

    val cardColor =
        when (
            solicitud.estado.uppercase()
        ) {

            "PENDIENTE" ->

                Color(0xFFFFF5DC)


            "ACEPTADA" ->

                Color(0xFFF0F7EC)


            "COMPLETADA" ->

                Color(0xFFEDF6EA)


            "CANCELADA" ->

                Color(0xFFFFE6E3)


            else ->

                Color.White
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    cardColor
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    3.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {


            // =================================================
            // TÍTULO + ESTADO
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
                        "Recolección",

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        DarkGreen
                )


                Box(
                    modifier =
                        Modifier
                            .background(
                                color =
                                    statusColor,

                                shape =
                                    RoundedCornerShape(
                                        50
                                    )
                            )
                            .padding(
                                horizontal =
                                    10.dp,

                                vertical =
                                    4.dp
                            )
                ) {

                    Text(
                        text =
                            solicitud.estado,

                        color =
                            Color.White,

                        fontSize =
                            10.sp
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
            // DIRECCIÓN
            // =================================================

            Text(
                text =
                    "Dirección: ${solicitud.direccion}",

                fontSize =
                    13.sp,

                color =
                    DarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            // =================================================
            // MATERIALES
            // =================================================

            Text(
                text =
                    "Materiales: ${
                        formatMateriales(
                            solicitud.materiales
                        )
                    }",

                fontSize =
                    13.sp,

                color =
                    DarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            // =================================================
            // PESO
            // =================================================

            Text(
                text =
                    "Peso estimado: ${solicitud.pesoKg} Kg",

                fontSize =
                    13.sp,

                color =
                    DarkGreen
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            // =================================================
            // FECHA
            // =================================================

            Text(
                text =
                    "Fecha de recolección: ${solicitud.fechaRecoleccion}",

                fontSize =
                    13.sp,

                color =
                    DarkGreen
            )
        }
    }
}


// ============================================================
// FORMATEAR MATERIALES
// ============================================================

private fun formatMateriales(
    materiales: List<String>
): String {

    if (
        materiales.isEmpty()
    ) {

        return "Sin materiales"
    }


    return materiales
        .map { material ->

            when (
                material.uppercase()
            ) {

                "PLASTICO" ->

                    "Plástico"


                "PAPEL" ->

                    "Papel"


                "CARTON" ->

                    "Cartón"


                "VIDRIO" ->

                    "Vidrio"


                "METAL" ->

                    "Metal"


                "ELECTRONICOS" ->

                    "Electrónicos"


                "MADERA" ->

                    "Madera"


                "OTROS" ->

                    "Otros"


                else ->

                    material
            }
        }
        .joinToString(
            ", "
        )
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SolicitudesUsuarioScreenPreview() {


    // ========================================================
    // DATOS DE PRUEBA
    // ========================================================

    val solicitudesPrueba =
        listOf(

            Solicitud(
                id = "1",

                usuarioId =
                    "usuario_prueba",

                direccion =
                    "Cra 15 # 136-32",

                materiales =
                    listOf(
                        "METAL",
                        "VIDRIO"
                    ),

                pesoKg =
                    8,

                fechaRecoleccion =
                    "2026-09-22",

                estado =
                    "PENDIENTE"
            ),


            Solicitud(
                id = "2",

                usuarioId =
                    "usuario_prueba",

                direccion =
                    "Cll 131 # 53-35",

                materiales =
                    listOf(
                        "CARTON"
                    ),

                pesoKg =
                    4,

                fechaRecoleccion =
                    "2026-09-29",

                estado =
                    "CANCELADA"
            ),


            Solicitud(
                id = "3",

                usuarioId =
                    "usuario_prueba",

                direccion =
                    "Cra 7 # 45-20",

                materiales =
                    listOf(
                        "PLASTICO",
                        "PAPEL"
                    ),

                pesoKg =
                    12,

                fechaRecoleccion =
                    "2026-10-03",

                estado =
                    "COMPLETADA"
            ),


            Solicitud(
                id = "4",

                usuarioId =
                    "usuario_prueba",

                direccion =
                    "Calle 100 # 19-45",

                materiales =
                    listOf(
                        "VIDRIO",
                        "METAL"
                    ),

                pesoKg =
                    13,

                fechaRecoleccion =
                    "2026-09-12",

                estado =
                    "COMPLETADA"
            )
        )


    // ========================================================
    // CALCULAR RESUMEN PARA PREVIEW
    // ========================================================

    val completadas =
        solicitudesPrueba.filter {

            it.estado.equals(
                "COMPLETADA",
                ignoreCase = true
            )
        }


    val totalKilos =
        completadas.sumOf {

            it.pesoKg
        }


    val totalRecolecciones =
        completadas.size


    // ========================================================
    // PREVIEW
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                BackgroundColor
            )
            .padding(
                horizontal = 20.dp
            )
    ) {

        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        // =====================================================
        // HEADER
        // =====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription =
                    "Volver",

                tint =
                    DarkGreen
            )


            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


            Text(
                text =
                    "Mis recolecciones",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DarkGreen
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // =====================================================
        // BOTÓN
        // =====================================================

        Button(
            onClick = {},

            modifier = Modifier
                .fillMaxWidth()
                .height(
                    45.dp
                ),

            shape =
                RoundedCornerShape(
                    12.dp
                ),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MainGreen
                )
        ) {

            Text(
                text =
                    "Nueva solicitud",

                color =
                    Color.White,

                fontSize =
                    15.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        // =====================================================
        // RESUMEN
        // =====================================================

        ResumenRecolecciones(
            totalKilos =
                totalKilos,

            totalRecolecciones =
                totalRecolecciones
        )


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        // =====================================================
        // HISTORIAL
        // =====================================================

        Text(
            text =
                "Historial",

            fontSize =
                18.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                DarkGreen
        )


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        LazyColumn(

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )

        ) {

            items(
                items =
                    solicitudesPrueba,

                key = {
                    it.id
                }
            ) { solicitud ->

                SolicitudCard(
                    solicitud =
                        solicitud
                )
            }
        }
    }
}