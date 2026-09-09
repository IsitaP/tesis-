package com.rebottle.app.ui.screens.fundacion.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme


// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF536D45)
private val LightGreen = Color(0xFFF2F7ED)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)
private val BorderGreen = Color(0xFFD5E3CD)
private val BrownIcon = Color(0xFFA55E37)


// ----------------------------------------------------
// EDIT COLLECTOR SCREEN
// ----------------------------------------------------

@Composable
fun EditCollectorScreen(

    modifier: Modifier = Modifier,

    collectorId: String = "001",

    collectorName: String = "Gabriela Cruz Pino",

    zone: String = "Teusaquillo",

    schedule: String = "7:00 am - 4:00 pm",

    status: String = "Disponible",

    onBackClick: () -> Unit = {},

    onAcceptClick: () -> Unit = {},

    onCancelClick: () -> Unit = {},

    onDeleteClick: () -> Unit = {}

) {

    val scrollState = rememberScrollState()


    Scaffold(

        modifier = modifier.fillMaxSize(),

        containerColor = BackgroundColor,

        topBar = {

            EditCollectorHeader(
                title = collectorName,
                onBackClick = onBackClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {


            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {


                // Espacio entre header y card
                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                // ------------------------------------------------
                // CARD RECOLECTOR
                // ------------------------------------------------

                CollectorInfoCard(
                    id = collectorId,
                    zone = zone,
                    schedule = schedule,
                    status = status
                )


                Spacer(
                    modifier = Modifier.height(32.dp)
                )


                // ------------------------------------------------
                // BOTONES
                // ------------------------------------------------

                ActionButtons(

                    onAcceptClick = onAcceptClick,

                    onCancelClick = onCancelClick,

                    onDeleteClick = onDeleteClick
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }


            // ------------------------------------------------
            // IMAGEN INFERIOR
            // ------------------------------------------------
            //
            // SIN OFFSET.
            // Equivale a y = 0.
            // ------------------------------------------------

            Image(

                painter = painterResource(
                    id = R.drawable.fundacion_edit
                ),

                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),

                contentScale =
                    ContentScale.FillWidth
            )
        }
    }
}


// ----------------------------------------------------
// HEADER
// ----------------------------------------------------

@Composable
private fun EditCollectorHeader(

    title: String,

    onBackClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()

            // ESTO evita que el título quede
            // encima de la barra de estado.
            .statusBarsPadding()

            .padding(
                start = 12.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        // ------------------------------------------------
        // BOTÓN ATRÁS
        // ------------------------------------------------
        //
        // IconButton tiene una zona de toque
        // mucho más grande que hacer clickable
        // directamente sobre un icono de 24.dp.
        // ------------------------------------------------

        IconButton(

            onClick = {

                onBackClick()
            },

            modifier =
                Modifier.size(40.dp)

        ) {

            Icon(

                imageVector =
                    Icons.AutoMirrored
                        .Outlined
                        .ArrowBack,

                contentDescription =
                    "Volver",

                tint = MainText,

                modifier =
                    Modifier.size(24.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(15.dp)
        )


        Text(

            text = title,

            fontSize = 22.sp,

            fontWeight =
                FontWeight.Bold,

            color = MainText
        )
    }
}


// ----------------------------------------------------
// CARD INFORMACIÓN RECOLECTOR
// ----------------------------------------------------

@Composable
private fun CollectorInfoCard(

    id: String,

    zone: String,

    schedule: String,

    status: String

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor =
                    Color.Black.copy(
                        alpha = 0.1f
                    )
            ),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = LightGreen
            ),

        border =
            BorderStroke(
                width = 1.dp,
                color =
                    BorderGreen.copy(
                        alpha = 0.5f
                    )
            )

    ) {

        Column(

            modifier = Modifier
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 24.dp
                )
                .fillMaxWidth(),

            horizontalAlignment =
                Alignment.Start

        ) {


            // ------------------------------------------------
            // ID
            // ------------------------------------------------

            Text(

                text = stringResource(
                    id = R.string.collector_id_short,
                    id
                ),

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Medium,

                color = MainText,

                modifier =
                    Modifier.padding(
                        start = 24.dp
                    )
            )


            // Antes estaba en 8.dp.
            // Lo aumentamos para bajar la foto.
            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ------------------------------------------------
            // FOTO RECOLECTOR
            // ------------------------------------------------

            Box(

                modifier =
                    Modifier.fillMaxWidth(),

                contentAlignment =
                    Alignment.Center

            ) {

                Image(

                    painter = painterResource(
                        id = R.drawable.fundacion_profile
                    ),

                    contentDescription =
                        "Foto recolector",

                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),

                    contentScale =
                        ContentScale.Crop
                )
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // ------------------------------------------------
            // ZONA
            // ------------------------------------------------

            DetailItem(

                iconRes =
                    R.drawable.ic_id_card,

                text =
                    stringResource(
                        id =
                            R.string.assigned_zone_label,
                        zone
                    )
            )


            // ------------------------------------------------
            // HORARIO
            // ------------------------------------------------

            DetailItem(

                iconRes =
                    R.drawable.ic_clock,

                text =
                    stringResource(
                        id =
                            R.string.work_schedule_label,
                        schedule
                    )
            )


            // ------------------------------------------------
            // ESTADO
            // ------------------------------------------------

            DetailItem(

                iconRes =
                    R.drawable.ic_check_circle,

                text =
                    stringResource(
                        id =
                            R.string.status_label,
                        status
                    )
            )
        }
    }
}


