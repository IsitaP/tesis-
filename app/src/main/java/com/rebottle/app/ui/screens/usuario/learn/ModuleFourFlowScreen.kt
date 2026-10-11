package com.rebottle.app.ui.screens.usuario.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme
import kotlin.math.roundToInt


private val M4Background = Color(0xFFFAF7EF)
private val M4Green = Color(0xFF668542)
private val M4DarkGreen = Color(0xFF3F542F)
private val M4Text = Color(0xFF74776E)
private val M4Inactive = Color(0xFFE5ECD9)
private val M4Wrong = Color(0xFFB84A3E)


// ============================================================
// ESTADOS
// ============================================================

private enum class ModuleFourStep {
    INTRO,
    LESSON_ONE,
    LESSON_TWO,
    LESSON_THREE,
    LESSON_FOUR,
    COMPLETE
}

private enum class M4Bin {
    WHITE,
    GREEN,
    BLACK
}


// ============================================================
// FLUJO PRINCIPAL
// ============================================================

@Composable
fun ModuleFourFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit,
    onModuleCompleted: () -> Unit = {}
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleFourStep.INTRO)
    }

    var dragItem by rememberSaveable {
        mutableStateOf(0)
    }

    when (currentStep) {

        ModuleFourStep.INTRO -> {

            ModuleFourIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    dragItem = 0
                    currentStep = ModuleFourStep.LESSON_ONE
                }
            )
        }

        ModuleFourStep.LESSON_ONE -> {

            ModuleFourDragScreen(
                currentItem = dragItem,
                onBack = {
                    if (dragItem > 0) {
                        dragItem--
                    } else {
                        currentStep = ModuleFourStep.INTRO
                    }
                },
                onNext = {
                    if (dragItem == 0) {
                        dragItem = 1
                    } else {
                        currentStep = ModuleFourStep.LESSON_TWO
                    }
                }
            )
        }

        ModuleFourStep.LESSON_TWO -> {

            ModuleFourDetectErrorScreen(
                onBack = {
                    currentStep = ModuleFourStep.LESSON_ONE
                    dragItem = 1
                },
                onNext = {
                    currentStep = ModuleFourStep.LESSON_THREE
                }
            )
        }

        ModuleFourStep.LESSON_THREE -> {

            ModuleFourSituationScreen(
                onBack = {
                    currentStep = ModuleFourStep.LESSON_TWO
                },
                onNext = {
                    currentStep = ModuleFourStep.LESSON_FOUR
                }
            )
        }

        ModuleFourStep.LESSON_FOUR -> {

            ModuleFourMythScreen(
                onBack = {
                    currentStep = ModuleFourStep.LESSON_THREE
                },
                onNext = {
                    currentStep = ModuleFourStep.COMPLETE
                }
            )
        }

        ModuleFourStep.COMPLETE -> {

            // Suma los puntos en Firebase apenas se llega a la pantalla final
            LaunchedEffect(Unit) { onModuleCompleted() }


            ModuleFourCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleFourIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Text(
            text = "‹",
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 24.dp, top = 25.dp)
                .clickable {
                    onBack()
                }
        )
        Image(
            painter = painterResource(id = R.drawable.m4_reto),
            contentDescription = "Ilustración de completado",
            modifier = Modifier
                .size(530.dp)
                .offset(y = 160.dp)
        )


        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = 50.dp,
                    start = 42.dp,
                    end = 42.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Reto Sostenible",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF272A24)
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Demuestra que sabes separar\n" +
                        "residuos en casa y tomar\n" +
                        "decisiones sostenibles.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M4Text
            )
        }

        M4PrimaryButton(
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
// LECCIÓN 1
// ARRASTRA Y RECICLA
// ============================================================

@Composable
private fun ModuleFourDragScreen(
    currentItem: Int,
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    val itemImage = if (currentItem == 0) {
        R.drawable.m4_can
    } else {
        R.drawable.m4_box
    }

    // Tanto la lata como el cartón van a bolsa blanca
    val correctBin = M4Bin.WHITE

    var dragOffset by remember(currentItem) {
        mutableStateOf(Offset.Zero)
    }

    var itemCoordinates by remember {
        mutableStateOf<LayoutCoordinates?>(null)
    }

    var whiteBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var greenBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var blackBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var answerCorrect by remember(currentItem) {
        mutableStateOf<Boolean?>(null)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 150.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M4LessonHeader(
                lessonNumber = 1,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = "Arrastra y recicla",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M4DarkGreen
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = "Arrastra cada residuo a la bolsa\n" +
                        "correcta según su tipo.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M4Text
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // OBJETO ARRASTRABLE
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .zIndex(2f)
                    .offset {
                        IntOffset(
                            x = dragOffset.x.roundToInt(),
                            y = dragOffset.y.roundToInt()
                        )
                    }
                    .onGloballyPositioned {
                        itemCoordinates = it
                    }
                    .pointerInput(currentItem) {

                        detectDragGestures(

                            onDragStart = {
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

                                val center =
                                    itemCoordinates
                                        ?.boundsInRoot()
                                        ?.center

                                if (center != null) {

                                    val droppedBin = when {

                                        whiteBounds?.contains(center) == true ->
                                            M4Bin.WHITE

                                        greenBounds?.contains(center) == true ->
                                            M4Bin.GREEN

                                        blackBounds?.contains(center) == true ->
                                            M4Bin.BLACK

                                        else -> null
                                    }

                                    answerCorrect =
                                        droppedBin == correctBin
                                }

                                dragOffset = Offset.Zero
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(itemImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // CANECAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.m4_bins
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {
                                whiteBounds = it.boundsInRoot()
                            }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {
                                greenBounds = it.boundsInRoot()
                            }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {
                                blackBounds = it.boundsInRoot()
                            }
                    )
                }
            }
        }


        // MENSAJE
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 95.dp)
        ) {

            when (answerCorrect) {

                true -> Text(
                    text = "¡Correcto! ✓",
                    color = M4Green,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                false -> Text(
                    text = "Incorrecto. Intenta nuevamente",
                    color = M4Wrong,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                null -> Text(
                    text = "Arrastra el residuo a una caneca",
                    color = M4Text,
                    fontSize = 12.sp
                )
            }
        }


        M4PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
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
// LECCIÓN 2
// DETECTA EL ERROR
// ============================================================
@Composable
private fun ModuleFourDetectErrorScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var whiteBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var greenBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var blackBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    var canCorrect by remember {
        mutableStateOf(false)
    }

    var pizzaCorrect by remember {
        mutableStateOf(false)
    }

    var bottleCorrect by remember {
        mutableStateOf(false)
    }

    var lastAnswerCorrect by remember {
        mutableStateOf<Boolean?>(null)
    }

    val allCorrect =
        canCorrect &&
                pizzaCorrect &&
                bottleCorrect


    // ---------------------------------------------------------
    // Detecta en cuál caneca fue soltado un residuo
    // ---------------------------------------------------------

    fun detectBin(center: Offset): M4Bin? {

        return when {

            whiteBounds?.contains(center) == true ->
                M4Bin.WHITE

            greenBounds?.contains(center) == true ->
                M4Bin.GREEN

            blackBounds?.contains(center) == true ->
                M4Bin.BLACK

            else -> null
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 150.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M4LessonHeader(
                lessonNumber = 2,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Detecta el error",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M4DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Observa con atención e\n" +
                        "identifica qué elementos están\n" +
                        "mal ubicados.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M4Text
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =================================================
            // RESIDUOS ARRASTRABLES
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // LATA
                M4DraggableWaste(
                    imageRes = R.drawable.m4_can2,
                    correctBin = M4Bin.WHITE,
                    alreadyCorrect = canCorrect,
                    detectBin = { center ->
                        detectBin(center)
                    },
                    onResult = { correct ->

                        lastAnswerCorrect = correct

                        if (correct) {
                            canCorrect = true
                        }
                    }
                )


                // CAJA DE PIZZA
                M4DraggableWaste(
                    imageRes = R.drawable.m4_pizza,
                    correctBin = M4Bin.BLACK,
                    alreadyCorrect = pizzaCorrect,
                    detectBin = { center ->
                        detectBin(center)
                    },
                    onResult = { correct ->

                        lastAnswerCorrect = correct

                        if (correct) {
                            pizzaCorrect = true
                        }
                    }
                )


                // BOTELLA
                M4DraggableWaste(
                    imageRes = R.drawable.m4_bottle,
                    correctBin = M4Bin.WHITE,
                    alreadyCorrect = bottleCorrect,
                    detectBin = { center ->
                        detectBin(center)
                    },
                    onResult = { correct ->

                        lastAnswerCorrect = correct

                        if (correct) {
                            bottleCorrect = true
                        }
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // =================================================
            // CANECAS
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.m4_bins
                    ),
                    contentDescription = "Canecas",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )


                // Zonas invisibles encima de cada caneca
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // BLANCA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                whiteBounds =
                                    it.boundsInRoot()
                            }
                    )


                    // VERDE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                greenBounds =
                                    it.boundsInRoot()
                            }
                    )


                    // NEGRA
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .onGloballyPositioned {

                                blackBounds =
                                    it.boundsInRoot()
                            }
                    )
                }
            }
        }


        // =====================================================
        // MENSAJE
        // =====================================================

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 95.dp),
            contentAlignment = Alignment.Center
        ) {

            when {

                allCorrect -> {

                    Text(
                        text = "¡Muy bien! Todos están correctos ✓",
                        color = M4Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }


                lastAnswerCorrect == true -> {

                    Text(
                        text = "¡Correcto! ✓",
                        color = M4Green,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


                lastAnswerCorrect == false -> {

                    Text(
                        text = "Incorrecto. Intenta de nuevo",
                        color = M4Wrong,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }


                else -> {

                    Text(
                        text = "Ubica cada residuo en su caneca",
                        color = M4Text,
                        fontSize = 12.sp
                    )
                }
            }
        }


        // =====================================================
        // BOTÓN
        // =====================================================

        M4PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            enabled = allCorrect,
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

@Composable
private fun M4DraggableWaste(
    imageRes: Int,
    correctBin: M4Bin,
    alreadyCorrect: Boolean,
    detectBin: (Offset) -> M4Bin?,
    onResult: (Boolean) -> Unit
) {

    var dragOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    var coordinates by remember {
        mutableStateOf<LayoutCoordinates?>(null)
    }


    Box(
        modifier = Modifier
            .size(90.dp)
            .zIndex(5f)
            .offset {

                IntOffset(
                    x = dragOffset.x.roundToInt(),
                    y = dragOffset.y.roundToInt()
                )
            }
            .onGloballyPositioned {

                coordinates = it
            }
            .pointerInput(
                imageRes,
                alreadyCorrect
            ) {

                if (!alreadyCorrect) {

                    detectDragGestures(

                        onDrag = { change, dragAmount ->

                            change.consume()

                            dragOffset += dragAmount
                        },

                        onDragCancel = {

                            dragOffset = Offset.Zero
                        },

                        onDragEnd = {

                            val center =
                                coordinates
                                    ?.boundsInRoot()
                                    ?.center

                            if (center != null) {

                                val droppedBin =
                                    detectBin(center)

                                val correct =
                                    droppedBin == correctBin

                                onResult(correct)
                            }

                            // Regresa a su posición inicial
                            dragOffset = Offset.Zero
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(
                id = imageRes
            ),
            contentDescription = null,
            modifier = Modifier.size(
                if (alreadyCorrect) {
                    72.dp
                } else {
                    82.dp
                }
            ),
            contentScale = ContentScale.Fit
        )


        // Si ya fue ubicado correctamente
        if (alreadyCorrect) {

            Text(
                text = "✓",
                color = M4Green,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(
                    Alignment.TopEnd
                )
            )
        }
    }
}


// ============================================================
// LECCIÓN 3
// SITUACIÓN
// ============================================================

@Composable
private fun ModuleFourSituationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var selectedAnswer by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    // En este ejemplo la correcta es la opción 0
    val isCorrect = selectedAnswer == 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 20.dp,
                    bottom = 130.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M4LessonHeader(
                lessonNumber = 3,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Situación",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M4DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Lee la situación y elige la mejor\n" +
                        "acción para cuidar el entorno.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M4Text
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Text(
                    text = "Acabas de terminar una botella\n" +
                            "de bebida y quieres reciclarla\n" +
                            "correctamente. ¿Qué deberías\n" +
                            "hacer?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = M4Text
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            M4SituationOption(
                text = "Vaciarla, enjuagarla si es necesario, dejarla seca y separarla en la bolsa blanca.",
                selected = selectedAnswer == 0,
                onClick = {
                    selectedAnswer = 0
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            M4SituationOption(
                text = "Botarla directamente en la bolsa blanca aunque todavía tenga líquido.",
                selected = selectedAnswer == 1,
                onClick = {
                    selectedAnswer = 1
                }
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            if (selectedAnswer != null) {

                Text(
                    text = if (isCorrect) {
                        "¡Muy bien! ✓"
                    } else {
                        "Esa no es la mejor opción"
                    },
                    color = if (isCorrect) {
                        M4Green
                    } else {
                        M4Wrong
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }

        M4PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            enabled = isCorrect,
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
// LECCIÓN 4
// MITO O REALIDAD
// ============================================================

@Composable
private fun ModuleFourMythScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var answer by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }

    // “Todos los plásticos se pueden reciclar”
    // es falso → Mito
    val isCorrect = answer == false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 20.dp,
                    bottom = 130.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M4LessonHeader(
                lessonNumber = 4,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = "Mito o realidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M4DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Decide si la afirmación es un\n" +
                        "mito o una realidad según lo\n" +
                        "aprendido.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = M4Text
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Image(
                painter = painterResource(
                    R.drawable.m4_myth
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                M4AnswerCard(
                    text = "Mito",
                    selected = answer == false,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        answer = false
                    }
                )

                M4AnswerCard(
                    text = "Verdad",
                    selected = answer == true,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        answer = true
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (answer != null) {

                Text(
                    text = if (isCorrect) {
                        "¡Correcto! ✓"
                    } else {
                        "Incorrecto. Es un mito"
                    },
                    color = if (isCorrect) {
                        M4Green
                    } else {
                        M4Wrong
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }

        M4PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            enabled = isCorrect,
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
// FINAL
// ============================================================

@Composable
private fun ModuleFourCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M4Background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.modulo4),
                contentDescription = "Ilustración de completado",
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
                textAlign = TextAlign.Center,
                color = Color(0xFF40423B)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "+200 puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            M4PrimaryButton(
                text = "Finalizar",
                onClick = onFinish
            )
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun M4LessonHeader(
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
                fontSize = 30.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        onBack()
                    }
            )

            Text(
                text = "Lección $lessonNumber de 4",
                fontSize = 12.sp,
                color = M4Text,
                modifier = Modifier.align(
                    Alignment.Center
                )
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
                                M4Green
                            } else {
                                M4Inactive
                            },
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}


// ============================================================
// OPCIÓN PEQUEÑA
// ============================================================

@Composable
private fun M4SmallChoice(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFFEAF1DF)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 9.dp
            ),
            color = M4DarkGreen,
            fontSize = 10.sp
        )
    }
}


// ============================================================
// OPCIÓN SITUACIÓN
// ============================================================

@Composable
private fun M4SituationOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFFEDF4E5)
            } else {
                Color.White
            }
        ),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 11.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            color = M4Text
        )
    }
}


// ============================================================
// MITO / VERDAD
// ============================================================

@Composable
private fun M4AnswerCard(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(65.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                Color(0xFFE7F0DC)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                color = M4Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// ============================================================
// BOTÓN
// ============================================================

@Composable
private fun M4PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = M4Green,
            disabledContainerColor = Color(0xFFB7C5A7),
            disabledContentColor = Color.White
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

@Preview(
    name = "M4 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4IntroPreview() {
    ModuleFourIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Lección 1 Lata",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4LessonOneCanPreview() {
    ModuleFourDragScreen(
        currentItem = 0,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Lección 1 Caja",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4LessonOneBoxPreview() {
    ModuleFourDragScreen(
        currentItem = 1,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Lección 2",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4LessonTwoPreview() {
    ModuleFourDetectErrorScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Lección 3",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4LessonThreePreview() {
    ModuleFourSituationScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Lección 4",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4LessonFourPreview() {
    ModuleFourMythScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M4 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M4CompletePreview() {
    ModuleFourCompleteScreen(
        onFinish = {}
    )
}

@Preview(
    name = "M4 - Flow",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleFourFlowPreview() {
    RebottleTheme {
        ModuleFourFlowScreen(
            onBackToRoadmap = {},
            onFinish = {}
        )
    }
}
