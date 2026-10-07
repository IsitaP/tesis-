package com.rebottle.app.ui.screens.usuario.gamificacion

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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

private val RewardBackground = Color(0xFFFBF8F2)

private val RewardGreen = Color(0xFF617F43)

private val RewardLightGreen = Color(0xFFEDF5E5)

private val RewardBottomGreen = Color(0xFFA2BC82)

private val RewardBorder = Color(0xFFD6E3CA)

private val RewardText = Color(0xFF34382F)

private val RewardSecondary = Color(0xFF7F887A)


// ============================================================
// PASOS DEL FLOW
// ============================================================

private enum class RecompensaStep {
    RECOMPENSAS,
    PERSONALIZAR,
    FINAL
}


// ============================================================
// CATEGORÍAS
// ============================================================

private enum class CategoriaQuis {
    ROPA,
    ACCESORIOS
}


// ============================================================
// MODELO ITEM
// ============================================================

private data class ItemQuis(
    val id: String,
    val nombre: String,
    val precio: Int,
    @DrawableRes val imagen: Int
)


// ============================================================
// MODELO MEDALLA
// ============================================================

private data class MedallaQuis(
    @DrawableRes val imagen: Int,
    val desbloqueada: Boolean
)


// ============================================================
// ACCESORIOS
// ============================================================

private val accesoriosQuis = listOf(

    ItemQuis(
        id = "guantes",
        nombre = "Guantes",
        precio = 0,
        imagen = R.drawable.acc_guantes
    ),

    ItemQuis(
        id = "gafas_fruta",
        nombre = "Gafas",
        precio = 25,
        imagen = R.drawable.acc_gafas_fruta
    ),

    ItemQuis(
        id = "gafas_estrellas",
        nombre = "Gafas de estrellas",
        precio = 40,
        imagen = R.drawable.acc_gafas_estrellas
    ),

    ItemQuis(
        id = "bufanda",
        nombre = "Bufanda",
        precio = 50,
        imagen = R.drawable.acc_bufanda
    ),

    ItemQuis(
        id = "sombrero",
        nombre = "Sombrero",
        precio = 100,
        imagen = R.drawable.acc_sombrero
    ),

    ItemQuis(
        id = "casco",
        nombre = "Casco",
        precio = 100,
        imagen = R.drawable.acc_casco
    ),

    ItemQuis(
        id = "pato",
        nombre = "Pato",
        precio = 120,
        imagen = R.drawable.acc_pato
    )
)


// ============================================================
// ROPA
// ============================================================

private val ropaQuis = listOf(

    ItemQuis(
        id = "tenis",
        nombre = "Tenis",
        precio = 110,
        imagen = R.drawable.ropa_tenis
    ),

    ItemQuis(
        id = "camisa",
        nombre = "Camisa",
        precio = 120,
        imagen = R.drawable.ropa_camisa
    ),

    ItemQuis(
        id = "chaleco",
        nombre = "Chaleco",
        precio = 120,
        imagen = R.drawable.ropa_chaleco
    )
)


// ============================================================
// MEDALLAS
// ============================================================

private val medallasQuis = listOf(

    MedallaQuis(
        imagen = R.drawable.medalla_1,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_2,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_3,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_4,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_5,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_6,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_7,
        desbloqueada = true
    ),

    MedallaQuis(
        imagen = R.drawable.medalla_8,
        desbloqueada = false
    )
)


// ============================================================
// FLOW PRINCIPAL
// ============================================================

@Composable
fun RecompensasFlowScreen(
    controller: NavController
) {

    var step by remember {
        mutableStateOf(
            RecompensaStep.RECOMPENSAS
        )
    }


    var itemSeleccionado by remember {
        mutableStateOf<ItemQuis?>(null)
    }


    when (step) {

        RecompensaStep.RECOMPENSAS -> {

            RecompensasScreen(
                puntos = 520,
                nivel = 6,

                onBack = {
                    controller.popBackStack()
                },

                onRedimirClick = {
                    step =
                        RecompensaStep.PERSONALIZAR
                }
            )
        }


        RecompensaStep.PERSONALIZAR -> {

            PersonalizarQuisScreen(
                puntos = 520,

                onBack = {
                    step =
                        RecompensaStep.RECOMPENSAS
                },

                onItemClick = { item ->

                    itemSeleccionado = item

                    step =
                        RecompensaStep.FINAL
                }
            )
        }


        RecompensaStep.FINAL -> {

            RecompensaFinalScreen(
                itemSeleccionado =
                    itemSeleccionado,

                onFinalizar = {

                    itemSeleccionado = null

                    step =
                        RecompensaStep.RECOMPENSAS
                }
            )
        }
    }
}


