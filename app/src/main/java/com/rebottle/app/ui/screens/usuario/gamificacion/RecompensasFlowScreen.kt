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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebottle.app.model.PuntosModulos
import com.rebottle.app.model.PuntosViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextDecoration
import kotlin.math.cos
import kotlin.math.sin


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
    val moduloId: String
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

// Cada medalla se desbloquea al completar su módulo (M1..M7)
private val medallasQuis = listOf(
    MedallaQuis(imagen = R.drawable.medalla_1, moduloId = PuntosModulos.M1),
    MedallaQuis(imagen = R.drawable.medalla_2, moduloId = PuntosModulos.M2),
    MedallaQuis(imagen = R.drawable.medalla_3, moduloId = PuntosModulos.M3),
    MedallaQuis(imagen = R.drawable.medalla_4, moduloId = PuntosModulos.M4),
    MedallaQuis(imagen = R.drawable.medalla_5, moduloId = PuntosModulos.M5),
    MedallaQuis(imagen = R.drawable.medalla_6, moduloId = PuntosModulos.M6),
    MedallaQuis(imagen = R.drawable.medalla_7, moduloId = PuntosModulos.M7)
)


// ============================================================
// FLOW PRINCIPAL
// ============================================================

@Composable
fun RecompensasFlowScreen(
    controller: NavController,
    puntosViewModel: PuntosViewModel = viewModel()
) {

    // Puntos reales desde Firebase (progresoAprendizaje/{uid})
    val puntosState by puntosViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        puntosViewModel.escucharPuntos()
    }

    RecompensasFlowContent(
        controller = controller,
        puntos = puntosState.puntosTotales.toInt(),
        nivel = 6,
        modulosCompletados =
            puntosState.modulosCompletados
                .map { it.moduloId }
                .toSet()
    )
}


// Contenido separado del ViewModel para que los Previews
// funcionen sin Firebase
@Composable
private fun RecompensasFlowContent(
    controller: NavController,
    puntos: Int,
    nivel: Int,
    modulosCompletados: Set<String> = emptySet()
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
                puntos = puntos,
                nivel = nivel,
                modulosCompletados = modulosCompletados,

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
                puntos = puntos,

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
    modulosCompletados: Set<String> = emptySet(),
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


                MedallasGrid(
                    modulosCompletados =
                        modulosCompletados
                )


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



@Composable
private fun MedallasGrid(
    modulosCompletados: Set<String>
) {

    medallasQuis
        .chunked(3)
        .forEach { fila ->

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
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
                            .weight(1f)
                            .aspectRatio(1f)
                            .alpha(
                                // Bloqueada (50%) hasta completar su módulo
                                if (
                                    medalla.moduloId in
                                    modulosCompletados
                                ) {
                                    1f
                                } else {
                                    0.5f
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
                                Modifier.weight(1f)
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
            .height(88.dp)
            .background(
                color = RewardBottomGreen,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(
                start = 18.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 10.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ====================================================
        // NIVEL
        // ====================================================

        Column(
            modifier = Modifier.width(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconoGlobo(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "NIVEL",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 12.sp
            )

            Text(
                text = nivel.toString(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
        }


        Spacer(modifier = Modifier.width(14.dp))


        // DIVISOR (de arriba a abajo)

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.3f))
        )


        Spacer(modifier = Modifier.width(16.dp))


        // ====================================================
        // ESTRELLA
        // ====================================================

        IconoEstrella(
            color = Color.White,
            modifier = Modifier.size(42.dp)
        )


        Spacer(modifier = Modifier.width(12.dp))


        // ====================================================
        // PUNTOS
        // ====================================================

        Column {

            Text(
                text = puntos.toString(),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Text(
                text = "Puntos",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp,
                lineHeight = 16.sp,
                modifier = Modifier.offset(y = (-2).dp)
            )
        }


        Spacer(modifier = Modifier.weight(1f))


        // ====================================================
        // REDIMIR (abajo a la derecha, subrayado)
        // ====================================================

        Row(
            modifier = Modifier
                .align(Alignment.Bottom)
                .clickable { onRedimirClick() }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Redimir",
                color = Color.White,
                fontSize = 13.sp,
                textDecoration = TextDecoration.Underline
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "→",
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}


// ============================================================
// ÍCONOS DIBUJADOS (no necesitan material-icons-extended)
// ============================================================

@Composable
private fun IconoEstrella(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val stroke = size.minDimension * 0.07f
        val cx = size.width / 2f
        val cy = size.height / 2f + size.height * 0.04f
        val rExt = size.minDimension / 2f - stroke
        val rInt = rExt * 0.45f

        val path = Path()

        for (i in 0 until 10) {
            val r = if (i % 2 == 0) rExt else rInt
            val ang = Math.toRadians(-90.0 + i * 36.0)
            val x = cx + (r * cos(ang)).toFloat()
            val y = cy + (r * sin(ang)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = stroke,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
    }
}


@Composable
private fun IconoGlobo(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val stroke = size.minDimension * 0.08f
        val r = size.minDimension / 2f - stroke
        val c = Offset(size.width / 2f, size.height / 2f)
        val estilo = Stroke(width = stroke)

        // Círculo exterior
        drawCircle(color = color, radius = r, center = c, style = estilo)

        // Meridiano (óvalo vertical)
        drawOval(
            color = color,
            topLeft = Offset(c.x - r * 0.45f, c.y - r),
            size = Size(r * 0.9f, r * 2f),
            style = estilo
        )

        // Línea vertical central
        drawLine(color, Offset(c.x, c.y - r), Offset(c.x, c.y + r), stroke)

        // Ecuador y paralelos
        drawLine(color, Offset(c.x - r, c.y), Offset(c.x + r, c.y), stroke)

        val dy = r * 0.5f
        val dx = r * 0.86f
        drawLine(color, Offset(c.x - dx, c.y - dy), Offset(c.x + dx, c.y - dy), stroke)
        drawLine(color, Offset(c.x - dx, c.y + dy), Offset(c.x + dx, c.y + dy), stroke)
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
            modulosCompletados = setOf("M1", "M2", "M3"),
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

        RecompensasFlowContent(
            controller =
                rememberNavController(),
            puntos = 520,
            nivel = 6
        )
    }
}