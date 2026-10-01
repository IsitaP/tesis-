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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.rebottle.app.ui.theme.RebottleTheme


private val M3Background = Color(0xFFFAF7EF)
private val M3Green = Color(0xFF668542)
private val M3DarkGreen = Color(0xFF3F542F)
private val M3Text = Color(0xFF74776E)
private val M3Inactive = Color(0xFFE6EDDD)

private val M3MythBackground = Color(0xFFFFECE8)
private val M3TruthBackground = Color(0xFFF0F5E9)
private val M3MythRed = Color(0xFFC94D43)


// ============================================================
// ESTADOS
// ============================================================

private enum class ModuleThreeStep {
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
fun ModuleThreeFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit
) {

    var currentStep by rememberSaveable {
        mutableStateOf(ModuleThreeStep.INTRO)
    }

    when (currentStep) {

        ModuleThreeStep.INTRO -> {

            ModuleThreeIntroScreen(
                onBack = onBackToRoadmap,
                onNext = {
                    currentStep = ModuleThreeStep.LESSON_ONE
                }
            )
        }

        ModuleThreeStep.LESSON_ONE -> {

            ModuleThreeLessonOneScreen(
                onBack = {
                    currentStep = ModuleThreeStep.INTRO
                },
                onNext = {
                    currentStep = ModuleThreeStep.LESSON_TWO
                }
            )
        }

        ModuleThreeStep.LESSON_TWO -> {

            ModuleThreeLessonTwoScreen(
                onBack = {
                    currentStep = ModuleThreeStep.LESSON_ONE
                },
                onNext = {
                    currentStep = ModuleThreeStep.LESSON_THREE
                }
            )
        }

        ModuleThreeStep.LESSON_THREE -> {

            ModuleThreeLessonThreeScreen(
                onBack = {
                    currentStep = ModuleThreeStep.LESSON_TWO
                },
                onNext = {
                    currentStep = ModuleThreeStep.LESSON_FOUR
                }
            )
        }

        ModuleThreeStep.LESSON_FOUR -> {

            ModuleThreeLessonFourScreen(
                onBack = {
                    currentStep = ModuleThreeStep.LESSON_THREE
                },
                onNext = {
                    currentStep = ModuleThreeStep.COMPLETE
                }
            )
        }

        ModuleThreeStep.COMPLETE -> {

            ModuleThreeCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}


// ============================================================
// INTRO
// ============================================================

@Composable
private fun ModuleThreeIntroScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
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
                    top = 48.dp,
                    start = 42.dp,
                    end = 42.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "El viaje de tus residuos",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF272A24),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Descubre qué pasa con los\n" +
                        "residuos después de separarlos y\n" +
                        "cómo se convierten en nuevas\n" +
                        "oportunidades.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )
        }

        M3PrimaryButton(
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
// DESPUÉS DE LA BOLSA BLANCA
// ============================================================

@Composable
private fun ModuleThreeLessonOneScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 20.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M3LessonHeader(
                lessonNumber = 1,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Después de la bolsa blanca",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M3DarkGreen,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Tu reciclaje inicia un nuevo viaje.\n" +
                        "Conoce el camino que sigue.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            M3JourneyCard(
                title = "En tu hogar",
                description = "Separan tus residuos\nen la bolsa blanca.",
                imageRes = R.drawable.lesson_home
            )

            M3Connector()

            M3JourneyCard(
                title = "Recolección",
                description = "Un recolector de oficio\nlos recoge en tu barrio.",
                imageRes = R.drawable.m3_collection
            )

            M3Connector()

            M3JourneyCard(
                title = "Ecoalianza",
                description = "Se clasifican y se\npreparan para su reciclaje.",
                imageRes = R.drawable.m3_ecoalliance
            )

            M3Connector()

            M3JourneyCard(
                title = "Nueva vida",
                description = "Se transforman en materia\nprima para nuevos productos.",
                imageRes = R.drawable.m3_new_life
            )
        }

        M3PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
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
// RECICLADORES DE OFICIO
// ============================================================

@Composable
private fun ModuleThreeLessonTwoScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 20.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M3LessonHeader(
                lessonNumber = 2,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Recicladores de oficio",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M3DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Detrás de cada bolsa, hay\n" +
                        "personas que hacen posible el\n" +
                        "cambio.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Image(
                painter = painterResource(
                    id = R.drawable.m3_collectors
                ),
                contentDescription = "Recicladores de oficio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            M3CollectorCard(
                imageRes = R.drawable.m3_recolectan,
                title = "Recolectan",
                description = "Recolectan los materiales en tu comunidad."
            )

            Spacer(modifier = Modifier.height(10.dp))

            M3CollectorCard(
                imageRes = R.drawable.m3_clasifican,
                title = "Clasifican",
                description = "Separan y limpian los residuos."
            )

            Spacer(modifier = Modifier.height(10.dp))

            M3CollectorCard(
                imageRes = R.drawable.m3_dan_vida,
                title = "Dan nueva vida",
                description = "Entregan los materiales para que se transformen."
            )
        }

        M3PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
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
// LECCIÓN 3
// MITOS DEL RECICLAJE
// ============================================================

