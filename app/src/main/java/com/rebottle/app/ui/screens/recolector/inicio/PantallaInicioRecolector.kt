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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebottle.app.model.EstadoParada
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.RutaProgresoViewModel
import com.rebottle.app.navigation.AppScreens
import java.util.Locale

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
    controller: NavController,
    model: RutaProgresoViewModel = viewModel()
) {

    val state by model.state.collectAsState()

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { model.escucharRutaDe(it) }
    }

    val ruta = state.ruta
    val siguiente = state.paradas.firstOrNull { it.estado == EstadoParada.PENDIENTE }

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
                lines = if (ruta == null) {
                    listOf("Sin ruta asignada")
                } else {
                    listOf(
                        "${state.pendientes} pendientes",
                        "${state.completadas} completadas",
                        "${state.total} en total"
                    )
                },
                linkText = "Ver todas →",
                imageRes = R.drawable.fundacion_requests,
                cardHeight = 162.dp,
                onLinkClick = { controller.navigate(AppScreens.solicitudesRecolector.name) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle(title = "Próxima ruta")
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaAccionRecolector(
                title = "Siguiente recolección",
                lines = if (siguiente == null) {
                    listOf(if (ruta == null) "Sin ruta asignada" else "No quedan paradas pendientes")
                } else {
                    listOf(
                        siguiente.direccion,
                        siguiente.nombreLugar.ifBlank { materialesLegibles(siguiente.materiales) },
                        "Peso: ${siguiente.pesoKg} kg"
                    )
                },
                linkText = if (siguiente == null) "Ver solicitudes →" else "Ver parada →",
                imageRes = R.drawable.fundacion_route,
                cardHeight = 165.dp,
                onLinkClick = {
                    if (siguiente != null) {
                        controller.navigate(
                            "${AppScreens.detalleAsignacionRecolector.name}/${siguiente.solicitudId}"
                        )
                    } else {
                        controller.navigate(AppScreens.solicitudesRecolector.name)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Ruta del día")
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaAccionRecolector(
                title = "Recorrido asignado",
                lines = if (ruta == null) {
                    listOf("Sin ruta asignada")
                } else {
                    listOf(
                        "${state.total} paradas",
                        "Distancia: ${formatoKm(ruta.distanciaTotalMetros)}",
                        "Tiempo estimado: ${formatoDuracion(ruta.duracionTotalSegundos)}"
                    )
                },
                linkText = "Ver paradas →",
                imageRes = R.drawable.fundacion_summary,
                cardHeight = 140.dp,
                onLinkClick = { controller.navigate(AppScreens.solicitudesRecolector.name) }
            )
        }
    }
}


private fun formatoKm(metros: Long): String =
    String.format(Locale.forLanguageTag("es-CO"), "%.1f km", metros / 1000.0)

private fun formatoDuracion(segundos: Long): String {
    val minutos = (segundos / 60).toInt()
    val horas = minutos / 60
    val resto = minutos % 60
    return if (horas > 0) "$horas h $resto min" else "$minutos min"
}

private fun materialesLegibles(materiales: List<String>): String =
    materiales.joinToString(", ") { nombre ->
        MaterialType.values().firstOrNull { it.name == nombre }?.label ?: nombre
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