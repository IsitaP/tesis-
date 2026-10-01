package com.rebottle.app.ui.screens.usuario.gamificacion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme


// ============================================================
// COLORES
// ============================================================

private val Background = Color(0xFFFAF7EF)
private val DarkGreen = Color(0xFF394C31)
private val MainGreen = Color(0xFF5F7D42)
private val SecondaryText = Color(0xFF7F887A)

private val PlasticGreen = Color(0xFF8BC516)
private val GlassGreen = Color(0xFF2F711F)
private val PaperGreen = Color(0xFF3E5F2B)
private val MetalGreen = Color(0xFF70836A)
private val OtherGreen = Color(0xFFD5E87C)


// ============================================================
// MODELO UI
// ============================================================

private data class ContributionItem(
    val title: String,
    val kilograms: String,
    val imageRes: Int,
    val color: Color
)


// ============================================================
// PANTALLA
// ============================================================

@Composable
fun MisAportesScreen(
    controller: NavController
) {

    var selectedRange by remember {
        mutableStateOf("1 mes")
    }

    val aportes = listOf(

        ContributionItem(
            title = "Plástico",
            kilograms = "3 Kg",
            imageRes = R.drawable.aportes_plastico,
            color = PlasticGreen
        ),

        ContributionItem(
            title = "Vidrio",
            kilograms = "3,2 Kg",
            imageRes = R.drawable.aportes_vidrio,
            color = GlassGreen
        ),

        ContributionItem(
            title = "Papel",
            kilograms = "6 Kg",
            imageRes = R.drawable.aportes_papel,
            color = PaperGreen
        ),

        ContributionItem(
            title = "Metal",
            kilograms = "3,6 Kg",
            imageRes = R.drawable.aportes_metal,
            color = MetalGreen
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 22.dp,
                end = 22.dp,
                bottom = 30.dp
            )
    ) {

        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ====================================================
        // HEADER
        // ====================================================

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(
                onClick = {
                    controller.popBackStack()
                },
                modifier = Modifier.align(
                    Alignment.CenterStart
                )
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = DarkGreen,
                    modifier = Modifier.size(26.dp)
                )
            }


            Text(
                text = "Mis aportes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGreen,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ====================================================
        // FILTRO DE TIEMPO
        // ====================================================

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            PeriodSelector(
                selected = selectedRange,
                onSelected = {
                    selectedRange = it
                }
            )
        }


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        // ====================================================
        // GRÁFICA
        // ====================================================

        ContributionChart()


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // ====================================================
        // LISTA DE MATERIALES
        // ====================================================

        aportes.forEach { aporte ->

            ContributionCard(
                item = aporte
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}


// ============================================================
// SELECTOR
// ============================================================

@Composable
private fun PeriodSelector(
    selected: String,
    onSelected: (String) -> Unit
) {

    val options = listOf(
        "1 semana",
        "1 mes",
        "Todo"
    )


    Card(
        modifier = Modifier
            .width(210.dp)
            .height(38.dp),

        shape = RoundedCornerShape(50.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            options.forEach { option ->

                val isSelected =
                    selected == option


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .background(
                            color = if (isSelected) {
                                Color(0xFFDDEED3)
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable {
                            onSelected(option)
                        },

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = option,
                        fontSize = 9.sp,
                        color = if (isSelected) {
                            MainGreen
                        } else {
                            SecondaryText
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// GRÁFICA
// ============================================================

@Composable
private fun ContributionChart() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.size(225.dp)
        ) {

            val strokeWidth =
                38.dp.toPx()

            val values =
                listOf(
                    15.5f,
                    40f,
                    15f,
                    9.5f,
                    11f
                )

            val colors =
                listOf(
                    GlassGreen,
                    PaperGreen,
                    MetalGreen,
                    OtherGreen,
                    PlasticGreen
                )


            val total =
                values.sum()


            var startAngle =
                -90f


            values.forEachIndexed { index, value ->

                val fullSweep =
                    (value / total) * 360f


                val gap =
                    3f


                drawArc(
                    color = colors[index],

                    startAngle = startAngle,

                    sweepAngle =
                        fullSweep - gap,

                    useCenter = false,

                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt
                    )
                )


                startAngle += fullSweep
            }
        }


        // ====================================================
        // CENTRO
        // ====================================================

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "15 Kg",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGreen
            )

            Text(
                text = "reciclados",
                fontSize = 14.sp,
                color = DarkGreen
            )
        }


        // ====================================================
        // ETIQUETAS
        // ====================================================

        Text(
            text = "15,5%\nVidrio",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = GlassGreen,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 22.dp,
                    top = 22.dp
                )
        )


        Text(
            text = "40%\nPapel",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = PaperGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 18.dp,
                    top = 45.dp
                )
        )


        Text(
            text = "15%\nMetal",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = MetalGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 20.dp,
                    bottom = 25.dp
                )
        )


        Text(
            text = "11%\nPlástico",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = PlasticGreen,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 15.dp,
                    bottom = 50.dp
                )
        )


        Text(
            text = "9,5%\nOtros",
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = OtherGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}


// ============================================================
// CARD MATERIAL
// ============================================================

@Composable
private fun ContributionCard(
    item: ContributionItem
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),

        shape = RoundedCornerShape(4.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // =================================================
            // ICONO TEMPORAL
            // =================================================

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(
                        color = Color(0xFFF5F6F3),
                        shape = RoundedCornerShape(6.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = item.imageRes
                    ),
                    contentDescription = item.title,
                    modifier = Modifier.size(42.dp),
                    contentScale = ContentScale.Fit
                )
            }


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            // =================================================
            // NOMBRE Y PESO
            // =================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )


                Text(
                    text = item.kilograms,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }


            // =================================================
            // PUNTO
            // =================================================

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = item.color,
                        shape = RoundedCornerShape(50.dp)
                    )
            )
        }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun MisAportesScreenPreview() {

    RebottleTheme {

        MisAportesScreen(
            controller = rememberNavController()
        )
    }
}