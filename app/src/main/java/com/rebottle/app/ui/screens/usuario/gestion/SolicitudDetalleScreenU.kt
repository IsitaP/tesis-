package com.rebottle.app.ui.screens.usuario.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.rebottle.app.R
import com.rebottle.app.components.MainGreen
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.ui.theme.RebottleTheme

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


// ============================================================
// COLORES
// ============================================================

private val BackgroundColor =
    RebottleColors.Background

private val MainText =
    RebottleColors.MainText

private val SecondaryText =
    RebottleColors.SecondaryText

private val BrownIcon =
    RebottleColors.Brown


// ============================================================
// PANTALLA DETALLE DE SOLICITUD
// ============================================================

@Composable
fun SolicitudDetalleScreenU(
    controller: NavController,
    solicitudId: String,
    viewModel: SolicitudViewModel = viewModel()
) {

    // ========================================================
    // ESTADO DEL VIEWMODEL
    // ========================================================

    val state by
    viewModel
        .state
        .collectAsState()


    // ========================================================
    // BUSCAR LA SOLICITUD
    // ========================================================

    val solicitud =
        state.solicitudes
            .find {

                it.id ==
                        solicitudId
            }


    // ========================================================
    // CARGANDO
    // ========================================================

    if (
        state.isHistoryLoading
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        BackgroundColor
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                color =
                    MainGreen
            )
        }


        return
    }


    // ========================================================
    // SOLICITUD NO ENCONTRADA
    // ========================================================

    if (
        solicitud == null
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        BackgroundColor
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =

                    if (
                        state.historyError
                            .isNotBlank()
                    ) {

                        state.historyError

                    } else {

                        "No se encontró la solicitud"
                    },

                color =
                    MainText
            )
        }


        return
    }


    // ========================================================
    // ESTADO ACTUAL
    // ========================================================

    var estado by
    remember(
        solicitud.estado
    ) {

        mutableStateOf(
            solicitud.estado
        )
    }


    val estadoUpper =
        estado.uppercase()


    // ========================================================
    // COLORES SEGÚN ESTADO
    // ========================================================

    val cardColor =
        when (
            estadoUpper
        ) {

            // Solicitud recién creada
            "PUBLICADA" ->
                Color(
                    0xFFFEF3D7
                )


            "EN_RUTA" ->
                Color(
                    0xFFF2F7ED
                )


            "RECOLECTADA",
            "COMPLETADA" ->
                Color(
                    0xFFE3EDDB
                )


            "CANCELADA" ->
                Color(
                    0xFFFFD8D4
                )


            else ->
                Color(
                    0xFFFEF3D7
                )
        }


    val borderColor =
        when (
            estadoUpper
        ) {

            "PUBLICADA" ->
                Color(
                    0xFFF5E1B8
                )


            "EN_RUTA",
            "RECOLECTADA",
            "COMPLETADA" ->
                Color(
                    0xFFD5E3CD
                )


            "CANCELADA" ->
                Color(
                    0xFFF3A29B
                )


            else ->
                Color(
                    0xFFF5E1B8
                )
        }


    val badgeColor =
        when (
            estadoUpper
        ) {

            "PUBLICADA" ->
                Color(
                    0xFFD99B4B
                )


            "EN_RUTA",
            "RECOLECTADA",
            "COMPLETADA" ->
                Color(
                    0xFF536D45
                )


            "CANCELADA" ->
                Color(
                    0xFFC94338
                )


            else ->
                Color(
                    0xFFD99B4B
                )
        }


    // ========================================================
    // PANTALLA COMPLETA
    //
    // YA NO HAY VERTICALSCROLL.
    // ========================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    BackgroundColor
                )
    ) {


        // ====================================================
        // HEADER
        // ====================================================

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            18.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        36.dp
                    )
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Icon(
                    imageVector =
                        Icons.Filled.ArrowBack,

                    contentDescription =
                        "Volver",

                    tint =
                        MainText,

                    modifier =
                        Modifier
                            .size(
                                24.dp
                            )
                            .clickable {

                                controller
                                    .popBackStack()
                            }
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            16.dp
                        )
                )


                Text(
                    text =
                        "Recolección # ${solicitud.id.take(8)}",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainText
                )
            }
        }


        // ====================================================
        // ZONA CENTRAL
        //
        // TOMA EL ESPACIO RESTANTE Y CENTRA EL CARD.
        // ====================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(
                        1f
                    )
                    .padding(
                        horizontal =
                            18.dp
                    ),

            contentAlignment =
                Alignment.Center
        ) {


            Column(
                modifier =
                    Modifier.fillMaxWidth()
            ) {


                // =================================================
                // TARJETA PRINCIPAL
                // =================================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            28.dp
                        ),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =
                                cardColor
                        ),

                    border =
                        BorderStroke(
                            width =
                                1.dp,

                            color =
                                borderColor
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
                                horizontal = 18.dp,
                                vertical = 24.dp
                            )
                    ) {


                        // =============================================
                        // ID + ESTADO
                        // =============================================

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween,

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            Text(
                                text =
                                    "#${solicitud.id.take(8)}",

                                fontSize =
                                    19.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    MainText,

                                textDecoration =
                                    TextDecoration.Underline
                            )


                            Box(
                                modifier =
                                    Modifier
                                        .background(
                                            color =
                                                badgeColor,

                                            shape =
                                                RoundedCornerShape(
                                                    50.dp
                                                )
                                        )
                                        .padding(
                                            horizontal =
                                                12.dp,

                                            vertical =
                                                4.dp
                                        )
                            ) {


                                Text(
                                    text =
                                        formatearEstado(
                                            estado
                                        ),

                                    color =
                                        Color.White,

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.Medium
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    13.dp
                                )
                        )


                        // =============================================
                        // DIRECCIÓN
                        // =============================================

                        Row(
                            verticalAlignment =
                                Alignment
                                    .CenterVertically
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
                                    solicitud.direccion,

                                fontSize =
                                    14.sp,

                                color =
                                    MainText
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )


                        // =============================================
                        // FECHA Y HORA
                        // =============================================

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    24.dp
                                ),

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            // -----------------------------------------
                            // FECHA
                            // -----------------------------------------

                            Row(
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
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
                                            solicitud
                                                .fechaRecoleccion
                                        ),

                                    fontSize =
                                        14.sp,

                                    color =
                                        MainText
                                )
                            }


                            // -----------------------------------------
                            // HORA
                            // -----------------------------------------

                            Row(
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
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


                                /*
                                 * La hora todavía no existe
                                 * en el modelo Solicitud.
                                 */
                                Text(
                                    text =
                                        "9:00 AM",

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
                                    13.dp
                                )
                        )


                        // =============================================
                        // MATERIALES
                        // =============================================

                        Text(
                            text =
                                "Materiales",

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
                                    7.dp
                                )
                        )


                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    28.dp
                                ),

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            solicitud.materiales
                                .take(
                                    3
                                )
                                .forEach { material ->


                                    Row(
                                        verticalAlignment =
                                            Alignment
                                                .CenterVertically
                                    ) {


                                        Icon(
                                            painter =
                                                painterResource(
                                                    id =
                                                        obtenerIconoMaterial(
                                                            material
                                                        )
                                                ),

                                            contentDescription =
                                                null,

                                            modifier =
                                                Modifier.size(
                                                    22.dp
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
                                                obtenerNombreMaterial(
                                                    material
                                                ),

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
                                    13.dp
                                )
                        )


                        // =============================================
                        // PESO Y PRIORIDAD
                        // =============================================

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween,

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            // -----------------------------------------
                            // PESO
                            // -----------------------------------------

                            Row(
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
                            ) {


                                Icon(
                                    painter =
                                        painterResource(
                                            id =
                                                R.drawable
                                                    .ic_weight
                                        ),

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(
                                            30.dp
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
                                            "Peso estimado",

                                        fontSize =
                                            12.sp,

                                        color =
                                            SecondaryText
                                    )


                                    Text(
                                        text =
                                            "${solicitud.pesoKg} KG",

                                        fontSize =
                                            14.sp,

                                        fontWeight =
                                            FontWeight.Bold,

                                        color =
                                            MainText
                                    )
                                }
                            }


                            // -----------------------------------------
                            // PRIORIDAD
                            // -----------------------------------------

                            Column {


                                Text(
                                    text =
                                        "Prioridad",

                                    fontSize =
                                        12.sp,

                                    color =
                                        SecondaryText
                                )


                                /*
                                 * La prioridad todavía no existe
                                 * en el modelo Solicitud.
                                 */
                                Text(
                                    text =
                                        "Baja",

                                    fontSize =
                                        14.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        MainGreen
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    13.dp
                                )
                        )


                        // =============================================
                        // RECOLECTOR + EDITAR
                        // =============================================

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement
                                    .SpaceBetween,

                            verticalAlignment =
                                Alignment
                                    .CenterVertically
                        ) {


                            Row(
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
                            ) {


                                Icon(
                                    painter =
                                        painterResource(
                                            id =
                                                R.drawable
                                                    .icon_collection
                                        ),

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(
                                            27.dp
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


                                /*
                                 * El recolector todavía no existe
                                 * dentro del modelo Solicitud.
                                 */
                                Text(
                                    text =
                                        "No asignado",

                                    fontSize =
                                        14.sp,

                                    color =
                                        SecondaryText
                                )
                            }


                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically,

                                modifier =
                                    Modifier.clickable {

                                        controller.navigate(
                                            "${AppScreens.editarSolicitud.name}/${solicitud.id}"
                                        )
                                    }
                            ) {


                                Text(
                                    text =
                                        "Editar Solicitud",

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.ExtraBold,

                                    textDecoration =
                                        TextDecoration.Underline,

                                    color =
                                        MainText
                                )
                            }
                        }
                    }
                }


                // =================================================
                // CANCELAR RECOLECCIÓN
                //
                // AHORA SOLO SE MUESTRA SI ESTÁ PUBLICADA.
                // =================================================

                if (
                    estadoUpper ==
                    "PUBLICADA"
                ) {


                    Spacer(
                        modifier =
                            Modifier.height(
                                16.dp
                            )
                    )


                    Button(
                        onClick = {

                            viewModel.cancelarSolicitud(

                                solicitudId =
                                    solicitud.id,

                                onSuccess = {

                                    // Actualizamos inmediatamente
                                    // la pantalla.
                                    estado =
                                        "CANCELADA"
                                },

                                onError = { error ->

                                    println(
                                        "Error al cancelar: $error"
                                    )
                                }
                            )
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    50.dp
                                ),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),

                        colors =
                            ButtonDefaults
                                .buttonColors(

                                    containerColor =
                                        MainGreen
                                )
                    ) {


                        Text(
                            text =
                                "Cancelar Recolección",

                            color =
                                Color.White,

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }
        }


        // ====================================================
        // PAISAJE
        // ====================================================

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
                    .height(
                        150.dp
                    ),

            contentScale =
                ContentScale.FillWidth
        )
    }
}


