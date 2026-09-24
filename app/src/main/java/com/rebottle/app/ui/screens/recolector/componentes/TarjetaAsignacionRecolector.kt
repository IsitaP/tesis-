package com.rebottle.app.ui.screens.recolector.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

    val bgColor = when (asignacion.estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFFE3EDDB)
        EstadoAsignacion.PENDIENTE -> Color(0xFFFBE9CF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asignación # $numero",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF34382F)
                )
                EtiquetaEstado(estado = asignacion.estado)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Material: ${asignacion.materiales.joinToString(", ")}", fontSize = 14.sp, color = Color(0xFF5B5F55))
            Text(text = "Peso: ${asignacion.pesoKg} kg", fontSize = 14.sp, color = Color(0xFF5B5F55))
            Text(text = "Hora: ${asignacion.hora}", fontSize = 14.sp, color = Color(0xFF5B5F55))

            Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(text = " ${asignacion.direccion}", fontSize = 13.sp, color = Color(0xFF5B5F55))
                }

                Text(
                    text = "Ver detalles →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB5652F)
                )
            }
        }
    }
}