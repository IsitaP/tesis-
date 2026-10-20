package com.rebottle.app.ui.screens.fundacion.componentes

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
import com.rebottle.app.ui.theme.RebottleColors


private val selectedColor = RebottleColors.MainGreen
private val unselectedColor = Color(0xFF7A806F)


@Composable
fun BarraNavegacionFundacion(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun ir(destino: String) {
        if (currentRoute != destino) {
            controller.navigate(destino) { launchSingleTop = true }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ItemBarraFundacion(
            title = "Home",
            selected = currentRoute == AppScreens.homeFundacion.name,
            imageVector = Icons.Outlined.Home,
            modifier = Modifier.weight(1f),
            onClick = { ir(AppScreens.homeFundacion.name) }
        )

        ItemBarraFundacion(
            title = "Solicitudes",
            selected = currentRoute == AppScreens.solicitudesFundacion.name,
            iconRes = R.drawable.icon_collection,
            modifier = Modifier.weight(1f),
            onClick = { ir(AppScreens.solicitudesFundacion.name) }
        )

        ItemBarraFundacion(
            title = "Rutas",
            selected = currentRoute == AppScreens.rutas.name ||
                    currentRoute == AppScreens.filtroRutas.name ||
                    currentRoute?.startsWith(AppScreens.detalleRecolectorRuta.name) == true,
            iconRes = R.drawable.icon_routes,
            modifier = Modifier.weight(1f),
            onClick = { ir(AppScreens.rutas.name) }
        )

        ItemBarraFundacion(
            title = "Perfil",
            selected = currentRoute == AppScreens.perfil.name,
            imageVector = Icons.Outlined.Person,
            modifier = Modifier.weight(1f),
            onClick = { ir(AppScreens.perfil.name) }
        )
    }
}


@Composable
private fun ItemBarraFundacion(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    imageVector: ImageVector? = null,
    onClick: () -> Unit
) {

    val itemColor = if (selected) selectedColor else unselectedColor

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(29.dp),
                tint = itemColor
            )
        } else if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(31.dp),
                tint = itemColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            color = itemColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(3.dp))

        if (selected) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color = selectedColor, shape = CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}