// ============================================================
// FORMATEAR ESTADO
// ============================================================

private fun formatearEstado(
    estado: String
): String {

    return when (
        estado.uppercase()
    ) {

        "PUBLICADA" ->
            "Publicada"

        "EN_RUTA" ->
            "En ruta"

        "RECOLECTADA" ->
            "Recolectada"

        "COMPLETADA" ->
            "Completada"

        "CANCELADA" ->
            "Cancelada"

        else ->
            estado
                .lowercase()
                .replaceFirstChar {

                    if (
                        it.isLowerCase()
                    ) {

                        it.titlecase()

                    } else {

                        it.toString()
                    }
                }
    }
}


// ============================================================
// FORMATEAR FECHA
// ============================================================

private fun formatearFecha(
    fecha: String
): String {

    return try {

        val fechaLocal =
            LocalDate.parse(
                fecha
            )


        val formatter =
            DateTimeFormatter
                .ofPattern(
                    "MMMM d",
                    Locale(
                        "es",
                        "ES"
                    )
                )


        fechaLocal
            .format(
                formatter
            )
            .replaceFirstChar {

                if (
                    it.isLowerCase()
                ) {

                    it.titlecase()

                } else {

                    it.toString()
                }
            }

    } catch (
        e: Exception
    ) {

        fecha
    }
}


// ============================================================
// NOMBRE DEL MATERIAL
// ============================================================

private fun obtenerNombreMaterial(
    material: String
): String {

    return when (
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


// ============================================================
// ICONO DEL MATERIAL
// ============================================================

private fun obtenerIconoMaterial(
    material: String
): Int {

    return when (
        material.uppercase()
    ) {

        "METAL" ->
            R.drawable.ic_metal

        "VIDRIO" ->
            R.drawable.ic_glass

        else ->
            R.drawable.icon_collection
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SolicitudDetalleScreenUPreview() {

    RebottleTheme {

        SolicitudDetalleScreenU(
            controller =
                rememberNavController(),

            solicitudId =
                "1"
        )
    }
}