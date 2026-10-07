package com.rebottle.app.ui.screens.fundacion.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.model.EstadoRuta
import com.rebottle.app.model.MapaViewModel
import com.rebottle.app.model.Ruta
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme


// ============================================================
// COLORES
// ============================================================

private val BackgroundColor =
    Color(0xFFFBF8F2)

private val MainGreen =
    Color(0xFF536D45)

private val LightOrange =
    Color(0xFFFEF3D7)

private val MainOrange =
    Color(0xFFD99B4B)

private val MainText =
    Color(0xFF34382F)

private val SecondaryText =
    Color(0xFF7F887A)

private val BorderOrange =
    Color(0xFFF5E1B8)

private val Brown =
    Color(0xFFA55E37)


// ============================================================
// PANTALLA PRINCIPAL
// ============================================================

@Composable
fun RutasCreadasScreen(
    controller: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    mapaViewModel: MapaViewModel = viewModel()
) {

    // ========================================================
    // ESTADO DE RUTAS
    // ========================================================

    val rutasState by
    mapaViewModel
        .rutasState
        .collectAsState()


    // ========================================================
    // ESCUCHAR FIRESTORE
    // ========================================================

    LaunchedEffect(Unit) {

        mapaViewModel
            .escucharRutasPublicadas()
    }


    // ========================================================
    // CONTENIDO
    // ========================================================

    CollectionRoutesContent(
        controller = controller,
        modifier = modifier,
        onBackClick = onBackClick,
        onCreateClick = onCreateClick,
        rutas = rutasState.rutasPublicadas,
        isLoading = rutasState.isLoading,
        error = rutasState.error
    )
}


// ============================================================
// CONTENIDO DE LA PANTALLA
// ============================================================

@Composable
private fun CollectionRoutesContent(
    controller: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onDetailsClick: (Ruta) -> Unit = {},
    rutas: List<Ruta>,
    isLoading: Boolean = false,
    error: String? = null
) {

    // ========================================================
    // BUSCADOR
    // ========================================================

    var searchText by
    remember {
        mutableStateOf("")
    }


    val rutasFiltradas =
        remember(
            rutas,
            searchText
        ) {

            if (
                searchText.isBlank()
            ) {

                rutas

            } else {

                rutas.filter { ruta ->

                    ruta.id.contains(
                        searchText,
                        ignoreCase = true
                    ) ||

                            ruta.fechaRecoleccion.contains(
                                searchText,
                                ignoreCase = true
                            ) ||

                            ruta.estado.contains(
                                searchText,
                                ignoreCase = true
                            )
                }
            }
        }


    // ========================================================
    // SCAFFOLD
    // ========================================================

    Scaffold(
        modifier =
            modifier.fillMaxSize(),

        containerColor =
            BackgroundColor,

        bottomBar = {

            CollectionBottomBar(
                controller = controller
            )
        }

    ) { innerPadding ->


        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        horizontal = 24.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {


            // =================================================
            // HEADER
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                Header(
                    onBackClick =
                        onBackClick
                )
            }


            // =================================================
            // BUSCADOR
            // =================================================

            item {

                SearchBar(
                    text =
                        searchText,

                    onValueChange = {
                        searchText = it
                    }
                )
            }


            // =================================================
            // ESTADÍSTICAS
            // =================================================

            item {

                StatsGrid(
                    rutas = rutas
                )
            }


            // =================================================
            // BOTÓN SUPERIOR
            // =================================================

            /*
             * Se conserva en la misma posición
             * para mantener exactamente la estructura
             * visual que ya tenía la pantalla.
             */

            item {

                Box(
                    modifier =
                        Modifier.fillMaxWidth(),

                    contentAlignment =
                        Alignment.CenterEnd
                ) {

                    Surface(
                        onClick =
                            onCreateClick,

                        color =
                            MainGreen,

                        shape =
                            RoundedCornerShape(
                                8.dp
                            )
                    ) {

                        Text(
                            text =
                                "Rutas publicadas →",

                            color =
                                Color.White,

                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),

                            fontSize =
                                14.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }


            // =================================================
            // TÍTULO DE LISTA
            // =================================================

            item {

                Text(
                    text =
                        "Rutas de recolección",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainText
                )
            }


            // =================================================
            // CARGANDO
            // =================================================

            if (isLoading) {

                item {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 32.dp
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color =
                                MainGreen
                        )
                    }
                }
            }


            // =================================================
            // ERROR
            // =================================================

            else if (
                error != null
            ) {

                item {

                    Text(
                        text =
                            error,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        fontSize =
                            14.sp
                    )
                }
            }


            // =================================================
            // SIN RUTAS
            // =================================================

            else if (
                rutasFiltradas.isEmpty()
            ) {

                item {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 32.dp
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                if (
                                    searchText.isBlank()
                                ) {
                                    "No hay rutas publicadas"
                                } else {
                                    "No se encontraron rutas"
                                },

                            color =
                                SecondaryText,

                            fontSize =
                                14.sp
                        )
                    }
                }
            }


            // =================================================
            // RUTAS
            // =================================================

            else {

                items(
                    items =
                        rutasFiltradas,

                    key = {
                        it.id
                    }
                ) { ruta ->


                    HistoryCard(
                        ruta = ruta,
                        controller = controller
                    )
                }
            }


            // =================================================
            // ESPACIO FINAL
            // =================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(
                            24.dp
                        )
                )
            }
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun Header(
    onBackClick: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                Icons
                    .AutoMirrored
                    .Outlined
                    .ArrowBack,

            contentDescription =
                null,

            modifier =
                Modifier
                    .size(
                        24.dp
                    )
                    .clickable {
                        onBackClick()
                    },

            tint =
                MainText
        )


        Spacer(
            modifier =
                Modifier.width(
                    16.dp
                )
        )


        Text(
            text =
                "Rutas de recolección",

            fontSize =
                22.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                MainText
        )
    }
}


