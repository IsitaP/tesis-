package com.rebottle.app.ui.screens.fundacion.gestion

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors


// ----------------------------------------------------
// COLORES GENERALES
// ----------------------------------------------------

private val BackgroundColor = RebottleColors.Background

private val MainText = RebottleColors.MainText

private val SecondaryText = RebottleColors.SecondaryText

private val Brown = RebottleColors.Brown


// ----------------------------------------------------
// COMPLETADA - VERDE
// ----------------------------------------------------

private val MainGreen = Color(0xFF536D45)

private val LightGreen = Color(0xFFF2F7ED)

private val BorderGreen = Color(0xFFD5E3CD)


// ----------------------------------------------------
// PENDIENTE - NARANJA
// ----------------------------------------------------

private val MainOrange = Color(0xFFD99B4B)

private val LightOrange = Color(0xFFFEF3D7)

private val BorderOrange = Color(0xFFF5E1B8)


// ----------------------------------------------------
// CANCELADA - ROJO
// ----------------------------------------------------

private val MainRed = Color(0xFFC94338)

private val LightRed = Color(0xFFFFD8D4)

private val BorderRed = Color(0xFFF3A29B)


// ----------------------------------------------------
// MANAGE REQUESTS SCREEN
// ----------------------------------------------------

@Composable
fun ManageRequestsScreen(

    modifier: Modifier = Modifier,

    onBackClick: () -> Unit = {},

    onHomeClick: () -> Unit = {},

    onProfileClick: () -> Unit = {},

    onEditClick: (RequestUiModel) -> Unit = {}

) {


    // ------------------------------------------------
    // DATOS TEMPORALES
    // Después vendrán de Firebase
    // ------------------------------------------------

    val requests = listOf(


        // ------------------------------------------------
        // COMPLETADA
        // ------------------------------------------------

        RequestUiModel(

            id = 1,

            address = "Cll 131a # 53-31",

            items = "Plástico, vidrio, cartón",

            weight = "3 kg",

            date = "Agosto 24",

            time = "10:00 AM",

            assignee = "Juan Correa",

            status = RequestStatus.COMPLETED
        ),


        // ------------------------------------------------
        // PENDIENTE
        // ------------------------------------------------

        RequestUiModel(

            id = 2,

            address = "Cra 15 # 136-32",

            items = "Metal, vidrio",

            weight = "8 kg",

            date = "Septiembre 12",

            time = "9:00 AM",

            assignee = "Juan Correa",

            status = RequestStatus.PENDING
        ),


        // ------------------------------------------------
        // CANCELADA
        // ------------------------------------------------

        RequestUiModel(

            id = 3,

            address = "Cra 45 # 82-20",

            items = "Cartón, plástico",

            weight = "5 kg",

            date = "Septiembre 15",

            time = "2:00 PM",

            assignee = "Samuel Robayo",

            status = RequestStatus.CANCELLED
        )
    )


    Scaffold(

        modifier =
            modifier.fillMaxSize(),

        containerColor =
            BackgroundColor,


        // ------------------------------------------------
        // HEADER
        // ------------------------------------------------

        topBar = {

            Header(
                onBackClick = onBackClick
            )
        },


        // ------------------------------------------------
        // BOTTOM BAR
        // ------------------------------------------------

        bottomBar = {

            ReBottleBottomBar(

                onHomeClick =
                    onHomeClick,

                onProfileClick =
                    onProfileClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 20.dp
                )

        ) {


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    stringResource(
                        id =
                            R.string.history_title
                    ),

                fontSize = 20.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    MainText
            )


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(
                        20.dp
                    ),

                modifier =
                    Modifier.fillMaxSize()

            ) {


                items(requests) { request ->
                    RequestCard(
                        request = request,
                        onEditClick = {
                            onEditClick(request)
                        }
                    )
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )
                }
            }
        }
    }
}


// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun Header(

    onBackClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 40.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        Icon(

            imageVector =
                Icons.AutoMirrored
                    .Outlined
                    .ArrowBack,

            contentDescription =
                "Volver",

            modifier = Modifier
                .size(24.dp)
                .clickable {

                    onBackClick()
                },

            tint =
                MainText
        )


        Spacer(
            modifier =
                Modifier.width(16.dp)
        )


        Text(

            text =
                stringResource(
                    id =
                        R.string
                            .manage_requests_title
                ),

            fontSize = 22.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                MainGreen
        )
    }
}


