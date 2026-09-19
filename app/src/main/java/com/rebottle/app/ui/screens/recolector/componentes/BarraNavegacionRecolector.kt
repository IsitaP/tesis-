package com.rebottle.app.ui.screens.recolector.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens


// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF536D45)
private val SecondaryText = Color(0xFF7F887A)


// ----------------------------------------------------
// BARRA DE NAVEGACIÓN INFERIOR — RECOLECTOR
// ----------------------------------------------------

@Composable
fun BarraNavegacionRecolector(
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
                selected = currentRoute == AppScreens.homeRecolector.name,
                onClick = {
                    if (currentRoute != AppScreens.homeRecolector.name) {
                        controller.navigate(AppScreens.homeRecolector.name)
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
                selected = currentRoute == AppScreens.solicitudesRecolector.name,
                onClick = {
                    controller.navigate(AppScreens.solicitudesRecolector.name)
                },
                icon = { BottomNavIcon(iconRes = R.drawable.icon_collection, contentDescription = "Solicitudes") },
                label = { Text(text = "Solicitudes", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            // RUTAS
            NavigationBarItem(
                selected = currentRoute == AppScreens.rutas.name,
                onClick = {
                    // TODO: descomentar cuando exista PantallaRutasRecolector
                    // controller.navigate(AppScreens.rutas.name)
                },
                icon = { BottomNavIcon(iconRes = R.drawable.icon_routes, contentDescription = "Rutas", size = 42.dp) },
                label = { Text(text = "Rutas", fontSize = 12.sp, modifier = Modifier.offset(y = (-4).dp)) },
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
                    controller.navigate(AppScreens.perfil.name)
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

@Composable
private fun BottomNavIcon(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    size: Dp = 25.dp
) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = Modifier.size(size)
    )
}