// ============================================================
// BUSCADOR
// ============================================================

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
private fun SearchBar(
    text: String,
    onValueChange: (String) -> Unit
) {

    TextField(
        value =
            text,

        onValueChange =
            onValueChange,

        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    4.dp,
                    RoundedCornerShape(
                        12.dp
                    )
                ),

        placeholder = {

            Text(
                text =
                    "Buscar ruta...",

                color =
                    SecondaryText,

                fontSize =
                    14.sp
            )
        },

        leadingIcon = {

            Icon(
                imageVector =
                    Icons.Default.Search,

                contentDescription =
                    null,

                tint =
                    SecondaryText,

                modifier =
                    Modifier.size(
                        20.dp
                    )
            )
        },

        trailingIcon = {

            Icon(
                painter =
                    painterResource(
                        id =
                            R.drawable.ic_box
                    ),

                contentDescription =
                    null,

                tint =
                    SecondaryText,

                modifier =
                    Modifier.size(
                        20.dp
                    )
            )
        },

        colors =
            TextFieldDefaults.colors(

                focusedContainerColor =
                    Color.White,

                unfocusedContainerColor =
                    Color.White,

                focusedIndicatorColor =
                    Color.Transparent,

                unfocusedIndicatorColor =
                    Color.Transparent
            ),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        singleLine =
            true
    )
}


// ============================================================
// ESTADÍSTICAS
// ============================================================