// ============================================================
// PANTALLA 1
// RECOMPENSAS
// ============================================================

@Composable
private fun RecompensasScreen(
    puntos: Int,
    nivel: Int,
    onBack: () -> Unit,
    onRedimirClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RewardBackground
            )
            .statusBarsPadding()
            .padding(
                horizontal = 20.dp
            )
    ) {

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        // ====================================================
        // HEADER
        // ====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,

                contentDescription =
                    "Volver",

                tint =
                    RewardText,

                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .size(30.dp)
                    .clickable {
                        onBack()
                    }
            )


            Text(
                text = "Recompensas",

                fontSize = 20.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    RewardText,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        // ====================================================
        // QUIS
        // ====================================================

        Image(
            painter = painterResource(
                id = R.drawable.recompensas_quis
            ),

            contentDescription =
                "Quis",

            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp),

            contentScale =
                ContentScale.Fit
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // CARD MEDALLAS
        // ====================================================

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(28.dp),

            border =
                BorderStroke(
                    width = 1.dp,
                    color = RewardBorder
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        RewardLightGreen
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Medallas",

                    fontSize = 17.sp,

                    color =
                        RewardText
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                MedallasGrid()


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                BarraPuntos(
                    puntos = puntos,
                    nivel = nivel,
                    onRedimirClick =
                        onRedimirClick
                )
            }
        }
    }
}


// ============================================================
// MEDALLAS
// ============================================================

@Composable
private fun MedallasGrid() {

    medallasQuis
        .chunked(3)
        .forEach { fila ->

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                fila.forEach { medalla ->

                    Image(
                        painter =
                            painterResource(
                                id =
                                    medalla.imagen
                            ),

                        contentDescription =
                            "Medalla",

                        modifier = Modifier
                            .size(110.dp)
                            .alpha(
                                if (
                                    medalla.desbloqueada
                                ) {
                                    1f
                                } else {
                                    0.35f
                                }
                            ),

                        contentScale =
                            ContentScale.Fit
                    )
                }


                if (fila.size < 3) {

                    repeat(
                        3 - fila.size
                    ) {

                        Spacer(
                            modifier =
                                Modifier.size(
                                    72.dp
                                )
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )
        }
}


// ============================================================
// BARRA NIVEL + PUNTOS + REDIMIR
// ============================================================

@Composable
private fun BarraPuntos(
    puntos: Int,
    nivel: Int,
    onRedimirClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color =
                    RewardBottomGreen,

                shape =
                    RoundedCornerShape(
                        17.dp
                    )
            )
            .padding(
                horizontal = 12.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ====================================================
        // NIVEL
        // ====================================================

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "◎",
                color = Color.White,
                fontSize = 20.sp
            )

            Text(
                text = "NIVEL",
                color =
                    Color.White.copy(
                        alpha = 0.85f
                    ),
                fontSize = 7.sp
            )

            Text(
                text =
                    nivel.toString(),

                color =
                    Color.White,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        // DIVISOR

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(48.dp)
                .background(
                    Color.White.copy(
                        alpha = 0.35f
                    )
                )
        )


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        // ====================================================
        // ESTRELLA
        // ====================================================

        Text(
            text = "☆",

            color =
                Color.White,

            fontSize =
                35.sp
        )


        Spacer(
            modifier =
                Modifier.width(7.dp)
        )


        // ====================================================
        // PUNTOS
        // ====================================================

        Column {

            Text(
                text =
                    puntos.toString(),

                color =
                    Color.White,

                fontSize =
                    26.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "Puntos",

                color =
                    Color.White.copy(
                        alpha = 0.9f
                    ),

                fontSize =
                    10.sp
            )
        }


        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // ====================================================
        // REDIMIR
        // ====================================================

        Text(
            text =
                "Redimir →",

            color =
                Color.White,

            fontSize =
                10.sp,

            modifier =
                Modifier
                    .clickable {
                        onRedimirClick()
                    }
                    .padding(
                        vertical = 12.dp
                    )
        )
    }
}


