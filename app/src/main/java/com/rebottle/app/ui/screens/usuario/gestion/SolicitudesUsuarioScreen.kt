package com.rebottle.app.ui.screens.usuario

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.model.Solicitud
import com.rebottle.app.model.SolicitudState
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.model.cuentaComoAporte
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.usuario.inicio.ReBottleBottomBar
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import java.text.Normalizer



private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val SecondaryText = RebottleColors.SecondaryText
private val CardBorderColor = Color(0xFFD9DDD2)
private val BrownIcon = RebottleColors.Brown

@Composable
fun SolicitudesUsuarioScreen(
    controller: NavController,
    model: SolicitudViewModel = viewModel(),
    onCreateClick: () -> Unit = {},
    onDetailsClick: (Solicitud) -> Unit = {}
) {
    val state by model.state.collectAsState()

    LaunchedEffect(Unit) {
        model.escucharSolicitudesUsuario()
    }

    SolicitudesUsuarioScreenContent(
        controller = controller,
        state = state,
        onCreateClick = onCreateClick,
        onDetailsClick = onDetailsClick
    )
}

@Composable
fun SolicitudesUsuarioScreenContent(
    controller: NavController,
    state: SolicitudState,
    onCreateClick: () -> Unit = {},
    onDetailsClick: (Solicitud) -> Unit = {}
) {
    var busqueda by remember { mutableStateOf("") }

    val textoBusqueda = normalizarTexto(busqueda)

    val solicitudesFiltradas = state.solicitudes.filter { solicitud ->

        val materiales =
            normalizarTexto(
                solicitud.materiales.joinToString(" ")
            )

        val direccion =
            normalizarTexto(solicitud.direccion)

        val estado =
            normalizarTexto(solicitud.estado)

        val fecha =
            normalizarTexto(solicitud.fechaRecoleccion)

        val id =
            normalizarTexto(solicitud.id)

        val peso =
            solicitud.pesoKg.toString()

        textoBusqueda.isBlank() ||

                materiales.contains(textoBusqueda) ||

                direccion.contains(textoBusqueda) ||

                estado.contains(textoBusqueda) ||

                fecha.contains(textoBusqueda) ||

                id.contains(textoBusqueda) ||

                peso.contains(textoBusqueda)
    }

    // Total neto de kg (sin separar por material) y número de
    // recolecciones, contando todas menos las canceladas
    val solicitudesValidas = state.solicitudes.filter { it.cuentaComoAporte() }

    val totalKilos = solicitudesValidas.sumOf { it.pesoKg }
    val totalRecolecciones = solicitudesValidas.size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            ReBottleBottomBar(
                controller = controller
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Header(onBackClick = { controller.popBackStack() })
            }

            item {
                // Buscador idéntico a la imagen
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = { Text("Recolección ...", color = SecondaryText) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = SecondaryText) },
                    trailingIcon = { Icon(Icons.Filled.Tune, contentDescription = "Filtros", tint = SecondaryText) },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = CardBorderColor,
                        unfocusedBorderColor = CardBorderColor
                    )
                )
            }

            item {
                ResumenRecolecciones(
                    totalKilos = totalKilos,
                    totalRecolecciones = totalRecolecciones
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )

                    Button(
                        onClick = {
                            controller.navigate(AppScreens.crearSolicitud.name)
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainGreen
                        ),

                        shape = RoundedCornerShape(7.dp)
                    ) {

                        Text(
                            text = "Solicitar Recolección  →",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (state.isHistoryLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MainGreen)
                    }
                }
            } else if (state.historyError.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.historyError, color = Color.Red, fontSize = 14.sp)
                    }
                }
            } else if (state.solicitudes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Todavía no tienes solicitudes de recolección.",
                            color = SecondaryText,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(
                    items = solicitudesFiltradas,
                    key = { it.id }
                ) { solicitud ->
                    SolicitudCard(
                        solicitud = solicitud,
                        navController = controller
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ============================================================
// HEADER
// ============================================================

@Composable
private fun Header(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = DarkGreen
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Solicitudes de recolección",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )
    }
}

// ============================================================
// RESUMEN DEL USUARIO
// ============================================================

@Composable
private fun ResumenRecolecciones(
    totalKilos: Int,
    totalRecolecciones: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(82.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_weight),
                        contentDescription = null,
                        tint = BrownIcon,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Total reciclado", fontSize = 11.sp, color = SecondaryText)
                    Text(text = "$totalKilos KG", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MainGreen)
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(82.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, CardBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_collection),
                        contentDescription = null,
                        tint = BrownIcon,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Recolecciones", fontSize = 11.sp, color = SecondaryText)
                    Text(text = totalRecolecciones.toString(), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MainGreen)
                }
            }
        }
    }
}

