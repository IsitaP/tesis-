package com.rebottle.app.ui.screens.usuario.inicio

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.rebottle.app.R
import com.rebottle.app.model.PerfilCache
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.model.cuentaComoAporte
import com.rebottle.app.ui.screens.usuario.gamificacion.MaterialPrincipal
import com.rebottle.app.ui.screens.usuario.gamificacion.formatKg
import com.rebottle.app.ui.screens.usuario.gamificacion.formatPorcentaje
import com.rebottle.app.ui.screens.usuario.gamificacion.materialPrincipal
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = Color(0xFF394C31)
private val LightGreen = Color(0xFFF1F7E9)
private val BorderGreen = Color(0xFFD5E3CD)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)


@Composable
fun ProfileUserScreen(
    controller: NavController,
    solicitudViewModel: SolicitudViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val nombre = PerfilCache.nombre ?: ""
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    val selectedImageBitmap = PerfilCache.foto
    var cargando by remember { mutableStateOf(true) }
    var subiendoFoto by remember { mutableStateOf(false) }

    // Recolecciones reales: mismas que en Solicitudes (sin canceladas)
    val solicitudState by solicitudViewModel.state.collectAsState()
    val totalRecolecciones = solicitudState.solicitudes.count { it.cuentaComoAporte() }
    val principal = remember(solicitudState.solicitudes) {
        materialPrincipal(solicitudState.solicitudes)
    }

    LaunchedEffect(uid) {
        if (uid != null) {
            PerfilCache.cargar(uid)
            solicitudViewModel.escucharSolicitudesUsuario()

            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    direccion = doc.getString("direccion") ?: ""
                    telefono = doc.getString("telefono") ?: ""
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && uid != null) {
            val bitmap = runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()

            if (bitmap != null) {
                val redimensionado = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                PerfilCache.actualizarFoto(redimensionado.asImageBitmap())
                subiendoFoto = true

                val stream = java.io.ByteArrayOutputStream()
                redimensionado.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val data = stream.toByteArray()

                val storageRef = Firebase.storage.reference.child("perfil_fotos/$uid.jpg")

                storageRef.putBytes(data)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { url ->
                            Firebase.firestore.collection("users").document(uid)
                                .update("fotoUrl", url.toString())

                            // Perfil público para el ranking
                            Firebase.firestore.collection("perfilesPublicos").document(uid)
                                .update("fotoUrl", url.toString())

                            subiendoFoto = false
                        }
                    }
                    .addOnFailureListener { subiendoFoto = false }
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
                subiendoFoto = subiendoFoto,
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
                onClick = { controller.navigate(AppScreens.editarPerfilUsuario.name) },
                modifier = Modifier.width(230.dp).height(36.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(width = 1.dp, color = MainGreen)
            ) {
                Text(text = "Editar perfil", fontSize = 14.sp, color = MainGreen)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    PerfilCache.limpiar()
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

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 27.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Impacto", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MainText)
                    Text(text = "Ver más →", fontSize = 14.sp, color = MainGreen)
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProfileUsuarioSummaryCard(
                    totalRecolecciones = totalRecolecciones,
                    principal = principal
                )
                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }
}


@Composable
private fun ProfileUsuarioHeader(
    selectedImageBitmap: ImageBitmap?,
    subiendoFoto: Boolean,
    onTap: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mostrarFotoGrande by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().height(215.dp)) {
        Image(
            painter = painterResource(id = R.drawable.fundacion_profile_header),
            contentDescription = "Fondo perfil Usuario",
            modifier = Modifier.fillMaxWidth().height(175.dp),
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
                                delay(350)
                                fueSostenido = true
                                mostrarFotoGrande = true
                            }
                            tryAwaitRelease()
                            job.cancel()
                            mostrarFotoGrande = false
                            if (!fueSostenido) onTap()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            FotoPerfilUsuario(selectedImageBitmap)

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
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
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


@Composable
private fun ProfileUsuarioSummaryCard(
    totalRecolecciones: Int,
    principal: MaterialPrincipal?
) {
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
                    // Material que más recicla y sus kg
                    Text(text = formatKg(principal?.kg ?: 0.0), fontSize = 20.sp, fontWeight = FontWeight.Medium, color = DarkGreen)
                    Text(text = principal?.nombre ?: "Sin aportes", fontSize = 14.sp, color = SecondaryText)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Qué porcentaje de todo su reciclaje es ese material
                    Text(text = formatPorcentaje(principal?.porcentaje ?: 0f), fontSize = 20.sp, fontWeight = FontWeight.Medium, color = DarkGreen)
                    Text(text = "Reciclaje", fontSize = 14.sp, color = SecondaryText)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (totalRecolecciones == 1) "1 recolección realizada"
                else "$totalRecolecciones recolecciones realizadas",
                fontSize = 15.sp,
                color = SecondaryText
            )
        }
    }
}


@Composable
private fun ReBottleUsuarioProfileBottomBar(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxWidth().background(BackgroundColor).padding(top = 3.dp)) {
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
                icon = { Image(painter = painterResource(id = R.drawable.icon_rewards), contentDescription = "Recompensas", modifier = Modifier.size(25.dp)) },
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileUsuarioScreenPreview() {
    val controller = rememberNavController()
    RebottleTheme {
        ProfileUserScreen(controller = controller)
    }
}