// ============================================================
// PANTALLA 2
// PERSONALIZA A QUIS
// ============================================================

@Composable
private fun PersonalizarQuisScreen(
    puntos: Int,
    onBack: () -> Unit,
    onItemClick: (ItemQuis) -> Unit
) {

    var categoria by remember {
        mutableStateOf(
            CategoriaQuis.ACCESORIOS
        )
    }


    val itemsActuales =

        if (
            categoria ==
            CategoriaQuis.ACCESORIOS
        ) {

            accesoriosQuis

        } else {

            ropaQuis
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RewardBackground
            )
            .statusBarsPadding()
            .padding(
                horizontal = 16.dp
            )
    ) {

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        // ====================================================
        // HEADER
        // ====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
        ) {

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,

                contentDescription =
                    "Volver",

                tint =
                    RewardText,

                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .size(30.dp)
                    .clickable {
                        onBack()
                    }
            )


            Column(
                modifier =
                    Modifier.align(
                        Alignment.Center
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "Personaliza a",

                    color =
                        RewardText,

                    fontSize =
                        16.sp
                )

                Text(
                    text =
                        "Quis",

                    color =
                        RewardText,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            // PUNTOS ARRIBA

            Box(
                modifier = Modifier
                    .align(
                        Alignment.TopEnd
                    )
                    .background(
                        color =
                            RewardBottomGreen,

                        shape =
                            RoundedCornerShape(
                                5.dp
                            )
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {

                Text(
                    text =
                        "☆ $puntos",

                    color =
                        Color.White,

                    fontSize =
                        10.sp
                )
            }
        }


        // ====================================================
        // TEXTO
        // ====================================================

        Text(
            text =
                "Usa tus puntos para darle\nun estilo único a Quis",

            modifier =
                Modifier.fillMaxWidth(),

            textAlign =
                TextAlign.Center,

            color =
                RewardSecondary,

            fontSize =
                10.sp,

            lineHeight =
                13.sp
        )


        Spacer(
            modifier =
                Modifier.height(5.dp)
        )


        // ====================================================
        // QUIS
        // ====================================================

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.recompensas_quis
                ),

            contentDescription =
                "Quis",

            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),

            contentScale =
                ContentScale.Fit
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // ROPA / ACCESORIOS
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            CategoriaButton(
                text = "Ropa",

                selected =
                    categoria ==
                            CategoriaQuis.ROPA,

                modifier =
                    Modifier.weight(1f),

                onClick = {

                    categoria =
                        CategoriaQuis.ROPA
                }
            )


            CategoriaButton(
                text =
                    "Accesorios",

                selected =
                    categoria ==
                            CategoriaQuis.ACCESORIOS,

                modifier =
                    Modifier.weight(1f),

                onClick = {

                    categoria =
                        CategoriaQuis.ACCESORIOS
                }
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // GRID DE ITEMS
        // ====================================================

        LazyVerticalGrid(
            columns =
                GridCells.Fixed(2),

            modifier =
                Modifier.fillMaxSize(),

            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            items(
                items =
                    itemsActuales,

                key = {
                    it.id
                }
            ) { item ->

                ItemQuisCard(
                    item =
                        item,

                    puntosDisponibles =
                        puntos,

                    onClick = {
                        onItemClick(
                            item
                        )
                    }
                )
            }
        }
    }
}


// ============================================================
// BOTÓN CATEGORÍA
// ============================================================

@Composable
private fun CategoriaButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color =
                    if (selected) {

                        RewardGreen

                    } else {

                        Color.White
                    },

                shape =
                    RoundedCornerShape(
                        5.dp
                    )
            )
            .clickable {
                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                text,

            color =
                if (selected) {

                    Color.White

                } else {

                    RewardGreen
                },

            fontSize =
                12.sp
        )
    }
}


