package com.rebottle.app.ui.screens.recolector.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val LightGreen = Color(0xFFF2F7ED)
private val BorderGreen = Color(0xFFD5E3CD)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)
private val MainGreen = Color(0xFF536D45)


// ----------------------------------------------------
// TARJETA DE ACCIÓN — genérica, usada por las 3 secciones de Inicio
// ----------------------------------------------------

@Composable
fun TarjetaAccionRecolector(
    title: String,
    lines: List<String>,
    linkText: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    cardHeight: Dp = 162.dp,
    imageSize: Dp = 150.dp,
    onLinkClick: () -> Unit = {}
) {

    // La altura es un mínimo: si el texto es largo, la tarjeta crece y no se corta.
    // Toda la tarjeta se puede tocar, igual que en el inicio del ciudadano.
    Card(
        onClick = onLinkClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = cardHeight),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(width = 1.dp, color = BorderGreen),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = cardHeight)
                .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(6.dp))

                lines.forEach { line ->
                    Text(text = line, fontSize = 14.sp, color = SecondaryText)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = linkText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Imagen un poco más pequeña para dejarle espacio al texto
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(minOf(imageSize, 120.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}