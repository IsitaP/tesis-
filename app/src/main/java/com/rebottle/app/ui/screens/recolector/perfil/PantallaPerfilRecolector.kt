package com.rebottle.app.ui.screens.recolector.perfil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import com.rebottle.app.components.rememberAbrirGaleriaConPermiso
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.rebottle.app.R
import com.rebottle.app.model.PerfilCache
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.recolector.componentes.BarraNavegacionRecolector
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkGreen = RebottleColors.DarkGreen
private val LightGreen = RebottleColors.LightGreen
private val BorderGreen = RebottleColors.BorderGreen
private val SecondaryText = RebottleColors.SecondaryText
private val AccentOrange = RebottleColors.AccentOrange


// ----------------------------------------------------
// PANTALLA PERFIL — RECOLECTOR
// ----------------------------------------------------

@Composable
fun PantallaPerfilRecolector(
    controller: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val nombre = PerfilCache.nombre ?: ""
    val selectedImageBitmap = PerfilCache.foto
    var subiendoFoto by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid != null) PerfilCache.cargar(uid)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && uid != null) {
            val bitmap = runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()

            if (bitmap != null) {
                PerfilCache.actualizarFoto(bitmap.asImageBitmap())
                subiendoFoto = true

                val stream = java.io.ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
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
                    .addOnFailureListener { subiendoFoto = false }
            }
        }
    }

    val openPhotoPicker = rememberAbrirGaleriaConPermiso {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            BarraNavegacionRecolector(controller = controller)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderPerfilRecolector(
                selectedImageBitmap = selectedImageBitmap,
                subiendoFoto = subiendoFoto,
                onPhotoClick = { openPhotoPicker() }
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (PerfilCache.nombre == null) {
                CircularProgressIndicator(color = MainGreen, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = nombre.ifEmpty { "Recolector" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
            }

            Text(
                text = "Fundación Ecolianza",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = { /* TODO: pantalla de edición cuando exista */ },
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

            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
                Text(
                    text = "Información laboral",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(10.dp))
                InfoLaboralCard()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
private fun HeaderPerfilRecolector(
    selectedImageBitmap: ImageBitmap?,
    subiendoFoto: Boolean,
    onPhotoClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var mostrarFotoGrande by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().height(215.dp)) {

        Image(
            painter = painterResource(id = R.drawable.fundacion_profile_header),
            contentDescription = "Fondo perfil Recolector",
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
                    contentDescription = "Foto del recolector",
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


@Composable
private fun InfoLaboralCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(width = 1.dp, color = BorderGreen),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
            FilaInfoLaboral(icono = Icons.Filled.Badge, texto = "Código de recolector: 001")
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoLaboral(icono = Icons.Filled.LocationOn, texto = "Zona asignada: Teusaquillo")
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoLaboral(icono = Icons.Filled.Schedule, texto = "Horario de trabajo: 7:00 am - 4:00 pm")
            Spacer(modifier = Modifier.height(14.dp))
            FilaInfoLaboral(icono = Icons.Filled.CheckCircle, texto = "Estado: Disponible")
        }
    }
}

@Composable
private fun FilaInfoLaboral(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icono, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = texto, fontSize = 15.sp, color = Color(0xFF3F433A))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaPerfilRecolectorPreview() {
    RebottleTheme {
        PantallaPerfilRecolector(controller = rememberNavController())
    }
}