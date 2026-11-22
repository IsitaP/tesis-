package com.rebottle.app.ui.screens.fundacion.gestion

import android.graphics.BitmapFactory
import android.widget.Toast


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

import com.rebottle.app.R
import com.rebottle.app.model.AsignacionRutaState
import com.rebottle.app.model.Usuario
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.auth.UserRole
import com.rebottle.app.ui.theme.RebottleColors

import com.rebottle.app.model.MapaViewModel



// ============================================================
// COLORES
// ============================================================

private val BackgroundColor =
    RebottleColors.Background

private val MainGreen =
    RebottleColors.MainGreen

private val LightGreen =
    RebottleColors.LightGreen

private val MainText =
    RebottleColors.MainText

private val SecondaryText =
    RebottleColors.SecondaryText

private val BorderGreen =
    RebottleColors.BorderGreen

private val BrownIcon =
    Color(0xFFA55E37)


// ============================================================
// ASIGNAR RECOLECTOR
// ============================================================

@Composable
fun AsignarRecolectorScreen(
    controller: NavController,
    rutaId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {
        controller.popBackStack()
    },
    mapaViewModel: MapaViewModel = viewModel()
) {


    // ========================================================
    // ESTADO
    // ========================================================

    var recolectores by remember {

        mutableStateOf<List<Usuario>>(
            emptyList()
        )
    }


    var isLoading by remember {

        mutableStateOf(true)
    }


    var error by remember {

        mutableStateOf<String?>(null)
    }


    val context = LocalContext.current


    val asignacionState by mapaViewModel.asignacionState.collectAsState()



    LaunchedEffect(
        asignacionState.asignacionExitosa,
        asignacionState.error
    ) {


        // ========================================================
        // ASIGNACIÓN EXITOSA
        // ========================================================

        if (
            asignacionState
                .asignacionExitosa
        ) {


            Toast
                .makeText(
                    context,
                    "Recolector asignado correctamente",
                    Toast.LENGTH_SHORT
                )
                .show()


            mapaViewModel
                .limpiarEstadoAsignacion()


            // Volvemos a la lista de rutas
            controller.navigate(
                AppScreens.rutas.name
            ) {

                popUpTo(
                    AppScreens.rutas.name
                ) {
                    inclusive = false
                }

                launchSingleTop =
                    true
            }
        }


        // ========================================================
        // ERROR
        // ========================================================

        else if (
            asignacionState.error != null
        ) {


            Toast
                .makeText(
                    context,
                    asignacionState.error,
                    Toast.LENGTH_LONG
                )
                .show()


            mapaViewModel
                .limpiarEstadoAsignacion()
        }
    }

    // ========================================================
    // ESCUCHAR RECOLECTORES EN FIRESTORE
    // ========================================================

    DisposableEffect(Unit) {

        /*
         * Buscamos todos los usuarios cuyo rol sea recolector.
         *
         * Después filtramos por estado DISPONIBLE.
         *
         * Lo hacemos así para mantener sencilla la consulta
         * de Firestore.
         */

        val listener =
            Firebase
                .firestore
                .collection("users")
                .whereEqualTo(
                    "role",
                    UserRole.RECOLECTOR.firestoreValue
                )
                .addSnapshotListener {
                        snapshot,
                        exception ->


                    // ------------------------------------------
                    // ERROR
                    // ------------------------------------------

                    if (exception != null) {

                        isLoading = false

                        error =
                            exception.message
                                ?: "Error al cargar los recolectores"

                        return@addSnapshotListener
                    }


                    // ------------------------------------------
                    // SIN SNAPSHOT
                    // ------------------------------------------

                    if (snapshot == null) {

                        isLoading = false

                        recolectores =
                            emptyList()

                        return@addSnapshotListener
                    }


                    // ------------------------------------------
                    // CONVERTIR DOCUMENTOS A USUARIO
                    // ------------------------------------------

                    val usuarios =
                        snapshot.documents.map {
                                document ->


                            Usuario(

                                // UID REAL DE FIREBASE
                                id =
                                    document.id,


                                name =
                                    document.getString(
                                        "name"
                                    ) ?: "",


                                email =
                                    document.getString(
                                        "email"
                                    ) ?: "",


                                role =
                                    document.getString(
                                        "role"
                                    ) ?: "",


                                telefono =
                                    document.getString(
                                        "telefono"
                                    ) ?: "",


                                direccion =
                                    document.getString(
                                        "direccion"
                                    ) ?: "",


                                estado =
                                    document.getString(
                                        "estado"
                                    ) ?: "",


                                fotoUrl =
                                    document.getString(
                                        "fotoUrl"
                                    ) ?: ""
                            )
                        }


                    // ------------------------------------------
                    // SOLO DISPONIBLES
                    // ------------------------------------------

                    recolectores =
                        usuarios.filter {

                            it.estado.equals(
                                "DISPONIBLE",
                                ignoreCase = true
                            )
                        }


                    isLoading = false

                    error = null
                }


        // ====================================================
        // QUITAR LISTENER AL SALIR
        // ====================================================

        onDispose {

            listener.remove()
        }
    }


    // ========================================================
    // UI
    // ========================================================

    Scaffold(

        modifier =
            modifier.fillMaxSize(),

        containerColor =
            BackgroundColor,


        // ====================================================
        // HEADER
        // ====================================================

        topBar = {

            Header(
                rutaId =
                    rutaId,

                onBackClick =
                    onBackClick
            )
        },


        // ====================================================
        // BOTTOM BAR
        // ====================================================

        bottomBar = {

            ReBottleBottomBar(
                controller =
                    controller
            )
        }

    ) { innerPadding ->


        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .padding(
                        horizontal = 20.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // TÍTULO
            // =================================================

            Text(

                text =
                    "Recolectores disponibles",

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    MainText
            )


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(

                text =
                    "Selecciona el recolector que deseas asignar a la ruta $rutaId",

                fontSize =
                    14.sp,

                color =
                    SecondaryText
            )


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            // =================================================
            // CONTENIDO SEGÚN ESTADO
            // =================================================

            when {


                // =============================================
                // CARGANDO
                // =============================================

                isLoading -> {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator(

                            color =
                                MainGreen
                        )
                    }
                }


                // =============================================
                // ERROR
                // =============================================

                error != null -> {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text =
                                error
                                    ?: "Error al cargar los recolectores",

                            color =
                                Color.Red,

                            fontSize =
                                14.sp
                        )
                    }
                }


                // =============================================
                // NO HAY RECOLECTORES
                // =============================================

                recolectores.isEmpty() -> {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        contentAlignment =
                            Alignment.Center
                    ) {


                        Column(

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {


                            Text(

                                text =
                                    "No hay recolectores disponibles",

                                color =
                                    MainText,

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )


                            Spacer(

                                modifier =
                                    Modifier.height(
                                        6.dp
                                    )
                            )


                            Text(

                                text =
                                    "Cuando un recolector esté disponible aparecerá aquí.",

                                color =
                                    SecondaryText,

                                fontSize =
                                    14.sp
                            )
                        }
                    }
                }


                // =============================================
                // LISTA DE RECOLECTORES
                // =============================================

                else -> {

                    LazyColumn(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                20.dp
                            ),

                        contentPadding =
                            PaddingValues(
                                bottom = 20.dp
                            )
                    ) {


                        items(

                            items =
                                recolectores,

                            key = {
                                it.id
                            }

                        ) { recolector ->


                            CollectorCard(

                                collector =
                                    recolector,

                                rutaId =
                                    rutaId,

                                mapaViewModel = mapaViewModel,

                                asignacionState = asignacionState
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun Header(

    rutaId: String,

    onBackClick: () -> Unit

) {


    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 40.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 10.dp
                )
    ) {


        Row(

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // =================================================
            // VOLVER
            // =================================================

            Icon(

                imageVector =
                    Icons
                        .AutoMirrored
                        .Outlined
                        .ArrowBack,

                contentDescription =
                    "Volver",

                modifier =
                    Modifier
                        .size(
                            24.dp
                        )
                        .clickable {

                            onBackClick()
                        },

                tint =
                    MainText
            )


            Spacer(

                modifier =
                    Modifier.width(
                        16.dp
                    )
            )


            // =================================================
            // TÍTULO
            // =================================================

            Column {


                Text(

                    text =
                        "Asignar recolector",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MainGreen
                )


                Text(

                    text =
                        "Ruta $rutaId",

                    fontSize =
                        13.sp,

                    color =
                        SecondaryText
                )
            }
        }
    }
}


// ============================================================
// CARD DEL RECOLECTOR
// ============================================================

@Composable
private fun CollectorCard(

    collector: Usuario,

    rutaId: String,

    mapaViewModel: MapaViewModel,

    asignacionState: AsignacionRutaState

) {


    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        border =
            BorderStroke(
                1.dp,
                BorderGreen
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    LightGreen
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    4.dp
            )
    ) {


        Column(

            modifier =
                Modifier.padding(
                    24.dp
                )
        ) {


            // =================================================
            // NOMBRE + FOTO
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.Top
            ) {


                Column(

                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(
                                end = 12.dp
                            )
                ) {


                    Text(

                        text =
                            collector.name,

                        fontSize =
                            20.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            MainText,

                        modifier =
                            Modifier.padding(
                                top = 12.dp
                            )
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )


                    Text(

                        text =
                            "Recolector",

                        fontSize =
                            14.sp,

                        color =
                            SecondaryText
                    )
                }


                // =================================================
                // FOTO DE FIREBASE STORAGE
                // =================================================

                FotoRecolector(

                    fotoUrl =
                        collector.fotoUrl,

                    nombre =
                        collector.name
                )
            }


            Spacer(

                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // EMAIL
            // =================================================

            if (
                collector.email.isNotBlank()
            ) {


                CollectorDetailItem(

                    icon =
                        Icons
                            .Outlined
                            .Email,

                    text =
                        collector.email
                )
            }


            // =================================================
            // TELÉFONO
            // =================================================

            if (
                collector.telefono.isNotBlank()
            ) {


                CollectorDetailItem(

                    icon =
                        Icons
                            .Outlined
                            .Phone,

                    text =
                        collector.telefono
                )
            }


            // =================================================
            // DIRECCIÓN
            // =================================================

            if (
                collector.direccion.isNotBlank()
            ) {


                CollectorDetailItem(

                    icon =
                        Icons
                            .Outlined
                            .LocationOn,

                    text =
                        collector.direccion
                )
            }


            // =================================================
            // ESTADO
            // =================================================

            CollectorDetailItem(

                iconRes =
                    R.drawable.ic_check_circle,

                text =
                    "Estado: ${collector.estado}"
            )


            Spacer(

                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // =================================================
            // BOTÓN ASIGNAR
            // =================================================

            Box(

                modifier =
                    Modifier.fillMaxWidth(),

                contentAlignment =
                    Alignment.BottomEnd
            ) {


                Button(
                    onClick = {

                        mapaViewModel.asignarRecolectorARuta(
                            rutaId = rutaId,
                            recolectorId = collector.id
                        )
                    },

                    enabled = !asignacionState.isLoading,

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    )
                ) {

                    val asignandoEsteRecolector =
                        asignacionState.isLoading &&
                                asignacionState.recolectorId == collector.id

                    if (asignandoEsteRecolector) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Asignando...",
                            color = Color.White
                        )

                    } else {

                        Text(
                            text = "Asignar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                }
            }
        }
    }



// ============================================================
// FOTO DEL RECOLECTOR
// ============================================================

@Composable
private fun FotoRecolector(

    fotoUrl: String,

    nombre: String

) {


    // ========================================================
    // IMAGEN CARGADA
    // ========================================================

    var imageBitmap by remember(
        fotoUrl
    ) {

        mutableStateOf<ImageBitmap?>(
            null
        )
    }


    // ========================================================
    // CARGANDO FOTO
    // ========================================================

    var cargandoFoto by remember(
        fotoUrl
    ) {

        mutableStateOf(
            fotoUrl.isNotBlank()
        )
    }


    // ========================================================
    // DESCARGAR FOTO DESDE FIREBASE STORAGE
    // ========================================================

    LaunchedEffect(
        fotoUrl
    ) {


        // ----------------------------------------------------
        // SI NO TIENE FOTO
        // ----------------------------------------------------

        if (
            fotoUrl.isBlank()
        ) {

            imageBitmap = null

            cargandoFoto = false

            return@LaunchedEffect
        }


        try {


            val storageReference =

                FirebaseStorage
                    .getInstance()
                    .getReferenceFromUrl(
                        fotoUrl
                    )


            storageReference

                // Máximo 5 MB
                .getBytes(
                    5L * 1024 * 1024
                )

                .addOnSuccessListener {
                        bytes ->


                    val bitmap =

                        BitmapFactory
                            .decodeByteArray(
                                bytes,
                                0,
                                bytes.size
                            )


                    imageBitmap =
                        bitmap
                            ?.asImageBitmap()


                    cargandoFoto =
                        false
                }

                .addOnFailureListener {


                    imageBitmap =
                        null


                    cargandoFoto =
                        false
                }


        } catch (
            exception: Exception
        ) {


            imageBitmap =
                null


            cargandoFoto =
                false
        }
    }


    // ========================================================
    // CONTENEDOR DE LA FOTO
    // ========================================================

    Box(

        modifier =
            Modifier
                .size(
                    90.dp
                )
                .clip(
                    CircleShape
                ),

        contentAlignment =
            Alignment.Center
    ) {


        // ====================================================
        // FOTO DEL USUARIO
        // ====================================================

        if (
            imageBitmap != null
        ) {


            Image(

                bitmap =
                    imageBitmap!!,

                contentDescription =
                    "Foto de $nombre",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )


        } else {


            // =================================================
            // FOTO POR DEFECTO
            // =================================================

            Image(

                painter =
                    painterResource(

                        id =
                            R.drawable
                                .fundacion_profile
                    ),

                contentDescription =
                    "Foto de $nombre",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )
        }


        // ====================================================
        // LOADING DE FOTO
        // ====================================================

        if (
            cargandoFoto
        ) {


            Box(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.15f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {


                CircularProgressIndicator(

                    modifier =
                        Modifier.size(
                            24.dp
                        ),

                    color =
                        Color.White,

                    strokeWidth =
                        2.dp
                )
            }
        }
    }
}


// ============================================================
// DETALLE DEL RECOLECTOR
// ============================================================

@Composable
private fun CollectorDetailItem(

    icon:
    androidx.compose.ui.graphics.vector.ImageVector? =
        null,

    iconRes:
    Int? =
        null,

    text:
    String

) {


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 5.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        // ====================================================
        // ICONO
        // ====================================================

        Box(

            modifier =
                Modifier.size(
                    28.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {


            if (
                icon != null
            ) {


                Icon(

                    imageVector =
                        icon,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            24.dp
                        ),

                    tint =
                        BrownIcon
                )


            } else if (
                iconRes != null
            ) {


                Icon(

                    painter =
                        painterResource(
                            id =
                                iconRes
                        ),

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            24.dp
                        ),

                    tint =
                        BrownIcon
                )
            }
        }


        Spacer(

            modifier =
                Modifier.width(
                    16.dp
                )
        )


        // ====================================================
        // TEXTO
        // ====================================================

        Text(

            text =
                text,

            fontSize =
                16.sp,

            color =
                SecondaryText
        )
    }
}


// ============================================================
// BOTTOM BAR
// ============================================================

@Composable
private fun ReBottleBottomBar(

    controller: NavController

) {


    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    BackgroundColor
                )
                .padding(
                    top = 3.dp
                )
    ) {


        NavigationBar(

            modifier =
                Modifier.fillMaxWidth(),

            containerColor =
                Color.White,

            tonalElevation =
                0.dp
        ) {


            // =================================================
            // INICIO
            // =================================================

            NavigationBarItem(

                selected =
                    false,

                onClick = {


                    controller.navigate(
                        AppScreens
                            .homeFundacion
                            .name
                    )
                },

                icon = {


                    Icon(

                        imageVector =
                            Icons
                                .Outlined
                                .Home,

                        contentDescription =
                            "Inicio",

                        modifier =
                            Modifier.size(
                                26.dp
                            )
                    )
                },

                label = {


                    Text(

                        text =
                            "Inicio",

                        fontSize =
                            12.sp
                    )
                },

                colors =
                    navigationItemColors()
            )


            // =================================================
            // SOLICITUDES
            // =================================================

            NavigationBarItem(

                selected =
                    false,

                onClick = {


                    controller.navigate(
                        AppScreens
                            .solicitudesFundacion
                            .name
                    )
                },

                icon = {


                    Icon(

                        painter =
                            painterResource(

                                id =
                                    R.drawable
                                        .icon_collection
                            ),

                        contentDescription =
                            "Solicitudes",

                        modifier =
                            Modifier.size(
                                26.dp
                            )
                    )
                },

                label = {


                    Text(

                        text =
                            "Solicitudes",

                        fontSize =
                            12.sp
                    )
                },

                colors =
                    navigationItemColors()
            )


            // =================================================
            // RUTAS
            // =================================================

            NavigationBarItem(

                selected =
                    true,

                onClick = {


                    controller.navigate(
                        AppScreens
                            .rutas
                            .name
                    )
                },

                icon = {


                    Icon(

                        painter =
                            painterResource(

                                id =
                                    R.drawable
                                        .icon_routes
                            ),

                        contentDescription =
                            "Recolectores",

                        modifier =
                            Modifier.size(
                                42.dp
                            )
                    )
                },

                label = {


                    Text(

                        text =
                            "Recolectores",

                        fontSize =
                            12.sp,

                        modifier =
                            Modifier.offset(
                                y =
                                    (-4).dp
                            )
                    )
                },

                colors =
                    navigationItemColors()
            )


            // =================================================
            // PERFIL
            // =================================================

            NavigationBarItem(

                selected =
                    false,

                onClick = {


                    controller.navigate(
                        AppScreens
                            .perfil
                            .name
                    )
                },

                icon = {


                    Icon(

                        imageVector =
                            Icons
                                .Outlined
                                .Person,

                        contentDescription =
                            "Perfil",

                        modifier =
                            Modifier.size(
                                26.dp
                            )
                    )
                },

                label = {


                    Text(

                        text =
                            "Perfil",

                        fontSize =
                            12.sp
                    )
                },

                colors =
                    navigationItemColors()
            )
        }
    }
}


// ============================================================
// COLORES DEL BOTTOM BAR
// ============================================================

@Composable
private fun navigationItemColors() =

    NavigationBarItemDefaults.colors(

        selectedIconColor =
            MainGreen,

        selectedTextColor =
            MainGreen,

        unselectedIconColor =
            SecondaryText,

        unselectedTextColor =
            SecondaryText,

        indicatorColor =
            Color.Transparent
    )