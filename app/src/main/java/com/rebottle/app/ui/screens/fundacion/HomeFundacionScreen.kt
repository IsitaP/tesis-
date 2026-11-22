package com.rebottle.app.ui.screens.fundacion

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.rebottle.app.R
import com.rebottle.app.model.HomeFundacionState
import com.rebottle.app.model.HomeFundacionViewModel
import com.rebottle.app.model.PerfilCache
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.usuario.gamificacion.formatKg
import com.rebottle.app.ui.theme.RebottleColors

// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val LightGreen = RebottleColors.LightGreen
private val BorderGreen = RebottleColors.BorderGreen
private val Brown = RebottleColors.Brown
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText


// ----------------------------------------------------
// HOME FUNDACIÓN
// ----------------------------------------------------

@Composable
fun HomeFundacionScreen(
   controller: NavController,
   model: HomeFundacionViewModel = viewModel()
) {

    // Datos reales: solicitudes y rutas en tiempo real
    val state by model.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        containerColor = BackgroundColor,

        bottomBar = {

            ReBottleBottomBar(
                controller = controller
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 25.dp,
                    bottom = 25.dp
                )
        ) {

            HeaderFundacion()

            Spacer(
                modifier = Modifier.height(34.dp)
            )


            Text(
                text = "¿Qué desea hacer hoy?",
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            SectionTitle(
                title = "Ver solicitudes pendientes"
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            RequestsCard(
                state = state,
                onClick = { controller.navigate(AppScreens.solicitudesFundacion.name) }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            SectionTitle(
                title = "Próxima ruta"
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            NextRouteCard(
                state = state,
                onClick = {
                    // El detalle solo muestra rutas publicadas o asignadas;
                    // una ruta en curso se sigue desde Rutas.
                    val ruta = state.proximaRuta
                    if (ruta != null && !ruta.enCurso) controller.navigate("${AppScreens.detalleRuta.name}/${ruta.rutaId}")
                    else controller.navigate(AppScreens.rutas.name)
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            SectionTitle(
                title = "Resumen del día"
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            DailySummaryCard(
                state = state,
                onClick = { controller.navigate(AppScreens.aportesFundacion.name) }
            )
        }
    }
}


// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun HeaderFundacion() {

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid != null) PerfilCache.cargar(uid)
    }

    val nombre = PerfilCache.nombre?.ifEmpty { null } ?: "Fundación"
    val direccion = PerfilCache.direccion?.trim()?.ifEmpty { null }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Hola, $nombre !",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Brown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Solo se muestra si la fundación registró una dirección
            if (direccion != null) {

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Ubicación",
                        tint = SecondaryText,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = direccion,
                        fontSize = 13.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


// ----------------------------------------------------
// TÍTULOS
// ----------------------------------------------------

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        color = MainText
    )
}


// ----------------------------------------------------
// RESUMEN DE SOLICITUDES
// ----------------------------------------------------

@Composable
private fun RequestsCard(
    state: HomeFundacionState,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(140.dp),

        shape =
            RoundedCornerShape(8.dp),

        border =
            BorderStroke(
                width = 1.dp,
                color = BorderGreen
            ),

        colors =
            CardDefaults.cardColors(
                containerColor = LightGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 14.dp,
                    end = 8.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "Resumen de solicitudes",

                    fontSize = 15.sp,

                    maxLines = 1,

                    fontWeight =
                        FontWeight.Medium,

                    color = MainText
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Solicitudes por atender",

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                Text(
                    text = if (state.nuevas == 1) "1 nueva" else "${state.nuevas} nuevas",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )


                Text(
                    text = if (state.aceptadas == 1) "1 aceptada" else "${state.aceptadas} aceptadas",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )


                Text(
                    text =
                        if (state.completadasHoy == 1) "1 completada hoy"
                        else "${state.completadasHoy} completadas hoy",

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(
                    text = "Ver todas →",
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color = MainGreen
                )
            }


            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .fundacion_requests
                    ),

                contentDescription =
                    "Resumen de solicitudes",

                modifier = Modifier
                    .width(120.dp)
                    .height(105.dp),

                contentScale =
                    ContentScale.Fit
            )
        }
    }
}


// ----------------------------------------------------
// PRÓXIMA RUTA
// ----------------------------------------------------

