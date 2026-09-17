import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R

@Composable
fun PantallaInicioRecolector(
    alPresionarSolicitudes: () -> Unit,
    alIniciarRuta: () -> Unit,
    alVerMapa: () -> Unit,
    alPresionarNotificaciones: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF7))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        EncabezadoRecolector(
            alPresionarNotificaciones = alPresionarNotificaciones
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "¿Qué desea hacer hoy?",
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF344427)
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "Ver solicitudes pendientes",
            fontSize = 18.sp,
            color = Color(0xFF292921)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TarjetaAccionRecolector(
            titulo = "Solicitudes asignadas",
            descripcion = "12 nuevas\n5 aceptadas\n18 completadas hoy",
            textoAccion = "Ver todas →",
            imagen = R.drawable.solicitudes_recolector,
            alPresionar = alPresionarSolicitudes
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Text(
            text = "Próxima ruta",
            fontSize = 18.sp,
            color = Color(0xFF292921)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TarjetaAccionRecolector(
            titulo = "Siguiente recolección",
            descripcion = "Barrio Las Floresta\n10:30 AM\nUsuario: Juan Pérez",
            textoAccion = "Iniciar ruta →",
            imagen = R.drawable.proxima_ruta_recolector,
            alPresionar = alIniciarRuta
        )

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Text(
            text = "Ruta del día",
            fontSize = 18.sp,
            color = Color(0xFF292921)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TarjetaAccionRecolector(
            titulo = "Recorrido asignado",
            descripcion = "5 paradas\nDistancia: 8.2 km\nTiempo estimado: 1 h 40 min",
            textoAccion = "Ver mapa →",
            imagen = R.drawable.ruta_dia_recolector,
            alPresionar = alVerMapa
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}