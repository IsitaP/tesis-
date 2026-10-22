package com.rebottle.app.ui.screens.usuario

import android.util.Log
import android.widget.Toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

import com.rebottle.app.R
import com.rebottle.app.components.buildCalendarGrid
import com.rebottle.app.components.monthYearLabel
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.SolicitudState
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.ui.theme.RebottleTheme

import kotlinx.coroutines.delay

import java.time.LocalDate


private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val CardWhite = Color.White
private val TextMuted = Color(0xFF8A8A8A)
private val WeekdayGreen = Color(0xFF6B8A5A)
private val PastMonthGray = Color(0xFFBFBFBF)


// ============================================================
// PANTALLA CREAR SOLICITUD
// ============================================================

@Composable
fun CreateRequestScreen(
    controller: NavController,
    model: SolicitudViewModel = viewModel()
) {

    val state by model.state.collectAsState()
    val context = LocalContext.current


    // ============================================================
    // GOOGLE PLACES CLIENT
    // ============================================================

    // Si falta la PLACES_API_KEY en local.properties, Places no se
    // inicializa y createClient() cerraría la app. En ese caso el
    // cliente queda en null y el buscador avisa en vez de crashear.
    val placesClient = remember(context) {
        if (Places.isInitialized()) Places.createClient(context) else null
    }

    LaunchedEffect(placesClient) {
        if (placesClient == null) {
            Log.e("Places", "Places no está inicializado: revisa PLACES_API_KEY en local.properties")
            model.updateLocationError("El buscador de direcciones no está disponible")
        }
    }


    // ============================================================
    // TEXTO QUE EL USUARIO ESCRIBE EN EL BUSCADOR
    // ============================================================

    var locationQuery by remember {
        mutableStateOf(state.location)
    }


    // ============================================================
    // LISTA DE PREDICCIONES DEVUELTAS POR GOOGLE
    // ============================================================

    var predictions by remember {
        mutableStateOf<List<AutocompletePrediction>>(
            emptyList()
        )
    }


    // ============================================================
    // ESTADO DE CARGA DEL BUSCADOR
    // ============================================================

    var isSearching by remember {
        mutableStateOf(false)
    }


    // ============================================================
    // INDICA SI LA UBICACIÓN ACTUAL FUE SELECCIONADA DE GOOGLE
    // ============================================================

    var locationSelected by remember {
        mutableStateOf(
            state.location.isNotBlank()
        )
    }


    // ============================================================
    // TOKEN DE SESIÓN DE GOOGLE PLACES
    // ============================================================

    var sessionToken by remember {
        mutableStateOf(
            AutocompleteSessionToken.newInstance()
        )
    }


    // ============================================================
    // BUSCAR PREDICCIONES EN GOOGLE PLACES
    // ============================================================

    LaunchedEffect(
        locationQuery,
        locationSelected
    ) {

        // Si ya seleccionamos una dirección válida,
        // dejamos de hacer búsquedas.
        if (locationSelected) {

            predictions = emptyList()
            isSearching = false

            return@LaunchedEffect
        }


        val query = locationQuery.trim()


        // Evitamos buscar con 0 o 1 caracteres.
        if (query.length < 2 || placesClient == null) {

            predictions = emptyList()
            isSearching = false

            return@LaunchedEffect
        }


        // Esperamos un poco antes de hacer la petición.
        // Así no llamamos a Google inmediatamente
        // con cada tecla que escribe el usuario.
        delay(350)


        isSearching = true


        val request =
            FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)

                // Solo mostrar lugares de Colombia.
                .setCountries(
                    listOf("CO")
                )

                // Ayuda a Google a formatear
                // correctamente los resultados.
                .setRegionCode("CO")

                // Agrupa las búsquedas de esta sesión.
                .setSessionToken(
                    sessionToken
                )

                .build()


        placesClient
            .findAutocompletePredictions(request)
            .addOnSuccessListener { response ->

                // Comprobamos que la respuesta todavía
                // pertenezca a lo que el usuario escribió.
                if (
                    query == locationQuery.trim() &&
                    !locationSelected
                ) {

                    predictions =
                        response.autocompletePredictions

                    isSearching = false
                }
            }
            .addOnFailureListener { exception ->

                if (
                    query == locationQuery.trim()
                ) {

                    predictions = emptyList()
                    isSearching = false
                }


                Log.e(
                    "Places",
                    "Error buscando ubicaciones",
                    exception
                )
            }
    }


    // ============================================================
    // SELECCIONAR UNA PREDICCIÓN
    // ============================================================

    fun selectPrediction(
        prediction: AutocompletePrediction
    ) {

        if (placesClient == null) return

        val placeFields = listOf(

            Place.Field.ID,

            Place.Field.FORMATTED_ADDRESS,

            Place.Field.LOCATION
        )


        val request =
            FetchPlaceRequest
                .builder(
                    prediction.placeId,
                    placeFields
                )
                .setSessionToken(
                    sessionToken
                )
                .setRegionCode("CO")
                .build()


        placesClient
            .fetchPlace(request)
            .addOnSuccessListener { response ->

                val place =
                    response.place


                val direccion =
                    place.formattedAddress
                        ?: prediction
                            .getFullText(null)
                            .toString()


                val ubicacion =
                    place.location


                // =================================================
                // MOSTRAR LA DIRECCIÓN SELECCIONADA
                // =================================================

                locationQuery =
                    direccion


                // Indicamos que esta dirección sí fue
                // seleccionada desde Google Places.
                locationSelected =
                    true


                // Cerramos la lista de predicciones.
                predictions =
                    emptyList()


                // Guardamos la dirección en el ViewModel.
                model.updateLocationFromPlaces(

                    nombreLugar =
                        prediction
                            .getPrimaryText(null)
                            .toString(),

                    direccion =
                        direccion,

                    placeId =
                        place.id
                            ?: prediction.placeId,

                    latitud =
                        ubicacion?.latitude
                            ?: 0.0,

                    longitud =
                        ubicacion?.longitude
                            ?: 0.0
                )


                // Quitamos cualquier error anterior.
                model.updateLocationError(
                    ""
                )


                // =================================================
                // MOSTRAR DATOS EN LOGCAT
                // =================================================

                Log.d(
                    "Places",
                    """
                    Lugar seleccionado correctamente:
                    
                    Nombre: ${
                        prediction
                            .getPrimaryText(null)
                    }
                    
                    Dirección: $direccion
                    
                    PlaceId: ${place.id}
                    
                    Latitud: ${ubicacion?.latitude}
                    
                    Longitud: ${ubicacion?.longitude}
                    """.trimIndent()
                )


                // La sesión terminó al seleccionar
                // un lugar, así que creamos un token nuevo
                // para una futura búsqueda.
                sessionToken =
                    AutocompleteSessionToken
                        .newInstance()
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "Places",
                    "Error obteniendo detalles del lugar",
                    exception
                )


                Toast.makeText(
                    context,
                    "No se pudo obtener la ubicación",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // ============================================================
    // CONTENIDO DE LA PANTALLA
    // ============================================================

    CreateRequestContent(

        state = state,


        // ========================================================
        // GOOGLE PLACES
        // ========================================================

        locationQuery =
            locationQuery,

        predictions =
            predictions,

        isSearching =
            isSearching,


        // ========================================================
        // USUARIO ESCRIBE EN EL BUSCADOR
        // ========================================================

        onLocationQueryChange = { newValue ->

            locationQuery =
                newValue


            // Si modifica el texto después de haber
            // seleccionado un lugar, esa ubicación
            // deja de ser válida.
            locationSelected =
                false


            // No guardamos texto libre como ubicación.
            // Solo se vuelve a guardar cuando selecciona
            // una predicción de Google.
            model.clearLocationSelection()


            model.updateLocationError(
                ""
            )
        },


        // ========================================================
        // USUARIO SELECCIONA UNA PREDICCIÓN
        // ========================================================

        onPredictionClick = { prediction ->

            selectPrediction(
                prediction
            )
        },


        // ========================================================
        // MATERIALES
        // ========================================================

        onToggleMaterial = {

            model.toggleMaterial(it)
        },


        // ========================================================
        // PESO
        // ========================================================

        onDecrementWeight = {

            model.decrementWeight()
        },

        onIncrementWeight = {

            model.incrementWeight()
        },


        // ========================================================
        // CALENDARIO
        // ========================================================

        onPreviousMonth = {

            model.previousMonth()
        },

        onNextMonth = {

            model.nextMonth()
        },

        onSelectDate = {

            model.selectDate(it)
        },


        // ========================================================
        // CREAR SOLICITUD
        // ========================================================

        onCreateClick = {


            // ====================================================
            // VALIDAR UBICACIÓN
            // ====================================================

            if (
                state.location.isBlank()
            ) {

                model.updateLocationError(
                    "Selecciona una dirección válida"
                )

            } else {

                model.updateLocationError(
                    ""
                )
            }


            // ====================================================
            // VALIDAR MATERIALES
            // ====================================================

            if (
                state.selectedMaterials.isEmpty()
            ) {

                model.updateMaterialsError(
                    "Selecciona al menos un material"
                )

            } else {

                model.updateMaterialsError(
                    ""
                )
            }


            // ====================================================
            // CREAR SOLICITUD SI TO DO ES VÁLIDO
            // ====================================================

            if (
                state.location.isNotBlank() &&
                state.selectedMaterials.isNotEmpty()
            ) {

                model.crearSolicitud(

                    onSuccess = { solicitudId ->


                        Toast.makeText(
                            context,
                            "Solicitud creada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()


                        model.resetForm()


                        controller.popBackStack()


                        controller.navigate(
                            "${AppScreens.confirmacionSolicitud.name}/$solicitudId"
                        )
                    },


                    onError = { mensaje ->


                        Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG
                        ).show()


                        controller.navigate(
                            AppScreens
                                .rechazoSolicitud
                                .name
                        )
                    }
                )
            }
        },


        // ========================================================
        // VOLVER
        // ========================================================

        onBackClick = {

            controller.popBackStack()
        }
    )
}


// ============================================================
// CONTENIDO VISUAL
// ============================================================

@Composable
fun CreateRequestContent(

    state: SolicitudState,

    locationQuery: String,

    predictions: List<AutocompletePrediction>,

    isSearching: Boolean,

    onLocationQueryChange: (String) -> Unit,

    onPredictionClick: (AutocompletePrediction) -> Unit,

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
            .verticalScroll(
                rememberScrollState()
            )
            .background(
                BackgroundColor
            )
            .padding(
                horizontal = 20.dp
            )
    ) {


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // ====================================================
        // HEADER: flecha + título
        // ====================================================

        Box(
            modifier =
                Modifier.fillMaxWidth()
        ) {


            Image(
                painter =
                    painterResource(
                        id = R.drawable.arrow_back
                    ),

                contentDescription =
                    "Volver",

                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .size(24.dp)
                    .clickable {

                        onBackClick()
                    }
            )


            Text(
                text =
                    "Nueva solicitud de\nrecolección",

                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .fillMaxWidth(),

                color =
                    DarkGreen,

                fontSize =
                    24.sp,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // UBICACIÓN
        // ====================================================

        OutlinedTextField(

            value =
                locationQuery,


            onValueChange = {

                onLocationQueryChange(
                    it
                )
            },


            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),


            singleLine =
                true,


            leadingIcon = {

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable.pin_location
                        ),

                    contentDescription =
                        "Ubicación",

                    modifier =
                        Modifier.size(16.dp)
                )
            },


            // =================================================
            // ICONO DE CARGA
            // =================================================

            trailingIcon = {

                if (
                    isSearching
                ) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.size(18.dp),

                        strokeWidth =
                            2.dp,

                        color =
                            MainGreen
                    )
                }
            },


            placeholder = {

                Text(
                    text =
                        "Busca una dirección o lugar",

                    color =
                        TextMuted,

                    fontSize =
                        14.sp
                )
            },


            isError =
                state.locationError
                    .isNotEmpty(),


            shape =
                RoundedCornerShape(50),


            colors =
                OutlinedTextFieldDefaults
                    .colors(

                        focusedBorderColor =
                            Color.Transparent,

                        unfocusedBorderColor =
                            Color.Transparent,

                        focusedContainerColor =
                            CardWhite,

                        unfocusedContainerColor =
                            CardWhite
                    )
        )


        // ====================================================
        // PREDICCIONES DE GOOGLE PLACES
        // ====================================================

        if (
            predictions.isNotEmpty()
        ) {


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()

                    // El contenido de Google queda
                    // visualmente separado del resto.
                    .background(
                        color =
                            CardWhite,

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )

                    .border(
                        width =
                            1.dp,

                        color =
                            Color(
                                0xFFE5E5E5
                            ),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )

                    .padding(
                        vertical =
                            4.dp
                    )
            ) {


                predictions
                    .forEachIndexed {
                            index,
                            prediction ->


                        // =========================================
                        // UNA PREDICCIÓN
                        // =========================================

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    onPredictionClick(
                                        prediction
                                    )
                                }
                                .padding(
                                    horizontal =
                                        16.dp,

                                    vertical =
                                        10.dp
                                )
                        ) {


                            // =====================================
                            // NOMBRE PRINCIPAL
                            // =====================================

                            Text(
                                text =
                                    prediction
                                        .getPrimaryText(
                                            null
                                        )
                                        .toString(),

                                color =
                                    DarkGreen,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )


                            // =====================================
                            // DIRECCIÓN / INFORMACIÓN SECUNDARIA
                            // =====================================

                            val secondaryText =
                                prediction
                                    .getSecondaryText(
                                        null
                                    )
                                    .toString()


                            if (
                                secondaryText.isNotBlank()
                            ) {


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            2.dp
                                        )
                                )


                                Text(
                                    text =
                                        secondaryText,

                                    color =
                                        TextMuted,

                                    fontSize =
                                        12.sp
                                )
                            }
                        }


                        // =========================================
                        // SEPARADOR
                        // =========================================

                        if (
                            index <
                            predictions.lastIndex
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        Color(
                                            0xFFF0F0F0
                                        )
                                    )
                            )
                        }
                    }


                // =================================================
                // ATRIBUCIÓN GOOGLE MAPS
                // =================================================

                Text(
                    text =
                        "Google Maps",

                    modifier = Modifier
                        .align(
                            Alignment.End
                        )
                        .padding(
                            end =
                                12.dp,

                            top =
                                6.dp,

                            bottom =
                                6.dp
                        ),

                    color =
                        Color(
                            0xFF5E5E5E
                        ),

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Normal
                )
            }
        }


        // ====================================================
        // ERROR DE UBICACIÓN
        // ====================================================

        if (
            state.locationError
                .isNotEmpty()
        ) {


            Text(
                text =
                    state.locationError,

                color =
                    Color.Red,

                fontSize =
                    12.sp,

                modifier =
                    Modifier.padding(
                        start =
                            16.dp,

                        top =
                            4.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        // ====================================================
        // MATERIALES A ENTREGAR
        // ====================================================

        SectionTitle(
            text =
                "Materiales a entregar"
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    CardWhite,
                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )
                .padding(8.dp)
        ) {


            val leftColumn =
                listOf(

                    MaterialType.PLASTICO,

                    MaterialType.PAPEL,

                    MaterialType.CARTON,

                    MaterialType.VIDRIO
                )


            val rightColumn =
                listOf(

                    MaterialType.METAL,

                    MaterialType.ELECTRONICOS,

                    MaterialType.MADERA,

                    MaterialType.OTROS
                )


            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {


                    leftColumn
                        .forEach { material ->


                            MaterialCheckboxRow(

                                material =
                                    material,

                                checked =
                                    material in
                                            state.selectedMaterials,

                                onCheckedChange = {

                                    onToggleMaterial(
                                        material
                                    )
                                }
                            )
                        }
                }


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {


                    rightColumn
                        .forEach { material ->


                            MaterialCheckboxRow(

                                material =
                                    material,

                                checked =
                                    material in
                                            state.selectedMaterials,

                                onCheckedChange = {

                                    onToggleMaterial(
                                        material
                                    )
                                }
                            )
                        }
                }
            }
        }


        // ====================================================
        // ERROR DE MATERIALES
        // ====================================================

        if (
            state.materialsError
                .isNotEmpty()
        ) {


            Text(
                text =
                    state.materialsError,

                color =
                    Color.Red,

                fontSize =
                    12.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // PESO ESTIMADO
        // ====================================================

        SectionTitle(
            text =
                "Peso estimado"
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    CardWhite,
                    shape =
                        RoundedCornerShape(
                            50
                        )
                ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.Center
        ) {


            // =================================================
            // RESTAR PESO
            // =================================================

            Text(
                text = "-",

                fontSize =
                    22.sp,

                color =
                    DarkGreen,

                modifier = Modifier
                    .clickable {

                        onDecrementWeight()
                    }
                    .padding(
                        horizontal =
                            24.dp
                    )
            )


            // =================================================
            // PESO
            // =================================================

            Box(
                modifier = Modifier
                    .background(
                        Color(
                            0xFFEFEAE0
                        ),

                        shape =
                            RoundedCornerShape(
                                50
                            )
                    )
                    .padding(
                        horizontal =
                            20.dp,

                        vertical =
                            3.dp
                    )
            ) {


                Text(
                    text =
                        "${state.weightKg} Kg",

                    fontSize =
                        14.sp,

                    color =
                        DarkGreen
                )
            }


            // =================================================
            // SUMAR PESO
            // =================================================

            Text(
                text = "+",

                fontSize =
                    22.sp,

                color =
                    DarkGreen,

                modifier = Modifier
                    .clickable {

                        onIncrementWeight()
                    }
                    .padding(
                        horizontal =
                            24.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // ====================================================
        // FECHA
        // ====================================================

        SectionTitle(
            text =
                "Fecha"
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        CalendarCard(

            state =
                state,

            onPreviousMonth =
                onPreviousMonth,

            onNextMonth =
                onNextMonth,

            onSelectDate =
                onSelectDate
        )


        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        // ====================================================
        // BOTÓN SOLICITAR
        // ====================================================

        Button(

            onClick =
                onCreateClick,


            enabled =
                !state.isLoading,


            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),


            shape =
                RoundedCornerShape(
                    50
                ),


            colors =
                ButtonDefaults
                    .buttonColors(
                        containerColor =
                            MainGreen
                    )
        ) {


            if (
                state.isLoading
            ) {


                CircularProgressIndicator(

                    modifier =
                        Modifier.size(
                            22.dp
                        ),

                    color =
                        Color.White,

                    strokeWidth =
                        2.dp
                )


            } else {


                Text(
                    text =
                        "Solicitar",

                    color =
                        Color.White,

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )
    }
}


// ============================================================
// TÍTULO DE SECCIÓN
// ============================================================

@Composable
private fun SectionTitle(
    text: String
) {


    Text(
        text =
            text,

        color =
            DarkGreen,

        fontSize =
            18.sp,

        fontWeight =
            FontWeight.SemiBold
    )
}


// ============================================================
// FILA DE CHECKBOX DE MATERIAL
// ============================================================

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
private fun MaterialCheckboxRow(

    material: MaterialType,

    checked: Boolean,

    onCheckedChange: (Boolean) -> Unit
) {


    Row(
        verticalAlignment =
            Alignment.CenterVertically,

        modifier = Modifier
            .clickable {

                onCheckedChange(
                    !checked
                )
            }
            .padding(
                vertical =
                    4.dp
            )
    ) {


        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement
                    provides false
        ) {


            Checkbox(

                checked =
                    checked,

                onCheckedChange =
                    onCheckedChange,

                modifier =
                    Modifier.size(
                        24.dp
                    ),

                colors =
                    CheckboxDefaults
                        .colors(
                            checkedColor =
                                MainGreen
                        )
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    6.dp
                )
        )


        Text(
            text =
                material.label,

            color =
                DarkGreen,

            fontSize =
                14.sp
        )
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
            .background(
                CardWhite,
                shape =
                    RoundedCornerShape(
                        16.dp
                    )
            )
            .padding(
                5.dp
            )
    ) {


        // ====================================================
        // ENCABEZADO DEL MES
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Text(
                text = "<",

                fontSize =
                    18.sp,

                color =
                    DarkGreen,

                modifier = Modifier
                    .clickable {

                        onPreviousMonth()
                    }
                    .padding(
                        8.dp
                    )
            )


            Text(
                text =
                    monthYearLabel(
                        state.displayedMonth
                    ),

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    MainGreen
            )


            Text(
                text = ">",

                fontSize =
                    18.sp,

                color =
                    DarkGreen,

                modifier = Modifier
                    .clickable {

                        onNextMonth()
                    }
                    .padding(
                        8.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )


        // ====================================================
        // DÍAS DE LA SEMANA
        // ====================================================

        val weekdays =
            listOf(
                "LUN",
                "MAR",
                "MIE",
                "JUE",
                "VIE",
                "SAB",
                "DOM"
            )


        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {


            weekdays
                .forEach { day ->


                    Text(
                        text =
                            day,

                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        textAlign =
                            TextAlign.Center,

                        color =
                            WeekdayGreen,

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.Medium
                    )
                }
        }


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )


        // ====================================================
        // GRILLA DEL CALENDARIO
        // ====================================================

        val days =
            buildCalendarGrid(
                state.displayedMonth
            )


        val weeks =
            days.chunked(
                7
            )


        weeks
            .forEach { week ->


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {


                    week
                        .forEach { date ->


                            Box(
                                modifier = Modifier
                                    .weight(
                                        1f
                                    )
                                    .padding(
                                        8.dp
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {


                                if (
                                    date != null
                                ) {


                                    val isSelected =
                                        date ==
                                                state.selectedDate


                                    val isWeekend =
                                        date
                                            .dayOfWeek
                                            .value >= 6


                                    val isCurrentMonth =
                                        date.month ==
                                                state
                                                    .displayedMonth
                                                    .month


                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()

                                            .let {

                                                if (
                                                    isSelected
                                                ) {

                                                    it.background(
                                                        MainGreen,
                                                        shape =
                                                            CircleShape
                                                    )

                                                } else {

                                                    it
                                                }
                                            }

                                            .clickable {

                                                onSelectDate(
                                                    date
                                                )
                                            },

                                        contentAlignment =
                                            Alignment.Center
                                    ) {


                                        Text(
                                            text =
                                                date
                                                    .dayOfMonth
                                                    .toString(),

                                            color =
                                                when {

                                                    isSelected ->

                                                        Color.White


                                                    !isCurrentMonth ->

                                                        PastMonthGray


                                                    isWeekend ->

                                                        Color(
                                                            0xFFD98555
                                                        )


                                                    else ->

                                                        DarkGreen
                                                },

                                            fontSize =
                                                13.sp
                                        )
                                    }
                                }
                            }
                        }
                }
            }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true
)
@Composable
fun CreateRequestScreenPreview() {


    RebottleTheme {


        val previewState =
            SolicitudState(

                location =
                    "Cra 15 # 136-32",

                selectedMaterials =
                    setOf(
                        MaterialType.PLASTICO,
                        MaterialType.VIDRIO
                    ),

                weightKg =
                    15
            )


        CreateRequestContent(

            state =
                previewState,


            locationQuery =
                previewState.location,


            predictions =
                emptyList(),


            isSearching =
                false,


            onLocationQueryChange = {},


            onPredictionClick = {},


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