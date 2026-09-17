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
import androidx.compose.runtime.remember
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

private val LearnBackground = Color(0xFFFAF7EF)
private val LearnGreen = Color(0xFF6A8A43)
private val LearnDark = Color(0xFF314126)
private val LearnText = Color(0xFF6F726A)
private val LearnProgressInactive = Color(0xFFE7EDDD)
private val LearnShadow = Color(0x22000000)

private enum class ModuleOneStep {
    INTRO,
    LESSON_1,
    LESSON_2,
    LESSON_3,
    LESSON_4,
    COMPLETE
}

private enum class LessonTwoTab {
    BASURA,
    RECURSO
}

@Composable
fun ModuleOneFlowScreen(
    onBackToRoadmap: () -> Unit,
    onFinish: () -> Unit
) {
    var currentStep by rememberSaveable {
        mutableStateOf(ModuleOneStep.INTRO)
    }

    var lessonTwoTab by rememberSaveable {
        mutableStateOf(LessonTwoTab.BASURA)
    }

    when (currentStep) {

        ModuleOneStep.INTRO -> {
            ModuleOneIntroScreen(
                onBack = onBackToRoadmap,
                onStart = {
                    currentStep = ModuleOneStep.LESSON_1
                }
            )
        }

        ModuleOneStep.LESSON_1 -> {
            ModuleOneLessonOneScreen(
                onBack = {
                    currentStep = ModuleOneStep.INTRO
                },
                onNext = {
                    currentStep = ModuleOneStep.LESSON_2
                }
            )
        }

        ModuleOneStep.LESSON_2 -> {
            ModuleOneLessonTwoScreen(
                selectedTab = lessonTwoTab,
                onSelectTab = {
                    lessonTwoTab = it
                },
                onBack = {
                    currentStep = ModuleOneStep.LESSON_1
                },
                onNext = {
                    currentStep = ModuleOneStep.LESSON_3
                }
            )
        }

        ModuleOneStep.LESSON_3 -> {
            ModuleOneLessonThreeScreen(
                onBack = {
                    currentStep = ModuleOneStep.LESSON_2
                },
                onNext = {
                    currentStep = ModuleOneStep.LESSON_4
                }
            )
        }

        ModuleOneStep.LESSON_4 -> {
            ModuleOneLessonFourScreen(
                onBack = {
                    currentStep = ModuleOneStep.LESSON_3
                },
                onNext = {
                    currentStep = ModuleOneStep.COMPLETE
                }
            )
        }

        ModuleOneStep.COMPLETE -> {
            ModuleOneCompleteScreen(
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun ModuleOneIntroScreen(
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
    ) {
        Text(
            text = "‹",
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp)
                .clickable { onBack() },
            fontSize = 32.sp,
            color = Color.Black
        )

        Text(
            text = "El valor de lo que\ndesechamos",
            modifier = Modifier
                .padding(start = 52.dp, top = 36.dp),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF22241D)
        )

        PrimaryLearnButton(
            text = "Empezar →",
            onClick = onStart,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 46.dp, vertical = 92.dp)
        )
    }
}

