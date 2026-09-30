package com.rebottle.app.ui.screens.usuario

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.components.monthYearLabel
import com.rebottle.app.model.MaterialType
import java.time.LocalDate
import com.rebottle.app.components.buildCalendarGrid
import android.widget.Toast
import com.rebottle.app.model.SolicitudState
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.ui.theme.RebottleTheme



private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val CardWhite = Color.White
private val PinOrange = Color(0xFFD98555)
private val TextMuted = Color(0xFF8A8A8A)
private val WeekdayGreen = Color(0xFF6B8A5A)
private val PastMonthGray = Color(0xFFBFBFBF)

@Composable
fun CreateRequestScreen(controller: NavController, model: SolicitudViewModel = viewModel()) {
    val state by model.state.collectAsState()
    val context = LocalContext.current

    CreateRequestContent(
        state = state,
        onLocationChange = { model.updateLocation(it) },
        onToggleMaterial = { model.toggleMaterial(it) },
        onDecrementWeight = { model.decrementWeight() },
        onIncrementWeight = { model.incrementWeight() },
        onPreviousMonth = { model.previousMonth() },
        onNextMonth = { model.nextMonth() },
        onSelectDate = { model.selectDate(it) },
        onCreateClick = {
            if (state.location.isBlank()) {
                model.updateLocationError("Ingresa la dirección de recolección")
            } else {
                model.updateLocationError("")
            }

            if (state.selectedMaterials.isEmpty()) {
                model.updateMaterialsError("Selecciona al menos un material")
            } else {
                model.updateMaterialsError("")
            }

            if (state.location.isNotBlank() && state.selectedMaterials.isNotEmpty()) {
                model.crearSolicitud(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Solicitud creada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        model.resetForm()
                        controller.popBackStack()
                    },
                    onError = { mensaje ->
                        Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        },
        onBackClick = { controller.popBackStack() }
    )
}

@Composable
fun CreateRequestContent(
    state: SolicitudState,
    onLocationChange: (String) -> Unit,
    onToggleMaterial: (MaterialType) -> Unit,
    onDecrementWeight: () -> Unit,
    onIncrementWeight: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(BackgroundColor)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // ====================================================
        // HEADER: flecha + título
        // ====================================================
        Box(modifier = Modifier.fillMaxWidth()) {

            Image(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Volver",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "Nueva solicitud de\nrecolección",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                color = DarkGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ====================================================
        // UBICACIÓN
        // ====================================================
        OutlinedTextField(
            value = state.location,
            onValueChange = onLocationChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            singleLine = true,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.pin_location),
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(16.dp)
                )
            },
            placeholder = { Text(text = "Dirección de recolección", color = TextMuted, fontSize = 14.sp) },
            isError = state.locationError.isNotEmpty(),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        // ====================================================
        // MATERIALES A ENTREGAR (4 y 4)
        // ====================================================
        SectionTitle(text = "Materiales a entregar")

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite, shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            val leftColumn = listOf(
                MaterialType.PLASTICO,
                MaterialType.PAPEL,
                MaterialType.CARTON,
                MaterialType.VIDRIO
            )
            val rightColumn = listOf(
                MaterialType.METAL,
                MaterialType.ELECTRONICOS,
                MaterialType.MADERA,
                MaterialType.OTROS
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    leftColumn.forEach { material ->
                        MaterialCheckboxRow(
                            material = material,
                            checked = material in state.selectedMaterials,
                            onCheckedChange = { onToggleMaterial(material) }
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    rightColumn.forEach { material ->
                        MaterialCheckboxRow(
                            material = material,
                            checked = material in state.selectedMaterials,
                            onCheckedChange = { onToggleMaterial(material) }
                        )
                    }
                }
            }
        }

        if (state.materialsError.isNotEmpty()) {
            Text(text = state.materialsError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ====================================================
        // PESO ESTIMADO
        // ====================================================
        SectionTitle(text = "Peso estimado")

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(CardWhite, shape = RoundedCornerShape(50)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "-",
                fontSize = 22.sp,
                color = DarkGreen,
                modifier = Modifier
                    .clickable { onDecrementWeight() }
                    .padding(horizontal = 24.dp)
            )

            Box(
                modifier = Modifier
                    .background(Color(0xFFEFEAE0), shape = RoundedCornerShape(50))
                    .padding(horizontal = 20.dp, vertical = 3.dp)
            ) {
                Text(text = "${state.weightKg} Kg", fontSize = 14.sp, color = DarkGreen)
            }

            Text(
                text = "+",
                fontSize = 22.sp,
                color = DarkGreen,
                modifier = Modifier
                    .clickable { onIncrementWeight() }
                    .padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ====================================================
        // FECHA
        // ====================================================
        SectionTitle(text = "Fecha")

        Spacer(modifier = Modifier.height(10.dp))

        CalendarCard(
            state = state,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
            onSelectDate = onSelectDate
        )

        Spacer(modifier = Modifier.height(15.dp))

        // ====================================================
        // BOTÓN SOLICITAR
        // ====================================================
        Button(
            onClick = onCreateClick,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainGreen
            )
        ) {

            if (state.isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

            } else {

                Text(
                    text = "Solicitar",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

// ============================================================
// TÍTULO DE SECCIÓN
// ============================================================

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = DarkGreen,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
}

// ============================================================
// FILA DE CHECKBOX DE MATERIAL
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialCheckboxRow(
    material: MaterialType,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(checkedColor = MainGreen)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = material.label, color = DarkGreen, fontSize = 14.sp)
    }
}

// ============================================================
// CALENDARIO
// ============================================================

@Composable
private fun CalendarCard(
    state: SolicitudState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite, shape = RoundedCornerShape(16.dp))
            .padding(5.dp)
    ) {

        // Encabezado de mes con flechas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                fontSize = 18.sp,
                color = DarkGreen,
                modifier = Modifier.clickable { onPreviousMonth() }.padding(8.dp)
            )
            Text(
                text = monthYearLabel(state.displayedMonth),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainGreen
            )
            Text(
                text = ">",
                fontSize = 18.sp,
                color = DarkGreen,
                modifier = Modifier.clickable { onNextMonth() }.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Encabezado de días de la semana
        val weekdays = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = WeekdayGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Grilla de días
        val days = buildCalendarGrid(state.displayedMonth)
        val weeks = days.chunked(7)

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val isSelected = date == state.selectedDate
                            val isWeekend = date.dayOfWeek.value >= 6
                            val isCurrentMonth = date.month == state.displayedMonth.month

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .let { if (isSelected) it.background(MainGreen, shape = CircleShape) else it }
                                    .clickable { onSelectDate(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = when {
                                        isSelected -> Color.White
                                        !isCurrentMonth -> PastMonthGray
                                        isWeekend -> Color(0xFFD98555)
                                        else -> DarkGreen
                                    },
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRequestScreenPreview() {
    RebottleTheme {
        CreateRequestContent(
            state = SolicitudState(
                location = "Cra 15 # 136-32",
                selectedMaterials = setOf(MaterialType.PLASTICO, MaterialType.VIDRIO),
                weightKg = 15
            ),
            onLocationChange = {},
            onToggleMaterial = {},
            onDecrementWeight = {},
            onIncrementWeight = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onSelectDate = {},
            onCreateClick = {},
            onBackClick = {}
        )
    }
}