// ----------------------------------------------------
// CARD DE SOLICITUD
// ----------------------------------------------------

@Composable
private fun RequestCard(

    request: RequestUiModel,

    onEditClick: () -> Unit

) {


    // ------------------------------------------------
    // COLOR DEL FONDO
    // ------------------------------------------------

    val backgroundColor =
        when (request.status) {

            RequestStatus.COMPLETED ->
                LightGreen

            RequestStatus.PENDING ->
                LightOrange

            RequestStatus.CANCELLED ->
                LightRed
        }


    // ------------------------------------------------
    // COLOR DEL BORDE
    // ------------------------------------------------

    val borderColor =
        when (request.status) {

            RequestStatus.COMPLETED ->
                BorderGreen

            RequestStatus.PENDING ->
                BorderOrange

            RequestStatus.CANCELLED ->
                BorderRed
        }


    // ------------------------------------------------
    // COLOR DE "EDITAR"
    // ------------------------------------------------

    val actionColor =
        when (request.status) {

            RequestStatus.COMPLETED ->
                MainGreen

            RequestStatus.PENDING ->
                MainOrange

            RequestStatus.CANCELLED ->
                MainRed
        }


    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(12.dp),

        border =
            BorderStroke(
                width = 1.dp,
                color = borderColor
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    backgroundColor
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {


        Column(

            modifier =
                Modifier.padding(16.dp)

        ) {


            // ------------------------------------------------
            // TÍTULO + ESTADO
            // ------------------------------------------------

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
                        stringResource(

                            id =
                                R.string
                                    .collection_label,

                            request.id
                        ),

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        MainText
                )


                StatusBadge(

                    status =
                        request.status
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ------------------------------------------------
            // DIRECCIÓN
            // ------------------------------------------------

            RequestDetailItem(

                icon =
                    Icons.Outlined.LocationOn,

                text =
                    request.address
            )


            // ------------------------------------------------
            // MATERIALES
            // ------------------------------------------------

            RequestDetailItem(

                iconRes =
                    R.drawable.ic_box,

                text =
                    request.items
            )


            // ------------------------------------------------
            // PESO
            // ------------------------------------------------

            RequestDetailItem(

                iconRes =
                    R.drawable.ic_weight,

                text =
                    request.weight
            )


            // ------------------------------------------------
            // FECHA
            // ------------------------------------------------

            RequestDetailItem(

                iconRes =
                    R.drawable.ic_calendar,

                text =
                    request.date
            )


            // ------------------------------------------------
            // HORA
            // ------------------------------------------------

            RequestDetailItem(

                iconRes =
                    R.drawable.ic_clock,

                text =
                    request.time
            )


            // ------------------------------------------------
            // RECOLECTOR
            // ------------------------------------------------

            RequestDetailItem(

                iconRes =
                    R.drawable
                        .icon_collection,

                text =
                    request.assignee
            )


            // ------------------------------------------------
            // EDITAR
            // ------------------------------------------------

            Box(

                modifier =
                    Modifier.fillMaxWidth(),

                contentAlignment =
                    Alignment.BottomEnd

            ) {


                Text(

                    text =
                        stringResource(
                            id =
                                R.string.edit_label
                        ) + " →",

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        actionColor,

                    modifier =
                        Modifier.clickable {

                            onEditClick()
                        }
                )
            }
        }
    }
}


// ----------------------------------------------------
// BADGE DE ESTADO
// ----------------------------------------------------

@Composable
private fun StatusBadge(

    status: RequestStatus

) {


    // ------------------------------------------------
    // COLOR SEGÚN ESTADO
    // ------------------------------------------------

    val backgroundColor =
        when (status) {

            RequestStatus.COMPLETED ->
                MainGreen

            RequestStatus.PENDING ->
                MainOrange

            RequestStatus.CANCELLED ->
                MainRed
        }


    // ------------------------------------------------
    // TEXTO SEGÚN ESTADO
    // ------------------------------------------------

    val text =
        when (status) {

            RequestStatus.COMPLETED ->

                stringResource(
                    id =
                        R.string
                            .status_completed
                )


            RequestStatus.PENDING ->

                stringResource(
                    id =
                        R.string
                            .status_pending
                )


            RequestStatus.CANCELLED ->

                stringResource(
                    id =
                        R.string
                            .status_cancelled
                )
        }


    Box(

        modifier = Modifier
            .background(

                color =
                    backgroundColor,

                shape =
                    RoundedCornerShape(
                        50.dp
                    )
            )
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            )

    ) {


        Text(

            text = text,

            color =
                Color.White,

            fontSize = 12.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// ----------------------------------------------------
// DETALLE SOLICITUD
// ----------------------------------------------------

@Composable
private fun RequestDetailItem(

    icon: ImageVector? = null,

    iconRes: Int? = null,

    text: String

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        Box(

            modifier =
                Modifier.size(20.dp),

            contentAlignment =
                Alignment.Center

        ) {


            if (icon != null) {

                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(18.dp),

                    tint =
                        Brown
                )

            } else if (
                iconRes != null
            ) {


                Icon(

                    painter =
                        painterResource(
                            id = iconRes
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(18.dp),

                    tint =
                        Brown
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(12.dp)
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


// ----------------------------------------------------
// BOTTOM BAR
// ----------------------------------------------------

@Composable
private fun ReBottleBottomBar(

    onHomeClick: () -> Unit,

    onProfileClick: () -> Unit

) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                BackgroundColor
            )
            .padding(
                top = 3.dp
            )

    ) {


        NavigationBar(

            modifier =
                Modifier.fillMaxWidth(),

            containerColor =
                Color.White,

            tonalElevation =
                0.dp

        ) {


            // ------------------------------------------------
            // INICIO
            // ------------------------------------------------

            NavigationBarItem(

                selected = false,

                onClick = {

                    onHomeClick()
                },

                icon = {

                    Icon(

                        imageVector =
                            Icons.Outlined.Home,

                        contentDescription =
                            stringResource(
                                id =
                                    R.string
                                        .tab_inicio
                            ),

                        modifier =
                            Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(

                        text =
                            stringResource(
                                id =
                                    R.string
                                        .tab_inicio
                            ),

                        fontSize =
                            12.sp
                    )
                },

                colors =
                    navigationItemColors()
            )


            // ------------------------------------------------
            // SOLICITUDES
            // ------------------------------------------------

            NavigationBarItem(

                selected = true,

                onClick = {
                    // Ya estamos aquí
                },

                icon = {

                    Icon(

                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .icon_collection
                            ),

                        contentDescription =
                            stringResource(
                                id =
                                    R.string
                                        .tab_solicitudes
                            ),

                        modifier =
                            Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(

                        text =
                            stringResource(
                                id =
                                    R.string
                                        .tab_solicitudes
                            ),

                        fontSize =
                            12.sp
                    )
                },

                colors =
                    navigationItemColors()
            )


            // ------------------------------------------------
            // RUTAS
            // ------------------------------------------------

            NavigationBarItem(

                selected = false,

                onClick = {
                    // Se conecta más adelante
                },

                icon = {

                    Icon(

                        painter =
                            painterResource(
                                id =
                                    R.drawable
                                        .icon_routes
                            ),

                        contentDescription =
                            stringResource(
                                id =
                                    R.string
                                        .tab_rutas
                            ),

                        modifier =
                            Modifier.size(42.dp)
                    )
                },

                label = {

                    Text(

                        text =
                            stringResource(
                                id =
                                    R.string
                                        .tab_rutas
                            ),

                        fontSize =
                            12.sp,

                        modifier =
                            Modifier.offset(
                                y = (-4).dp
                            )
                    )
                },

                colors =
                    navigationItemColors()
            )


            // ------------------------------------------------
            // PERFIL
            // ------------------------------------------------

            NavigationBarItem(

                selected = false,

                onClick = {

                    onProfileClick()
                },

                icon = {

                    Icon(

                        imageVector =
                            Icons.Outlined.Person,

                        contentDescription =
                            stringResource(
                                id =
                                    R.string
                                        .tab_perfil
                            ),

                        modifier =
                            Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(

                        text =
                            stringResource(
                                id =
                                    R.string
                                        .tab_perfil
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
}


// ----------------------------------------------------
// COLORES BOTTOM BAR
// ----------------------------------------------------

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


// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(
    showBackground = true
)
@Composable
fun ManageRequestsScreenPreview() {

    RebottleTheme {

        ManageRequestsScreen()
    }
}