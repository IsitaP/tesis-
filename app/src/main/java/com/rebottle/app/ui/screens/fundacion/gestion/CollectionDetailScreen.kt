package com.rebottle.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rebottle.app.R
import com.rebottle.app.ui.theme.RebottleTheme

private val BackgroundColor = Color(0xFFFBF8F2)
private val MainGreen = Color(0xFF536D45)
private val LightOrange = Color(0xFFFEF3D7)
private val MainOrange = Color(0xFFD99B4B)
private val MainText = Color(0xFF34382F)
private val SecondaryText = Color(0xFF7F887A)
private val BorderOrange = Color(0xFFF5E1B8)
private val Brown = Color(0xFFA55E37)

@Composable
fun CollectionDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAcceptChanges: () -> Unit = {},
    onCancelChanges: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            CollectionHeader(
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                CollectionDetailCard()

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onAcceptChanges,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainGreen),
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.accept_changes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onCancelChanges,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    border = BorderStroke(1.dp, MainText),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = MainGreen
                    ),
                    shape = RoundedCornerShape(25.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel_changes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MainGreen
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.fundacion_edit),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
private fun CollectionHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = MainText
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.collection_label, 1),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )
    }
}

@Composable
private fun CollectionDetailCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = LightOrange),
        border = BorderStroke(1.dp, BorderOrange),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#RB-20262",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
                StatusBadge(text = stringResource(id = R.string.status_pending))
            }

            Spacer(modifier = Modifier.height(18.dp))

            DetailItem(
                icon = Icons.Default.LocationOn,
                text = "Cll 131a # 53- 31"
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                DetailItem(
                    iconRes = R.drawable.ic_calendar,
                    text = "Septiembre 12",
                    modifier = Modifier.weight(1f)
                )
                DetailItem(
                    iconRes = R.drawable.ic_clock,
                    text = "9:00 AM",
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(id = R.string.materials_label),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MaterialItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Brown.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        )
                    },
                    text = "Metal"
                )
                MaterialItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Brown.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        )
                    },
                    text = "Vidrio"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DetailColumn(
                    iconRes = R.drawable.ic_weight,
                    label = stringResource(id = R.string.estimated_weight_label),
                    value = "8 KG",
                    modifier = Modifier.weight(2f)
                )
                DetailColumn(
                    label = stringResource(id = R.string.priority_label),
                    value = stringResource(id = R.string.priority_low),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            DetailColumn(
                iconRes = R.drawable.icon_collection,
                label = stringResource(id = R.string.assigned_collector_label),
                value = "Juan Correa"
            )
        }
    }
}

@Composable
private fun StatusBadge(text: String) {
    Box(
        modifier = Modifier
            .background(MainOrange, RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailItem(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    text: String
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Brown
            )
        } else if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Brown
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 15.sp, color = MainText)
    }
}

@Composable
private fun MaterialItem(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 15.sp, color = SecondaryText)
    }
}

@Composable
private fun DetailColumn(
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    label: String,
    value: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = Brown
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column {
            Text(text = label, fontSize = 13.sp, color = MainText, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 15.sp, color = SecondaryText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionDetailScreenPreview() {
    RebottleTheme {
        CollectionDetailScreen()
    }
}
