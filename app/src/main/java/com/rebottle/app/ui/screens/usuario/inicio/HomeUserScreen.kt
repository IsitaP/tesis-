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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

import com.rebottle.app.R
import com.rebottle.app.model.Solicitud
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.model.PerfilCache
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors



private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val LightGreen = RebottleColors.LightGreen
private val BorderGreen = RebottleColors.BorderGreen
private val Brown = RebottleColors.Brown
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText


// ============================================================
// HOME USUARIO
// ============================================================

@Composable
fun HomeUserScreen(
    controller: NavController,
    viewModel: SolicitudViewModel = viewModel()
) {

    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val state by viewModel.state.collectAsState()

    val ultimaSolicitud = state.solicitudes
        .filter { it.usuarioId == usuarioId }
        .maxByOrNull { solicitud ->
            solicitud.id
                .removePrefix("RB-")
                .toIntOrNull() ?: 0
        }

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


            if (ultimaSolicitud != null) {

                CollectionCard(
                    solicitud = ultimaSolicitud,
                    onDetailsClick = {
                        controller.navigate(
                            "${AppScreens.detalleSolicitudUsuario.name}/${ultimaSolicitud.id}"
                        )
                    }
                )

            } else {

                Text(
                    text = "Aún no tienes solicitudes",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }


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
    solicitud: Solicitud,
    onDetailsClick: () -> Unit
) {

    // Toda la tarjeta abre el detalle
    Card(
        onClick = onDetailsClick,

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),

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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText,
                    maxLines = 1
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                Text(
                    text = "Estado: ${solicitud.estado}",
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = SecondaryText
                )


                Text(
                    text = "Fecha: ${solicitud.fechaRecoleccion}",
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = SecondaryText
                )


                Text(
                    text = "Hora: ${
                        if (solicitud.horaRuta.isBlank()) {
                            "Por asignar"
                        } else {
                            formatearHoraSolicitud(
                                solicitud.horaRuta
                            )
                        }
                    }",
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = SecondaryText
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

    // Toda la tarjeta abre la ruta de aprendizaje
    Card(
        onClick = onLearnClick,

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 155.dp),

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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                Text(
                    text = "Aprende a separar tus residuos por material y mantén limpios los envases antes de entregarlos.",
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = SecondaryText
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
                        id = R.drawable.home_learn1
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
            modifier = Modifier.weight(1f),
            onClick = {
                controller.navigate(
                    AppScreens.ranking.name
                )
            }
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
            .background(Color.White)
            // Deja libre el espacio de los botones o la barra de gestos del celular
            .navigationBarsPadding()
            .height(82.dp)
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
                if (currentRoute != AppScreens.solicitudesUsuario.name) {
                    controller.navigate(AppScreens.solicitudesUsuario.name)
                }
            }
        )


        // =====================================================
        // RECOMPENSAS
        // =====================================================

        ReBottleBottomItem(
            title = "Recompensas",

            selected = currentRoute == AppScreens.recompensas.name,

            iconRes =
                R.drawable.icon_rewards,

            selectedColor =
                selectedColor,

            unselectedColor =
                unselectedColor,

            modifier =
                Modifier.weight(1f),

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.recompensas.name
                ) {

                    controller.navigate(
                        AppScreens.recompensas.name
                    )
                }
            }
        )


        // =====================================================
        // PERFIL
        // =====================================================

        ReBottleBottomItem(
            title = "Perfil",

            selected = currentRoute == AppScreens.perfilUsuario.name,

            imageVector = Icons.Outlined.Person,

            selectedColor = selectedColor,

            unselectedColor = unselectedColor,

            modifier = Modifier.weight(1f),

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
private fun formatearHoraSolicitud(
    hora: String
): String {

    val partes =
        hora.split(":")

    if (partes.size != 2) {
        return hora
    }

    val horas =
        partes[0].toIntOrNull()
            ?: return hora

    val minutos =
        partes[1].toIntOrNull()
            ?: return hora


    val periodo =
        if (horas < 12) {
            "AM"
        } else {
            "PM"
        }


    val hora12 =
        when {
            horas == 0 -> 12
            horas > 12 -> horas - 12
            else -> horas
        }


    return String.format(
        "%d:%02d %s",
        hora12,
        minutos,
        periodo
    )
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

        //HomeUserScreen(
            //controller = controller
        //)
    }
}