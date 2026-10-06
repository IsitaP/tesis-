package com.rebottle.app.ui.screens.fundacion.gestion

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.rebottle.app.ui.theme.RebottleColors

// ----------------------------------------------------
// COLORES
// ----------------------------------------------------

private val BackgroundColor = RebottleColors.Background
private val MainGreen = RebottleColors.MainGreen
private val LightGreen = RebottleColors.LightGreen
private val MainText = RebottleColors.MainText
private val SecondaryText = RebottleColors.SecondaryText
private val BorderGreen = RebottleColors.BorderGreen
private val BrownIcon = Color(0xFFA55E37)


// ----------------------------------------------------
// MANAGE COLLECTORS SCREEN
// ----------------------------------------------------

@Composable
fun ManageCollectorsScreen(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onEditClick: (CollectorUiModel) -> Unit = {},
    onProfileClick: () -> Unit = {}

) {
    val collectors = listOf(
        CollectorUiModel(
            id = "001",
            name = "Gabriela Cruz Pino",
            zone = "Teusaquillo",
            schedule = "7:00 am - 4:00 pm",
            status = stringResource(id = R.string.status_available),
            imageRes = R.drawable.fundacion_profile
        ),
        CollectorUiModel(
            id = "002",
            name = "Samuel Robayo",
            zone = "Chía",
            schedule = "9:00 am - 8:00 pm",
            status = stringResource(id = R.string.status_available),
            imageRes = R.drawable.fundacion_profile
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            Header(onBackClick = onBackClick)
        },
        bottomBar = {
            ReBottleBottomBar(
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(id = R.string.collectors_list_subtitle),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MainText
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(collectors) { collector ->
                    CollectorCard(
                        collector = collector,
                        onEditClick = { onEditClick(collector) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = MainText
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.manage_collectors_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MainGreen
        )
    }
}

@Composable
private fun CollectorCard(
    collector: CollectorUiModel,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderGreen),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = collector.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainText,
                    modifier = Modifier.padding(top = 12.dp)
                )

                Image(
                    painter = painterResource(id = collector.imageRes),
                    contentDescription = collector.name,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            CollectorDetailItem(
                iconRes = R.drawable.ic_id_card,
                text = stringResource(id = R.string.collector_code_label, collector.id)
            )
            
            CollectorDetailItem(
                icon = Icons.Outlined.LocationOn,
                text = stringResource(id = R.string.assigned_zone_label, collector.zone)
            )
            
            CollectorDetailItem(
                iconRes = R.drawable.ic_clock,
                text = stringResource(id = R.string.work_schedule_label, collector.schedule)
            )
            
            CollectorDetailItem(
                iconRes = R.drawable.ic_check_circle,
                text = stringResource(id = R.string.status_label, collector.status)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = stringResource(id = R.string.edit_label) + " →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainGreen,
                    modifier = Modifier.clickable { onEditClick() }
                )
            }
        }
    }
}

@Composable
private fun CollectorDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BrownIcon
                )
            } else if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BrownIcon
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = text,
            fontSize = 16.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun ReBottleBottomBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(top = 3.dp)
    ) {

        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {


            // ------------------------------------------------
            // INICIO
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,

                onClick = {
                    onHomeClick()
                },

                icon = {

                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = stringResource(
                            id = R.string.tab_inicio
                        ),
                        modifier = Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(
                        text = stringResource(
                            id = R.string.tab_inicio
                        ),
                        fontSize = 12.sp
                    )
                },

                colors = navigationItemColors()
            )


            // ------------------------------------------------
            // SOLICITUDES
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,

                onClick = {
                    // Luego conectamos solicitudes
                },

                icon = {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.icon_collection
                        ),
                        contentDescription = stringResource(
                            id = R.string.tab_solicitudes
                        ),
                        modifier = Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(
                        text = stringResource(
                            id = R.string.tab_solicitudes
                        ),
                        fontSize = 12.sp
                    )
                },

                colors = navigationItemColors()
            )


            // ------------------------------------------------
            // RUTAS
            // ------------------------------------------------

            NavigationBarItem(
                selected = false,

                onClick = {
                    // Luego conectamos rutas
                },

                icon = {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.icon_routes
                        ),
                        contentDescription = stringResource(
                            id = R.string.tab_rutas
                        ),
                        modifier = Modifier.size(42.dp)
                    )
                },

                label = {

                    Text(
                        text = stringResource(
                            id = R.string.tab_rutas
                        ),
                        fontSize = 12.sp,
                        modifier = Modifier.offset(
                            y = (-4).dp
                        )
                    )
                },

                colors = navigationItemColors()
            )


            // ------------------------------------------------
            // PERFIL
            // ------------------------------------------------

            NavigationBarItem(
                selected = true,

                onClick = {
                    onProfileClick()
                },

                icon = {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = stringResource(
                            id = R.string.tab_perfil
                        ),
                        modifier = Modifier.size(26.dp)
                    )
                },

                label = {

                    Text(
                        text = stringResource(
                            id = R.string.tab_perfil
                        ),
                        fontSize = 12.sp
                    )
                },

                colors = navigationItemColors()
            )
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MainGreen,
    selectedTextColor = MainGreen,
    unselectedIconColor = SecondaryText,
    unselectedTextColor = SecondaryText,
    indicatorColor = Color.Transparent
)

@Preview(showBackground = true)
@Composable
fun ManageCollectorsScreenPreview() {
    RebottleTheme {
        ManageCollectorsScreen()
    }
}
