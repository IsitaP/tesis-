package com.rebottle.app.ui.screens.fundacion.rutas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.rebottle.app.model.EstadoParada
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.ParadaRuta
import com.rebottle.app.model.RutaProgresoState
import com.rebottle.app.model.RutaProgresoViewModel
import com.rebottle.app.navigation.AppScreens
import com.rebottle.app.ui.screens.fundacion.componentes.BarraNavegacionFundacion
import com.rebottle.app.ui.theme.RebottleColors


private val Naranja = Color(0xFFC96A3E)


@Composable
fun DetalleRecolectorScreen(
    controller: NavController,
    recolectorId: String,
    model: RutaProgresoViewModel = viewModel()
) {

    val state by model.state.collectAsState()
    var pestana by remember { mutableStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var editandoNombre by remember { mutableStateOf(false) }

    LaunchedEffect(recolectorId) {
        model.escucharRutaDe(recolectorId)

        Firebase.firestore.collection("users").document(recolectorId).get()
            .addOnSuccessListener { nombre = it.getString("name").orEmpty() }
    }

    val context = LocalContext.current

    if (editandoNombre) {
        DialogoEditarRecolector(
            nombreActual = nombre,
            onDismiss = { editandoNombre = false },
            onGuardar = { nuevo ->
                Firebase.firestore.collection("users").document(recolectorId)
                    .update("name", nuevo)
                    .addOnSuccessListener {
                        nombre = nuevo
                        editandoNombre = false
                        Toast.makeText(context, "Recolector actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "No se pudo actualizar el nombre", Toast.LENGTH_SHORT).show()
                    }
            }
        )
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
                    .padding(start = 8.dp, end = 8.dp, top = 40.dp, bottom = 6.dp)
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
                    text = nombre.ifEmpty { "Recolector" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebottleColors.DarkGreen,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 48.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = { editandoNombre = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar recolector",
                        tint = RebottleColors.MainGreen
                    )
                }
            }

            Pestanas(
                seleccionada = pestana,
                onSeleccion = { pestana = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp)
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(
                        color = RebottleColors.MainGreen,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(28.dp)
                    )

                    state.error != null -> Text(
                        text = state.error ?: "",
                        fontSize = 13.sp,
                        color = RebottleColors.SecondaryText,
                        modifier = Modifier.padding(20.dp)
                    )

                    state.ruta == null -> Text(
                        text = "Este recolector no tiene una ruta asignada",
                        fontSize = 14.sp,
                        color = RebottleColors.SecondaryText,
                        modifier = Modifier.padding(20.dp)
                    )

                    pestana == 0 -> PestanaMapa(state)

                    else -> PestanaDetalles(
                        state = state,
                        onVerMas = { parada ->
                            controller.navigate(
                                "${AppScreens.detalleParadaRecolector.name}/$recolectorId/${parada.solicitudId}"
                            )
                        }
                    )
                }
            }
        }
    }
}


// ============================================================
// EDITAR RECOLECTOR (NOMBRE)
// ============================================================

@Composable
private fun DialogoEditarRecolector(
    nombreActual: String,
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit
) {
    var nuevoNombre by remember { mutableStateOf(nombreActual) }
    val valido = nuevoNombre.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RebottleColors.Background,
        title = {
            Text(
                text = "Editar recolector",
                fontWeight = FontWeight.Bold,
                color = RebottleColors.DarkGreen
            )
        },
        text = {
            OutlinedTextField(
                value = nuevoNombre,
                onValueChange = { nuevoNombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                isError = !valido
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(nuevoNombre.trim()) },
                enabled = valido
            ) {
                Text("Guardar", color = RebottleColors.MainGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = RebottleColors.SecondaryText)
            }
        }
    )
}


// ============================================================
// PESTAÑAS
// ============================================================

@Composable
private fun Pestanas(
    seleccionada: Int,
    onSeleccion: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        listOf("Mapa", "Detalles").forEachIndexed { indice, titulo ->

            val activa = indice == seleccionada

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSeleccion(indice) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (activa) RebottleColors.MainGreen else RebottleColors.MainText,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (activa) RebottleColors.MainGreen else Color(0xFFC9CDC3))
                )
            }
        }
    }
}


