package com.rebottle.app.ui.screens.recolector.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.ui.screens.recolector.solicitudes.EstadoAsignacion

@Composable
fun EtiquetaEstado(estado: EstadoAsignacion) {

    val (texto, color) = when (estado) {
        EstadoAsignacion.COMPLETADA -> "Completada" to Color(0xFF536D45)
        EstadoAsignacion.PENDIENTE -> "Pendiente" to Color(0xFFDB9A3C)
    }

    Text(
        text = texto,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}