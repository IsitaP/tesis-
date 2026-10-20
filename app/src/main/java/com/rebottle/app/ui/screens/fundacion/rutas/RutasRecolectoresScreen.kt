package com.rebottle.app.ui.screens.fundacion.rutas

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.rebottle.app.model.EstadoParada
import com.rebottle.app.model.EstadoRecolectorVista
import com.rebottle.app.model.RecolectorRuta
import com.rebottle.app.model.RutasFundacionViewModel
import com.rebottle.app.ui.screens.fundacion.componentes.BarraNavegacionFundacion
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.navigation.AppScreens


private val Naranja = Color(0xFFC96A3E)


@Composable
fun RutasRecolectoresScreen(
    controller: NavController,
    model: RutasFundacionViewModel = viewModel()
) {

    val state by model.state.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { model.escuchar() }

    val lista = remember(state.recolectores, busqueda) {
        if (busqueda.isBlank()) {
            state.recolectores
        } else {
            state.recolectores.filter {
                it.nombre.contains(busqueda, ignoreCase = true) ||
                        it.localidad.contains(busqueda, ignoreCase = true)
            }
        }
    }

    val puntosMapa = remember(state.puntos) {
        state.puntos.map { PuntoMapa(it.latitud, it.longitud, colorParada(it.estado)) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = RebottleColors.Background,
        bottomBar = { BarraNavegacionFundacion(controller = controller) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ------------------------------------------------
            // ENCABEZADO
            // ------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 40.dp, bottom = 10.dp)
            ) {
                IconButton(
                    onClick = { controller.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = RebottleColors.DarkGreen
                    )
                }

                Text(
                    text = "Rutas recolectores",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebottleColors.DarkGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ------------------------------------------------
            // MAPA + BUSCADOR
            // ------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 12.dp)
            ) {
                MapaParadas(
                    puntos = puntosMapa,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                )

                BuscadorRutas(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    onFiltroClick = { /* pantalla de filtros */ },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }

            // ------------------------------------------------
            // LISTA DE RECOLECTORES
            // ------------------------------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color(0xFFFBF8F2))
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0xFFC9CDC3), RoundedCornerShape(2.dp))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recolectores",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RebottleColors.DarkGreen
                    )
                    Text(
                        text = "${state.disponibles} disponibles •",
                        fontSize = 14.sp,
                        color = RebottleColors.MainGreen
                    )
                }

                when {
                    state.isLoading -> Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = RebottleColors.MainGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    state.error != null -> Text(
                        text = state.error ?: "",
                        fontSize = 13.sp,
                        color = RebottleColors.SecondaryText,
                        modifier = Modifier.padding(20.dp)
                    )

                    lista.isEmpty() -> Text(
                        text = "No hay recolectores para mostrar",
                        fontSize = 14.sp,
                        color = RebottleColors.SecondaryText,
                        modifier = Modifier.padding(20.dp)
                    )

                    else -> LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.uid }) { recolector ->
                            TarjetaRecolector(
                                item = recolector,
                                onClick = {
                                    controller.navigate("${AppScreens.detalleRecolectorRuta.name}/${recolector.uid}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// BUSCADOR
// ============================================================

@Composable
private fun BuscadorRutas(
    value: String,
    onValueChange: (String) -> Unit,
    onFiltroClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        placeholder = { Text("Buscar ....") },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = RebottleColors.MainGreen)
        },
        trailingIcon = {
            IconButton(onClick = onFiltroClick) {
                Icon(Icons.Filled.Tune, contentDescription = "Filtros", tint = RebottleColors.MainGreen)
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}


// ============================================================
// TARJETA DE RECOLECTOR
// ============================================================

@Composable
private fun TarjetaRecolector(
    item: RecolectorRuta,
    onClick: () -> Unit
) {

    val colorEstado = colorEstado(item.estado)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, RebottleColors.MainGreen),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.size(58.dp)) {

                FotoRecolector(
                    fotoUrl = item.fotoUrl,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(15.dp)
                        .background(colorEstado, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = RebottleColors.MainText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.localidad.isNotEmpty()) {
                    Text(
                        text = "Localidad ${item.localidad}",
                        fontSize = 13.sp,
                        color = RebottleColors.SecondaryText
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(colorEstado, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.estado.etiqueta,
                        fontSize = 13.sp,
                        color = colorEstado
                    )
                }
            }

            if (item.rutaId != null) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFFBEBE4), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${item.pendientes}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Text(
                        text = if (item.pendientes == 1) "Solicitud" else "Solicitudes",
                        fontSize = 11.sp,
                        color = Naranja
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RebottleColors.DarkGreen
            )
        }
    }
}


// ============================================================
// FOTO DEL RECOLECTOR (desde Storage, con caché en memoria)
// ============================================================

private val cacheFotos = mutableMapOf<String, ImageBitmap>()

@Composable
fun FotoRecolector(
    fotoUrl: String,
    modifier: Modifier = Modifier
) {

    var bitmap by remember(fotoUrl) { mutableStateOf(cacheFotos[fotoUrl]) }

    LaunchedEffect(fotoUrl) {
        if (fotoUrl.isBlank() || bitmap != null) return@LaunchedEffect

        runCatching { Firebase.storage.getReferenceFromUrl(fotoUrl) }
            .getOrNull()
            ?.getBytes(2 * 1024 * 1024)
            ?.addOnSuccessListener { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?.asImageBitmap()
                    ?.let {
                        cacheFotos[fotoUrl] = it
                        bitmap = it
                    }
            }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFE3EDDB)),
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap

        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = "Foto del recolector",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = RebottleColors.MainGreen
            )
        }
    }
}


private fun colorEstado(estado: EstadoRecolectorVista): Color =
    when (estado) {
        EstadoRecolectorVista.EN_RUTA -> RebottleColors.MainGreen
        EstadoRecolectorVista.TERMINANDO -> Naranja
        EstadoRecolectorVista.ASIGNADA -> Color(0xFFDB9A3C)
        EstadoRecolectorVista.DISPONIBLE -> Color(0xFF8FAE57)
        EstadoRecolectorVista.NO_DISPONIBLE -> Color(0xFF9AA093)
    }

private fun colorParada(estado: EstadoParada): Color =
    when (estado) {
        EstadoParada.PENDIENTE -> Naranja
        EstadoParada.COMPLETADA -> RebottleColors.MainGreen
        EstadoParada.CANCELADA -> Color(0xFFC0392B)
    }