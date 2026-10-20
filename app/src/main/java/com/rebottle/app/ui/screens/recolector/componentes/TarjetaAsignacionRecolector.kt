package com.rebottle.app.ui.screens.recolector.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.ui.screens.recolector.solicitudes.AsignacionRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.EstadoAsignacion

@Composable
fun TarjetaAsignacionRecolector(
    numero: Int,
    asignacion: AsignacionRecolector,
    onClick: () -> Unit
) {
    // Color de fondo según el estado
    val bgColor = when (asignacion.estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFFE3EDDB)
        EstadoAsignacion.PENDIENTE -> Color(0xFFFBE9CF)
        EstadoAsignacion.CANCELADA -> Color(0xFFFFD8D4)
    }

    // Color del borde según el estado (BorderStroke)
    val borderColor = when (asignacion.estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFFD5E3CD)
        EstadoAsignacion.PENDIENTE -> Color(0xFFF5E1B8)
        EstadoAsignacion.CANCELADA -> Color(0xFFF3A29B)
    }

    // Color del texto de detalles según el estado
    val actionColor = when (asignacion.estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFF536D45)
        EstadoAsignacion.PENDIENTE -> Color(0xFFB5652F)
        EstadoAsignacion.CANCELADA -> Color(0xFFC94338)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor), // <-- ¡Aquí está el BorderStroke aplicado!
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asignación # $numero",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF34382F)
                )
                EtiquetaEstado(estado = asignacion.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Material: ${asignacion.materiales.joinToString(", ")}", fontSize = 14.sp, color = Color(0xFF5B5F55))
            Text(text = "Peso: ${asignacion.pesoKg} kg", fontSize = 14.sp, color = Color(0xFF5B5F55))
            if (asignacion.hora.isNotBlank()) {
                Text(text = "Hora: ${asignacion.hora}", fontSize = 14.sp, color = Color(0xFF5B5F55))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF536D45),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = asignacion.direccion, fontSize = 14.sp, color = Color(0xFF34382F))
                }

                Text(
                    text = "Ver detalles →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = actionColor
                )
            }
        }
    }
}