@Composable
private fun ModuleOneLessonOneScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
    ) {

        // ====================================================
        // CONTENIDO DE LA LECCIÓN
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 18.dp,
                    bottom = 110.dp // deja espacio para el botón
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER + PROGRESO
            LessonHeader(
                lessonText = "Lección 1 de 4",
                completedBars = 1,
                onBack = onBack
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // TÍTULO
            Text(
                text = "¿A dónde va lo que botas?",
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = LearnDark,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // SUBTÍTULO
            Text(
                text = "Tu residuo puede tener un\ngran viaje, conoce su ruta !!",
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = LearnText,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ====================================================
            // RECORRIDO
            // ====================================================

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    RouteStepCard(
                        imageRes = R.drawable.lesson_home,
                        title = "En casa",
                        description = "Usas un producto y\nlo botas."
                    )

                    RouteConnector()

                    RouteStepCard(
                        imageRes = R.drawable.lesson_bins,
                        title = "Separación",
                        description = "Lo separas en la\nfuente."
                    )

                    RouteConnector()

                    RouteStepCard(
                        imageRes = R.drawable.lesson_recycler,
                        title = "Reciclador de\noficio",
                        description = "Lo recoge y lo\nclasifica"
                    )

                    RouteConnector()

                    RouteStepCard(
                        imageRes = R.drawable.lesson_factory,
                        title = "Nueva materia\nprima",
                        description = "Se transforma en\nnuevos productos"
                    )
                }
            }
        }


        // ====================================================
        // BOTÓN SIEMPRE ABAJO
        // ====================================================

        Button(
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 45.dp,
                    end = 45.dp,
                    bottom = 32.dp
                )
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LearnGreen
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            )
        ) {

            Text(
                text = "Siguiente →",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun ModuleOneLessonTwoScreen(
    selectedTab: LessonTwoTab,
    onSelectTab: (LessonTwoTab) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val isBasura = selectedTab == LessonTwoTab.BASURA

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
    ) {

        // CONTENIDO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 26.dp,
                    end = 26.dp,
                    top = 18.dp,
                    bottom = 110.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LessonHeader(
                lessonText = "Lección 2 de 4",
                completedBars = 2,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "¿Basura o recurso?",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = LearnDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "No todo lo que botas es basura.\nMuchos residuos pueden tener una\nsegunda vida.",
                fontSize = 15.sp,
                lineHeight = 17.sp,
                color = LearnText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabOption(
                    text = "Basura",
                    selected = isBasura,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectTab(LessonTwoTab.BASURA)
                    }
                )

                TabOption(
                    text = "Recurso",
                    selected = !isBasura,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectTab(LessonTwoTab.RECURSO)
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isBasura) {
                    "Residuos sucios o contaminados difícilmente\nse reciclan."
                } else {
                    "Materiales limpios que pueden reciclarse y\ntransformarse."
                },
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = LearnText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(
                    id = if (isBasura) {
                        R.drawable.lesson_pizza
                    } else {
                        R.drawable.lesson_resource
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp),
                contentScale = ContentScale.Fit
            )
        }

        // BOTÓN FIJO ABAJO
        PrimaryLearnButton(
            text = "Siguiente →",
            onClick = onNext,
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
@Composable
private fun ModuleOneLessonThreeScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        LessonHeader(
            lessonText = "Lección 3 de 4",
            completedBars = 3,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Las 3R",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = LearnDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tres acciones, un gran impacto.",
            fontSize = 13.sp,
            color = LearnText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(46.dp))

        TripleRCard(
            imageRes = R.drawable.lesson_reduce,
            title = "Reducir",
            description = "Consume menos\npara generar menos\nresiduos.",
            footer = "¡Realmente lo\nnecesitas?"
        )

        Spacer(modifier = Modifier.height(12.dp))

        TripleRCard(
            imageRes = R.drawable.lesson_reuse,
            title = "Reutilizar",
            description = "Dale una segunda\nvida a los objetos.",
            footer = "Sé creativo!!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        TripleRCard(
            imageRes = R.drawable.lesson_recycle_icon,
            title = "Reciclar",
            description = "Separa\ncorrectamente para\nque se transformen.",
            footer = "La separación hace\ntoda la diferencia."
        )

        Spacer(modifier = Modifier.height(22.dp))

        PrimaryLearnButton(
            text = "Siguiente →",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ModuleOneLessonFourScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        LessonHeader(
            lessonText = "Lección 4 de 4",
            completedBars = 4,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "¿Por qué debería\nimportarme?",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = LearnDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Reciclar transforma realidades.",
            fontSize = 13.sp,
            color = LearnText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(52.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BenefitCard(
                title = "Menos\ncontaminación",
                subtitle = "Aire, ríos y mares\nmás limpios.",
                imageRes = R.drawable.lesson_earth,
                modifier = Modifier.weight(1f)
            )

            BenefitCard(
                title = "Apoyo a los\nrecicladores\nformales",
                subtitle = "Más oportunidades.",
                imageRes = R.drawable.lesson_collector,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BenefitCard(
                title = "Ahorro de\nrecursos",
                subtitle = "Cuidamos lo que\ntiene el planeta.",
                imageRes = R.drawable.lesson_coins,
                modifier = Modifier.weight(1f)
            )

            BenefitCard(
                title = "Futuro\nsostenible",
                subtitle = "Un mejor mañana\npara todos.",
                imageRes = R.drawable.lesson_future,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        PrimaryLearnButton(
            text = "Siguiente →",
            onClick = onNext
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ModuleOneCompleteScreen(
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LearnBackground)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Felicidades estas un paso\nmás en el mundo del\nreciclaje !!",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF35372F)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "+100 puntos",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(26.dp))

            PrimaryLearnButton(
                text = "Finalizar",
                onClick = onFinish
            )
        }
    }
}

@Composable
private fun LessonHeader(
    lessonText: String,
    completedBars: Int,
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
                    .clickable { onBack() },
                fontSize = 50.sp,
                color = Color.Black
            )

            Text(
                text = lessonText,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 12.sp,
                color = LearnText
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                            color = if (index < completedBars) LearnGreen else LearnProgressInactive,
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun RouteStepCard(
    imageRes: Int,
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        shape = RoundedCornerShape(7.dp),
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
                    horizontal = 18.dp,
                    vertical = 5.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4E514A)
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = LearnText
                )
            }
        }
    }
}

@Composable
private fun RouteConnector() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(22.dp)
            .background(Color(0xFFB3C691))
    )
}

@Composable
private fun TabOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) LearnGreen else Color.White,
            contentColor = if (selected) Color.White else LearnGreen
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TripleRCard(
    imageRes: Int,
    title: String,
    description: String,
    footer: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4E514A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    color = LearnText
                )

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = footer,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    color = LearnText
                )
            }
        }
    }
}

@Composable
private fun BenefitCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(194.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color(0xFF4E514A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                color = LearnText
            )
        }
    }
}

@Composable
private fun PrimaryLearnButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(19.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LearnGreen
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 23.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModuleOneFlowScreenPreview() {
    RebottleTheme {
        ModuleOneFlowScreen(
            onBackToRoadmap = {},
            onFinish = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneIntroScreenPreview() {
    RebottleTheme {
        ModuleOneIntroScreen(onBack = {}, onStart = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneLessonOneScreenPreview() {
    RebottleTheme {
        ModuleOneLessonOneScreen(onBack = {}, onNext = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneLessonTwoScreenPreview() {
    RebottleTheme {
        ModuleOneLessonTwoScreen(
            selectedTab = LessonTwoTab.BASURA,
            onSelectTab = {},
            onBack = {},
            onNext = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneLessonThreeScreenPreview() {
    RebottleTheme {
        ModuleOneLessonThreeScreen(onBack = {}, onNext = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneLessonFourScreenPreview() {
    RebottleTheme {
        ModuleOneLessonFourScreen(onBack = {}, onNext = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ModuleOneCompleteScreenPreview() {
    RebottleTheme {
        ModuleOneCompleteScreen(onFinish = {})
    }
}
