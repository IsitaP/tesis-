package com.rebottle.app.ui.screens.usuario.learn

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt


private val M2Background = Color(0xFFFAF7EF)
private val M2Green = Color(0xFF668542)
private val M2DarkGreen = Color(0xFF3F542F)
private val M2Text = Color(0xFF777A70)
private val M2InactiveProgress = Color(0xFFE6EDDD)


// ============================================================
// ESTADOS DEL MÓDULO
// ============================================================

private enum class ModuleTwoStep {
    INTRO,
    LESSON_ONE,
    LESSON_TWO,
    LESSON_THREE,
    LESSON_FOUR,
    COMPLETE
}

private enum class WasteBin {
    WHITE,
    GREEN,
    BLACK
}

// ============================================================
// FLUJO PRINCIPAL
// ============================================================

@Composable
fun ModuleTwoFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit,
    onModuleCompleted: () -> Unit = {}
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleTwoStep.INTRO)
    }

    var trickyWasteIndex by rememberSaveable {
        mutableStateOf(0)
    }


    when (currentStep) {

        ModuleTwoStep.INTRO -> {

            ModuleTwoIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    currentStep = ModuleTwoStep.LESSON_ONE
                }
            )
        }


        ModuleTwoStep.LESSON_ONE -> {

            BagLessonScreen(
                lessonNumber = 1,
                title = "Bolsa blanca",
                description = "Aquí van los envases reciclables\ny materiales limpios.",
                imageRes = R.drawable.m2_white_bag,
                onBack = {
                    currentStep = ModuleTwoStep.INTRO
                },
                onNext = {
                    currentStep = ModuleTwoStep.LESSON_TWO
                }
            )
        }


        ModuleTwoStep.LESSON_TWO -> {

            BagLessonScreen(
                lessonNumber = 2,
                title = "Bolsa negra",
                description = "Aquí van los residuos que no se\npueden reciclar.",
                imageRes = R.drawable.m2_black_bag,
                onBack = {
                    currentStep = ModuleTwoStep.LESSON_ONE
                },
                onNext = {
                    currentStep = ModuleTwoStep.LESSON_THREE
                }
            )
        }


        ModuleTwoStep.LESSON_THREE -> {

            BagLessonScreen(
                lessonNumber = 3,
                title = "Bolsa verde",
                description = "Aquí van los residuos orgánicos\nque vuelven a la naturaleza.",
                imageRes = R.drawable.m2_green_bag,
                onBack = {
                    currentStep = ModuleTwoStep.LESSON_TWO
                },
                onNext = {
                    trickyWasteIndex = 0
                    currentStep = ModuleTwoStep.LESSON_FOUR
                }
            )
        }


        ModuleTwoStep.LESSON_FOUR -> {

            TrickyWasteScreen(
                currentItem = trickyWasteIndex,

                onBack = {
                    if (trickyWasteIndex > 0) {
                        trickyWasteIndex--
                    } else {
                        currentStep = ModuleTwoStep.LESSON_THREE
                    }
                },

                onNext = {

                    if (trickyWasteIndex < 2) {
                        trickyWasteIndex++
                    } else {
                        currentStep = ModuleTwoStep.COMPLETE
                    }
                }
            )
        }


        ModuleTwoStep.COMPLETE -> {


            // Suma los puntos en Firebase apenas se llega a la pantalla final

            LaunchedEffect(Unit) { onModuleCompleted() }


            ModuleTwoCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleTwoIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M2Background)
    ) {

        Text(
            text = "‹",
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(
                    start = 24.dp,
                    top = 25.dp
                )
                .clickable {
                    onBack()
                }
        )
        Image(
            painter = painterResource(id = R.drawable.m2_aprende_reciclar),
            contentDescription = "Ilustración de completado",
            modifier = Modifier
                .size(530.dp)
                .offset(y = 160.dp)
        )


        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = 44.dp,
                    start = 45.dp,
                    end = 45.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Aprende a separar\ncorrectamente",
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF272A24)
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Text(
                text = "Cada residuo tiene su lugar.\n" +
                        "Juntos hacemos un planeta más\n" +
                        "limpio!!",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M2Text
            )
        }


        M2PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 45.dp,
                    end = 45.dp,
                    bottom = 35.dp
                )
        )
    }
}


