package com.rebottle.app.ui.screens.fundacion

import android.graphics.BitmapFactory

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

import androidx.annotation.DrawableRes

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val BackgroundColor = RebottleColors.Background

private val MainGreen = RebottleColors.MainGreen

private val DarkGreen = RebottleColors.DarkGreen

private val LightGreen = RebottleColors.LightGreen

private val BorderGreen = RebottleColors.BorderGreen

private val MainText = RebottleColors.MainText

private val SecondaryText = RebottleColors.SecondaryText

@Composable
fun ProfileFundacionScreen(
    controller: NavController
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var nombre by remember { mutableStateOf("") }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var subiendoFoto by remember { mutableStateOf(false) }

    // ------------------------------------------------
    // CARGAR DATOS + FOTO DESDE STORAGE
    // ------------------------------------------------

    LaunchedEffect(uid) {
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    nombre = doc.getString("name") ?: ""
                    cargando = false

                    val fotoUrl = doc.getString("fotoUrl")
                    if (!fotoUrl.isNullOrEmpty()) {
                        runCatching {
                            Firebase.storage.getReferenceFromUrl(fotoUrl)
                        }.getOrNull()?.getBytes(2 * 1024 * 1024)
                            ?.addOnSuccessListener { bytes ->
                                selectedImageBitmap = BitmapFactory
                                    .decodeByteArray(bytes, 0, bytes.size)
                                    .asImageBitmap()
                            }
                    }
                }
                .addOnFailureListener { cargando = false }
        }
    }

    // ------------------------------------------------
    // SELECTOR + SUBIDA DE FOTO A STORAGE
    // ------------------------------------------------

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && uid != null) {
            val bitmap = runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()

            if (bitmap != null) {
                selectedImageBitmap = bitmap.asImageBitmap()
                subiendoFoto = true

                val stream = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                val data = stream.toByteArray()

                val storageRef = Firebase.storage.reference.child("perfil_fotos/$uid.jpg")

                storageRef.putBytes(data)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { url ->
                            Firebase.firestore.collection("users").document(uid)
                                .update("fotoUrl", url.toString())
                            subiendoFoto = false
                        }
                    }
                    .addOnFailureListener {
                        subiendoFoto = false
                    }
            }
        }
    }

    val openPhotoPicker = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    // ------------------------------------------------
    // SCAFFOLD
    // ------------------------------------------------

    Scaffold(

        modifier =
            Modifier.fillMaxSize(),

        containerColor =
            BackgroundColor,

        bottomBar = {

            ReBottleProfileBottomBar(

                controller = controller
            )
        }

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {


            // ------------------------------------------------
            // CABECERA
            // ------------------------------------------------

            ProfileHeader(
                selectedImageBitmap = selectedImageBitmap,
                subiendoFoto = subiendoFoto,
                onPhotoClick = { openPhotoPicker() }
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            // ------------------------------------------------
            // NOMBRE FUNDACIÓN
            // ------------------------------------------------

            if (cargando) {
                CircularProgressIndicator(color = MainGreen, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = nombre.ifEmpty { "Fundación" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
                Text(
                    text = "Sede principal",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ------------------------------------------------
            // EDITAR PERFIL
            // ------------------------------------------------

            OutlinedButton(

                onClick = {
                    controller.navigate(AppScreens.editarPerfilFundacion.name)
                },

                modifier = Modifier
                    .width(230.dp)
                    .height(36.dp),

                shape =
                    RoundedCornerShape(50.dp),

                border = BorderStroke(
                    width = 1.dp,
                    color = MainGreen
                )

            ) {

                Text(
                    text = "Editar perfil",

                    fontSize = 14.sp,

                    color = MainGreen
                )
            }

            // CERRAR SESIÓN

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    controller.navigate(AppScreens.login.name) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.width(230.dp).height(36.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(width = 1.dp, color = MainGreen)
            ) {
                Text(text = "Cerrar sesión", fontSize = 14.sp, color = MainGreen)
            }

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            // ------------------------------------------------
            // CONTENIDO
            // ------------------------------------------------

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 27.dp
                    )

            ) {


                // ------------------------------------------------
                // TÍTULO RESUMEN
                // ------------------------------------------------

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(
                        text = "Resumen",

                        fontSize = 22.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color = MainText
                    )


                    Text(
                        text = "Ver más →",

                        fontSize = 14.sp,

                        color = MainGreen
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                // ------------------------------------------------
                // CARD RESUMEN
                // ------------------------------------------------

                ProfileSummaryCard()


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )
                // ------------------------------------------------
// GESTIÓN
// ------------------------------------------------

                Text(
                    text = "Gestión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(
                    modifier = Modifier.height(9.dp)
                )


// ------------------------------------------------
// ADMINISTRAR SOLICITUDES
// ------------------------------------------------

                Button(
                    onClick = {

                        // Va a ManageRequestsScreen
                        //onManageRequestsClick()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),

                    shape = RoundedCornerShape(50.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    )
                ) {

                    Text(
                        text = "Administrar solicitudes",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


// ------------------------------------------------
// ADMINISTRAR RECOLECTORES
// ------------------------------------------------

                Button(
                    onClick = {

                        // Va a ManageCollectorsScreen
                        //onManageCollectorsClick()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),

                    shape = RoundedCornerShape(50.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainGreen
                    )
                ) {

                    Text(
                        text = "Administrar recolectores",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(25.dp)
                )
            }
        }
    }
}


// ----------------------------------------------------
// HEADER PERFIL
// ----------------------------------------------------

@Composable
private fun ProfileHeader(
    selectedImageBitmap: ImageBitmap?,
    subiendoFoto: Boolean,
    onPhotoClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mostrarFotoGrande by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().height(215.dp)
    ) {

        Image(
            painter = painterResource(id = R.drawable.fundacion_profile_header),
            contentDescription = "Fondo perfil Fundación",
            modifier = Modifier.fillMaxWidth().height(175.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .border(width = 2.dp, color = BackgroundColor, shape = CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            var fueSostenido = false
                            val job = coroutineScope.launch {
                                delay(350)
                                fueSostenido = true
                                mostrarFotoGrande = true
                            }
                            tryAwaitRelease()
                            job.cancel()
                            mostrarFotoGrande = false
                            if (!fueSostenido) {
                                onPhotoClick()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {

            if (selectedImageBitmap != null) {
                Image(
                    bitmap = selectedImageBitmap,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.fundacion_profile),
                    contentDescription = "Foto de Fundación",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (subiendoFoto) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }
    }

    if (mostrarFotoGrande) {
        Dialog(
            onDismissRequest = { mostrarFotoGrande = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageBitmap != null) {
                    Image(
                        bitmap = selectedImageBitmap,
                        contentDescription = "Foto de perfil ampliada",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.fundacion_profile),
                        contentDescription = "Foto de perfil ampliada",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}


// ----------------------------------------------------
// CARD RESUMEN
// ----------------------------------------------------

@Composable
private fun ProfileSummaryCard() {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),

        shape =
            RoundedCornerShape(13.dp),

        border =
            BorderStroke(
                width = 1.dp,
                color = BorderGreen
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    LightGreen
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )

    ) {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
                )

        ) {


            // ------------------------------------------------
            // PARTE SUPERIOR
            // ------------------------------------------------

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {


                // ------------------------------------------------
                // GRÁFICO
                // ------------------------------------------------

                Image(

                    painter =
                        painterResource(
                            id =
                                R.drawable.fundacion_summary
                        ),

                    contentDescription =
                        "Resumen de reciclaje",

                    modifier =
                        Modifier.size(100.dp),

                    contentScale =
                        ContentScale.Fit
                )


                Spacer(
                    modifier =
                        Modifier.width(20.dp)
                )


                // ------------------------------------------------
                // PLÁSTICO
                // ------------------------------------------------

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally

                ) {

                    Text(
                        text = "8 Kg",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color = DarkGreen
                    )


                    Text(
                        text = "Plástico",

                        fontSize = 14.sp,

                        color =
                            SecondaryText
                    )
                }


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )


                // ------------------------------------------------
                // RECICLAJE
                // ------------------------------------------------

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally

                ) {

                    Text(
                        text = "13,5%",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color = DarkGreen
                    )


                    Text(
                        text = "Reciclaje",

                        fontSize = 14.sp,

                        color =
                            SecondaryText
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // ------------------------------------------------
            // DATOS
            // ------------------------------------------------

            Text(

                text =
                    "100 Kg de material recolectado este mes\n" +
                            "20 recolecciones realizadas\n" +
                            "50 recolectores activos",

                fontSize = 16.sp,

                lineHeight = 22.sp,

                color =
                    SecondaryText
            )
        }
    }
}


// ----------------------------------------------------
// ICONO PERSONALIZADO
// ----------------------------------------------------

@Composable
private fun BottomNavIcon(

    @DrawableRes iconRes: Int,

    contentDescription: String,

    size: Dp = 25.dp

) {

    Image(

        painter =
            painterResource(
                id = iconRes
            ),

        contentDescription =
            contentDescription,

        modifier =
            Modifier.size(size)
    )
}


// ----------------------------------------------------
// BARRA INFERIOR DEL PERFIL
// ----------------------------------------------------

@Composable
private fun ReBottleProfileBottomBar(

    controller: NavController

) {

    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(

        modifier = Modifier
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


            // ------------------------------------------------
            // INICIO
            // ------------------------------------------------

            NavigationBarItem(

                selected = currentRoute == AppScreens.homeFundacion.name,
                onClick = {
                    if (currentRoute != AppScreens.homeFundacion.name) {
                        controller.navigate(AppScreens.homeFundacion.name)
                    }
                },

                icon = {

                    Icon(

                        imageVector =
                            Icons.Outlined.Home,

                        contentDescription =
                            "Inicio",

                        modifier =
                            Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(
                        text = "Inicio",
                        fontSize = 12.sp
                    )
                },

                colors =
                    NavigationBarItemDefaults
                        .colors(

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
            )


            // ------------------------------------------------
            // SOLICITUDES
            // ------------------------------------------------

            NavigationBarItem(

                selected = currentRoute == AppScreens.solicitudesUsuario.name,
                onClick = {
                    controller.navigate(AppScreens.solicitudes.name)
                },

                icon = {

                    BottomNavIcon(

                        iconRes =
                            R.drawable
                                .icon_collection,

                        contentDescription =
                            "Solicitudes"
                    )
                },

                label = {

                    Text(
                        text = "Solicitudes",
                        fontSize = 12.sp
                    )
                },

                colors =
                    NavigationBarItemDefaults
                        .colors(

                            selectedTextColor =
                                MainGreen,

                            unselectedTextColor =
                                SecondaryText,

                            indicatorColor =
                                Color.Transparent
                        )
            )


            // ------------------------------------------------
            // RUTAS
            // ------------------------------------------------

            NavigationBarItem(

                selected = currentRoute == AppScreens.rutas.name,
                onClick = {
                    // TODO: descomentar cuando exista la pantalla de rutas
                    // controller.navigate(AppScreens.rutas.name)
                },

                icon = {

                    BottomNavIcon(

                        iconRes =
                            R.drawable
                                .icon_routes,

                        contentDescription =
                            "Rutas",

                        size = 42.dp
                    )
                },

                label = {

                    Text(

                        text = "Rutas",

                        fontSize = 12.sp,

                        modifier =
                            Modifier.offset(
                                y = (-4).dp
                            )
                    )
                },

                colors =
                    NavigationBarItemDefaults
                        .colors(

                            selectedTextColor =
                                MainGreen,

                            unselectedTextColor =
                                SecondaryText,

                            indicatorColor =
                                Color.Transparent
                        )
            )


            // ------------------------------------------------
            // PERFIL
            // ------------------------------------------------

            NavigationBarItem(

                selected = currentRoute == AppScreens.perfil.name,
                onClick = {
                    controller.navigate(AppScreens.perfil.name)
                },

                icon = {

                    Icon(

                        imageVector =
                            Icons.Outlined.Person,

                        contentDescription =
                            "Perfil",

                        modifier =
                            Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(
                        text = "Perfil",
                        fontSize = 12.sp
                    )
                },

                colors =
                    NavigationBarItemDefaults
                        .colors(

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
            )
        }
    }
}


// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ProfileFundacionScreenPreview() {

    val controller =
        rememberNavController()


    RebottleTheme {

        ProfileFundacionScreen(
            controller = controller
        )
    }
}