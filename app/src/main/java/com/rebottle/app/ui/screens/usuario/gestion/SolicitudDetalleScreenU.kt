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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.components.MainGreen
import com.rebottle.app.ui.theme.RebottleTheme

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)
private val BrownIcon = Color(0xFFA55E37)

@Composable
fun SolicitudDetalleScreenU(
    controller: NavController,
    solicitudId: String
) {
    // Estado de prueba o conectado a tu ViewModel
    var estado by remember { mutableStateOf("PENDIENTE") } // PENDIENTE, COMPLETADA, CANCELADA

    // Colores dinámicos según el estado
    val cardColor = when (estado.uppercase()) {
        "COMPLETADA" -> Color(0xFFE3EDDB) // Verde claro
        "PENDIENTE" -> Color(0xFFFEF3D7)  // Naranja claro
        "CANCELADA" -> Color(0xFFFFD8D4)  // Rojo claro
        else -> Color(0xFFFEF3D7)
    }

    val borderColor = when (estado.uppercase()) {
        "COMPLETADA" -> Color(0xFFD5E3CD)
        "PENDIENTE" -> Color(0xFFF5E1B8)
        "CANCELADA" -> Color(0xFFF3A29B)
        else -> Color(0xFFF5E1B8)
    }

    val badgeColor = when (estado.uppercase()) {
        "COMPLETADA" -> Color(0xFF536D45) // Verde
        "PENDIENTE" -> Color(0xFFD99B4B)  // Naranja
        "CANCELADA" -> Color(0xFFC94338)  // Rojo
        else -> Color(0xFFD99B4B)
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
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MainText,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { controller.popBackStack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Recolección # $solicitudId",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tarjeta principal de detalles
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#RB-20262",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            textDecoration = TextDecoration.Underline
                        )

                        // Etiqueta de Estado Dinámica
                        Box(
                            modifier = Modifier
                                .background(badgeColor, RoundedCornerShape(50.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = estado.replaceFirstChar { it.uppercase() },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dirección
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_pin),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = BrownIcon
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cll 131a # 53- 31",
                            fontSize = 14.sp,
                            color = MainText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fecha y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Septiembre 12",
                                fontSize = 14.sp,
                                color = MainText
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "9:00 AM",
                                fontSize = 14.sp,
                                color = MainText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Materiales",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Materiales (Metal y Vidrio)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_metal),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Metal", fontSize = 14.sp, color = SecondaryText)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_glass),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Vidrio", fontSize = 14.sp, color = SecondaryText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila inferior: Peso estimado y Prioridad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_weight),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Peso estimado", fontSize = 12.sp, color = SecondaryText)
                                Text(text = "8 KG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MainText)
                            }
                        }

                        Column {
                            Text(text = "Prioridad", fontSize = 12.sp, color = SecondaryText)
                            Text(text = "Baja", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MainGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estado del Recolector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_collection),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "No asignado",
                                fontSize = 14.sp,
                                color = SecondaryText
                            )
                        }

                        // Sección de Ayuda / Chat flotante
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { /* TODO: Abrir ayuda/chat */ }
                        ) {
                            Text(
                                text = "¿Necesita ayuda?",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.ChatBubble,
                                contentDescription = null,
                                tint = MainGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Botón de Cancelar Recolección (Visible solo si está pendiente)
            if (estado.uppercase() == "PENDIENTE") {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        estado = "CANCELADA"
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

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Imagen del paisaje alineada abajo
        Image(
            painter = painterResource(id = R.drawable.solicitudes_paisaje),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SolicitudDetalleScreenUPreview() {
    RebottleTheme {
        SolicitudDetalleScreenU(
            controller = rememberNavController(),
            solicitudId = "1"
        )
    }
}