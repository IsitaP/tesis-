package com.rebottle.app.ui.screens.usuario.gamificacion

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.storage.FirebaseStorage
import com.rebottle.app.R
import com.rebottle.app.model.RankingViewModel
import com.rebottle.app.model.UsuarioRanking
import com.rebottle.app.ui.theme.RebottleTheme
import com.rebottle.app.ui.theme.RebottleColors
import androidx.compose.foundation.shape.GenericShape
import java.util.Locale
import androidx.compose.foundation.layout.fillMaxHeight


private val ScreenBackground = Color(0xFFB5C99A) // Verde claro de fondo
private val CardBackground = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val DarkText = Color(0xFF34382F)
private val SecondaryText = RebottleColors.SecondaryText
private val AvatarBackground = Color(0xFFE8F0E0)

private val Gold = Color(0xFFFFC83D)
private val Silver = Color(0xFFC0C0C0)
private val Bronze = Color(0xFFCD7F32)

val GoldPodium = Color(0xFFD4AF37)
val SilverPodium = Color(0xFFC0C0C0)
val BronzePodium = Color(0xFFCD7F32)

val GoldLight = Color(0xFFFFF3C4)
val SilverLight = Color(0xFFF1F1F1)
val BronzeLight = Color(0xFFF3D2B8)

// ============================================================
// DATOS DE PRUEBA (solo para los Previews)
// ============================================================

private val previewRanking = listOf(
    UsuarioRanking(1, "u1", "Davis Curtis", 650),
    UsuarioRanking(2, "u2", "Alena Donin", 450),
    UsuarioRanking(3, "u3", "Craig Gouse", 300),
    UsuarioRanking(4, "u4", "Madelyn Dias", 150),
    UsuarioRanking(4, "u5", "Zain Vaccaro", 150)
)

private fun colorMedalla(puesto: Int): Color = when (puesto) {
    1 -> Gold
    2 -> Silver
    3 -> Bronze
    else -> Color.White
}

// ============================================================
// PANTALLA PRINCIPAL
// ============================================================

@Composable
fun RankingScreen(
    controller: NavController,
    rankingViewModel: RankingViewModel = viewModel()
) {
    // Ranking real desde Firebase (users + progresoAprendizaje)
    val state by rankingViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        rankingViewModel.escucharRanking()
    }

    RankingContent(
        semanal = state.semanal,
        historico = state.historico,
        uidActual = state.uidActual,
        onBack = { controller.popBackStack() }
    )
}


