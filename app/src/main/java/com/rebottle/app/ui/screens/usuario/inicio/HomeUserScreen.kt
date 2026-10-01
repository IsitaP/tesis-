package com.rebottle.app.ui.screens.usuario.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Person

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme


// ============================================================
// COLORES
// ============================================================

private val BackgroundColor = Color(0xFFFBF9F3)
private val MainGreen = Color(0xFF536D45)
private val DarkGreen = Color(0xFF394C31)
private val LightGreen = Color(0xFFF2F7ED)
private val BorderGreen = Color(0xFFD5E3CD)
private val Brown = Color(0xFFA55E37)
private val MainText = Color(0xFF3D433A)
private val SecondaryText = Color(0xFF7F887A)


// ============================================================
// HOME USUARIO
// ============================================================

@Composable
fun HomeUserScreen(
    controller: NavController
) {

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

            // =================================================
            // HEADER
            // =================================================

            Header()


            Spacer(
                modifier = Modifier.height(34.dp)
            )


            // =================================================
            // ¿QUÉ DESEA HACER HOY?
            // =================================================

            Text(
                text = "¿Qué desea hacer hoy?",
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =================================================
            // BOTÓN SOLICITAR RECOLECCIÓN
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

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


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // MIS SOLICITUDES
            // =================================================

            SectionTitle(
                title = "Mis Solicitudes"
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            CollectionCard(
                solicitudId = "1B98kIH6ntq5wieY7SV7"
                ,
                onDetailsClick = { id ->
                    controller.navigate(
                        "${AppScreens.detalleSolicitudUsuario.name}/$id"
                    )
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // APRENDE A RECICLAR
            // =================================================

            SectionTitle(
                title = "Aprende a reciclar"
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            LearnCard(
                onLearnClick = {
                    controller.navigate(AppScreens.aprendizaje.name)
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // MÁS ACCIONES
            // =================================================

            SectionTitle(
                title = "Mas acciones"
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            MoreActionsSection(controller=controller)
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun Header() {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Hola, Usuario !",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Brown
        )


        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color(0xFFF1F5ED),
                    shape = CircleShape
                ),

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


// ============================================================
// TÍTULOS DE SECCIÓN
// ============================================================

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


// ============================================================
// TARJETA DE SOLICITUD
// ============================================================

@Composable
private fun CollectionCard(
    solicitudId: String,
    onDetailsClick: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),

        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),

        elevation = CardDefaults.cardElevation(
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

            verticalAlignment = Alignment.CenterVertically
        ) {

            // =================================================
            // TEXTO
            // =================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Recolección programada",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText,
                    maxLines = 1
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                Text(
                    text = "Estado: Programada",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )


                Text(
                    text = "Fecha: Agosto 24",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )


                Text(
                    text = "Hora: 10:00 AM",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = SecondaryText
                )


                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                Text(
                    text = "Ver más detalles →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen,
                    modifier = Modifier.clickable {
                        onDetailsClick(solicitudId)
                    }
                )
            }


            // =================================================
            // IMAGEN
            // =================================================

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),

                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.home_collection
                    ),

                    contentDescription = "Recolección programada",

                    modifier = Modifier
                        .width(110.dp)
                        .height(105.dp),

                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}


// ============================================================
// TARJETA APRENDE A RECICLAR
// ============================================================

@Composable
private fun LearnCard(
    onLearnClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp),

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),

        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "¿Cómo reciclar?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                Text(
                    text = "Aprende a separar tus residuos por material y mantén limpios los envases antes de entregarlos.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = SecondaryText
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                Text(
                    text = "Aprender más  →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen,
                    modifier = Modifier.clickable {
                        onLearnClick()
                    }
                )
            }


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            Box(
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight(),

                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.home_learn
                    ),

                    contentDescription = "Rocco reciclando",

                    modifier = Modifier
                        .width(125.dp)
                        .height(115.dp),

                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}


// ============================================================
// MÁS ACCIONES
// ============================================================

@Composable
private fun MoreActionsSection(controller: NavController) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(
            12.dp
        )
    ) {

        ActionCard(
            title = "Mis aportes",
            imageRes = R.drawable.home_impact,
            modifier = Modifier.weight(1f),
            onClick = {
                controller.navigate(
                    AppScreens.misAportes.name
                )
            }
        )


        ActionCard(
            title = "Ranking",
            imageRes = R.drawable.home_ranking,
            modifier = Modifier.weight(1f)
        )
    }
}