// ============================================================
// PESTAÑA MAPA (pantalla 3)
// ============================================================

@Composable
private fun PestanaMapa(state: RutaProgresoState) {

    val puntos = remember(state.paradas) {
        state.paradas
            .filter { it.latitud != 0.0 || it.longitud != 0.0 }
            .map { PuntoMapa(it.latitud, it.longitud, colorParada(it.estado)) }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        MapaParadas(
            puntos = puntos,
            polyline = state.ruta?.polyline.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(18.dp))
        )

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
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rutas asignadas para hoy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = RebottleColors.DarkGreen
                )
                Text(
                    text = "${state.total} paradas",
                    fontSize = 14.sp,
                    color = RebottleColors.MainGreen
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp)
            ) {
                itemsIndexed(state.paradas, key = { _, p -> p.solicitudId }) { indice, parada ->
                    FilaParada(
                        parada = parada,
                        esUltima = indice == state.paradas.lastIndex
                    )
                }
            }
        }
    }
}


@Composable
private fun FilaParada(
    parada: ParadaRuta,
    esUltima: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {

        Column(
            modifier = Modifier.width(34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Naranja,
                modifier = Modifier.size(30.dp)
            )
            if (!esUltima) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(Naranja.copy(alpha = 0.6f))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
        ) {
            Text(
                text = parada.direccion,
                fontSize = 16.sp,
                color = RebottleColors.MainText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = parada.nombreLugar.ifBlank { materialesLegibles(parada.materiales) },
                fontSize = 13.sp,
                color = RebottleColors.SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        EtiquetaParada(parada.estado)
    }
}


// ============================================================
// PESTAÑA DETALLES (pantalla 4)
// ============================================================

@Composable
private fun PestanaDetalles(
    state: RutaProgresoState,
    onVerMas: (ParadaRuta) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(state.paradas, key = { it.solicitudId }) { parada ->
            TarjetaParada(parada, onVerMas = { onVerMas(parada) })
        }
    }
}


@Composable
private fun TarjetaParada(
    parada: ParadaRuta,
    onVerMas: () -> Unit
) {

    val (fondo, borde, acento) =
        when (parada.estado) {
            EstadoParada.COMPLETADA -> Triple(Color(0xFFE3EDDB), Color(0xFFD5E3CD), RebottleColors.MainGreen)
            EstadoParada.PENDIENTE -> Triple(Color(0xFFFEF3D7), Color(0xFFF5E1B8), Color(0xFFDB9A3C))
            EstadoParada.CANCELADA -> Triple(Color(0xFFFFD8D4), Color(0xFFF3A29B), Color(0xFFC0392B))
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borde),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parada.direccion,
                    fontSize = 17.sp,
                    color = RebottleColors.MainText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaParada(parada.estado)
            }

            Text(
                text = "Material: ${materialesLegibles(parada.materiales)}",
                fontSize = 14.sp,
                color = Color(0xFF5B5F55),
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = "Peso: ${parada.pesoKg} kg",
                fontSize = 14.sp,
                color = Color(0xFF5B5F55)
            )

            Text(
                text = "Ver más →",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = acento,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onVerMas() }
            )
        }
    }
}


// ============================================================
// UTILIDADES
// ============================================================

@Composable
private fun EtiquetaParada(estado: EstadoParada) {

    val (texto, color) =
        when (estado) {
            EstadoParada.COMPLETADA -> "Completada" to Color(0xFF536D45)
            EstadoParada.PENDIENTE -> "Pendiente" to Color(0xFFDB9A3C)
            EstadoParada.CANCELADA -> "Cancelada" to Color(0xFFC0392B)
        }

    Text(
        text = texto,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

private fun materialesLegibles(materiales: List<String>): String =
    materiales.joinToString(", ") { nombre ->
        MaterialType.values().firstOrNull { it.name == nombre }?.label ?: nombre
    }

private fun colorParada(estado: EstadoParada): Color =
    when (estado) {
        EstadoParada.PENDIENTE -> Naranja
        EstadoParada.COMPLETADA -> RebottleColors.MainGreen
        EstadoParada.CANCELADA -> Color(0xFFC0392B)
    }