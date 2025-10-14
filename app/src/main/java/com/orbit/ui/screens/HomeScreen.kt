package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import com.orbit.ui.components.OrderCard
import com.orbit.ui.components.QuickActionButton
import com.orbit.ui.components.SummaryCard
import com.orbit.ui.components.DateSelector
import com.orbit.ui.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
 

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOrdersClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onAddOrderClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isOrdersVisible by remember { mutableStateOf(true) }
    var isSalesVisible by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orbit",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.5).sp
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { /* Settings */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFF8F9FA),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onAddOrderClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF007AFF),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppleSummaryCard(
                    title = "Pedidos de hoy",
                    value = uiState.todayOrdersCount.toString(),
                    icon = Icons.Default.LocalShipping,
                    iconColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    isAmountVisible = isOrdersVisible,
                    onEyeClick = { isOrdersVisible = !isOrdersVisible }
                )
                
                AppleSummaryCard(
                    title = "Venta Total",
                    value = "$${String.format("%.2f", uiState.todaySales)}",
                    icon = Icons.Default.AttachMoney,
                    iconColor = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f),
                    isAmountVisible = isSalesVisible,
                    onEyeClick = { isSalesVisible = !isSalesVisible }
                )
            }
        }
        
        // Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppleActionButton(
                    title = "Pedidos",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = Color(0xFF2196F3),
                    onClick = onOrdersClick,
                    modifier = Modifier.weight(1f)
                )
                
                AppleActionButton(
                    title = "Inventario",
                    icon = Icons.Default.Inventory,
                    iconColor = Color(0xFFFF9800),
                    onClick = onInventoryClick,
                    modifier = Modifier.weight(1f)
                )
                
                AppleActionButton(
                    title = "Mapa",
                    icon = Icons.Default.LocationOn,
                    iconColor = Color(0xFF4CAF50),
                    onClick = { /* Mapa */ },
                    modifier = Modifier.weight(1f)
                )
                
                AppleActionButton(
                    title = "Reportes",
                    icon = Icons.Default.BarChart,
                    iconColor = Color(0xFF9C27B0),
                    onClick = { /* Reporte */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Date Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF000000),
                        ambientColor = Color(0xFF000000)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                DateSelector(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        
        // Recent Orders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedidos recientes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.3).sp
                )
                
                IconButton(
                    onClick = { /* Calendar */ },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color(0xFFF2F2F7),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Calendario",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // Orders List
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2196F3))
                }
            }
        } else {
            items(uiState.recentOrders) { orderWithDetails ->
                OrderCard(
                    orderWithDetails = orderWithDetails,
                    onClick = { /* Navigate to order detail */ }
                )
            }
        }
        
        // Extra padding at the end to ensure full scroll
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


// Apple Design Components
@Composable
private fun AppleSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isAmountVisible: Boolean = true,
    onEyeClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row with icon, value and eye icon (TODO EN UNA LÍNEA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Icon with strong background
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            iconColor.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Center: Value (oculto o visible)
                Text(
                    text = if (isAmountVisible) value else "••••",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.5).sp
                )

                // Right: Eye icon (clickeable)
                IconButton(
                    onClick = onEyeClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isAmountVisible) "Ocultar" else "Mostrar",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom: Title
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF48484A),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun AppleActionButton(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono LIBRE, sin fondo de color
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Título debajo del ícono (como en la segunda imagen)
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F),
                letterSpacing = (-0.1).sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

