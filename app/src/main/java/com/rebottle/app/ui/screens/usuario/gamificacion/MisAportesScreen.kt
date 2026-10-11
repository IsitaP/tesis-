package com.rebottle.app.ui.screens.usuario.gamificacion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.model.Solicitud
import com.rebottle.app.model.SolicitudViewModel
import com.rebottle.app.model.cuentaComoAporte
import com.rebottle.app.ui.theme.RebottleTheme
import java.text.Normalizer
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin


// ============================================================
// COLORES
// ============================================================

private val Background = Color(0xFFFAF7EF)
private val DarkGreen = Color(0xFF394C31)
private val MainGreen = Color(0xFF5F7D42)
private val SecondaryText = Color(0xFF7F887A)

private val PlasticGreen = Color(0xFF8BC516)
private val GlassGreen = Color(0xFF2F711F)
private val PaperGreen = Color(0xFF3E5F2B)
private val MetalGreen = Color(0xFF70836A)
private val OtherGreen = Color(0xFFD5E87C)
private val EmptyRing = Color(0xFFE3E7DC)


// ============================================================
// MODELO
// ============================================================

/** Un material entregado en una recolección ya completada. */
data class Aporte(
    val material: String,
    val kg: Double,
    val fechaMillis: Long          // cuándo se completó la recolección
)

enum class PeriodoAportes(val etiqueta: String, val dias: Long?) {
    SEMANA("1 semana", 7),
    MES("1 mes", 30),
    TODO("Todo", null)
}

private data class MaterialInfo(
    val nombre: String,
    val imageRes: Int?,
    val color: Color
)

private data class ResumenMaterial(
    val titulo: String,
    val kg: Double,
    val porcentaje: Float,
    val imageRes: Int?,
    val color: Color
)

private val infoMateriales = listOf(
    MaterialInfo("Plástico", R.drawable.aportes_plastico, PlasticGreen),
    MaterialInfo("Vidrio", R.drawable.aportes_vidrio, GlassGreen),
    MaterialInfo("Papel", R.drawable.aportes_papel, PaperGreen),
    MaterialInfo("Metal", R.drawable.aportes_metal, MetalGreen),
    MaterialInfo("Otros", null, OtherGreen)
)


// ============================================================
// LÓGICA (sin UI)
// ============================================================

