package com.rebottle.app.ui.screens.recolector.confirmaciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme

private val BackgroundColor = Color(0xFFFBF8F2)
private val PrimaryGreenColor = Color(0xFF597042)
private val PrimaryTextColor = Color(0xFF34382F)
private val SecondaryTextColor = Color(0xFF7F887A)

@Composable
fun RecorridoIniciado(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onBackToHomeClick: () -> Unit = {
        navController?.navigate(AppScreens.homeRecolector.name) {
            popUpTo(0) { inclusive = true }
        }
    },
    onGoToFirstRouteClick: () -> Unit = {
        // Ajusta la ruta a donde deba ir el botón "Ver primera ruta"
        navController?.navigate(AppScreens.rutas.name)
    },
) {
    RecorridoIniciadoScreen(
        modifier = modifier,
        onBackToHomeClick = onBackToHomeClick,
        onGoToFirstRouteClick = onGoToFirstRouteClick,
    )
}

@Composable
fun RecorridoIniciadoScreen(
    modifier: Modifier = Modifier,
    onBackToHomeClick: () -> Unit = {},
    onGoToFirstRouteClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Ilustración del recurso (inciado)
            IniciadoIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Título: ¡ Recorrido Iniciado !
            Text(
                text = stringResource(id = R.string.started_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo: Buena suerte con los trayectos de hoy. Gracias por tu trabajo.
            Text(
                text = stringResource(id = R.string.started_subtitle),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botón principal: Volver al inicio
            Button(
                onClick = onBackToHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreenColor,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.back_to_home),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón secundario: Ver primera ruta
            OutlinedButton(
                onClick = onGoToFirstRouteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, PrimaryGreenColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryGreenColor,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.view_first_route),
                    color = PrimaryGreenColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun IniciadoIllustration(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.iniciado),
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun RecorridoIniciadoPreview() {
    RebottleTheme {
        RecorridoIniciadoScreen()
    }
}