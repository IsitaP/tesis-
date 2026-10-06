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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
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
import com.rebottle.app.ui.theme.RebottleTheme

import com.rebottle.app.ui.theme.RebottleColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val BackgroundColor = RebottleColors.Background
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText
private val BrownIcon = RebottleColors.Brown




@Composable
fun SolicitudDetalleScreenU(
    controller: NavController,
    solicitudId: String,
    viewModel: SolicitudViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()



    val solicitud = state.solicitudes.find {
        it.id == solicitudId
    }


    if (state.isHistoryLoading) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                color = MainGreen
            )
        }

        return
    }


    if (solicitud == null) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = if (state.historyError.isNotBlank()) {
                    state.historyError
                } else {
                    "No se encontró la solicitud"
                },
                color = MainText
            )
        }

        return
    }


    var estado by remember(solicitud.estado) {
        mutableStateOf(solicitud.estado)
    }


    val cardColor = when (estado.uppercase()) {

        "COMPLETADA" ->
            Color(0xFFE3EDDB)

        "PENDIENTE" ->
            Color(0xFFFEF3D7)

        "CANCELADA" ->
            Color(0xFFFFD8D4)

        else ->
            Color(0xFFFEF3D7)
    }


    val borderColor = when (estado.uppercase()) {

        "COMPLETADA" ->
            Color(0xFFD5E3CD)

        "PENDIENTE" ->
            Color(0xFFF5E1B8)

        "CANCELADA" ->
            Color(0xFFF3A29B)

        else ->
            Color(0xFFF5E1B8)
    }


    val badgeColor = when (estado.uppercase()) {

        "COMPLETADA" ->
            Color(0xFF536D45)

        "PENDIENTE" ->
            Color(0xFFD99B4B)

        "CANCELADA" ->
            Color(0xFFC94338)

        else ->
            Color(0xFFD99B4B)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {

            Spacer(
                modifier = Modifier.height(40.dp)
            )


            // ====================================================
            // HEADER
            // ====================================================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MainText,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            controller.popBackStack()
                        }
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Text(
                    text = "Recolección # ${solicitud.id.take(8)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ====================================================
            // TARJETA PRINCIPAL
            // ====================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),
                border = BorderStroke(
                    1.dp,
                    borderColor
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {


                    // =================================================
                    // ID + ESTADO
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "#${solicitud.id.take(8)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            textDecoration =
                                TextDecoration.Underline
                        )


                        // =============================================
                        // ESTADO
                        // =============================================

                        Box(
                            modifier = Modifier
                                .background(
                                    badgeColor,
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                        ) {

                            Text(
                                text = estado
                                    .lowercase()
                                    .replaceFirstChar {
                                        it.uppercase()
                                    },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =================================================
                    // DIRECCIÓN
                    // =================================================

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_location_pin
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = BrownIcon
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = solicitud.direccion,
                            fontSize = 14.sp,
                            color = MainText
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =================================================
                    // FECHA Y HORA
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(24.dp),
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
                                painter = painterResource(
                                    id = R.drawable.ic_calendar
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BrownIcon
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = formatearFecha(
                                    solicitud.fechaRecoleccion
                                ),
                                fontSize = 14.sp,
                                color = MainText
                            )
                        }


                        // =============================================
                        // HORA
                        // =============================================

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_clock
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BrownIcon
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            /*
                             * La hora todavía no existe
                             * en el modelo Solicitud.
                             */
                            Text(
                                text = "9:00 AM",
                                fontSize = 14.sp,
                                color = MainText
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =================================================
                    // MATERIALES
                    // =================================================

                    Text(
                        text = "Materiales",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainText
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(28.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        solicitud.materiales.take(3).forEach { material ->

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    painter = painterResource(
                                        id = obtenerIconoMaterial(
                                            material
                                        )
                                    ),
                                    contentDescription = null,
                                    modifier =
                                        Modifier.size(22.dp),
                                    tint = BrownIcon
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Text(
                                    text =
                                        obtenerNombreMaterial(
                                            material
                                        ),
                                    fontSize = 14.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =================================================
                    // PESO Y PRIORIDAD
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        // =============================================
                        // PESO
                        // =============================================

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_weight
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = BrownIcon
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Column {

                                Text(
                                    text = "Peso estimado",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )

                                Text(
                                    text = "${solicitud.pesoKg} KG",
                                    fontSize = 14.sp,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color = MainText
                                )
                            }
                        }


                        // =============================================
                        // PRIORIDAD
                        // =============================================

                        Column {

                            Text(
                                text = "Prioridad",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )

                            /*
                             * La prioridad todavía no existe
                             * en el modelo Solicitud.
                             */
                            Text(
                                text = "Baja",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainGreen
                            )
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =================================================
                    // ESTADO DEL RECOLECTOR
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(
                                    id = R.drawable.icon_collection
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = BrownIcon
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            /*
                             * El recolector todavía no existe
                             * dentro del modelo Solicitud.
                             */
                            Text(
                                text = "No asignado",
                                fontSize = 14.sp,
                                color = SecondaryText
                            )
                        }


                        // =============================================
                        // AYUDA / CHAT
                        // =============================================

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                controller.navigate(
                                    "${AppScreens.editarSolicitud.name}/${solicitud.id}"
                                )

                            }
                        ) {

                            Text(
                                text = "Editar Solicitud",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )
                        }
                    }
                }
            }


            // ========================================================
            // BOTÓN CANCELAR
            // ========================================================

            if (estado.uppercase() == "PENDIENTE") {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(
                    onClick = {

                        viewModel.cancelarSolicitud(
                            solicitudId = solicitud.id,
                            onSuccess = {
                            },
                            onError = { error ->
                                println("Error al cancelar: $error")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    )
                ) {

                    Text(
                        text = "Cancelar Recolección",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }


        // ============================================================
        // IMAGEN DEL PAISAJE
        // ============================================================

        Image(
            painter = painterResource(
                id = R.drawable.solicitudes_paisaje
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}


// ================================================================
// FORMATEAR FECHA
// ================================================================

private fun formatearFecha(
    fecha: String
): String {

    return try {

        val fechaLocal =
            LocalDate.parse(fecha)

        val formatter =
            DateTimeFormatter.ofPattern(
                "MMMM d",
                Locale("es", "ES")
            )

        fechaLocal
            .format(formatter)
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase()
                } else {
                    it.toString()
                }
            }

    } catch (e: Exception) {

        fecha
    }
}


// ================================================================
// NOMBRE DEL MATERIAL
// ================================================================

private fun obtenerNombreMaterial(
    material: String
): String {

    return when (material.uppercase()) {

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


// ================================================================
// ICONO DEL MATERIAL
// ================================================================

private fun obtenerIconoMaterial(
    material: String
): Int {

    return when (material.uppercase()) {

        "METAL" ->
            R.drawable.ic_metal

        "VIDRIO" ->
            R.drawable.ic_glass

        /*
         * Solo usamos drawables que sabemos
         * que ya existen en tu proyecto.
         */
        else ->
            R.drawable.icon_collection
    }
}


// ================================================================
// PREVIEW
// ================================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SolicitudDetalleScreenUPreview() {

    RebottleTheme {

        /*
         * El Preview no tendrá datos reales de Firebase.
         * Se conserva para que el archivo compile.
         */
        SolicitudDetalleScreenU(
            controller = rememberNavController(),
            solicitudId = "1"
        )
    }
}