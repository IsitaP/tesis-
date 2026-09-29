package com.rebottle.app.ui.screens.usuario.gamificacion

import androidx.annotation.DrawableRes
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme
import androidx.compose.foundation.shape.GenericShape
import java.util.Locale

// ============================================================
// COLORES
// ============================================================

private val ScreenBackground = Color(0xFFB5C99A) // Verde claro de fondo
private val CardBackground = Color(0xFFFBF8F2)   // Crema del panel inferior
private val MainGreen = Color(0xFF536D45)        // Verde corporativo
private val DarkText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)
private val AvatarBackground = Color(0xFFE8F0E0)

private val Gold = Color(0xFFFFC83D)
private val Silver = Color(0xFFC0C0C0)
private val Bronze = Color(0xFFCD7F32)

// ============================================================
// MODELO Y DATOS DE PRUEBA
// ============================================================

data class UsuarioRanking(
    val puesto: Int,
    val nombre: String,
    val puntos: Int,
    @DrawableRes val avatarRes: Int
)

private val rankingSemanal = listOf(
    UsuarioRanking(1, "Davis Curtis", 2569, R.drawable.avatar_davis),
    UsuarioRanking(2, "Alena Donin", 1469, R.drawable.avatar_alena),
    UsuarioRanking(3, "Craig Gouse", 1053, R.drawable.avatar_craig),
    UsuarioRanking(4, "Madelyn Dias", 590, R.drawable.avatar_madelyn),
    UsuarioRanking(5, "Zain Vaccaro", 448, R.drawable.avatar_zain)
)

private val rankingHistorico = listOf(
    UsuarioRanking(1, "Davis Curtis", 8950, R.drawable.avatar_davis),
    UsuarioRanking(2, "Alena Donin", 7420, R.drawable.avatar_alena),
    UsuarioRanking(3, "Craig Gouse", 6100, R.drawable.avatar_craig),
    UsuarioRanking(4, "Madelyn Dias", 4300, R.drawable.avatar_madelyn),
    UsuarioRanking(5, "Zain Vaccaro", 3900, R.drawable.avatar_zain)
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
    posicionUsuario: Int = 4 // Luego lo traes del ViewModel
) {
    var esSemanal by rememberSaveable { mutableStateOf(true) }
    val lista = if (esSemanal) rankingSemanal else rankingHistorico

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
            HeaderRanking(onBack = { controller.popBackStack() })
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
            VistaSemanal(lista = lista, modifier = Modifier.weight(1f))
        } else {
            VistaHistorica(lista = lista, modifier = Modifier.weight(1f))
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
private fun TarjetaMotivacion(posicion: Int) {
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
                    text = "#$posicion",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "¡Estás haciendo un muy buen trabajo!",
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
    modifier: Modifier = Modifier
) {
    val resto = lista.filter { it.puesto > 3 }

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
                items(resto, key = { it.puesto }) { usuario ->
                    FilaRanking(usuario = usuario)
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
    val primero = lista.find { it.puesto == 1 }
    val segundo = lista.find { it.puesto == 2 }
    val tercero = lista.find { it.puesto == 3 }

    val painter = painterResource(id = R.drawable.podio)
    val proporcion = 1.85f

    // Medimos el ancho real que ocupa el podio en pantalla
    var anchoPx by remember { mutableIntStateOf(0) }
    val ancho = with(LocalDensity.current) { anchoPx.toDp() }
    val altoImagen = ancho / proporcion
    val separacion = 4.dp // espacio entre los puntos y el bloque

    Column(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { anchoPx = it.width }
    ) {
        // Fila de usuarios: cada uno se "baja" hasta el tope de su bloque
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = ancho * MARGEN_IZQ, end = ancho * MARGEN_DER)
                .zIndex(1f), // que se dibujen encima de la imagen
            verticalAlignment = Alignment.Bottom
        ) {
            InfoPodio(
                usuario = segundo,
                modifier = Modifier
                    .weight(ANCHO_BLOQUE_LATERAL)
                    .offset(y = altoImagen * TOPE_BLOQUE_2 - separacion)
            )
            InfoPodio(
                usuario = primero,
                esPrimero = true,
                modifier = Modifier
                    .weight(ANCHO_BLOQUE_CENTRAL)
                    .offset(y = altoImagen * TOPE_BLOQUE_1 - separacion)
            )
            InfoPodio(
                usuario = tercero,
                modifier = Modifier
                    .weight(ANCHO_BLOQUE_LATERAL)
                    .offset(y = altoImagen * TOPE_BLOQUE_3 - separacion)
            )
        }

        Image(
            painter = painter,
            contentDescription = "Podio",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(proporcion)
        )
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
        // Corona ENCIMA del avatar (antes se montaba sobre la cara con offset)
        if (esPrimero) {
            Icon(
                painter = painterResource(id = R.drawable.ic_crown),
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Avatar(
            res = usuario.avatarRes,
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
private fun FilaRanking(usuario: UsuarioRanking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
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
        Avatar(res = usuario.avatarRes, size = 44.dp)
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(lista, key = { it.puesto }) { usuario ->
            TarjetaHistorica(usuario = usuario)
        }
    }
}


@Composable
private fun TarjetaHistorica(usuario: UsuarioRanking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
            Avatar(res = usuario.avatarRes, size = 48.dp)
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

@Composable
private fun Avatar(
    @DrawableRes res: Int,
    size: Dp,
    borderColor: Color = Color.White
) {
    Image(
        painter = painterResource(id = res),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AvatarBackground)
            .border(2.dp, borderColor, CircleShape)
    )
}


