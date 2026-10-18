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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R


// ============================================================
// COLORES
// ============================================================

private val M6Background = Color(0xFFFAF7EF)
private val M6Green = Color(0xFF668542)
private val M6DarkGreen = Color(0xFF3F542F)
private val M6Text = Color(0xFF74776E)
private val M6Inactive = Color(0xFFE5ECD9)


// ============================================================
// ESTADOS
// ============================================================

private enum class ModuleSixStep {
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
fun ModuleSixFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit,
    onModuleCompleted: () -> Unit = {}
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleSixStep.INTRO)
    }


    when (currentStep) {

        ModuleSixStep.INTRO -> {

            ModuleSixIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    currentStep = ModuleSixStep.LESSON_ONE
                }
            )
        }


        ModuleSixStep.LESSON_ONE -> {

            ModuleSixInformationScreen(
                lessonNumber = 1,
                title = "¿Realmente lo necesito?",
                description =
                    "Antes de comprar algo nuevo\n" +
                            "piensa si de verdad lo necesitas.",
                imageRes = R.drawable.m6_need,

                onBack = {
                    currentStep = ModuleSixStep.INTRO
                },

                onNext = {
                    currentStep = ModuleSixStep.LESSON_TWO
                }
            )
        }


        ModuleSixStep.LESSON_TWO -> {

            ModuleSixInformationScreen(
                lessonNumber = 2,
                title = "Repara antes de\nreemplazar",
                description =
                    "Muchos objetos aún pueden\n" +
                            "servir si los arreglamos a tiempo.",
                imageRes = R.drawable.m6_repair,

                onBack = {
                    currentStep = ModuleSixStep.LESSON_ONE
                },

                onNext = {
                    currentStep = ModuleSixStep.LESSON_THREE
                }
            )
        }


        ModuleSixStep.LESSON_THREE -> {

            ModuleSixInformationScreen(
                lessonNumber = 3,
                title = "Elige mejor",
                description =
                    "Prefiere opciones reutilizables y\n" +
                            "productos con menos residuos.",
                imageRes = R.drawable.m6_choose,

                onBack = {
                    currentStep = ModuleSixStep.LESSON_TWO
                },

                onNext = {
                    currentStep = ModuleSixStep.LESSON_FOUR
                }
            )
        }


        ModuleSixStep.LESSON_FOUR -> {

            ModuleSixInformationScreen(
                lessonNumber = 4,
                title = "Desperdicio de comida",
                description =
                    "Comprar y servir solo lo\n" +
                            "necesario también ayuda al\n" +
                            "planeta.",
                imageRes = R.drawable.m6_food,

                onBack = {
                    currentStep = ModuleSixStep.LESSON_THREE
                },

                onNext = {
                    currentStep = ModuleSixStep.COMPLETE
                }
            )
        }


        ModuleSixStep.COMPLETE -> {


            // Suma los puntos en Firebase apenas se llega a la pantalla final

            LaunchedEffect(Unit) { onModuleCompleted() }


            ModuleSixCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleSixIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M6Background)
    ) {

        // FLECHA ATRÁS
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
            painter = painterResource(id = R.drawable.m6_reciclar),
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
                text = "Reciclar empieza antes de\nbotar",
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF272A24),
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Text(
                text =
                    "Las mejores decisiones\n" +
                            "sostenibles comienzan antes de\n" +
                            "comprar o desechar.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M6Text,
                textAlign = TextAlign.Center
            )
        }


        M6PrimaryButton(
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
// LECCIONES 1 - 4
// ============================================================

@Composable
private fun ModuleSixInformationScreen(
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
            .background(M6Background)
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

            // HEADER
            M6LessonHeader(
                lessonNumber = lessonNumber,
                onBack = onBack
            )


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // TÍTULO
            Text(
                text = title,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = M6DarkGreen,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // DESCRIPCIÓN
            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M6Text,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // IMAGEN
            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                contentScale = ContentScale.Fit
            )
        }


        // BOTÓN
        M6PrimaryButton(
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
// FINAL
// ============================================================

@Composable
private fun ModuleSixCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M6Background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = 45.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.modulo6),
                contentDescription = "Ilustración de completado",
                modifier = Modifier
                    .size(320.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text =
                    "Felicidades estas un paso\n" +
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
                text = "+50 puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            M6PrimaryButton(
                text = "Finalizar",
                onClick = onFinish
            )
        }
    }
}


// ============================================================
// HEADER DE LECCIONES
// ============================================================

@Composable
private fun M6LessonHeader(
    lessonNumber: Int,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            // FLECHA
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


            // TEXTO
            Text(
                text = "Lección $lessonNumber de 4",
                fontSize = 12.sp,
                color = M6Text,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // BARRAS DE PROGRESO
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
                                M6Green
                            } else {
                                M6Inactive
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
private fun M6PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,

        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),

        shape = RoundedCornerShape(10.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = M6Green
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
    name = "M6 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6IntroPreview() {

    ModuleSixIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M6 - Lección 1",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6LessonOnePreview() {

    ModuleSixInformationScreen(
        lessonNumber = 1,
        title = "¿Realmente lo necesito?",
        description =
            "Antes de comprar algo nuevo\n" +
                    "piensa si de verdad lo necesitas.",
        imageRes = R.drawable.m6_need,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M6 - Lección 2",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6LessonTwoPreview() {

    ModuleSixInformationScreen(
        lessonNumber = 2,
        title = "Repara antes de\nreemplazar",
        description =
            "Muchos objetos aún pueden\n" +
                    "servir si los arreglamos a tiempo.",
        imageRes = R.drawable.m6_repair,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M6 - Lección 3",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6LessonThreePreview() {

    ModuleSixInformationScreen(
        lessonNumber = 3,
        title = "Elige mejor",
        description =
            "Prefiere opciones reutilizables y\n" +
                    "productos con menos residuos.",
        imageRes = R.drawable.m6_choose,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M6 - Lección 4",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6LessonFourPreview() {

    ModuleSixInformationScreen(
        lessonNumber = 4,
        title = "Desperdicio de comida",
        description =
            "Comprar y servir solo lo\n" +
                    "necesario también ayuda al\n" +
                    "planeta.",
        imageRes = R.drawable.m6_food,
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M6 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M6CompletePreview() {

    ModuleSixCompleteScreen(
        onFinish = {}
    )
}
