package com.rebottle.app.ui.screens.recolector.confirmaciones

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

// Assuming these theme values are available in the original project.
// If not, they need to be defined here or imported.
private val BackgroundColor = Color(0xFFFBF8F2)
private val PrimaryGreenColor = Color(0xFF597042)
private val PrimaryTextColor = Color(0xFF34382F)
private val SecondaryTextColor = Color(0xFF7F887A)

@Composable
fun RecorridoFinalizado(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onBackToHomeClick: () -> Unit = {
        navController?.navigate(AppScreens.homeRecolector.name) {
            // Clear the back stack to prevent going back to the "finished" screen
            popUpTo(0) { inclusive = true }
        }
    },
) {
    RecorridoFinalizadoScreen(
        modifier = modifier,
        onBackToHomeClick = onBackToHomeClick,
    )
}

@Composable
fun RecorridoFinalizadoScreen(
    modifier: Modifier = Modifier,
    onBackToHomeClick: () -> Unit = {},
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
            verticalArrangement = Arrangement.Center, // Centers content vertically
        ) {
            // The illustration
            FinalizadoIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Adjusted height to better fit the visual
            )

            Spacer(modifier = Modifier.height(40.dp)) // Space between illustration and text

            // Title Text: ¡ Recorrido Finalizado !
            Text(
                text = stringResource(id = R.string.finished_title), // Add this to strings.xml
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp)) // Space between title and subtitle

            // Subtitle Text: Gracias por tu trabajo el día de hoy.
            Text(
                text = stringResource(id = R.string.finished_subtitle), // Add this to strings.xml
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(64.dp)) // Large space before the button

            // Button: Volver al inicio
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
                    text = stringResource(id = R.string.back_to_home), // Assuming this exists
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun FinalizadoIllustration(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.finalizado), // Use the specified drawable
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun RecorridoFinalizadoPreview() {
    RebottleTheme {
        RecorridoFinalizadoScreen()
    }
}