package com.rebottle.app.ui.screens.usuario.confirmaciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors

private val BackgroundColor = RebottleColors.Background
private val PrimaryGreenColor = Color(0xFF597042)
private val PrimaryTextColor = RebottleColors.MainText
private val SecondaryTextColor = RebottleColors.SecondaryText
private val CardBackgroundColor = Color(0xFFEFF5E6)
private val CardBorderColor = Color(0xFFDFE9D4)
private val IconTerracottaColor = Color(0xFFCD693A)

/*@Composable
fun ConfirmacionSolicitudUsuario(
    NavController: NavController,
    modifier: Modifier = Modifier,
    horarioEstimado: String = stringResource(id = R.string.estimated_schedule_value),
) {
    ConfirmacionSolicitudUsuarioScreen(
        modifier = modifier,
        horarioEstimado = horarioEstimado,
        controller = NavController,
        idSolicitud = ""
    )
}
*/
@Composable
fun ConfirmacionSolicitudUsuarioScreen(
    controller: NavController,
    modifier: Modifier = Modifier,
    horarioEstimado: String = stringResource(id = R.string.estimated_schedule_value),
    solicitudId: String,
    viewModel: SolicitudViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()



    val solicitud = state.solicitudes.find {
        it.id == solicitudId
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
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
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(id = R.drawable.aprobados),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.confirmation_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.confirmation_subtitle),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = SecondaryTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                InfoCard(
                    iconRes = R.drawable.ic_calendar,
                    label = stringResource(id = R.string.collection_date_title),
                    value = solicitud?.fechaRecoleccion ?: "Cargando...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoCard(
                    iconRes = R.drawable.ic_clock,
                    label = stringResource(id = R.string.estimated_schedule_title),
                    value = horarioEstimado,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { controller.navigate(AppScreens.homeCiudadano.name) },
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

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { controller.navigate(AppScreens.solicitudesUsuario.name) },
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
private fun InfoCard(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CardBackgroundColor,
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = IconTerracottaColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = SecondaryTextColor
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTextColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmacionSolicitudUsuarioPreview() {
    RebottleTheme {
        //ConfirmacionSolicitudUsuarioScreen(controller = NavController(LocalContext.current))
    }
}