// ============================================================
// CARD DEL ITEM
// ============================================================

@Composable
private fun ItemQuisCard(
    item: ItemQuis,
    puntosDisponibles: Int,
    onClick: () -> Unit
) {

    val puedeComprar =
        puntosDisponibles >=
                item.precio


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .clickable(
                enabled =
                    puedeComprar
            ) {
                onClick()
            },

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        shape =
            RoundedCornerShape(
                4.dp
            ),

        border =
            BorderStroke(
                width = 1.dp,
                color = RewardBorder
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Image(
                painter =
                    painterResource(
                        id =
                            item.imagen
                    ),

                contentDescription =
                    item.nombre,

                modifier =
                    Modifier.size(
                        95.dp
                    ),

                contentScale =
                    ContentScale.Fit
            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )


            // PRECIO

            Box(
                modifier =
                    Modifier
                        .background(
                            color =
                                if (
                                    puedeComprar
                                ) {

                                    RewardBottomGreen

                                } else {

                                    Color.LightGray
                                },

                            shape =
                                RoundedCornerShape(
                                    4.dp
                                )
                        )
                        .padding(
                            horizontal =
                                10.dp,

                            vertical =
                                4.dp
                        )
            ) {

                Text(
                    text =
                        "☆ ${item.precio}",

                    color =
                        Color.White,

                    fontSize =
                        10.sp
                )
            }
        }
    }
}


// ============================================================
// PANTALLA 3
// LISTO
// ============================================================

@Composable
private fun RecompensaFinalScreen(
    itemSeleccionado: ItemQuis?,
    onFinalizar: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RewardBackground
            )
            .statusBarsPadding()
            .padding(
                horizontal = 30.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(65.dp)
        )


        Text(
            text =
                "Listo !",

            color =
                RewardText,

            fontSize =
                21.sp,

            fontWeight =
                FontWeight.Medium
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(
            text =
                "Tu avatar se ha actualizado",

            color =
                RewardSecondary,

            fontSize =
                10.sp
        )


        Spacer(
            modifier =
                Modifier.height(65.dp)
        )


        // ====================================================
        // QUIS FINAL
        // ====================================================

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.recompensas_quis_final
                ),

            contentDescription =
                "Quis actualizado",

            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),

            contentScale =
                ContentScale.Fit
        )


        Spacer(
            modifier =
                Modifier.weight(1f)
        )


        // ====================================================
        // FINALIZAR
        // ====================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color =
                            RewardGreen,

                        shape =
                            RoundedCornerShape(
                                10.dp
                            )
                    )
                    .clickable {

                        onFinalizar()
                    },

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "Finalizar",

                color =
                    Color.White,

                fontSize =
                    13.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(55.dp)
        )
    }
}


// ============================================================
// PREVIEW - RECOMPENSAS
// ============================================================

@Preview(
    name = "1 - Recompensas",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun RecompensasPreview() {

    RebottleTheme {

        RecompensasScreen(
            puntos = 520,
            nivel = 6,
            onBack = {},
            onRedimirClick = {}
        )
    }
}


// ============================================================
// PREVIEW - ACCESORIOS
// ============================================================

@Preview(
    name = "2 - Accesorios",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun AccesoriosPreview() {

    RebottleTheme {

        PersonalizarQuisScreen(
            puntos = 520,
            onBack = {},
            onItemClick = {}
        )
    }
}


// ============================================================
// PREVIEW - FINAL
// ============================================================

@Preview(
    name = "3 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun RecompensaFinalPreview() {

    RebottleTheme {

        RecompensaFinalScreen(
            itemSeleccionado =
                accesoriosQuis.first(),

            onFinalizar = {}
        )
    }
}


// ============================================================
// PREVIEW - FLOW COMPLETO
// ============================================================

@Preview(
    name = "4 - Flow completo",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun RecompensasFlowPreview() {

    RebottleTheme {

        RecompensasFlowScreen(
            controller =
                rememberNavController()
        )
    }
}