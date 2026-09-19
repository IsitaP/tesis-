package com.rebottle.app.ui.screens.recolector.solicitudes

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.ui.screens.recolector.componentes.EtiquetaEstado
import com.rebottle.app.ui.theme.RebottleTheme


private val BackgroundColor = Color(0xFFFBF8F2)
private val CardColor = Color(0xFFFBE9CF)
private val MainGreen = Color(0xFF536D45)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)


@Composable
fun PantallaDetalleAsignacionRecolector(
    controller: NavController,
    asignacionId: String
) {

    val asignacion = asignacionesEjemplo.firstOrNull { it.id == asignacionId }
        ?: asignacionesEjemplo.first()

    var estado by remember { mutableStateOf(asignacion.estado) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {

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
            Text(
                text = "  Asignación # ${asignacion.id}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = asignacion.codigo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainGreen,
                        textDecoration = TextDecoration.Underline
                    )
                    EtiquetaEstado(estado = estado)
                }

                Spacer(modifier = Modifier.height(12.dp))

                FilaDetalle(icono = Icons.Filled.LocationOn, texto = asignacion.direccion)
                Spacer(modifier = Modifier.height(6.dp))
                FilaDetalle(icono = Icons.Filled.CalendarToday, texto = asignacion.fecha)
                Spacer(modifier = Modifier.height(6.dp))
                FilaDetalle(icono = Icons.Filled.Schedule, texto = asignacion.hora)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Materiales",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    asignacion.materiales.forEach { material ->
                        Text(text = "•  $material", fontSize = 14.sp, color = SecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FilaDetalle(icono = Icons.Filled.ShoppingBag, texto = "Peso estimado ${asignacion.pesoKg} KG")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: abrir mapa con la dirección */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = MainGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(0.dp))
                        Text(text = "  Ver mapa", color = MainGreen)
                    }
                    OutlinedButton(
                        onClick = { /* TODO: abrir chat con el usuario */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, tint = MainGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(0.dp))
                        Text(text = "  Contactar Usuario", color = MainGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                estado = EstadoAsignacion.COMPLETADA
                // TODO: al conectar Firestore, actualizar el documento acá
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainGreen)
        ) {
            Text(text = "Completada", color = Color.White, fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.solicitudes_paisaje),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}

@Composable
private fun FilaDetalle(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MainGreen, modifier = Modifier.size(16.dp))
        Text(text = "  $texto", fontSize = 14.sp, color = SecondaryText)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaDetalleAsignacionRecolectorPreview() {
    RebottleTheme {
        PantallaDetalleAsignacionRecolector(
            controller = rememberNavController(),
            asignacionId = "1"
        )
    }
}