@Composable
private fun StatsGrid(
    rutas: List<Ruta>
) {

    val cantidadRutas =
        rutas.size


    val cantidadSolicitudes =
        rutas.sumOf {
            it.solicitudesIds.size
        }


    val distanciaTotalMetros =
        rutas.sumOf {
            it.distanciaTotalMetros
        }


    val distanciaTotalKm =
        distanciaTotalMetros /
                1000.0


    val duracionTotalMinutos =
        rutas.sumOf {
            it.duracionTotalSegundos
        } / 60


    Column(
        verticalArrangement =
            Arrangement.spacedBy(
                16.dp
            )
    ) {


        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {


            StatCard(
                label =
                    "Rutas publicadas",

                value =
                    cantidadRutas.toString(),

                iconRes =
                    R.drawable.ic_recycled,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            StatCard(
                label =
                    "Solicitudes",

                value =
                    cantidadSolicitudes.toString(),

                iconRes =
                    R.drawable.ic_check_circle,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )
        }


        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {


            StatCard(
                label =
                    "Distancia",

                value =
                    String.format(
                        "%.1f KM",
                        distanciaTotalKm
                    ),

                iconRes =
                    R.drawable.ic_weight,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            StatCard(
                label =
                    "Duración",

                value =
                    "$duracionTotalMinutos MIN",

                iconRes =
                    R.drawable.icon_collection,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )
        }
    }
}


// ============================================================
// TARJETA ESTADÍSTICA
// ============================================================

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {

    Card(
        modifier =
            modifier.shadow(
                2.dp,
                RoundedCornerShape(
                    20.dp
                )
            ),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    16.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Box(
                modifier =
                    Modifier.size(
                        40.dp
                    ),

                contentAlignment =
                    Alignment.Center
            ) {


                if (
                    iconRes != null
                ) {

                    Icon(
                        painter =
                            painterResource(
                                id =
                                    iconRes
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                32.dp
                            ),

                        tint =
                            MainOrange
                    )

                } else {

                    Box(
                        modifier =
                            Modifier
                                .size(
                                    24.dp
                                )
                                .background(
                                    Color.LightGray,
                                    CircleShape
                                )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Column {

                Text(
                    text =
                        label,

                    fontSize =
                        12.sp,

                    color =
                        SecondaryText
                )


                Text(
                    text =
                        value,

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainGreen
                )
            }
        }
    }
}


// ============================================================
// CARD DE RUTA
// ============================================================

@Composable
private fun HistoryCard(
    ruta: Ruta,
    controller: NavController
) {

    val distanciaKm =
        ruta.distanciaTotalMetros /
                1000.0


    val minutos =
        if (
            ruta.duracionTotalSegundos <= 0
        ) {
            0
        } else {
            (
                    ruta.duracionTotalSegundos +
                            59
                    ) / 60
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    LightOrange
            ),

        border =
            BorderStroke(
                1.dp,
                BorderOrange
            )
    ) {


        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {


            // =================================================
            // ID Y ESTADO
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Text(
                    text =
                        "Ruta ${ruta.id}",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        MainText
                )


                StatusBadge(
                    text =
                        formatEstadoRuta(
                            ruta.estado
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // INFORMACIÓN DE LA RUTA
            // =================================================

            DetailItem(
                icon =
                    Icons.Outlined.LocationOn,

                text =
                    "${ruta.ordenSolicitudes.size} puntos de recolección"
            )


            DetailItem(
                iconRes =
                    R.drawable.ic_box,

                text =
                    "${ruta.solicitudesIds.size} solicitudes"
            )


            DetailItem(
                iconRes =
                    R.drawable.ic_weight,

                text =
                    String.format(
                        "%.1f km",
                        distanciaKm
                    )
            )


            DetailItem(
                iconRes =
                    R.drawable.ic_calendar,

                text =
                    formatearFecha(
                        ruta.fechaRecoleccion
                    )
            )


            DetailItem(
                iconRes =
                    R.drawable.ic_clock,

                text =
                    "$minutos min"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // =================================================
            // VER DETALLES
            // =================================================

            Box(
                modifier =
                    Modifier.fillMaxWidth(),

                contentAlignment =
                    Alignment.CenterEnd
            ) {


                Text(
                    text =
                        stringResource(
                            R.string.view_details_label
                        ) + " →",

                    color =
                        Brown,

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Medium,

                    modifier =
                        Modifier.clickable {

                            controller.navigate(
                                "${AppScreens.detalleRuta.name}/${ruta.id}"
                            )
                        }
                )
            }
        }
    }
}


// ============================================================
// ESTADO DE RUTA
// ============================================================

@Composable
private fun StatusBadge(
    text: String
) {

    Surface(
        color =
            MainOrange,

        shape =
            RoundedCornerShape(
                50.dp
            )
    ) {

        Text(
            text =
                text,

            color =
                Color.White,

            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                ),

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// ============================================================
// DETALLE
// ============================================================

@Composable
private fun DetailItem(
    text: String,
    icon: ImageVector? = null,
    iconRes: Int? = null
) {

    Row(
        modifier =
            Modifier.padding(
                vertical = 4.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        if (
            icon != null
        ) {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(
                        18.dp
                    ),

                tint =
                    MainOrange
            )

        } else if (
            iconRes != null
        ) {

            Icon(
                painter =
                    painterResource(
                        id =
                            iconRes
                    ),

                contentDescription =
                    null,

                modifier =
                    Modifier.size(
                        18.dp
                    ),

                tint =
                    MainOrange
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Text(
            text =
                text,

            fontSize =
                14.sp,

            color =
                SecondaryText
        )
    }
}


// ============================================================
// FORMATEAR ESTADO
// ============================================================

private fun formatEstadoRuta(
    estado: String
): String {

    return when (estado) {

        EstadoRuta.PUBLICADA.name ->
            "Publicada"

        EstadoRuta.ASIGNADA.name ->
            "Asignada"

        EstadoRuta.EN_CURSO.name ->
            "En curso"

        EstadoRuta.COMPLETADA.name ->
            "Completada"

        EstadoRuta.CANCELADA.name ->
            "Cancelada"

        else ->
            estado
    }
}


// ============================================================
// FORMATEAR FECHA
// ============================================================

private fun formatearFecha(
    fecha: String
): String {

    /*
     * Firestore:
     *
     * 2026-10-10
     *
     * Pantalla:
     *
     * 10/10/2026
     */

    val partes =
        fecha.split("-")


    return if (
        partes.size == 3
    ) {

        "${partes[2]}/${partes[1]}/${partes[0]}"

    } else {

        fecha
    }
}


// ============================================================
// BOTTOM BAR
// ============================================================

@Composable
private fun CollectionBottomBar(
    controller: NavController
) {

    val navBackStackEntry by
    controller
        .currentBackStackEntryAsState()


    val currentRoute =
        navBackStackEntry
            ?.destination
            ?.route


    NavigationBar(
        containerColor =
            Color.White,

        tonalElevation =
            0.dp
    ) {


        // ====================================================
        // INICIO
        // ====================================================

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppScreens.homeFundacion.name,

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.homeFundacion.name
                ) {

                    controller.navigate(
                        AppScreens
                            .homeFundacion
                            .name
                    )
                }
            },

            icon = {

                Icon(
                    Icons.Outlined.Home,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            26.dp
                        )
                )
            },

            label = {

                Text(
                    stringResource(
                        R.string.tab_inicio
                    ),

                    fontSize =
                        12.sp
                )
            },

            colors =
                navigationItemColors()
        )


        // ====================================================
        // SOLICITUDES
        // ====================================================

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppScreens.solicitudesFundacion.name,

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.solicitudesFundacion.name
                ) {

                    controller.navigate(
                        AppScreens
                            .solicitudesFundacion
                            .name
                    )
                }
            },

            icon = {

                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable.icon_collection
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            26.dp
                        )
                )
            },

            label = {

                Text(
                    stringResource(
                        R.string.tab_solicitudes
                    ),

                    fontSize =
                        12.sp
                )
            },

            colors =
                navigationItemColors()
        )


        // ====================================================
        // RUTAS
        // ====================================================

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppScreens.rutas.name,

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.rutas.name
                ) {

                    controller.navigate(
                        AppScreens
                            .rutas
                            .name
                    )
                }
            },

            icon = {

                Icon(
                    painter =
                        painterResource(
                            id =
                                R.drawable.icon_routes
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            42.dp
                        )
                )
            },

            label = {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text =
                            stringResource(
                                R.string.tab_rutas
                            ),

                        fontSize =
                            12.sp,

                        modifier =
                            Modifier.offset(
                                y = (-4).dp
                            )
                    )


                    if (
                        currentRoute ==
                        AppScreens.rutas.name
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(
                                        4.dp
                                    )
                                    .background(
                                        MainGreen,
                                        CircleShape
                                    )
                        )
                    }
                }
            },

            colors =
                navigationItemColors()
        )


        // ====================================================
        // PERFIL
        // ====================================================

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppScreens.perfil.name,

            onClick = {

                if (
                    currentRoute !=
                    AppScreens.perfil.name
                ) {

                    controller.navigate(
                        AppScreens
                            .perfil
                            .name
                    )
                }
            },

            icon = {

                Icon(
                    Icons.Outlined.Person,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            26.dp
                        )
                )
            },

            label = {

                Text(
                    stringResource(
                        R.string.tab_perfil
                    ),

                    fontSize =
                        12.sp
                )
            },

            colors =
                navigationItemColors()
        )
    }
}


