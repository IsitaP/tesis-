package com.rebottle.app.ui.screens.recolector.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens


// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val selectedColor = Color(0xFF536D45)
private val unselectedColor = Color(0xFF7A806F)


// ----------------------------------------------------
// BARRA DE NAVEGACIÓN INFERIOR — RECOLECTOR
// ----------------------------------------------------

@Composable
fun BarraNavegacionRecolector(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // INICIO
        ItemBarraRecolector(
            title = "Inicio",
            selected = currentRoute == AppScreens.homeRecolector.name,
            imageVector = Icons.Outlined.Home,
            modifier = Modifier.weight(1f),
            onClick = {
                if (currentRoute != AppScreens.homeRecolector.name) {
                    controller.navigate(AppScreens.homeRecolector.name)
                }
            }
        )

        // SOLICITUDES
        ItemBarraRecolector(
            title = "Solicitudes",
            selected = currentRoute == AppScreens.solicitudesRecolector.name,
            iconRes = R.drawable.icon_collection,
            modifier = Modifier.weight(1f),
            onClick = {
                if (currentRoute != AppScreens.solicitudesRecolector.name) {
                    controller.navigate(AppScreens.solicitudesRecolector.name)
                }
            }
        )

        // RUTAS (aún sin pantalla)
        ItemBarraRecolector(
            title = "Rutas",
            selected = currentRoute == AppScreens.rutas.name,
            iconRes = R.drawable.icon_routes,
            modifier = Modifier.weight(1f),
            onClick = {
                // TODO: descomentar cuando exista PantallaRutasRecolector
                // controller.navigate(AppScreens.rutas.name)
            }
        )

        // PERFIL
        ItemBarraRecolector(
            title = "Perfil",
            selected = currentRoute == AppScreens.perfil.name,
            imageVector = Icons.Outlined.Person,
            modifier = Modifier.weight(1f),
            onClick = {
                if (currentRoute != AppScreens.perfil.name) {
                    controller.navigate(AppScreens.perfil.name)
                }
            }
        )
    }
}


// ----------------------------------------------------
// ITEM INDIVIDUAL DE LA BARRA
// ----------------------------------------------------

@Composable
private fun ItemBarraRecolector(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    imageVector: ImageVector? = null,
    onClick: () -> Unit
) {

    val itemColor = if (selected) {
        selectedColor
    } else {
        unselectedColor
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ICONO
        if (imageVector != null) {

            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(29.dp),
                tint = itemColor
            )

        } else if (iconRes != null) {

            Icon(
                painter = painterResource(
                    id = iconRes
                ),
                contentDescription = title,
                modifier = Modifier.size(31.dp),
                tint = itemColor
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // TEXTO
        Text(
            text = title,
            fontSize = 12.sp,
            color = itemColor,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        // PUNTICO DEL ITEM SELECCIONADO
        if (selected) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(
                        color = selectedColor,
                        shape = CircleShape
                    )
            )

        } else {

            // Mantiene todos los textos alineados
            Spacer(
                modifier = Modifier.height(7.dp)
            )
        }
    }
}