@Composable
private fun ModuleThreeLessonThreeScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M3LessonHeader(
                lessonNumber = 3,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Mitos del reciclaje",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M3DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Aclaremos algunas ideas para\nreciclar más.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            M3MythTruthRow(
                myth = "\"Todo el plástico se\npuede reciclar\"",
                truth = "No todos los plásticos\nse reciclan. Depende\ndel tipo y estado."
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            M3MythTruthRow(
                myth = "\"Si está sucio, igual\nse puede reciclar\"",
                truth = "Los residuos deben\nestar limpios para\npoder ser reciclados."
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            M3MythTruthRow(
                myth = "\"El reciclaje no hace\nuna gran diferencia\"",
                truth = "Sí hace la diferencia.\nReduce contaminación y\ngenera empleo."
            )
        }

        M3PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
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
// ECONOMÍA CIRCULAR
// ============================================================

@Composable
private fun ModuleThreeLessonFourScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 20.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            M3LessonHeader(
                lessonNumber = 4,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Economía circular",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = M3DarkGreen
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Los residuos no son el final, sino\n" +
                        "el inicio de algo nuevo.",
                fontSize = 13.sp,
                lineHeight = 17.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            Image(
                painter = painterResource(
                    id = R.drawable.m3_circular_economy
                ),
                contentDescription = "Economía circular",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                contentScale = ContentScale.Fit
            )
        }

        M3PrimaryButton(
            text = "Siguiente →",
            onClick = onNext,
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
private fun ModuleThreeCompleteScreen(
    onFinish: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.modulo3),
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

            M3PrimaryButton(
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
private fun M3LessonHeader(
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
                color = M3Text,
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
                                M3Green
                            } else {
                                M3Inactive
                            },
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}


// ============================================================
// TARJETA RECORRIDO
// ============================================================

@Composable
private fun M3JourneyCard(
    title: String,
    description: String,
    imageRes: Int
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription = title,
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF33352F)
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    color = M3Text
                )
            }
        }
    }
}


@Composable
private fun M3Connector() {

    Box(
        modifier = Modifier
            .size(
                width = 2.dp,
                height = 18.dp
            )
            .background(
                Color(0xFFB9C5A9)
            )
    )
}


// ============================================================
// RECICLADORES
// ============================================================

@Composable
private fun M3CollectorCard(
    imageRes: Int,
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = M3DarkGreen
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = description,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    color = M3Text
                )
            }
        }
    }
}

// ============================================================
// MITO / VERDAD
// ============================================================

@Composable
private fun M3MythTruthRow(
    myth: String,
    truth: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        M3MythTruthCard(
            label = "Mito",
            text = myth,
            background = M3MythBackground,
            labelColor = M3MythRed,
            modifier = Modifier.weight(1f)
        )

        M3MythTruthCard(
            label = "Verdad",
            text = truth,
            background = M3TruthBackground,
            labelColor = M3Green,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun M3MythTruthCard(
    label: String,
    text: String,
    background: Color,
    labelColor: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .height(115.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = labelColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 3.dp
                    )
            ) {

                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = text,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                color = M3Text,
                textAlign = TextAlign.Center
            )
        }
    }
}


// ============================================================
// BOTÓN
// ============================================================

@Composable
private fun M3PrimaryButton(
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
            containerColor = M3Green
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
    name = "M3 - Intro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3IntroPreview() {

    ModuleThreeIntroScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M3 - Lección 1",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3LessonOnePreview() {

    ModuleThreeLessonOneScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M3 - Lección 2",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3LessonTwoPreview() {

    ModuleThreeLessonTwoScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M3 - Lección 3",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3LessonThreePreview() {

    ModuleThreeLessonThreeScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M3 - Lección 4",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3LessonFourPreview() {

    ModuleThreeLessonFourScreen(
        onBack = {},
        onNext = {}
    )
}


@Preview(
    name = "M3 - Final",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun M3CompletePreview() {

    ModuleThreeCompleteScreen(
        onFinish = {}
    )
}

@Preview(
    name = "M3 - Flow",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun ModuleThreeFlowPreview() {
    RebottleTheme {
        ModuleThreeFlowScreen(
            onBackToRoadmap = {},
            onFinish = {}
        )
    }
}