@Composable
private fun NextRouteCard(
    state: HomeFundacionState,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(140.dp),

        shape =
            RoundedCornerShape(8.dp),

        border =
            BorderStroke(
                width = 1.dp,
                color = BorderGreen
            ),

        colors =
            CardDefaults.cardColors(
                containerColor = LightGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 14.dp,
                    end = 8.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                val ruta = state.proximaRuta

                Text(
                    text =
                        "Siguiente recolección",

                    fontSize = 15.sp,

                    maxLines = 1,

                    fontWeight =
                        FontWeight.Medium,

                    color = MainText
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        ruta?.lugar ?: "No hay rutas pendientes",

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis,

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                if (ruta != null) {

                Text(
                    text = ruta.cuando,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = if (ruta.enCurso) MainGreen else SecondaryText
                )


                Text(
                    text =
                        "Usuario: ${ruta.usuario}",

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis,

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                Text(
                    text =
                        "Recolector: ${ruta.recolector}",

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis,

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )
                }


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(
                    text = if (ruta != null) "Ver ruta →" else "Ver rutas →",
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color = MainGreen
                )
            }


            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .fundacion_route
                    ),

                contentDescription =
                    "Próxima ruta",

                modifier = Modifier
                    .width(120.dp)
                    .height(105.dp),

                contentScale =
                    ContentScale.Fit
            )
        }
    }
}


// ----------------------------------------------------
// RESUMEN DEL DÍA
// ----------------------------------------------------

@Composable
private fun DailySummaryCard(
    state: HomeFundacionState,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(140.dp),

        shape =
            RoundedCornerShape(8.dp),

        border =
            BorderStroke(
                width = 1.dp,
                color = BorderGreen
            ),

        colors =
            CardDefaults.cardColors(
                containerColor = LightGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 14.dp,
                    end = 8.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "Actividad de hoy",

                    fontSize = 15.sp,

                    maxLines = 1,

                    fontWeight =
                        FontWeight.Medium,

                    color = MainText
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        if (state.completadasHoy == 1) "1 recolección realizada"
                        else "${state.completadasHoy} recolecciones realizadas",

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                Text(
                    text =
                        "${formatKg(state.kgRecicladosHoy)} de materiales reciclados",

                    fontSize = 11.sp,
                    lineHeight = 14.sp,

                    color = SecondaryText
                )


                if (state.kgPorRecolectarHoy > 0) {
                    Text(
                        text = "${formatKg(state.kgPorRecolectarHoy)} por recolectar hoy",
                        fontSize = 11.sp,
                    lineHeight = 14.sp,
                        color = SecondaryText
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Ver estadísticas →",

                    fontSize = 11.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color = MainGreen
                )
            }


            Image(
                painter =
                    painterResource(
                        id =
                            R.drawable
                                .fundacion_summary
                    ),

                contentDescription =
                    "Resumen de actividad",

                modifier = Modifier
                    .width(120.dp)
                    .height(105.dp),

                contentScale =
                    ContentScale.Fit
            )
        }
    }
}


// ----------------------------------------------------
// ICONOS PERSONALIZADOS BOTTOM BAR
// ----------------------------------------------------

@Composable
private fun BottomNavIcon(

    @DrawableRes iconRes: Int,

    contentDescription: String,

    size: Dp = 25.dp

) {

    Image(
        painter =
            painterResource(
                id = iconRes
            ),

        contentDescription =
            contentDescription,

        modifier =
            Modifier.size(size)
    )
}


// ----------------------------------------------------
// BOTTOM NAVIGATION FUNDACIÓN
// ----------------------------------------------------

@Composable
private fun ReBottleBottomBar(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(top = 3.dp)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {

            // INICIO
            NavigationBarItem(
                selected = currentRoute == AppScreens.homeFundacion.name,
                onClick = {
                    if (currentRoute != AppScreens.homeFundacion.name) {
                        controller.navigate(AppScreens.homeFundacion.name)
                    }
                },
                icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = "Inicio", modifier = Modifier.size(26.dp)) },
                label = { Text(text = "Inicio", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            // SOLICITUDES
            NavigationBarItem(
                selected = currentRoute == AppScreens.solicitudesFundacion.name,
                onClick = {
                    if (currentRoute != AppScreens.solicitudesFundacion.name) {
                        controller.navigate(AppScreens.solicitudesFundacion.name)
                    }
                },
                icon = { BottomNavIcon(iconRes = R.drawable.icon_collection, contentDescription = "Solicitudes") },
                label = { Text(text = "Solicitudes", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            // RUTAS (aún sin pantalla)
            NavigationBarItem(
                selected = currentRoute == AppScreens.rutas.name,
                onClick = {
                    if (currentRoute != AppScreens.rutas.name) {
                        controller.navigate(AppScreens.rutas.name)
                    }
                },
                icon = { BottomNavIcon(iconRes = R.drawable.icon_routes, contentDescription = "Recolectores", size = 42.dp) },
                label = { Text(text = "Recolectores", fontSize = 12.sp, modifier = Modifier.offset(y = (-4).dp)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            // PERFIL
            NavigationBarItem(
                selected = currentRoute == AppScreens.perfil.name,
                onClick = {
                    if (currentRoute != AppScreens.perfil.name) {
                        controller.navigate(AppScreens.perfil.name)
                    }
                },
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Perfil", modifier = Modifier.size(26.dp)) },
                label = { Text(text = "Perfil", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}