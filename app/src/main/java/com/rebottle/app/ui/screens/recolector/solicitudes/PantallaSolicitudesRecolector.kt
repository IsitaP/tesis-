package com.rebottle.app.ui.screens.recolector.solicitudes

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.recolector.componentes.BarraNavegacionRecolector
import com.rebottle.app.ui.screens.recolector.componentes.TarjetaAsignacionRecolector
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val MainText = RebottleColors.MainText


@Composable
fun PantallaSolicitudesRecolector(
    controller: NavController
) {

    var busqueda by remember { mutableStateOf("") }

    val asignaciones = asignacionesEjemplo
    val pendientes = asignaciones.count { it.estado == EstadoAsignacion.PENDIENTE }
    val completadas = asignaciones.count { it.estado == EstadoAsignacion.COMPLETADA }

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
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MainText,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { controller.popBackStack() }
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "  Solicitudes asignadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("Recolección ...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MainGreen) },
                trailingIcon = { Icon(Icons.Filled.Tune, contentDescription = "Filtros", tint = MainGreen) },
                shape = RoundedCornerShape(50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tarjetas de resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenEstadoCard(
                    modifier = Modifier.weight(1f),
                    icono = Icons.Filled.Schedule,
                    iconoBg = Color(0xFFDB9A3C),
                    label = "Pendientes",
                    valor = pendientes.toString()
                )
                ResumenEstadoCard(
                    modifier = Modifier.weight(1f),
                    icono = Icons.Filled.CheckCircle,
                    iconoBg = Color(0xFF536D45),
                    label = "Completadas",
                    valor = completadas.toString()
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Historial",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                asignaciones.forEachIndexed { index, asignacion ->
                    TarjetaAsignacionRecolector(
                        numero = index + 1,
                        asignacion = asignacion,
                        onClick = {
                            controller.navigate("${AppScreens.detalleAsignacionRecolector.name}/${asignacion.id}")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ResumenEstadoCard(
    modifier: Modifier = Modifier,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    iconoBg: Color,
    label: String,
    valor: String
) {
    Card(
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color = iconoBg, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(0.dp))
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = label, fontSize = 13.sp, color = Color(0xFF7F887A))
                Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MainText)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaSolicitudesRecolectorPreview() {
    RebottleTheme {
        PantallaSolicitudesRecolector(controller = rememberNavController())
    }
}