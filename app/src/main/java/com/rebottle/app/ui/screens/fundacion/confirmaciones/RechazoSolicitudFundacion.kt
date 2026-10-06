package com.rebottle.app.ui.screens.fundacion.solicitudes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.rebottle.app.ui.theme.RebottleColors

private val BackgroundColor = RebottleColors.Background
private val PrimaryGreenColor = Color(0xFF597042)
private val PrimaryTextColor = Color(0xFF34382F)
private val SecondaryTextColor = RebottleColors.SecondaryText

@Composable
fun RechazoSolicitudFundacion(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onBackToHomeClick: () -> Unit = {
        navController?.navigate(AppScreens.homeFundacion.name) {
            popUpTo(AppScreens.homeFundacion.name) { inclusive = true }
        }
    },
    onGoToRequestsClick: () -> Unit = {
        navController?.navigate(AppScreens.solicitudesFundacion.name)
    },
) {
    RechazoSolicitudFundacionScreen(
        modifier = modifier,
        onBackToHomeClick = onBackToHomeClick,
        onGoToRequestsClick = onGoToRequestsClick
    )
}

@Composable
fun RechazoSolicitudFundacionScreen(
    modifier: Modifier = Modifier,
    onBackToHomeClick: () -> Unit = {},
    onGoToRequestsClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(78.dp))

                RechazoIllustration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = stringResource(id = R.string.rejection_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.rejection_subtitle),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = SecondaryTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onBackToHomeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreenColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.back_to_home),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onGoToRequestsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, PrimaryGreenColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryGreenColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.go_to_my_requests),
                        color = PrimaryGreenColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RechazoIllustration(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.rechazos),
        contentDescription = null,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun RechazoSolicitudFundacionPreview() {
    RebottleTheme {
        RechazoSolicitudFundacionScreen()
    }
}
