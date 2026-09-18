package com.rebottle.app.ui.screens.recolector.inicio

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.ui.screens.recolector.componentes.BarraNavegacionRecolector
import com.rebottle.app.ui.screens.recolector.componentes.TarjetaAccionRecolector


// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF536D45)
private val DarkGreen = Color(0xFF394C31)
private val Brown = Color(0xFFA55E37)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)


// ----------------------------------------------------
// PANTALLA INICIO — RECOLECTOR
// ----------------------------------------------------

@Composable
fun PantallaInicioRecolector(
    controller: NavController
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            BarraNavegacionRecolector(controller = controller)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 24.dp)
        ) {

            HeaderRecolector()

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "¿Qué desea hacer hoy?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(title = "Ver solicitudes pendientes")
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaAccionRecolector(
                title = "Solicitudes asignadas",
                lines = listOf("12 nuevas", "5 aceptadas", "18 completadas hoy"),
                linkText = "Ver todas →",
                imageRes = R.drawable.fundacion_requests,
                cardHeight = 162.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle(title = "Próxima ruta")
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaAccionRecolector(
                title = "Siguiente recolección",
                lines = listOf("Barrio La Floresta", "10:30 AM", "Usuario: Juan Pérez"),
                linkText = "Iniciar ruta →",
                imageRes = R.drawable.fundacion_route,
                cardHeight = 165.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Ruta del día")
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaAccionRecolector(
                title = "Recorrido asignado",
                lines = listOf("5 paradas", "Distancia: 8.2 km", "Tiempo estimado: 1 h 40 min"),
                linkText = "Ver mapa →",
                imageRes = R.drawable.fundacion_summary,
                cardHeight = 140.dp
            )
        }
    }
}


// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun HeaderRecolector() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = "Hola, Recolector !",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Fundación X",
                fontSize = 18.sp,
                color = SecondaryText
            )
        }

        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = MainGreen,
                modifier = Modifier.size(25.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 7.dp, end = 6.dp)
                    .size(6.dp)
                    .background(color = Color(0xFFE16A58), shape = CircleShape)
            )
        }
    }
}


// ----------------------------------------------------
// TÍTULOS DE SECCIÓN
// ----------------------------------------------------

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MainText
    )
}