// ============================================================
// LECCIONES 1 - 3
// ============================================================

@Composable
private fun BagLessonScreen(
    lessonNumber: Int,
    title: String,
    description: String,
    imageRes: Int,
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M2Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 28.dp,
                    end = 28.dp,
                    top = 24.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M2LessonHeader(
                lessonNumber = lessonNumber,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            Text(
                text = title,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = M2DarkGreen,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            Text(
                text = description,
                fontSize = 17.sp,
                lineHeight = 17.sp,
                color = M2Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                contentScale = ContentScale.Fit
            )
        }


        M2PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 45.dp,
                    end = 45.dp,
                    bottom = 30.dp
                )
        )
    }
}


// ============================================================
// LECCIÓN 4
// ============================================================

@Composable
private fun TrickyWasteScreen(
    currentItem: Int,
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    // ========================================================
    // RESIDUO ACTUAL
    // ========================================================

    val itemImage = when (currentItem) {

        0 -> R.drawable.m2_tricky_bottle

        1 -> R.drawable.m2_tricky_tissues

        else -> R.drawable.m2_tricky_apple
    }


    // ========================================================
    // CANECA CORRECTA
    // ========================================================

    val correctBin = when (currentItem) {

        // Botella reciclable
        0 -> WasteBin.WHITE

        // Servilletas / papel contaminado
        1 -> WasteBin.BLACK

        // Manzana / residuo orgánico
        else -> WasteBin.GREEN
    }


    // ========================================================
    // ESTADO DEL ARRASTRE
    // ========================================================

    var dragOffset by remember(currentItem) {
        mutableStateOf(Offset.Zero)
    }

    var itemCoordinates by remember {
        mutableStateOf<LayoutCoordinates?>(null)
    }


    // ========================================================
    // POSICIONES DE LAS CANECAS
    // ========================================================

    var whiteBinBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var greenBinBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var blackBinBounds by remember {
        mutableStateOf<Rect?>(null)
    }


    // null = todavía no respondió
    // true = correcto
    // false = incorrecto

    var answerCorrect by remember(currentItem) {
        mutableStateOf<Boolean?>(null)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M2Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =================================================
            // HEADER
            // =================================================

            M2LessonHeader(
                lessonNumber = 4,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text = "Los residuos tramposos",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M2DarkGreen,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Text(
                text = "Algunos residuos pueden confundirse.\n" +
                        "Arrastra el residuo a la caneca correcta.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M2Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // OBJETO ARRASTRABLE
            // =================================================

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .zIndex(2f)
                    .offset {
                        IntOffset(
                            x = dragOffset.x.roundToInt(),
                            y = dragOffset.y.roundToInt()
                        )
                    }
                    .onGloballyPositioned { coordinates ->

                        itemCoordinates = coordinates
                    }
                    .pointerInput(currentItem) {

                        detectDragGestures(

                            onDragStart = {

                                // Borra mensaje anterior
                                answerCorrect = null
                            },

                            onDrag = { change, dragAmount ->

                                change.consume()

                                dragOffset += dragAmount
                            },

                            onDragCancel = {

                                dragOffset = Offset.Zero
                            },

                            onDragEnd = {

                                // Centro actual del objeto
                                val objectCenter =
                                    itemCoordinates
                                        ?.boundsInRoot()
                                        ?.center


                                if (objectCenter != null) {

                                    // Descubrimos sobre cuál caneca cayó
                                    val droppedBin = when {

                                        whiteBinBounds?.contains(
                                            objectCenter
                                        ) == true -> {
                                            WasteBin.WHITE
                                        }


                                        greenBinBounds?.contains(
                                            objectCenter
                                        ) == true -> {
                                            WasteBin.GREEN
                                        }


                                        blackBinBounds?.contains(
                                            objectCenter
                                        ) == true -> {
                                            WasteBin.BLACK
                                        }


                                        else -> null
                                    }


                                    // Comprobamos respuesta
                                    answerCorrect =
                                        droppedBin == correctBin
                                }


                                // Regresa el objeto a su posición inicial
                                dragOffset = Offset.Zero
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = itemImage
                    ),
                    contentDescription = "Residuo para clasificar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // CANECAS
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {

                // Imagen que ya tienes con las 3 canecas
                Image(
                    painter = painterResource(
                        id = R.drawable.m2_bins
                    ),
                    contentDescription = "Canecas de clasificación",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )


                // ---------------------------------------------
                // ZONAS INVISIBLES PARA DETECTAR DÓNDE SUELTA
                // ---------------------------------------------

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // CANECA BLANCA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                whiteBinBounds =
                                    it.boundsInRoot()
                            }
                    )


                    // CANECA VERDE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                greenBinBounds =
                                    it.boundsInRoot()
                            }
                    )


                    // CANECA NEGRA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                blackBinBounds =
                                    it.boundsInRoot()
                            }
                    )
                }
            }

        }
            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =================================================
            // MENSAJE CORRECTO / INCORRECTO
            // =================================================

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 95.dp),
                contentAlignment = Alignment.Center
            ) {

                when (answerCorrect) {

                    true -> {
                        Text(
                            text = "¡Correcto! ✓",
                            color = Color(0xFF5F7D43),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    false -> {
                        Text(
                            text = "Incorrecto. Intenta de nuevo",
                            color = Color(0xFFB34A3C),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    null -> {
                        Text(
                            text = "Arrastra el residuo a una caneca",
                            color = M2Text,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }


        // =====================================================
        // BOTÓN SIGUIENTE
        // =====================================================

        M2PrimaryButton(
            text = "Siguiente →",

            onClick = {

                if (answerCorrect == true) {
                    onNext()
                }
            },

            enabled = answerCorrect == true,

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 45.dp,
                    end = 45.dp,
                    bottom = 25.dp
                )
        )
    }
}

// ============================================================
// PANTALLA FINAL
// ============================================================

@Composable
private fun ModuleTwoCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M2Background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = 42.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.modulo2),
                contentDescription = "Ilustración modulo 2",
                modifier = Modifier
                    .size(320.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Felicidades estas un paso\n" +
                        "más en el mundo del\n" +
                        "reciclaje !!",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = Color(0xFF40423B),
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            Text(
                text = "+150 puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            M2PrimaryButton(
                text = "Finalizar",
                onClick = onFinish
            )
        }
    }
}


// ============================================================
// HEADER DE LAS LECCIONES
// ============================================================

@Composable
private fun M2LessonHeader(
    lessonNumber: Int,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "‹",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        onBack()
                    },
                fontSize = 30.sp,
                color = Color.Black
            )


            Text(
                text = "Lección $lessonNumber de 4",
                modifier = Modifier.align(
                    Alignment.Center
                ),
                fontSize = 12.sp,
                color = M2Text
            )
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            repeat(4) { index ->

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            color = if (index < lessonNumber) {
                                M2Green
                            } else {
                                M2InactiveProgress
                            },
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}