// ============================================================
// COLORES BOTTOM BAR
// ============================================================

@Composable
private fun navigationItemColors() =
    NavigationBarItemDefaults.colors(

        selectedIconColor =
            MainGreen,

        selectedTextColor =
            MainGreen,

        unselectedIconColor =
            SecondaryText,

        unselectedTextColor =
            SecondaryText,

        indicatorColor =
            Color.Transparent
    )


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true
)
@Composable
fun CollectionRequestsScreenPreview() {

    RebottleTheme {

        CollectionRoutesContent(

            controller =
                rememberNavController(),

            rutas =
                listOf(
                    Ruta(
                        id =
                            "RT-0002",

                        solicitudesIds =
                            listOf(
                                "RB-0029",
                                "RB-0028",
                                "RB-0027",
                                "RB-0030",
                                "RB-0026"
                            ),

                        fechaRecoleccion =
                            "2026-10-10",

                        estado =
                            EstadoRuta.PUBLICADA.name,

                        recolectorId =
                            null,

                        ordenSolicitudes =
                            listOf(
                                "RB-0027",
                                "RB-0029",
                                "RB-0028",
                                "RB-0030",
                                "RB-0026"
                            ),

                        distanciaTotalMetros =
                            5863,

                        duracionTotalSegundos =
                            780
                    )
                )
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CollectionRequestsScreenPreview2() {

    RebottleTheme {

        CollectionRoutesContent(

            controller =
                rememberNavController(),

            rutas =
                listOf(
                    Ruta(
                        id =
                            "RT-0002",

                        solicitudesIds =
                            listOf(
                                "RB-0029",
                                "RB-0028",
                                "RB-0027",
                                "RB-0030",
                                "RB-0026"
                            ),

                        fechaRecoleccion =
                            "2026-10-10",

                        estado =
                            EstadoRuta.PUBLICADA.name,

                        ordenSolicitudes =
                            listOf(
                                "RB-0027",
                                "RB-0029",
                                "RB-0028",
                                "RB-0030",
                                "RB-0026"
                            ),

                        distanciaTotalMetros =
                            5863,

                        duracionTotalSegundos =
                            780
                    )
                )
        )
    }
}