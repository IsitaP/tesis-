package com.rebottle.app.ui.screens.usuario

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import com.rebottle.app.ui.theme.RebottleColors


private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val LightGreen = RebottleColors.LightGreen
private val BorderGreen = RebottleColors.BorderGreen
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText


@Composable
fun ProfileUserScreen(
    controller: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // ------------------------------------------------
    // DATOS CARGADOS DE FIRESTORE
    // ------------------------------------------------

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    nombre = doc.getString("name") ?: ""
                    direccion = doc.getString("direccion") ?: ""
                    telefono = doc.getString("telefono") ?: ""

                    val fotoBase64 = doc.getString("fotoBase64")
                    if (!fotoBase64.isNullOrEmpty()) {
                        selectedImageBitmap = base64ToImageBitmap(fotoBase64)
                    }
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        }
    }

    // ------------------------------------------------
    // SELECTOR DE FOTO
    // ------------------------------------------------

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && uid != null) {
            val bitmap = runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()

            if (bitmap != null) {
                val redimensionado = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
                selectedImageBitmap = redimensionado.asImageBitmap()

                val base64 = imageBitmapToBase64(redimensionado)

                Firebase.firestore.collection("users").document(uid)
                    .update("fotoBase64", base64)
            }
        }
    }

    val openPhotoPicker = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            ReBottleUsuarioProfileBottomBar(controller = controller)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileUsuarioHeader(
                selectedImageBitmap = selectedImageBitmap,
                onTap = { openPhotoPicker() }
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (cargando) {
                CircularProgressIndicator(color = MainGreen, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = nombre.ifEmpty { "Usuario" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )

                if (direccion.isNotEmpty()) {
                    Text(text = direccion, fontSize = 14.sp, color = SecondaryText)
                }
                if (telefono.isNotEmpty()) {
                    Text(text = telefono, fontSize = 14.sp, color = SecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    controller.navigate(AppScreens.editarPerfilUsuario.name)
                },
                modifier = Modifier
                    .width(230.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(width = 1.dp, color = MainGreen)
            ) {
                Text(text = "Editar perfil", fontSize = 14.sp, color = MainGreen)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 27.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Impacto", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MainText)
                    Text(text = "Ver más →", fontSize = 14.sp, color = MainGreen)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ProfileUsuarioSummaryCard()

                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }
}


// ----------------------------------------------------
// HEADER — foto tocable (editar) y sostenible (ver grande, tipo IG)
// ----------------------------------------------------

@Composable
private fun ProfileUsuarioHeader(
    selectedImageBitmap: ImageBitmap?,
    onTap: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mostrarFotoGrande by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.fundacion_profile_header),
            contentDescription = "Fondo perfil Usuario",
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .border(width = 10.dp, color = BackgroundColor, shape = CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            var fueSostenido = false

                            val job = coroutineScope.launch {
                                kotlinx.coroutines.delay(350)
                                fueSostenido = true
                                mostrarFotoGrande = true
                            }

                            tryAwaitRelease()
                            job.cancel()
                            mostrarFotoGrande = false

                            if (!fueSostenido) {
                                onTap()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            FotoPerfilUsuario(selectedImageBitmap)
        }
    }

    // ------------------------------------------------
    // VISTA GRANDE (se muestra mientras se sostiene)
    // ------------------------------------------------

    if (mostrarFotoGrande) {
        Dialog(
            onDismissRequest = { mostrarFotoGrande = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
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
                        painter = painterResource(id = R.drawable.usuario_perfil),
                        contentDescription = "Foto de perfil ampliada",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun FotoPerfilUsuario(selectedImageBitmap: ImageBitmap?) {
    if (selectedImageBitmap != null) {
        Image(
            bitmap = selectedImageBitmap,
            contentDescription = "Foto seleccionada",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.usuario_perfil),
            contentDescription = "Foto de Usuario",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


// ----------------------------------------------------
// CARD RESUMEN DE IMPACTO (sin cambios)
// ----------------------------------------------------

@Composable
private fun ProfileUsuarioSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(width = 1.dp, color = BorderGreen),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.fundacion_summary),
                    contentDescription = "Gráfico de impacto",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "8 Kg", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = DarkGreen)
                    Text(text = "Plástico", fontSize = 14.sp, color = SecondaryText)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "13,5%", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = DarkGreen)
                    Text(text = "Reciclaje", fontSize = 14.sp, color = SecondaryText)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "20 recolecciones realizadas", fontSize = 15.sp, color = SecondaryText)
        }
    }
}


// ----------------------------------------------------
// BARRA INFERIOR (igual que ya quedó corregida)
// ----------------------------------------------------

@Composable
private fun ReBottleUsuarioProfileBottomBar(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier.fillMaxWidth().background(BackgroundColor).padding(top = 3.dp)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = currentRoute == AppScreens.homeCiudadano.name,
                onClick = {
                    if (currentRoute != AppScreens.homeCiudadano.name) {
                        controller.navigate(AppScreens.homeCiudadano.name)
                    }
                },
                icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = "Inicio", modifier = Modifier.size(26.dp)) },
                label = { Text(text = "Inicio", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen, selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText, unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = currentRoute == AppScreens.solicitudesUsuario.name,
                onClick = {
                    if (currentRoute != AppScreens.solicitudesUsuario.name) {
                        controller.navigate(AppScreens.solicitudesUsuario.name)
                    }
                },
                icon = { Image(painter = painterResource(id = R.drawable.icon_collection), contentDescription = "Recolecciones", modifier = Modifier.size(25.dp)) },
                label = { Text(text = "Recolecciones", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MainGreen, unselectedTextColor = SecondaryText, indicatorColor = Color.Transparent)
            )
            NavigationBarItem(
                selected = false,
                onClick = { /* TODO: recompensas */ },
                icon = { Image(painter = painterResource(id = R.drawable.icon_routes), contentDescription = "Recompensas", modifier = Modifier.size(25.dp)) },
                label = { Text(text = "Recompensas", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MainGreen, unselectedTextColor = SecondaryText, indicatorColor = Color.Transparent)
            )
            NavigationBarItem(
                selected = currentRoute == AppScreens.perfilUsuario.name,
                onClick = {
                    if (currentRoute != AppScreens.perfilUsuario.name) {
                        controller.navigate(AppScreens.perfilUsuario.name)
                    }
                },
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Perfil", modifier = Modifier.size(26.dp)) },
                label = { Text(text = "Perfil", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen, selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText, unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


// ----------------------------------------------------
// UTILIDADES BASE64 (compresión + conversión)
// ----------------------------------------------------

private fun imageBitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
    val bytes = stream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

private fun base64ToImageBitmap(base64: String): ImageBitmap? {
    return runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    }.getOrNull()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileUsuarioScreenPreview() {
    val controller = rememberNavController()
    RebottleTheme {
        ProfileUserScreen(controller = controller)
    }
}