package com.rebottle.app.ui.screens.fundacion.gestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rebottle.app.R
import com.rebottle.app.navigation.AppScreens
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
fun CollectionRequestsScreen(
    controller: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onDetailsClick: (RequestUiModel) -> Unit = {},
) {
    val requests = listOf(
        RequestUiModel(
            id = 1,
            address = "Cra 15# 136-32",
            items = "Metal, vidrio.",
            weight = "8 kg",
            date = "Septiembre 12",
            time = "9:00 AM",
            assignee = "",
            status = RequestStatus.PENDING
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            CollectionBottomBar(controller = controller)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Header(onBackClick = onBackClick)
            }

            item {
                SearchBar()
            }

            item {
                StatsGrid()
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        onClick = onCreateClick,
                        color = MainGreen,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.create_request_label) + " →",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )
            }

            items(requests) { request ->
                HistoryCard(
                    request = request
                ) {
                    onDetailsClick(request)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = MainText
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.collection_requests_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = MainText
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                color = SecondaryText,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_box),
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun StatsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                label = stringResource(R.string.total_recycled_label),
                value = "25 KG",
                iconRes = R.drawable.ic_recycled,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.completed_label),
                value = "10",
                iconRes = R.drawable.ic_check_circle,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                label = stringResource(R.string.pending_label),
                value = "5",
                iconRes = R.drawable.ic_weight,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.en_route_label),
                value = "9",
                iconRes = R.drawable.icon_collection,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MainOrange
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.LightGray, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = SecondaryText)
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MainGreen)
            }
        }
    }
}

@Composable
private fun HistoryCard(
    request: RequestUiModel,
    onDetailsClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightOrange),
        border = BorderStroke(1.dp, BorderOrange)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.collection_label, request.id),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MainText
                )
                StatusBadge(text = stringResource(R.string.status_pending))
            }
            Spacer(modifier = Modifier.height(12.dp))
            DetailItem(icon = Icons.Outlined.LocationOn, text = request.address)
            DetailItem(iconRes = R.drawable.ic_box, text = request.items)
            DetailItem(iconRes = R.drawable.ic_weight, text = request.weight)
            DetailItem(iconRes = R.drawable.ic_calendar, text = request.date)
            DetailItem(iconRes = R.drawable.ic_clock, text = request.time)
            
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.view_details_label) + " →",
                    color = Brown,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onDetailsClick() }
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String) {
    Surface(
        color = MainOrange,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailItem(
    text: String,
    icon: ImageVector? = null,
    iconRes: Int? = null
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MainOrange
            )
        } else if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MainOrange
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = SecondaryText)
    }
}

@Composable
private fun CollectionBottomBar(controller: NavController) {

    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route



    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == AppScreens.homeFundacion.name,
            onClick = {
                if (currentRoute != AppScreens.homeFundacion.name) {
                    controller.navigate(AppScreens.homeFundacion.name)
                }
            },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text(stringResource(R.string.tab_inicio), fontSize = 12.sp) },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = currentRoute == AppScreens.solicitudes.name,
            onClick = {
                controller.navigate(AppScreens.solicitudes.name)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_collection),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            },
            label = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.tab_solicitudes), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(MainGreen, CircleShape)
                    )
                }
            },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = currentRoute == AppScreens.rutas.name,
            onClick = {
                // TODO: descomentar cuando exista la pantalla de rutas
                // controller.navigate(AppScreens.rutas.name)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_routes),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.tab_rutas),
                    fontSize = 12.sp,
                    modifier = Modifier.offset(y = (-4).dp)
                )
            },
            colors = navigationItemColors()
        )
        NavigationBarItem(
            selected = currentRoute == AppScreens.perfil.name,
            onClick = {
                controller.navigate(AppScreens.perfil.name)
            },
            icon = { Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(26.dp)) },
            label = { Text(stringResource(R.string.tab_perfil), fontSize = 12.sp) },
            colors = navigationItemColors()
        )
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
fun CollectionRequestsScreenPreview() {
    RebottleTheme {
        //CollectionRequestsScreen()
    }
}
