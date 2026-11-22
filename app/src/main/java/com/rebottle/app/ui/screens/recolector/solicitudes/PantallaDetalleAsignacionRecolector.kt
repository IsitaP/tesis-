package com.rebottle.app.ui.screens.recolector.solicitudes

import android.R.attr.id
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.ui.screens.recolector.componentes.EtiquetaEstado
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.rebottle.app.components.MainGreen
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.RutaProgresoViewModel
import com.rebottle.app.navigation.AppScreens
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

private val BackgroundColor = RebottleColors.Background
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText
private val BrownIcon = RebottleColors.Brown
private val ButtonBorder = Color(0xFF7F887A)
private val ButtonText = Color(0xFF5A6255)

@Composable
fun PantallaDetalleAsignacionRecolector(
    controller: NavController,
    asignacionId: String,
    // La fundación pasa el recolector a consultar; el recolector usa su propia sesión
    recolectorId: String? = null,
    // Oculta las acciones del recolector (marcar como completada)
    soloLectura: Boolean = false,
    model: RutaProgresoViewModel = viewModel()
) {
    val state by model.state.collectAsState()

    LaunchedEffect(recolectorId) {
        (recolectorId ?: FirebaseAuth.getInstance().currentUser?.uid)?.let { model.escucharRutaDe(it) }
    }

    // Celular del ciudadano que hizo la solicitud (vacío = no lo ha registrado)
    val usuarioIdParada =
        state.paradas.firstOrNull { it.solicitudId == asignacionId }?.usuarioId.orEmpty()

    var telefonoCiudadano by remember(usuarioIdParada) { mutableStateOf("") }

    LaunchedEffect(usuarioIdParada, soloLectura) {
        if (usuarioIdParada.isNotBlank() && !soloLectura) {
            Firebase.firestore.collection("users").document(usuarioIdParada).get()
                .addOnSuccessListener { doc ->
                    telefonoCiudadano = doc.getString("telefono")?.trim().orEmpty()
                }
        }
    }

    val context = LocalContext.current

    val fecha = formatearFecha(state.ruta?.fechaRecoleccion.orEmpty())

    val asignacion = state.paradas
        .firstOrNull { it.solicitudId == asignacionId }
        ?.aAsignacion(fecha)

    // Mientras llega la información, o si la parada ya no existe
    if (asignacion == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = RebottleColors.MainGreen)
            } else {
                Text("No encontramos esta asignación", color = SecondaryText)
            }
        }
        return
    }

    val estado = asignacion.estado
    val numero = state.paradas.indexOfFirst { it.solicitudId == asignacionId } + 1

    val cardColor = when (estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFFE3EDDB)
        EstadoAsignacion.PENDIENTE -> Color(0xFFFEF3D7)
        EstadoAsignacion.CANCELADA -> Color(0xFFFFD8D4)
    }

    val borderColor = when (estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFFD5E3CD)
        EstadoAsignacion.PENDIENTE -> Color(0xFFF5E1B8)
        EstadoAsignacion.CANCELADA -> Color(0xFFF3A29B)
    }

    val primaryButtonColor = when (estado) {
        EstadoAsignacion.COMPLETADA -> Color(0xFF536D45)
        EstadoAsignacion.PENDIENTE -> Color(0xFFD99B4B)
        EstadoAsignacion.CANCELADA -> Color(0xFFC94338)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Header
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MainText,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { controller.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Asignación # $numero",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )
        }

        // La tarjeta va justo debajo del encabezado y hace scroll si no cabe
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            // Tarjeta de detalles
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = asignacion.codigo,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            textDecoration = TextDecoration.Underline
                        )
                        EtiquetaEstado(estado = estado)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dirección
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location_pin),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = BrownIcon
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = asignacion.direccion,
                            fontSize = 14.sp,
                            color = MainText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fecha y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BrownIcon
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = asignacion.fecha,
                                fontSize = 14.sp,
                                color = MainText
                            )
                        }

                        if (asignacion.hora.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = BrownIcon
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = asignacion.hora,
                                    fontSize = 14.sp,
                                    color = MainText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Materiales",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MainText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Materiales reales de la solicitud, dos por fila
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (asignacion.materiales.isEmpty()) {
                            Text(
                                text = "Sin materiales registrados",
                                fontSize = 14.sp,
                                color = SecondaryText
                            )
                        }
                        asignacion.materiales.chunked(2).forEach { fila ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(28.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                fila.forEach { material ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = iconoMaterial(material)),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp),
                                            tint = BrownIcon
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = material,
                                            fontSize = 14.sp,
                                            color = SecondaryText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Peso estimado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = BrownIcon
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Peso estimado",
                                fontSize = 13.sp,
                                color = SecondaryText
                            )
                            Text(
                                text = "${asignacion.pesoKg} KG",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MainText
                            )
                        }
                    }
                }
            }

            // Contactar al ciudadano: solo si registró su celular
            if (telefonoCiudadano.isNotBlank() && !soloLectura) {
                Spacer(modifier = Modifier.height(16.dp))

                ContactarCiudadano(
                    telefono = telefonoCiudadano,
                    onLlamar = {
                        // Abre el marcador con el número listo; la llamada la hace el recolector
                        val numero = telefonoCiudadano.filter { it.isDigit() || it == '+' }
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))
                        )
                    }
                )
            }

            // El botón inferior solo se muestra si el estado es PENDIENTE
            if (estado == EstadoAsignacion.PENDIENTE && !soloLectura) {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { model.completarParada(asignacion.id) },
                    enabled = state.resolviendoId == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryButtonColor
                    )
                ) {
                    Text(
                        text = if (state.resolviendoId != null) "Guardando..." else "Marcar como Completada",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            state.resolucionError?.let { mensaje ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = mensaje, fontSize = 13.sp, color = Color(0xFFC94338))
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    controller.navigate(
                        AppScreens.rutasRecolector.name
                    )
                },
                enabled = state.resolviendoId == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainGreen
                )
            ) {
                Text(
                    text = "Ver ruta en el mapa",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        // Imagen del paisaje pegada al borde inferior de la pantalla
        Image(
            painter = painterResource(id = R.drawable.solicitudes_paisaje),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

// ============================================================
// CONTACTAR AL CIUDADANO
// ============================================================

@Composable
private fun ContactarCiudadano(
    telefono: String,
    onLlamar: () -> Unit
) {
    Card(
        onClick = onLlamar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RebottleColors.LightGreen),
        border = BorderStroke(1.dp, RebottleColors.BorderGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Necesitas comunicarte con el ciudadano?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Toca para llamar al $telefono",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Botón redondo con el ícono de teléfono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(RebottleColors.MainGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Llamar al ciudadano",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Los materiales llegan con su etiqueta legible (ej. "Vidrio")
private fun iconoMaterial(material: String): Int =
    when (material) {
        MaterialType.METAL.label -> R.drawable.ic_metal
        MaterialType.VIDRIO.label -> R.drawable.ic_glass
        else -> R.drawable.icon_collection
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaDetalleAsignacionRecolectorPreview() {
    RebottleTheme {
        PantallaDetalleAsignacionRecolector(
            controller = rememberNavController(),
            asignacionId = "1"
        )
    }
}