// ============================================================
// CARD DE CADA SOLICITUD
// ============================================================

@Composable
private fun SolicitudCard(
    solicitud: Solicitud,
    navController: NavController
) {
    val estadoUpper = solicitud.estado.uppercase()

    val cardColor = when (estadoUpper) {
        "PUBLICADA" -> Color(0xFFFEF3D7)
        "ACEPTADA" -> Color(0xFFF2F7ED)
        "COMPLETADA" -> Color(0xFFE3EDDB)
        "CANCELADA" -> Color(0xFFFFD8D4)
        else -> Color.White
    }

    val borderColor = when (estadoUpper) {
        "PUBLICADA" -> Color(0xFFF5E1B8)
        "ACEPTADA" -> Color(0xFFD5E3CD)
        "COMPLETADA" -> Color(0xFFD5E3CD)
        "CANCELADA" -> Color(0xFFF3A29B)
        else -> CardBorderColor
    }

    val badgeColor = when (estadoUpper) {
        "PUBLICADA" -> Color(0xFFD99B4B)
        "ACEPTADA" -> Color(0xFF536D45)
        "COMPLETADA" -> Color(0xFF536D45)
        "CANCELADA" -> Color(0xFFC94338)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recolección # ${solicitud.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = badgeColor,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = solicitud.estado,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            DetailItem(icon = Icons.Outlined.LocationOn, text = solicitud.direccion)
            DetailItem(iconRes = R.drawable.ic_box, text = formatMateriales(solicitud.materiales))
            DetailItem(iconRes = R.drawable.ic_weight, text = "${solicitud.pesoKg} kg")
            DetailItem(iconRes = R.drawable.ic_calendar, text = solicitud.fechaRecoleccion)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Ver detalles →",
                    color = badgeColor, // El color del texto "Ver detalles" coincide exactamente con el estado (naranja, verde o rojo)
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { navController.navigate(
                        "${AppScreens.detalleSolicitudUsuario.name}/${solicitud.id}"
                    ) }
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null
) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = BrownIcon
            )
        } else if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = BrownIcon
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 14.sp, color = SecondaryText)
    }
}

// ============================================================
// BOTTOM BAR PARA USUARIO
// ============================================================

@Composable
private fun UserBottomBar(controller: NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { controller.navigate(AppScreens.homeCiudadano.name) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text("Inicio", fontSize = 12.sp) },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_collection),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Recolecciones", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(MainGreen, CircleShape)
                    )
                }
            },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Recompensas */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_rewards),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Recompensas", fontSize = 12.sp) },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = false,
            onClick = { controller.navigate(AppScreens.perfil.name) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text("Perfil", fontSize = 12.sp) },
            colors = navigationItemColors()
        )
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MainGreen,
    selectedTextColor = MainGreen,
    unselectedIconColor = SecondaryText,
    unselectedTextColor = SecondaryText,
    indicatorColor = Color.Transparent
)

// ============================================================
// FORMATEAR MATERIALES
// ============================================================

private fun formatMateriales(materiales: List<String>): String {
    if (materiales.isEmpty()) return "Sin materiales"
    return materiales.map { material ->
        when (material.uppercase()) {
            "PLASTICO" -> "Plástico"
            "PAPEL" -> "Papel"
            "CARTON" -> "Cartón"
            "VIDRIO" -> "Vidrio"
            "METAL" -> "Metal"
            "ELECTRONICOS" -> "Electrónicos"
            "MADERA" -> "Madera"
            "OTROS" -> "Otros"
            else -> material
        }
    }.joinToString(", ")
}
private fun normalizarTexto(texto: String): String {
    return Normalizer
        .normalize(texto, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()
        .trim()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SolicitudesUsuarioScreenPreview() {
    RebottleTheme {
        SolicitudesUsuarioScreenContent(
            controller = rememberNavController(),
            state = SolicitudState(
                isHistoryLoading = false,
                solicitudes = listOf(
                    Solicitud(
                        id = "1",
                        direccion = "Calle 123 #45-67",
                        materiales = listOf("Plástico", "Cartón"),
                        pesoKg = 15,
                        fechaRecoleccion = "2026-10-05",
                        estado = "PENDIENTE"
                    ),
                    Solicitud(
                        id = "2",
                        direccion = "Carrera 80 #12-34",
                        materiales = listOf("Vidrio"),
                        pesoKg = 10,
                        fechaRecoleccion = "2026-09-20",
                        estado = "COMPLETADA"
                    )
                )
            )
        )
    }
}

