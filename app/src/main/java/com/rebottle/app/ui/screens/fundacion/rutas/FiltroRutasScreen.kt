package com.rebottle.app.ui.screens.fundacion.rutas

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.model.MaterialType
import com.rebottle.app.model.RutasFundacionViewModel
import com.rebottle.app.ui.screens.fundacion.componentes.BarraNavegacionFundacion
import com.rebottle.app.ui.screens.recolector.solicitudes.formatearFecha
import com.rebottle.app.ui.theme.RebottleColors


private enum class Campo { RECOLECTOR, FECHA, MATERIAL, PESO }


@Composable
fun FiltroRutasScreen(
    controller: NavController,
    model: RutasFundacionViewModel
) {

    val state by model.state.collectAsState()
    val filtroActual by model.filtro.collectAsState()

    var borrador by remember { mutableStateOf(filtroActual) }
    var abierto by remember { mutableStateOf<Campo?>(null) }

    LaunchedEffect(Unit) { model.escuchar() }

    val recolectores = state.recolectores

    // Opciones: solo lo que existe hoy en los datos
    val opcionesRecolector = recolectores.map { it.uid to it.nombre }

    val opcionesFecha =
        recolectores.map { it.fecha }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .map { it to formatearFecha(it) }

    val opcionesMaterial =
        MaterialType.values()
            .filter { tipo -> recolectores.any { tipo.name in it.materiales } }
            .map { it.name to it.label }

    val opcionesPeso = RangoPeso.values().map { it.name to it.etiqueta }

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 40.dp, bottom = 12.dp)
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
                    text = "Filtro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebottleColors.DarkGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                FiltroDesplegable(
                    titulo = "Recolector",
                    icono = { Icon(Icons.Filled.Person, null, tint = RebottleColors.MainGreen, modifier = Modifier.size(22.dp)) },
                    opciones = opcionesRecolector,
                    seleccion = borrador.recolectorId,
                    expandido = abierto == Campo.RECOLECTOR,
                    onToggle = { abierto = if (abierto == Campo.RECOLECTOR) null else Campo.RECOLECTOR },
                    onElegir = { borrador = borrador.copy(recolectorId = it); abierto = null }
                )

                FiltroDesplegable(
                    titulo = "Fecha",
                    icono = { IconoRecurso(R.drawable.ic_calendar) },
                    opciones = opcionesFecha,
                    seleccion = borrador.fecha,
                    expandido = abierto == Campo.FECHA,
                    onToggle = { abierto = if (abierto == Campo.FECHA) null else Campo.FECHA },
                    onElegir = { borrador = borrador.copy(fecha = it); abierto = null }
                )

                FiltroDesplegable(
                    titulo = "Material",
                    icono = { IconoRecurso(R.drawable.ic_box) },
                    opciones = opcionesMaterial,
                    seleccion = borrador.material,
                    expandido = abierto == Campo.MATERIAL,
                    onToggle = { abierto = if (abierto == Campo.MATERIAL) null else Campo.MATERIAL },
                    onElegir = { borrador = borrador.copy(material = it); abierto = null }
                )

                FiltroDesplegable(
                    titulo = "Peso",
                    icono = { IconoRecurso(R.drawable.ic_weight) },
                    opciones = opcionesPeso,
                    seleccion = borrador.peso?.name,
                    expandido = abierto == Campo.PESO,
                    onToggle = { abierto = if (abierto == Campo.PESO) null else Campo.PESO },
                    onElegir = { clave ->
                        borrador = borrador.copy(peso = clave?.let { RangoPeso.valueOf(it) })
                        abierto = null
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // ------------------------------------------------
            // BOTONES
            // ------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        model.limpiarFiltro()
                        controller.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, RebottleColors.MainGreen)
                ) {
                    Text("Eliminar filtro", color = RebottleColors.MainGreen, fontSize = 15.sp)
                }

                Button(
                    onClick = {
                        model.aplicarFiltro(borrador)
                        controller.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RebottleColors.MainGreen)
                ) {
                    Text("Aplicar filtro", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}


@Composable
private fun IconoRecurso(id: Int) {
    Icon(
        painter = painterResource(id = id),
        contentDescription = null,
        tint = RebottleColors.MainGreen,
        modifier = Modifier.size(22.dp)
    )
}


@Composable
private fun FiltroDesplegable(
    titulo: String,
    icono: @Composable () -> Unit,
    opciones: List<Pair<String, String>>,
    seleccion: String?,
    expandido: Boolean,
    onToggle: () -> Unit,
    onElegir: (String?) -> Unit,
    habilitado: Boolean = true
) {

    val etiquetaSeleccion = opciones.firstOrNull { it.first == seleccion }?.second

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = habilitado) { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                icono()

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = when {
                        !habilitado -> "$titulo (aún no disponible)"
                        etiquetaSeleccion != null -> "$titulo: $etiquetaSeleccion"
                        else -> titulo
                    },
                    fontSize = 16.sp,
                    fontWeight = if (etiquetaSeleccion != null) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (habilitado) RebottleColors.DarkGreen else Color(0xFF9AA093),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expandido) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (habilitado) RebottleColors.DarkGreen else Color(0xFF9AA093)
                )
            }

            if (expandido) {

                if (opciones.isEmpty()) {
                    Text(
                        text = "Sin opciones por ahora",
                        fontSize = 14.sp,
                        color = RebottleColors.SecondaryText,
                        modifier = Modifier.padding(start = 50.dp, bottom = 16.dp)
                    )
                } else {
                    opciones.forEach { (clave, etiqueta) ->

                        val elegida = clave == seleccion

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onElegir(if (elegida) null else clave) }
                                .padding(start = 50.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = etiqueta,
                                fontSize = 15.sp,
                                color = if (elegida) RebottleColors.MainGreen else RebottleColors.MainText,
                                fontWeight = if (elegida) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (elegida) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = RebottleColors.MainGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}