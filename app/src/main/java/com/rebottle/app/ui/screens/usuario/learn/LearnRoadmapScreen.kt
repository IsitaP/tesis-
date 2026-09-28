package com.rebottle.app.ui.screens.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R
import androidx.compose.foundation.layout.aspectRatio


private val ModuleGreen = Color(0xFF536D3C)
private val ShadowGreen = Color(0xFF78835D)


@Composable
fun LearnRoadmapScreen(
    onBack: () -> Unit,
    onM1Click: () -> Unit,
    onM2Click: () -> Unit,
    onM3Click: () -> Unit,
    onM4Click: () -> Unit,
    onM5Click: () -> Unit,
    onM6Click: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(640f/1600f)

        ) {

            // ==========================================
            // FONDO DEL CAMINO
            // ==========================================

            Image(
                painter = painterResource(
                    id = R.drawable.learn_roadmap
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )


            // ==========================================
            // BOTÓN DEVOLVERSE
            // ==========================================

            Text(
                text = "‹",
                fontSize = 42.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 18.dp,
                        top = 16.dp
                    )
                    .clickable {
                        onBack()
                    }
            )


            // ==========================================
            // TÍTULO
            // ==========================================

            Text(
                text = "Aprende a reciclar",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF20251D),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 27.dp)
            )


            // ==========================================
            // MÓDULOS
            // ==========================================

            ModuleButton(
                text = "M1",
                x = 20.dp,
                y = 115.dp,
                onClick={
                    onM1Click()
                }
            )

            ModuleButton(
                text = "M2",
                x = 265.dp,
                y = 230.dp,
                onClick={
                    onM2Click()
                }
            )

            ModuleButton(
                text = "M3",
                x = 5.dp,
                y = 350.dp,
                onClick={
                    onM3Click()
                }
            )

            ModuleButton(
                text = "M4",
                x = 260.dp,
                y = 455.dp,
                onClick={
                    onM4Click()
                }
            )

            ModuleButton(
                text = "M5",
                x = 20.dp,
                y = 560.dp,
                onClick={
                    onM5Click()
                }
            )

            ModuleButton(
                text = "M6",
                x = 265.dp,
                y = 665.dp,
                onClick={
                    onM6Click()
                }
            )

            ModuleButton(
                text = "M7",
                x = 5.dp,
                y = 780.dp
            )

        }
    }
}


@Composable
private fun ModuleButton(
    text: String,
    x: androidx.compose.ui.unit.Dp,
    y: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .offset(
                x = x,
                y = y
            )
    ) {

        // SOMBRA VERDE
        Box(
            modifier = Modifier
                .offset(
                    x = 3.dp,
                    y = 8.dp
                )
                .size(85.dp)
                .background(
                    color = ShadowGreen,
                    shape = CircleShape
                )
        )


        // CÍRCULO BLANCO
        Card(
            modifier = Modifier
                .size(85.dp)
                .clickable {
                    onClick()
                },
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = text,
                    color = ModuleGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}