/** Lleva cualquier texto de material a una de las categorías de la pantalla. */
private fun categoria(raw: String): String {
    val t = Normalizer.normalize(raw.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")

    return when {
        "plast" in t || "pet" in t -> "Plástico"
        "vidri" in t -> "Vidrio"
        "papel" in t || "carton" in t -> "Papel"
        "metal" in t || "lata" in t || "alumin" in t -> "Metal"
        else -> "Otros"
    }
}

private fun resumir(
    aportes: List<Aporte>,
    periodo: PeriodoAportes
): List<ResumenMaterial> {

    val ahora = System.currentTimeMillis()
    val desde = periodo.dias
        ?.let { ahora - it * 24L * 60L * 60L * 1000L }
        ?: Long.MIN_VALUE

    val kgPorCategoria = aportes
        .filter { it.fechaMillis >= desde }
        .groupBy { categoria(it.material) }
        .mapValues { (_, lista) -> lista.sumOf { it.kg } }

    val total = kgPorCategoria.values.sum()

    return infoMateriales
        .map { info ->
            val kg = kgPorCategoria[info.nombre] ?: 0.0
            ResumenMaterial(
                titulo = info.nombre,
                kg = kg,
                porcentaje = if (total > 0) (kg / total * 100).toFloat() else 0f,
                imageRes = info.imageRes,
                color = info.color
            )
        }
        .filter { it.titulo != "Otros" || it.kg > 0 }
}

private fun formatDecimal(valor: Double): String {
    val r = Math.round(valor * 10) / 10.0
    return if (r % 1.0 == 0.0) {
        r.toInt().toString()
    } else {
        String.format(Locale.forLanguageTag("es-CO"), "%.1f", r)
    }
}

internal fun formatKg(kg: Double) = "${formatDecimal(kg)} Kg"

internal fun formatPorcentaje(p: Float) = "${formatDecimal(p.toDouble())}%"


/** Material que más ha reciclado el usuario (para la tarjeta del Perfil). */
data class MaterialPrincipal(
    val nombre: String,
    val kg: Double,
    val porcentaje: Float
)

/**
 * Usa el mismo cálculo que esta pantalla (período "Todo"), así el
 * Perfil y Mis aportes siempre muestran los mismos números.
 * null si todavía no hay aportes.
 */
fun materialPrincipal(solicitudes: List<Solicitud>): MaterialPrincipal? =
    resumir(aportesDesde(solicitudes), PeriodoAportes.TODO)
        .filter { it.kg > 0 }
        .maxByOrNull { it.kg }
        ?.let { MaterialPrincipal(it.titulo, it.kg, it.porcentaje) }


// ============================================================
// PANTALLA
// ============================================================

@Composable
fun MisAportesScreen(
    controller: NavController,
    model: SolicitudViewModel = viewModel()
) {
    // Aportes reales: salen de las solicitudes del usuario
    val state by model.state.collectAsState()

    LaunchedEffect(Unit) {
        model.escucharSolicitudesUsuario()
    }

    val aportes = remember(state.solicitudes) {
        aportesDesde(state.solicitudes)
    }

    MisAportesContent(
        controller = controller,
        aportes = aportes
    )
}


/**
 * Convierte cada solicitud (no cancelada) en aportes por material.
 * La solicitud guarda un solo peso para todos sus materiales, así
 * que los kg se reparten por igual entre ellos.
 */
private fun aportesDesde(solicitudes: List<Solicitud>): List<Aporte> =
    solicitudes
        .filter { it.cuentaComoAporte() && it.materiales.isNotEmpty() }
        .flatMap { solicitud ->
            val kgPorMaterial = solicitud.pesoKg.toDouble() / solicitud.materiales.size
            val fecha = solicitud.creadaEn?.toDate()?.time ?: System.currentTimeMillis()

            solicitud.materiales.map { material ->
                Aporte(material = material, kg = kgPorMaterial, fechaMillis = fecha)
            }
        }

@Composable
private fun MisAportesContent(
    controller: NavController,
    aportes: List<Aporte>
) {

    var selectedRange by remember { mutableStateOf(PeriodoAportes.MES) }

    val resumen = remember(aportes, selectedRange) {
        resumir(aportes, selectedRange)
    }

    val totalKg = resumen.sumOf { it.kg }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(start = 22.dp, end = 22.dp, bottom = 30.dp)
    ) {

        Spacer(modifier = Modifier.height(48.dp))


        // ====================================================
        // HEADER
        // ====================================================

        Box(modifier = Modifier.fillMaxWidth()) {

            IconButton(
                onClick = { controller.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = DarkGreen,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = "Mis aportes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGreen,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Spacer(modifier = Modifier.height(12.dp))


        // ====================================================
        // FILTRO DE TIEMPO
        // ====================================================

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PeriodSelector(
                selected = selectedRange,
                onSelected = { selectedRange = it }
            )
        }


        Spacer(modifier = Modifier.height(25.dp))


        // ====================================================
        // GRÁFICA
        // ====================================================

        ContributionChart(
            resumen = resumen,
            totalKg = totalKg
        )

        if (totalKg == 0.0) {
            Text(
                text = "Aún no tienes aportes en este período",
                fontSize = 13.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.height(30.dp))


        // ====================================================
        // LISTA DE MATERIALES
        // ====================================================

        resumen.forEach { item ->
            ContributionCard(item = item)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


// ============================================================
// SELECTOR
// ============================================================

@Composable
private fun PeriodSelector(
    selected: PeriodoAportes,
    onSelected: (PeriodoAportes) -> Unit
) {

    Card(
        modifier = Modifier
            .width(210.dp)
            .height(38.dp),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            PeriodoAportes.values().forEach { option ->

                val isSelected = selected == option

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .background(
                            color = if (isSelected) Color(0xFFDDEED3) else Color.Transparent,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { onSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.etiqueta,
                        fontSize = 9.sp,
                        color = if (isSelected) MainGreen else SecondaryText
                    )
                }
            }
        }
    }
}


// ============================================================
// GRÁFICA
// ============================================================

@Composable
private fun ContributionChart(
    resumen: List<ResumenMaterial>,
    totalKg: Double
) {

    val conDatos = resumen.filter { it.kg > 0 }
    val gap = if (conDatos.size > 1) 3f else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {

        // ---------------- ANILLO ----------------

        Canvas(modifier = Modifier.size(225.dp)) {

            val strokeWidth = 38.dp.toPx()
            val inset = strokeWidth / 2f

            // El anillo se dibuja hacia adentro para que no se salga del cuadro
            val topLeft = Offset(inset, inset)
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            if (conDatos.isEmpty()) {

                drawArc(
                    color = EmptyRing,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

            } else {

                var startAngle = -90f

                conDatos.forEach { item ->

                    val fullSweep = (item.porcentaje / 100f) * 360f

                    drawArc(
                        color = item.color,
                        startAngle = startAngle,
                        sweepAngle = (fullSweep - gap).coerceAtLeast(0f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    startAngle += fullSweep
                }
            }
        }


        // ---------------- CENTRO ----------------

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = formatKg(totalKg),
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = DarkGreen
            )

            Text(
                text = "reciclados",
                fontSize = 14.sp,
                color = DarkGreen
            )
        }


        // ---------------- ETIQUETAS (según el ángulo de cada porción) ----------------

        var inicio = -90f

        conDatos.forEach { item ->

            val sweep = (item.porcentaje / 100f) * 360f

            // Las porciones muy pequeñas no llevan etiqueta (se leen en la lista de abajo)
            if (item.porcentaje >= 4f) {

                val medio = Math.toRadians((inicio + sweep / 2f).toDouble())
                val radio = 140f

                val x = (radio * cos(medio)).toFloat()
                val y = (radio * sin(medio)).toFloat()

                Text(
                    text = "${formatPorcentaje(item.porcentaje)}\n${item.titulo}",
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    color = if (item.color.luminance() > 0.4f) SecondaryText else item.color,
                    modifier = Modifier.offset(x = x.dp, y = y.dp)
                )
            }

            inicio += sweep
        }
    }
}


// ============================================================
// CARD MATERIAL
// ============================================================

@Composable
private fun ContributionCard(
    item: ResumenMaterial
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(
                        color = Color(0xFFF5F6F3),
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageRes != null) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.titulo,
                        modifier = Modifier.size(42.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = "•••", fontSize = 14.sp, color = SecondaryText)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )

                Text(
                    text = formatKg(item.kg),
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = item.color, shape = RoundedCornerShape(50.dp))
            )
        }
    }
}


// ============================================================
// PREVIEW (con datos de ejemplo para ver la gráfica funcionando)
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun MisAportesScreenPreview() {

    val ahora = System.currentTimeMillis()
    val dia = 24L * 60L * 60L * 1000L

    RebottleTheme {
        MisAportesContent(
            controller = rememberNavController(),
            aportes = listOf(
                Aporte("Plástico", 3.0, ahora - 2 * dia),
                Aporte("Vidrio", 3.2, ahora - 5 * dia),
                Aporte("Papel", 6.0, ahora - 10 * dia),
                Aporte("Metal", 3.6, ahora - 20 * dia),
                Aporte("Cartón", 1.5, ahora - 1 * dia),
                Aporte("Textil", 1.4, ahora - 3 * dia)
            )
        )
    }
}