// ----------------------------------------------------
// DETALLE
// ----------------------------------------------------

@Composable
private fun DetailItem(

    iconRes: Int,

    text: String

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Icon(

            painter =
                painterResource(
                    id = iconRes
                ),

            contentDescription = null,

            modifier =
                Modifier.size(24.dp),

            tint = BrownIcon
        )


        Spacer(
            modifier =
                Modifier.width(16.dp)
        )


        Text(

            text = text,

            fontSize = 16.sp,

            color = SecondaryText,

            fontWeight =
                FontWeight.Normal
        )
    }
}


// ----------------------------------------------------
// BOTONES
// ----------------------------------------------------

@Composable
private fun ActionButtons(

    onAcceptClick: () -> Unit,

    onCancelClick: () -> Unit,

    onDeleteClick: () -> Unit

) {

    Column(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {


        // ------------------------------------------------
        // ACEPTAR CAMBIOS
        // ------------------------------------------------

        Button(

            onClick = {

                onAcceptClick()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 4.dp,
                    shape =
                        RoundedCornerShape(20.dp)
                ),

            shape =
                RoundedCornerShape(20.dp),

            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MainGreen
                )

        ) {

            Text(

                text =
                    stringResource(
                        id =
                            R.string.accept_changes
                    ),

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Medium,

                color = Color.White
            )
        }


        // ------------------------------------------------
        // CANCELAR CAMBIOS
        // ------------------------------------------------

        OutlinedButton(

            onClick = {

                onCancelClick()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 4.dp,
                    shape =
                        RoundedCornerShape(20.dp)
                ),

            shape =
                RoundedCornerShape(20.dp),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(

                        containerColor =
                            Color.White,

                        contentColor =
                            MainGreen
                    ),

            border =
                BorderStroke(
                    width = 1.dp,
                    color = BorderGreen
                )

        ) {

            Text(

                text =
                    stringResource(
                        id =
                            R.string.cancel_changes
                    ),

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Medium
            )
        }


        // ------------------------------------------------
        // ELIMINAR
        // ------------------------------------------------

        TextButton(

            onClick = {

                onDeleteClick()
            }

        ) {

            Text(

                text =
                    stringResource(
                        id =
                            R.string.delete_collector
                    ),

                fontSize = 16.sp,

                fontWeight =
                    FontWeight.Medium,

                color = MainGreen,

                textDecoration =
                    TextDecoration.Underline
            )
        }
    }
}


// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(showBackground = true)
@Composable
fun EditCollectorScreenPreview() {

    RebottleTheme {

        EditCollectorScreen()
    }
}