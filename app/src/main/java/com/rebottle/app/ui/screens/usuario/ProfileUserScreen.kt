package com.rebottle.app.ui.screens.usuario

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

// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF5F7D42)
private val DarkGreen = Color(0xFF394C31)
private val LightGreen = Color(0xFFF1F7E9)
private val BorderGreen = Color(0xFFD5E3CD)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)

@Composable
fun ProfileUsuarioScreen(
    controller: NavController
) {
    val context = LocalContext.current

    var selectedImageBitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    // ------------------------------------------------
    // SELECTOR DE FOTO
    // ------------------------------------------------

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                selectedImageBitmap = runCatching {
                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                        }
                }.getOrNull()
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
            // ------------------------------------------------
            // CABECERA
            // ------------------------------------------------

            ProfileUsuarioHeader(
                selectedImageBitmap = selectedImageBitmap,
                onPhotoClick = { openPhotoPicker() }
            )

            Spacer(modifier = Modifier.height(5.dp))

            // ------------------------------------------------
            // DATOS DEL USUARIO
            // ------------------------------------------------

            Text(
                text = "Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )

            Text(
                text = "Cl 131a # 53-31, Bogotá",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Text(
                text = "+57 3112229987",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ------------------------------------------------
            // EDITAR PERFIL
            // ------------------------------------------------

            OutlinedButton(
                onClick = { openPhotoPicker() },
                modifier = Modifier
                    .width(230.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(width = 1.dp, color = MainGreen)
            ) {
                Text(
                    text = "Editar perfil",
                    fontSize = 14.sp,
                    color = MainGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ------------------------------------------------
            // CONTENIDO (IMPACTO)
            // ------------------------------------------------

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
                    Text(
                        text = "Impacto",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MainText
                    )

                    Text(
                        text = "Ver más →",
                        fontSize = 14.sp,
                        color = MainGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ProfileUsuarioSummaryCard()

                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }
}

// ----------------------------------------------------
// HEADER PERFIL (CON BORDE CIRCULAR MÁS GORDITO)
// ----------------------------------------------------

@Composable
private fun ProfileUsuarioHeader(
    selectedImageBitmap: ImageBitmap?,
    onPhotoClick: () -> Unit
) {
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

        // ------------------------------------------------
        // FOTO CIRCULAR CON BORDE GRUESO ("GORDITO")
        // ------------------------------------------------

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .border(
                    width = 10.dp,
                    color = BackgroundColor,
                    shape = CircleShape
                )
                .clickable { onPhotoClick() },
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
                    painter = painterResource(id = R.drawable.usuario_perfil), // O cámbialo por tu recurso de usuario
                    contentDescription = "Foto de Usuario",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// ----------------------------------------------------
// CARD RESUMEN DE IMPACTO
// ----------------------------------------------------

@Composable
private fun ProfileUsuarioSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(width = 1.dp, color = BorderGreen),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fundacion_summary),
                    contentDescription = "Gráfico de impacto",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "8 Kg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen
                    )
                    Text(
                        text = "Plástico",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "13,5%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreen
                    )
                    Text(
                        text = "Reciclaje",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "20 recolecciones realizadas",
                fontSize = 15.sp,
                color = SecondaryText
            )
        }
    }
}

// ----------------------------------------------------
// BARRA INFERIOR DEL USUARIO
// ----------------------------------------------------

@Composable
private fun ReBottleUsuarioProfileBottomBar(
    controller: NavController
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(top = 3.dp)
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
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Inicio",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text(text = "Inicio", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
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
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon_collection),
                        contentDescription = "Recolecciones",
                        modifier = Modifier.size(25.dp)
                    )
                },
                label = { Text(text = "Recolecciones", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    // TODO: pantalla de recompensas aún no existe
                },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon_routes),
                        contentDescription = "Recompensas",
                        modifier = Modifier.size(25.dp)
                    )
                },
                label = { Text(text = "Recompensas", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MainGreen,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = currentRoute == AppScreens.perfilUsuario.name,
                onClick = {
                    if (currentRoute != AppScreens.perfilUsuario.name) {
                        controller.navigate(AppScreens.perfilUsuario.name)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = { Text(text = "Perfil", fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainGreen,
                    selectedTextColor = MainGreen,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ProfileUsuarioScreenPreview() {
    val controller = rememberNavController()
    RebottleTheme {
        ProfileUsuarioScreen(controller = controller)
    }
}

