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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.model.PerfilCache
import com.google.firebase.auth.FirebaseAuth

private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val Brown = RebottleColors.Brown
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText


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
                .padding(start = 17.dp, end = 18.dp, top = 24.dp, bottom = 24.dp)
        ) {

            Header()

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
private fun Header() {

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid != null) PerfilCache.cargar(uid)
    }

    val primerNombre = PerfilCache.nombre
        ?.substringBefore(" ")
        ?.ifEmpty { null }
        ?: "Usuario"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Hola, $primerNombre !",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Brown,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(color = Color(0xFFF1F5ED), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = MainGreen,
                modifier = Modifier.size(23.dp)
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
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        color = MainText
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaInicioRecolectorPreview() {
    RebottleTheme {
        PantallaInicioRecolector(controller = rememberNavController())
    }
}