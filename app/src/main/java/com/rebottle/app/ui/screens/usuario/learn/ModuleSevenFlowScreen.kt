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
import androidx.compose.foundation.border
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R


private val M7Background = Color(0xFFFAF7EF)
private val M7Green = Color(0xFF668542)
private val M7DarkGreen = Color(0xFF3F542F)
private val M7Text = Color(0xFF74776E)
private val M7Inactive = Color(0xFFE5ECD9)
private val M7Selected = Color(0xFFEDF4E5)


// ============================================================
// ESTADOS
// ============================================================

private enum class ModuleSevenStep {
    INTRO,
    LESSON_ONE,
    LESSON_TWO,
    COMPLETE
}


// ============================================================
// FLUJO PRINCIPAL
// ============================================================

@Composable
fun ModuleSevenFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleSevenStep.INTRO)
    }


    when (currentStep) {

        ModuleSevenStep.INTRO -> {

            ModuleSevenIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    currentStep = ModuleSevenStep.LESSON_ONE
                }
            )
        }


        ModuleSevenStep.LESSON_ONE -> {

            ModuleSevenSpecialWasteScreen(
                onBack = {
                    currentStep = ModuleSevenStep.INTRO
                },
                onNext = {
                    currentStep = ModuleSevenStep.LESSON_TWO
                }
            )
        }


        ModuleSevenStep.LESSON_TWO -> {

            ModuleSevenDecisionScreen(
                onBack = {
                    currentStep = ModuleSevenStep.LESSON_ONE
                },
                onNext = {
                    currentStep = ModuleSevenStep.COMPLETE
                }
            )
        }


        ModuleSevenStep.COMPLETE -> {

            ModuleSevenCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleSevenIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M7Background)
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
                text = "DESAFÍO: MAESTRO DEL\nRECICLAJE",
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF272A24)
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            Text(
                text =
                    "Demuestra todo lo que\n" +
                            "aprendiste y conviértete en\n" +
                            "maestro del reciclaje.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M7Text,
                textAlign = TextAlign.Center
            )
        }


        M7PrimaryButton(
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
// RESIDUOS ESPECIALES
// ============================================================

// ============================================================
// LECCIÓN 1
// RESIDUOS ESPECIALES - INTERACTIVA
// ============================================================

@Composable
private fun ModuleSevenSpecialWasteScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    // --------------------------------------------------------
    // ESTADO DE CADA RESIDUO
    // --------------------------------------------------------

    var batterySelected by rememberSaveable {
        mutableStateOf(false)
    }

    var earphonesSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var boxSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var cableSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var medicinesSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var cutlerySelected by rememberSaveable {
        mutableStateOf(false)
    }


    // --------------------------------------------------------
    // RESPUESTA CORRECTA
    // --------------------------------------------------------

    val allCorrect =
        batterySelected &&
                earphonesSelected &&
                cableSelected &&
                medicinesSelected &&
                cutlerySelected &&
                !boxSelected



    val hasWrongSelection =
        boxSelected || cutlerySelected


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M7Background)
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

            // =================================================
            // HEADER
            // =================================================

            M7LessonHeader(
                lessonNumber = 1,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // TÍTULO
            // =================================================

            Text(
                text = "Residuos especiales",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M7DarkGreen
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            Text(
                text = "Identifica cuáles residuos\n" +
                        "necesitan un manejo especial.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M7Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =================================================
            // FILA 1
            // PILA / AUDÍFONOS
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_battery,
                    description = "Pila",
                    selected = batterySelected,
                    wrong = false,
                    onClick = {
                        batterySelected = !batterySelected
                    }
                )


                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_earphones,
                    description = "Audífonos",
                    selected = earphonesSelected,
                    wrong = false,
                    onClick = {
                        earphonesSelected = !earphonesSelected
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // FILA 2
            // CAJA / CABLE
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_box,
                    description = "Caja",
                    selected = boxSelected,
                    wrong = boxSelected,
                    onClick = {
                        boxSelected = !boxSelected
                    }
                )


                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_cable,
                    description = "Cable",
                    selected = cableSelected,
                    wrong = false,
                    onClick = {
                        cableSelected = !cableSelected
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // FILA 3
            // MEDICAMENTOS / CUBIERTOS
            // =================================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_medicines,
                    description = "Medicamentos",
                    selected = medicinesSelected,
                    wrong = false,
                    onClick = {
                        medicinesSelected = !medicinesSelected
                    }
                )


                M7SelectableWasteImage(
                    imageRes = R.drawable.m7_cutlery,
                    description = "Cubiertos",
                    selected = cutlerySelected,
                    wrong = false,
                    onClick = {
                        cutlerySelected = !cutlerySelected
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // =================================================
            // MENSAJE
            // =================================================

            when {

                allCorrect -> {

                    Text(
                        text = "¡Muy bien! Identificaste todos ✓",
                        color = M7Green,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }


                hasWrongSelection -> {

                    Text(
                        text = "Hay un residuo que no necesita manejo especial",
                        color = Color(0xFFB84A3E),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }


                else -> {

                    Text(
                        text = "Toca los residuos que consideres especiales",
                        color = M7Text,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }


        // =====================================================
        // BOTÓN
        // =====================================================

        M7PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
            enabled = allCorrect,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 45.dp,
                    end = 45.dp,
                    bottom = 28.dp
                )
        )
    }
}


// ============================================================
// IMAGEN SELECCIONABLE
// ============================================================

@Composable
private fun M7SelectableWasteImage(
    imageRes: Int,
    description: String,
    selected: Boolean,
    wrong: Boolean,
    onClick: () -> Unit
) {

    val borderColor = when {

        wrong -> {
            Color(0xFFB84A3E)
        }

        selected -> {
            M7Green
        }

        else -> {
            Color.Transparent
        }
    }


    Box(
        modifier = Modifier
            .size(
                width = 135.dp,
                height = 115.dp
            )
            .border(
                width = if (selected) {
                    2.dp
                } else {
                    0.dp
                },
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable {
                onClick()
            }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(
                id = imageRes
            ),
            contentDescription = description,
            modifier = Modifier.size(105.dp),
            contentScale = ContentScale.Fit
        )


        // CHECK EN LOS CORRECTOS SELECCIONADOS
        if (selected && !wrong) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(25.dp)
                    .background(
                        color = M7Green,
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        // X EN LOS INCORRECTOS
        if (wrong) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(25.dp)
                    .background(
                        color = Color(0xFFB84A3E),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "×",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================
// LECCIÓN 2
// TOMA DECISIONES
// ============================================================

@Composable
private fun ModuleSevenDecisionScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    var clothesAnswer by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var bottleAnswer by rememberSaveable {
        mutableStateOf<Int?>(null)
    }


    // Primera situación:
    // correcta = reparar/donar (1)
    val clothesCorrect = clothesAnswer == 1

    // Segunda situación:
    // correcta = llevar botella reutilizable (1)
    val bottleCorrect = bottleAnswer == 1


    val allCorrect =
        clothesCorrect &&
                bottleCorrect


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M7Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 120.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M7LessonHeader(
                lessonNumber = 2,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text = "Toma decisiones",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M7DarkGreen
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Text(
                text =
                    "Elige la acción más responsable\n" +
                            "en cada situación.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M7Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =================================================
            // SITUACIÓN 1
            // =================================================

            M7SituationTitle(
                text = "Tu camisa favorita está rota.\n¿Qué haces?"
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                M7DecisionCard(
                    imageRes = R.drawable.m7_trash,
                    text = "La tiro a la\nbasura",
                    selected = clothesAnswer == 0,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        clothesAnswer = 0
                    }
                )


                M7DecisionCard(
                    imageRes = R.drawable.m7_repair,
                    text = "La reparo o la\ndono",
                    selected = clothesAnswer == 1,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        clothesAnswer = 1
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // SITUACIÓN 2
            // =================================================

            M7SituationTitle(
                text = "Vas a salir de casa.\n¿Qué llevas contigo?"
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                M7DecisionCard(
                    imageRes = R.drawable.m7_plastic_bottle,
                    text = "Una botella de\nagua",
                    selected = bottleAnswer == 0,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        bottleAnswer = 0
                    }
                )


                M7DecisionCard(
                    imageRes = R.drawable.m7_reusable_bottle,
                    text = "Un botilito",
                    selected = bottleAnswer == 1,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        bottleAnswer = 1
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            if (
                clothesAnswer != null ||
                bottleAnswer != null
            ) {

                Text(
                    text = if (allCorrect) {
                        "¡Muy bien! ✓"
                    } else {
                        "Revisa tus decisiones"
                    },
                    color = if (allCorrect) {
                        M7Green
                    } else {
                        M7Text
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        M7PrimaryButton(
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
// TÍTULO DE SITUACIÓN
// ============================================================

@Composable
private fun M7SituationTitle(
    text: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 11.dp,
                    horizontal = 12.dp
                ),
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = M7Text,
            textAlign = TextAlign.Center
        )
    }
}


// ============================================================
// TARJETA DE DECISIÓN
// ============================================================

@Composable
private fun M7DecisionCard(
    imageRes: Int,
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(165.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                M7Selected
            } else {
                Color.White
            }
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =============================================
            // ESPACIO RESERVADO PARA LA IMAGEN
            // =============================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(
                        id = imageRes
                    ),
                    contentDescription = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp),
                    contentScale = ContentScale.Fit
                )
            }


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // =============================================
            // TEXTO
            // =============================================

            Text(
                text = text,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = M7Text,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// ============================================================
// FINAL
// ============================================================

@Composable
private fun ModuleSevenCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M7Background)
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
                    "Felicidades has acabado\n" +
                            "todos los módulos !!",
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


            M7PrimaryButton(
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
private fun M7LessonHeader(
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
                text = "Lección $lessonNumber de 2",
                fontSize = 12.sp,
                color = M7Text,
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

            repeat(2) { index ->

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            color = if (index < lessonNumber) {
                                M7Green
                            } else {
                                M7Inactive
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
private fun M7PrimaryButton(
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
            containerColor = M7Green,
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
    name = "M7 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M7IntroPreview() {

    ModuleSevenIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M7 - Lección 1",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M7LessonOnePreview() {

    ModuleSevenSpecialWasteScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M7 - Lección 2",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M7LessonTwoPreview() {

    ModuleSevenDecisionScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M7 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M7CompletePreview() {

    ModuleSevenCompleteScreen(
        onFinish = {}
    )
}