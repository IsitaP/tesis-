package com.rebottle.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ----------------------------------------------------
// COLORES DE REBOTTLE
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF9F3)
private val MainGreen = Color(0xFF536D45)
private val DarkGreen = Color(0xFF394C31)
private val LightGreen = Color(0xFFF2F7ED)
private val BorderGreen = Color(0xFFD5E3CD)
private val Brown = Color(0xFFA55E37)
private val MainText = Color(0xFF3D433A)
private val SecondaryText = Color(0xFF7F887A)


// ----------------------------------------------------
// PANTALLA PRINCIPAL
// ----------------------------------------------------

@Composable
fun HomeUserScreen() {

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
                    start = 22.dp,
                    end = 22.dp,
                    top = 25.dp,
                    bottom = 25.dp
                )
        ) {

            Header()

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "¿Qué desea hacer hoy?",
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    onClick = {
                        // Sin funcionalidad por ahora
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

            Spacer(modifier = Modifier.height(12.dp))

            SectionTitle("Mis Solicitudes")

            Spacer(modifier = Modifier.height(9.dp))

            CollectionCard()

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Aprende a reciclar")

            Spacer(modifier = Modifier.height(9.dp))

            LearnCard()

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Mi impacto")

            Spacer(modifier = Modifier.height(9.dp))

            ImpactCard()
        }
    }
}


// ----------------------------------------------------
// ENCABEZADO
// ----------------------------------------------------

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


// ----------------------------------------------------
// TÍTULOS DE SECCIÓN
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
// TARJETA DE SOLICITUD
// ----------------------------------------------------

@Composable
private fun CollectionCard() {

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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Recolección programada",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Estado: Programada",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Fecha: Agosto 24",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Hora: 10:00 AM",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Ver más detalles  →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            IllustrationPlaceholder(
                symbol = "♻"
            )
        }
    }
}


// ----------------------------------------------------
// TARJETA APRENDE A RECICLAR
// ----------------------------------------------------

@Composable
private fun LearnCard() {

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

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Aprende a separar tus residuos por material y mantén limpios los envases antes de entregarlos.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Aprender más  →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IllustrationPlaceholder(
                symbol = "R"
            )
        }
    }
}


// ----------------------------------------------------
// TARJETA IMPACTO
// ----------------------------------------------------

@Composable
private fun ImpactCard() {

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {

            Text(
                text = "Tu aporte al medio ambiente",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "12 recolecciones realizadas",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "48 kg de materiales reciclados",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "1.250 puntos acumulados",
                fontSize = 12.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Saber más  →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MainGreen
            )
        }
    }
}


// ----------------------------------------------------
// PLACEHOLDER PARA LAS ILUSTRACIONES DE FIGMA
// ----------------------------------------------------

@Composable
private fun IllustrationPlaceholder(
    symbol: String
) {

    Box(
        modifier = Modifier
            .size(90.dp)
            .background(
                color = Color(0xFFE3EEDC),
                shape = RoundedCornerShape(15.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = symbol,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = MainGreen
        )
    }
}


// ----------------------------------------------------
// BARRA DE NAVEGACIÓN INFERIOR
// ----------------------------------------------------

@Composable
private fun ReBottleBottomBar() {

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                BottomIcon(
                    symbol = "⌂",
                    selected = true
                )
            },
            label = {
                Text(
                    text = "Inicio",
                    fontSize = 10.sp
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                BottomIcon(
                    symbol = "♻",
                    selected = false
                )
            },
            label = {
                Text(
                    text = "Recolecciones",
                    fontSize = 10.sp
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                BottomIcon(
                    symbol = "★",
                    selected = false
                )
            },
            label = {
                Text(
                    text = "Recompensas",
                    fontSize = 10.sp
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                BottomIcon(
                    symbol = "●",
                    selected = false
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    fontSize = 10.sp
                )
            }
        )
    }
}


@Composable
private fun BottomIcon(
    symbol: String,
    selected: Boolean
) {

    Text(
        text = symbol,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        color = if (selected) {
            MainGreen
        } else {
            Color(0xFF888E85)
        }
    )
}