// Contenido separado del ViewModel para que los Previews
// funcionen sin Firebase
@Composable
private fun RankingContent(
    semanal: List<UsuarioRanking>,
    historico: List<UsuarioRanking>,
    uidActual: String?,
    onBack: () -> Unit,
    esSemanalInicial: Boolean = true
) {
    var esSemanal by rememberSaveable { mutableStateOf(esSemanalInicial) }
    val lista = if (esSemanal) semanal else historico
    val posicionUsuario = lista.find { it.uid == uidActual }?.puesto

    // OJO: el Column raíz NO lleva padding horizontal,
    // así el panel inferior puede ir de borde a borde.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            HeaderRanking(onBack = onBack)
            Spacer(modifier = Modifier.height(12.dp))
            SelectorPestanas(
                esSemanal = esSemanal,
                onSeleccion = { esSemanal = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TarjetaMotivacion(posicion = posicionUsuario)
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (esSemanal) {
            VistaSemanal(lista = lista, uidActual = uidActual, modifier = Modifier.weight(1f))
        } else {
            VistaHistorica(lista = lista, uidActual = uidActual, modifier = Modifier.weight(1f))
        }
    }
}

// ============================================================
// HEADER, PESTAÑAS Y TARJETA DE MOTIVACIÓN
// ============================================================

@Composable
private fun HeaderRanking(onBack: () -> Unit) {
    // Box: el título queda centrado de verdad, sin Spacers "a ojo"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Chevron simple "<" en negro, como en el diseño
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = "Ranking",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SelectorPestanas(
    esSemanal: Boolean,
    onSeleccion: (Boolean) -> Unit
) {
    // Dos botones rectangulares pegados, sin contenedor de fondo
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        BotonPestana(
            texto = "Semanal",
            seleccionado = esSemanal,
            onClick = { onSeleccion(true) },
            modifier = Modifier.weight(1f)
        )
        BotonPestana(
            texto = "Histórico",
            seleccionado = !esSemanal,
            onClick = { onSeleccion(false) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BotonPestana(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .height(40.dp)
            .shadow(elevation = 4.dp, shape = forma)
            .clip(forma)
            .background(if (seleccionado) MainGreen else Color.White)
            .border(
                width = 1.dp,
                color = if (seleccionado) MainGreen else Color(0xFFD9D9D9),
                shape = forma
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else MainGreen,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun TarjetaMotivacion(posicion: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MainGreen, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (posicion != null) "#$posicion" else "#–",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = when (posicion) {
                        null -> "Completa módulos para entrar al ranking"
                        1 -> "¡Vas de primero, sigue así!"
                        else -> "¡Estás haciendo un muy buen trabajo!"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )

            }
        }
    }
}

// ============================================================
// VISTA SEMANAL (PODIO + PANEL INFERIOR)
// ============================================================

@Composable
private fun VistaSemanal(
    lista: List<UsuarioRanking>,
    uidActual: String?,
    modifier: Modifier = Modifier
) {
    // Los 3 primeros van al podio, el resto a la lista
    val resto = lista.drop(3)

    Column(modifier = modifier.fillMaxWidth()) {
        // El podio va pegado al panel: sin SpaceBetween que deje huecos
        Podio(
            lista = lista,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        // Panel inferior: ancho completo y ocupa todo lo que queda hasta abajo
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = CardBackground,
            shadowElevation = 8.dp
        ) {
            LazyColumn(
                modifier = Modifier.navigationBarsPadding(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.LightGray, RoundedCornerShape(2.dp))
                        )
                    }
                }
                items(resto, key = { it.uid }) { usuario ->
                    FilaRanking(usuario = usuario, esActual = usuario.uid == uidActual)
                }
            }
        }
    }
}

// Medidas tomadas de la imagen R.drawable.podio:
// dónde empieza el borde superior de cada bloque (fracción del ALTO de la imagen)
private const val TOPE_BLOQUE_1 = 0.04f
private const val TOPE_BLOQUE_2 = 0.22f
private const val TOPE_BLOQUE_3 = 0.39f

// Ancho de cada bloque y márgenes laterales (fracción del ANCHO de la imagen)
private const val ANCHO_BLOQUE_LATERAL = 0.31f
private const val ANCHO_BLOQUE_CENTRAL = 0.32f
private const val MARGEN_IZQ = 0.04f
private const val MARGEN_DER = 0.02f


@Composable
private fun Podio(
    lista: List<UsuarioRanking>,
    modifier: Modifier = Modifier
) {

    val primero = lista.getOrNull(0)
    val segundo = lista.getOrNull(1)
    val tercero = lista.getOrNull(2)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),

        verticalAlignment = Alignment.Bottom
    ) {

        // ====================================================
        // SEGUNDO PUESTO - PLATA
        // ====================================================

        PuestoPodio(
            usuario = segundo,
            puesto = 2,
            color = Silver,
            altura = 105.dp,
            modifier = Modifier.weight(1f)
        )


        // ====================================================
        // PRIMER PUESTO - ORO
        // ====================================================

        PuestoPodio(
            usuario = primero,
            puesto = 1,
            color = Gold,
            altura = 145.dp,
            esPrimero = true,
            modifier = Modifier.weight(1f)
        )


        // ====================================================
        // TERCER PUESTO - BRONCE
        // ====================================================

        PuestoPodio(
            usuario = tercero,
            puesto = 3,
            color = Bronze,
            altura = 85.dp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PuestoPodio(
    usuario: UsuarioRanking?,
    puesto: Int,
    color: Color,
    altura: Dp,
    modifier: Modifier = Modifier,
    esPrimero: Boolean = false
) {

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        // ====================================================
        // INFORMACIÓN DEL USUARIO
        // ====================================================

        InfoPodio(
            usuario = usuario,
            esPrimero = esPrimero
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )


        // ====================================================
        // BLOQUE DEL PODIO
        // ====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(altura)
                .background(
                    color = color,
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = puesto.toString(),
                color = Color.White,
                fontSize =
                    if (puesto == 1) {
                        48.sp
                    } else {
                        42.sp
                    },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoPodio(
    usuario: UsuarioRanking?,
    modifier: Modifier = Modifier,
    esPrimero: Boolean = false
) {
    if (usuario == null) {
        Spacer(modifier = modifier)
        return
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Corona ENCIMA del avatar para todo el podio:
        // oro, plata o bronce según el puesto
        Icon(
            painter = painterResource(id = R.drawable.ic_crown),
            contentDescription = null,
            tint = colorMedalla(usuario.puesto),
            modifier = Modifier.size(if (esPrimero) 28.dp else 22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))

        Avatar(
            fotoUrl = usuario.fotoUrl,
            nombre = usuario.nombre,
            size = if (esPrimero) 68.dp else 56.dp,
            borderColor = colorMedalla(usuario.puesto)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = usuario.nombre.substringBefore(" "),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${usuario.puntos} QP",
            fontSize = 11.sp,
            color = DarkText.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun FilaRanking(usuario: UsuarioRanking, esActual: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (esActual) AvatarBackground else Color.White, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = usuario.puesto.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SecondaryText,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Avatar(fotoUrl = usuario.fotoUrl, nombre = usuario.nombre, size = 44.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.nombre,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${usuario.puntos} QP",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
    }
}

// ============================================================
// VISTA HISTÓRICA (LISTA DE TARJETAS)
// ============================================================

@Composable
private fun VistaHistorica(
    lista: List<UsuarioRanking>,
    uidActual: String?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(lista, key = { it.uid }) { usuario ->
            TarjetaHistorica(usuario = usuario, esActual = usuario.uid == uidActual)
        }
    }
}


@Composable
private fun TarjetaHistorica(usuario: UsuarioRanking, esActual: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (esActual) AvatarBackground else Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número en círculo blanco con borde gris
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.puesto.toString(),
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Avatar(fotoUrl = usuario.fotoUrl, nombre = usuario.nombre, size = 48.dp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0C092A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format(Locale.US, "%,d", usuario.puntos)} points",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }
            // Insignia hexagonal solo para el top 3
            if (usuario.puesto <= 3) {
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 34.dp)
                        .background(colorInsignia(usuario.puesto), HexagonShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun colorInsignia(puesto: Int): Color = when (puesto) {
    1 -> Color(0xFFFFC83D) // Oro
    2 -> Color(0xFFDCDCDC) // Plata
    else -> Color(0xFFF4A77E) // Bronce
}

// Hexágono con punta arriba
private val HexagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2f, 0f)
    lineTo(w, h * 0.25f)
    lineTo(w, h * 0.75f)
    lineTo(w / 2f, h)
    lineTo(0f, h * 0.75f)
    lineTo(0f, h * 0.25f)
    close()
}

fun moveTo(value: Any, f: Float) {}

// Fotos ya descargadas, para no volver a pedirlas al
// cambiar de pestaña o hacer scroll
private val cacheFotos = mutableMapOf<String, ImageBitmap>()

@Composable
private fun Avatar(
    fotoUrl: String,
    nombre: String,
    size: Dp,
    borderColor: Color = Color.White
) {
    var foto by remember(fotoUrl) { mutableStateOf(cacheFotos[fotoUrl]) }

    LaunchedEffect(fotoUrl) {
        if (fotoUrl.isBlank() || foto != null) return@LaunchedEffect

        runCatching { FirebaseStorage.getInstance().getReferenceFromUrl(fotoUrl) }
            .getOrNull()
            ?.getBytes(2L * 1024 * 1024)
            ?.addOnSuccessListener { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()?.let {
                    cacheFotos[fotoUrl] = it
                    foto = it
                }
            }
    }

    val modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(AvatarBackground)
        .border(2.dp, borderColor, CircleShape)

    val bitmap = foto
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        // Sin foto: la inicial del nombre
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = nombre.take(1).uppercase(),
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold,
                color = MainGreen
            )
        }
    }
}

// ============================================================
// PREVIEW SEMANAL
// ============================================================

@Preview(
    name = "Ranking - Semanal",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun RankingSemanalPreview() {

    RebottleTheme {

        RankingContent(
            semanal = previewRanking,
            historico = previewRanking,
            uidActual = "u4",
            onBack = {},
            esSemanalInicial = true
        )
    }
}


// ============================================================
// PREVIEW HISTÓRICO
// ============================================================

@Preview(
    name = "Ranking - Histórico",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=393dp,height=851dp,dpi=440"
)
@Composable
private fun RankingHistoricoPreview() {

    RebottleTheme {

        RankingContent(
            semanal = previewRanking,
            historico = previewRanking,
            uidActual = "u4",
            onBack = {},
            esSemanalInicial = false
        )
    }
}


