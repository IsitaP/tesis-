package com.rebottle.app.ui.screens.recolector.rutas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.screens.recolector.componentes.BarraNavegacionRecolector
import com.rebottle.app.ui.theme.RebottleColors
import com.rebottle.app.ui.theme.RebottleTheme


// ----------------------------------------------------
// RUTAS DEL RECOLECTOR
// ----------------------------------------------------

// Por ahora está en blanco; aquí irá la ruta del recolector.
@Composable
fun PantallaRutasRecolector(
    controller: NavController
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = RebottleColors.Background,
        bottomBar = {
            BarraNavegacionRecolector(controller = controller)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = RebottleColors.MainText,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { controller.popBackStack() }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Rutas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebottleColors.MainText
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Próximamente",
                    fontSize = 15.sp,
                    color = Color(0xFF7F887A)
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaRutasRecolectorPreview() {
    RebottleTheme {
        PantallaRutasRecolector(controller = rememberNavController())
    }
}