// ============================================================
// ACTION CARD
// ============================================================

@Composable
private fun ActionCard(
    title: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier
            .height(150.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            width = 1.dp,
            color = BorderGreen
        ),

        colors = CardDefaults.cardColors(
            containerColor = LightGreen
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Image(
                painter = painterResource(
                    id = imageRes
                ),

                contentDescription = title,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),

                contentScale = ContentScale.Fit
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Text(
                text = title,
                fontSize = 14.sp,
                color = MainText,
                textAlign = TextAlign.Center
            )
        }
    }
}


// ============================================================
// BARRA DE NAVEGACIÓN INFERIOR
// ============================================================

@Composable
fun ReBottleBottomBar(
    controller: NavController
) {

    // Observamos la ruta actual del NavController
    val navBackStackEntry by
    controller.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route


    val selectedColor =
        Color(0xFF5F7D43)

    val unselectedColor =
        Color(0xFF7A806F)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // =====================================================
        // INICIO
        // =====================================================

        ReBottleBottomItem(
            title = "Inicio",

            selected =
                currentRoute == AppScreens.homeCiudadano.name,

            imageVector =
                Icons.Default.Home,

            selectedColor =
                selectedColor,

            unselectedColor =
                unselectedColor,

            modifier =
                Modifier.weight(1f),

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.homeCiudadano.name
                ) {

                    controller.navigate(
                        AppScreens.homeCiudadano.name
                    )
                }
            }
        )


        // =====================================================
        // RECOLECCIONES
        // =====================================================

        ReBottleBottomItem(
            title = "Recolecciones",

            selected =
                currentRoute == AppScreens.solicitudesUsuario.name,

            iconRes =
                R.drawable.icon_collection,

            selectedColor =
                selectedColor,

            unselectedColor =
                unselectedColor,

            modifier =
                Modifier.weight(1f),

            onClick = {
                controller.navigate(AppScreens.solicitudesUsuario.name)
            }
        )


        // =====================================================
        // RECOMPENSAS
        // =====================================================

        ReBottleBottomItem(
            title = "Recompensas",

            // Todavía queda false mientras no exista
            // la pantalla correspondiente.
            selected = false,

            iconRes =
                R.drawable.icon_rewards,

            selectedColor =
                selectedColor,

            unselectedColor =
                unselectedColor,

            modifier =
                Modifier.weight(1f),

            onClick = {

                /*
                 * Aqui va la pantalla de recompensas
                 */
            }
        )


        // =====================================================
        // PERFIL
        // =====================================================

        ReBottleBottomItem(
            title = "Perfil",

            selected =
                currentRoute == AppScreens.perfilUsuario.name,

            imageVector =
                Icons.Outlined.Person,

            selectedColor =
                selectedColor,

            unselectedColor =
                unselectedColor,

            modifier =
                Modifier.weight(1f),

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.perfilUsuario.name
                ) {

                    controller.navigate(
                        AppScreens.perfilUsuario.name
                    )
                }
            }
        )
    }
}


// ============================================================
// ITEM DE LA BARRA INFERIOR
// ============================================================

@Composable
private fun ReBottleBottomItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    imageVector: ImageVector? = null,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {

    val itemColor =

        if (selected) {

            selectedColor

        } else {

            unselectedColor
        }


    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable {

                onClick()
            },

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        // =====================================================
        // ICONO
        // =====================================================

        if (imageVector != null) {

            Icon(
                imageVector = imageVector,

                contentDescription = title,

                modifier =
                    Modifier.size(29.dp),

                tint = itemColor
            )


        } else if (iconRes != null) {

            Icon(
                painter =
                    painterResource(
                        id = iconRes
                    ),

                contentDescription =
                    title,

                modifier =
                    Modifier.size(31.dp),

                tint =
                    itemColor
            )
        }


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        // =====================================================
        // TEXTO
        // =====================================================

        Text(
            text = title,

            fontSize = 12.sp,

            color = itemColor,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(3.dp)
        )


        // =====================================================
        // PUNTO DE SELECCIÓN
        // =====================================================

        if (selected) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(
                        color =
                            selectedColor,

                        shape =
                            CircleShape
                    )
            )


        } else {

            // Espacio para mantener todos alineados
            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )
        }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeUserScreenPreview() {

    val controller =
        rememberNavController()


    RebottleTheme {

        HomeUserScreen(
            controller = controller
        )
    }
}