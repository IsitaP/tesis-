package com.rebottle.app.ui.screens.fundacion

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R


// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF536D45)
private val DarkGreen = Color(0xFF394C31)
private val LightGreen = Color(0xFFF2F7ED)
private val BorderGreen = Color(0xFFD5E3CD)
private val Brown = Color(0xFFA55E37)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)


// ----------------------------------------------------
// HOME FUNDACIÓN
// ----------------------------------------------------

@Composable
fun HomeFundacionScreen() {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            ReBottleBottomBar()
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 24.dp,
                    bottom = 24.dp
                )
        ) {

            HeaderFundacion()

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "¿Qué desea hacer hoy?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Ver solicitudes pendientes")

            Spacer(modifier = Modifier.height(8.dp))

            RequestsCard()

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Próxima ruta")

            Spacer(modifier = Modifier.height(8.dp))

            NextRouteCard()

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Resumen del día")

            Spacer(modifier = Modifier.height(8.dp))

            DailySummaryCard()
        }
    }
}


// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun HeaderFundacion() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "Hola, EcoAlianza !",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Ubicación",
                    tint = SecondaryText,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Cll 34b #18-24",
                    fontSize = 18.sp,
                    color = SecondaryText
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = MainGreen,
                modifier = Modifier.size(25.dp)
            )

            // Punto rojo de notificación
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 7.dp,
                        end = 6.dp
                    )
                    .size(6.dp)
                    .background(
                        color = Color(0xFFE16A58),
                        shape = CircleShape
                    )
            )
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
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MainText
    )
}


// ----------------------------------------------------
// RESUMEN DE SOLICITUDES
// ----------------------------------------------------

@Composable
private fun RequestsCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(162.dp),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Resumen de solicitudes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Solicitudes por atender",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "12 nuevas",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "5 aceptadas",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "18 completadas hoy",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Ver todas →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.fundacion_requests
                ),
                contentDescription = "Resumen de solicitudes",
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


// ----------------------------------------------------
// PRÓXIMA RUTA
// ----------------------------------------------------

@Composable
private fun NextRouteCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Siguiente recolección",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Barrio La Floresta",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "10:30 AM",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "Usuario: Juan Pérez",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "Recolector: Lucho Díaz",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Ver ruta →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.fundacion_route
                ),
                contentDescription = "Próxima ruta",
                modifier = Modifier
                    .size(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


// ----------------------------------------------------
// RESUMEN DEL DÍA
// ----------------------------------------------------

@Composable
private fun DailySummaryCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 12.dp,
                    top = 9.dp,
                    bottom = 9.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Actividad de hoy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = "12 recolecciones realizadas",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Text(
                    text = "48 kg de materiales reciclados",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ver estadísticas →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Image(
                painter = painterResource(
                    id = R.drawable.fundacion_summary
                ),
                contentDescription = "Resumen de actividad",
                modifier = Modifier
                    .size(150.dp),
                contentScale = ContentScale.Fit
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
        painter = painterResource(
            id = iconRes
        ),
        contentDescription = contentDescription,
        modifier = Modifier.size(size)
    )
}


// ----------------------------------------------------
// BOTTOM NAVIGATION FUNDACIÓN
// ----------------------------------------------------

@Composable
private fun ReBottleBottomBar() {

    // Esta caja conserva el color de fondo de la pantalla
    // y deja una pequeña separación antes de la barra blanca.
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

            // ------------------------------------------------
            // INICIO
            // ------------------------------------------------

            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = {

                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Inicio",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {

                    Text(
                        text = "Inicio",
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )


            // ------------------------------------------------
            // SOLICITUDES
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {

                    BottomNavIcon(
                        iconRes = R.drawable.icon_collection,
                        contentDescription = "Solicitudes"
                    )
                },
                label = {

                    Text(
                        text = "Solicitudes",
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )


            // ------------------------------------------------
            // RUTAS
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {

                    BottomNavIcon(
                        iconRes = R.drawable.icon_routes,
                        contentDescription = "Rutas",
                        size = 42.dp
                    )
                },
                label = {

                    Text(
                        text = "Rutas",
                        fontSize = 12.sp,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )


            // ------------------------------------------------
            // PERFIL
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {

                    Text(
                        text = "Perfil",
                        fontSize = 12.sp
                    )
                },
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