// ============================================================
// BOTÓN
// ============================================================

@Composable
private fun M2PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    Button(
        onClick = onClick,
        enabled=enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = M2Green
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp
        )
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}
// ============================================================
// PREVIEWS MÓDULO 2
// ============================================================

@Preview(
    name = "M2 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoIntroPreview() {

    ModuleTwoIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 1 Bolsa blanca",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonOnePreview() {

    BagLessonScreen(
        lessonNumber = 1,
        title = "Bolsa blanca",
        description = "Aquí van los envases reciclables\ny materiales limpios.",
        imageRes = R.drawable.m2_white_bag,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 2 Bolsa negra",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonTwoPreview() {

    BagLessonScreen(
        lessonNumber = 2,
        title = "Bolsa negra",
        description = "Aquí van los residuos que no se\npueden reciclar.",
        imageRes = R.drawable.m2_black_bag,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 3 Bolsa verde",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonThreePreview() {

    BagLessonScreen(
        lessonNumber = 3,
        title = "Bolsa verde",
        description = "Aquí van los residuos orgánicos\nque vuelven a la naturaleza.",
        imageRes = R.drawable.m2_green_bag,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 4 Botella",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonFourBottlePreview() {

    TrickyWasteScreen(
        currentItem = 0,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 4 Servilletas",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonFourTissuesPreview() {

    TrickyWasteScreen(
        currentItem = 1,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Lección 4 Manzana",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoLessonFourApplePreview() {

    TrickyWasteScreen(
        currentItem = 2,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M2 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleTwoCompletePreview() {

    ModuleTwoCompleteScreen(
        onFinish = {}
    )
}
