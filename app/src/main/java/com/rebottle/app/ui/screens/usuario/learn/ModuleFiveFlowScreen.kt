package com.rebottle.app.ui.screens.usuario.learn

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R


// ============================================================
// COLORES
// ============================================================

private val M5Background = Color(0xFFFAF7EF)
private val M5Green = Color(0xFF668542)
private val M5DarkGreen = Color(0xFF3F542F)
private val M5Text = Color(0xFF74776E)
private val M5Inactive = Color(0xFFE5ECD9)
private val M5Wrong = Color(0xFFB84A3E)


// ============================================================
// ESTADOS DEL MÓDULO
// ============================================================

private enum class ModuleFiveStep {
    INTRO,
    LESSON_ONE,
    LESSON_TWO,
    LESSON_THREE,
    LESSON_FOUR,
    COMPLETE
}


// ============================================================
// FLUJO PRINCIPAL
// ============================================================

@Composable
fun ModuleFiveFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleFiveStep.INTRO)
    }


    when (currentStep) {

        ModuleFiveStep.INTRO -> {

            ModuleFiveIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    currentStep = ModuleFiveStep.LESSON_ONE
                }
            )
        }


        ModuleFiveStep.LESSON_ONE -> {

            ModuleFiveInformationScreen(
                lessonNumber = 1,
                title = "Pilas",
                description =
                    "Las pilas contienen sustancias\n" +
                            "peligrosas que pueden\n" +
                            "contaminar el suelo y el agua.",
                imageRes = R.drawable.m5_batteries,

                onBack = {
                    currentStep = ModuleFiveStep.INTRO
                },

                onNext = {
                    currentStep = ModuleFiveStep.LESSON_TWO
                }
            )
        }


        ModuleFiveStep.LESSON_TWO -> {

            ModuleFiveInformationScreen(
                lessonNumber = 2,
                title = "Tecnología",
                description =
                    "Celulares, cargadores y otros\n" +
                            "dispositivos electrónicos ya\n" +
                            "también son residuos que\n" +
                            "necesitan un manejo especial.",
                imageRes = R.drawable.m5_technology,

                onBack = {
                    currentStep = ModuleFiveStep.LESSON_ONE
                },

                onNext = {
                    currentStep = ModuleFiveStep.LESSON_THREE
                }
            )
        }


        ModuleFiveStep.LESSON_THREE -> {

            ModuleFiveInformationScreen(
                lessonNumber = 3,
                title = "Medicamentos",
                description =
                    "Los medicamentos vencidos o\n" +
                            "que ya no usas pueden ser\n" +
                            "peligrosos si se desechan mal.\n" +
                            "Deséchalos en puntos azules.",
                imageRes = R.drawable.m5_medicines,

                onBack = {
                    currentStep = ModuleFiveStep.LESSON_TWO
                },

                onNext = {
                    currentStep = ModuleFiveStep.LESSON_FOUR
                }
            )
        }


        ModuleFiveStep.LESSON_FOUR -> {

            ModuleFiveCollectionPointScreen(
                onBack = {
                    currentStep = ModuleFiveStep.LESSON_THREE
                },

                onNext = {
                    currentStep = ModuleFiveStep.COMPLETE
                }
            )
        }


        ModuleFiveStep.COMPLETE -> {

            ModuleFiveCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleFiveIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M5Background)
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
                text = "No todo va a la basura",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF272A24),
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text =
                    "Descubre qué residuos se\n" +
                            "pueden aprovechar o cuáles\n" +
                            "realmente deben ir a la basura.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M5Text,
                textAlign = TextAlign.Center
            )
        }


        M5PrimaryButton(
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
// LECCIONES 1, 2 Y 3
// ============================================================

@Composable
private fun ModuleFiveInformationScreen(
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
            .background(M5Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 20.dp,
                    bottom = 120.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M5LessonHeader(
                lessonNumber = lessonNumber,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M5DarkGreen,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M5Text,
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
                    .height(420.dp),
                contentScale = ContentScale.Fit
            )
        }


        M5PrimaryButton(
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
// PUNTO DE RECOLECCIÓN
// ============================================================

@Composable
private fun ModuleFiveCollectionPointScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    // 0 = Pilas
    // 1 = Tecnología
    // 2 = Medicamentos

    var selectedWaste by rememberSaveable {
        mutableStateOf<Int?>(null)
    }


    var batteriesCorrect by rememberSaveable {
        mutableStateOf(false)
    }


    var technologyCorrect by rememberSaveable {
        mutableStateOf(false)
    }


    var medicinesCorrect by rememberSaveable {
        mutableStateOf(false)
    }


    var lastCorrect by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }


    val allCorrect =
        batteriesCorrect &&
                technologyCorrect &&
                medicinesCorrect


    // ========================================================
    // COMPROBAR RESPUESTA
    // ========================================================

    fun checkAnswer(destination: Int) {

        val correct = when (selectedWaste) {

            // Pilas → Supermercados
            0 -> destination == 1

            // Tecnología → Puntos RAEE
            1 -> destination == 2

            // Medicamentos → Puntos azules
            2 -> destination == 0

            else -> false
        }


        lastCorrect = correct


        if (correct) {

            when (selectedWaste) {

                0 -> batteriesCorrect = true

                1 -> technologyCorrect = true

                2 -> medicinesCorrect = true
            }


            selectedWaste = null
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M5Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 20.dp,
                    bottom = 135.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =================================================
            // HEADER
            // =================================================

            M5LessonHeader(
                lessonNumber = 4,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // TÍTULO
            // =================================================

            Text(
                text = "Punto de recolección",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M5DarkGreen
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            Text(
                text =
                    "En Bogotá existen puntos\n" +
                            "especiales para llevar pilas,\n" +
                            "electrónicos y medicamentos.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M5Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Text(
                text = "¿Sabes cuáles son?",
                fontSize = 12.sp,
                color = M5Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(32.dp)
            )


            // =================================================
            // FILA 1
            // PILAS / PUNTOS AZULES
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                M5ChoiceCard(
                    text = if (batteriesCorrect) {
                        "Pilas ✓"
                    } else {
                        "Pilas"
                    },
                    selected = selectedWaste == 0,
                    completed = batteriesCorrect,
                    isDestination = false,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (!batteriesCorrect) {

                            selectedWaste = 0

                            lastCorrect = null
                        }
                    }
                )


                M5ChoiceCard(
                    text = "Puntos azules",
                    selected = false,
                    completed = medicinesCorrect,
                    isDestination = true,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (selectedWaste != null) {

                            checkAnswer(0)
                        }
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // FILA 2
            // TECNOLOGÍA / SUPERMERCADOS
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                M5ChoiceCard(
                    text = if (technologyCorrect) {
                        "Tecnología ✓"
                    } else {
                        "Tecnología"
                    },
                    selected = selectedWaste == 1,
                    completed = technologyCorrect,
                    isDestination = false,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (!technologyCorrect) {

                            selectedWaste = 1

                            lastCorrect = null
                        }
                    }
                )


                M5ChoiceCard(
                    text = "Supermercados",
                    selected = false,
                    completed = batteriesCorrect,
                    isDestination = true,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (selectedWaste != null) {

                            checkAnswer(1)
                        }
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // FILA 3
            // MEDICAMENTOS / PUNTOS RAEE
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                M5ChoiceCard(
                    text = if (medicinesCorrect) {
                        "Medicamentos ✓"
                    } else {
                        "Medicamentos"
                    },
                    selected = selectedWaste == 2,
                    completed = medicinesCorrect,
                    isDestination = false,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (!medicinesCorrect) {

                            selectedWaste = 2

                            lastCorrect = null
                        }
                    }
                )


                M5ChoiceCard(
                    text = "Puntos RAEE",
                    selected = false,
                    completed = technologyCorrect,
                    isDestination = true,
                    modifier = Modifier.weight(1f),

                    onClick = {

                        if (selectedWaste != null) {

                            checkAnswer(2)
                        }
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // =================================================
            // MENSAJE
            // =================================================

            when {

                allCorrect -> {

                    Text(
                        text = "¡Muy bien! Completaste todas las relaciones ✓",
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = M5Green,
                        textAlign = TextAlign.Center
                    )
                }


                lastCorrect == true -> {

                    Text(
                        text = "¡Correcto! ✓",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = M5Green
                    )
                }


                lastCorrect == false -> {

                    Text(
                        text = "Incorrecto. Intenta nuevamente",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = M5Wrong
                    )
                }


                selectedWaste != null -> {

                    Text(
                        text = "Ahora selecciona el punto de recolección",
                        fontSize = 12.sp,
                        color = M5Text,
                        textAlign = TextAlign.Center
                    )
                }


                else -> {

                    Text(
                        text = "Selecciona primero un residuo",
                        fontSize = 12.sp,
                        color = M5Text
                    )
                }
            }
        }


        // =====================================================
        // BOTÓN SIGUIENTE
        // COLORES SIN CAMBIAR
        // =====================================================

        M5PrimaryButton(
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


// ============================================================
// TARJETA DE OPCIÓN
// ============================================================

@Composable
private fun M5ChoiceCard(
    text: String,
    selected: Boolean,
    completed: Boolean,
    isDestination: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    // ========================================================
    // FONDO
    // ========================================================

    val backgroundColor = when {

        // Respuesta ya relacionada correctamente
        completed -> {
            Color(0xFFE8F1DE)
        }


        // Columna derecha
        isDestination -> {
            Color(0xFFF0F5E9)
        }


        // Columna izquierda
        else -> {
            Color.White
        }
    }


    // ========================================================
    // BORDE
    // ========================================================

    val borderColor = when {

        // Seleccionada actualmente
        selected -> {
            Color(0xFF718C4E)
        }


        // Columna izquierda
        !isDestination -> {
            Color(0xFF8BA268)
        }


        // Columna derecha
        else -> {
            Color(0xFFDDE8D1)
        }
    }


    val borderWidth = when {

        selected -> {
            1.4.dp
        }


        !isDestination -> {
            1.dp
        }


        else -> {
            0.5.dp
        }
    }


    // ========================================================
    // COLOR DEL TEXTO
    // ========================================================

    val textColor = when {

        completed -> {
            Color(0xFF5F7D43)
        }


        selected -> {
            Color(0xFF6A8052)
        }


        else -> {
            Color(0xFF8A8D84)
        }
    }


    Card(
        modifier = modifier
            .height(55.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(10.dp),

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),

        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                fontSize = 11.sp,
                color = textColor,
                fontWeight = if (
                    selected || completed
                ) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                textAlign = TextAlign.Center
            )
        }
    }
}


// ============================================================
// PANTALLA FINAL
// ============================================================

@Composable
private fun ModuleFiveCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M5Background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = 45.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "Felicidades estas un paso\n" +
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
                text = "+100 puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            M5PrimaryButton(
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
private fun M5LessonHeader(
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
                color = M5Text,
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
                                M5Green
                            } else {
                                M5Inactive
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
// NO CAMBIAR COLORES
// ============================================================

@Composable
private fun M5PrimaryButton(
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
            containerColor = M5Green,
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


// ============================================================
// PREVIEWS
// ============================================================


@Preview(
    name = "M5 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5IntroPreview() {

    ModuleFiveIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M5 - Lección 1 Pilas",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5LessonOnePreview() {

    ModuleFiveInformationScreen(
        lessonNumber = 1,
        title = "Pilas",
        description =
            "Las pilas contienen sustancias\n" +
                    "peligrosas que pueden\n" +
                    "contaminar el suelo y el agua.",
        imageRes = R.drawable.m5_batteries,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M5 - Lección 2 Tecnología",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5LessonTwoPreview() {

    ModuleFiveInformationScreen(
        lessonNumber = 2,
        title = "Tecnología",
        description =
            "Celulares, cargadores y otros\n" +
                    "dispositivos electrónicos ya\n" +
                    "también son residuos que\n" +
                    "necesitan un manejo especial.",
        imageRes = R.drawable.m5_technology,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M5 - Lección 3 Medicamentos",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5LessonThreePreview() {

    ModuleFiveInformationScreen(
        lessonNumber = 3,
        title = "Medicamentos",
        description =
            "Los medicamentos vencidos o\n" +
                    "que ya no usas pueden ser\n" +
                    "peligrosos si se desechan mal.\n" +
                    "Deséchalos en puntos azules.",
        imageRes = R.drawable.m5_medicines,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M5 - Lección 4 Punto de recolección",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5LessonFourPreview() {

    ModuleFiveCollectionPointScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M5 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M5CompletePreview() {

    ModuleFiveCompleteScreen(
        onFinish = {}
    )
}