package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.ui.components.DateSelector
import com.orbit.ui.components.OrderCard
import com.orbit.ui.viewmodel.HomeViewModel
import java.time.LocalDate

/**
 * Pantalla principal de la aplicación Orbit
 * Muestra resumen de pedidos, acciones rápidas y lista de pedidos recientes
 */
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
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
    ) {
        // Header con título y botones de acción
        item {
            HomeHeader(
                onAddOrderClick = onAddOrderClick
            )
        }
        
        // Tarjetas de resumen (Pedidos de hoy y Venta Total)
        item {
            SummaryCardsSection(
                uiState = uiState,
                isOrdersVisible = isOrdersVisible,
                isSalesVisible = isSalesVisible,
                onOrdersVisibilityToggle = { isOrdersVisible = !isOrdersVisible },
                onSalesVisibilityToggle = { isSalesVisible = !isSalesVisible }
            )
        }
        
        // Botones de acción rápida
        item {
            QuickActionsSection(
                onOrdersClick = onOrdersClick,
                onInventoryClick = onInventoryClick
            )
        }
        
        // Selector de fechas
        item {
            DateSelectorCard(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }
        
        // Título de pedidos recientes
        item {
            RecentOrdersHeader()
        }
        
        // Lista de pedidos recientes
        if (uiState.isLoading) {
            item {
                LoadingIndicator()
            }
        } else {
            items(uiState.recentOrders) { orderWithDetails ->
                OrderCard(
                    orderWithDetails = orderWithDetails,
                    onClick = { /* Navigate to order detail */ }
                )
            }
        }
        
        // Espaciado final
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Header de la pantalla con título y botones de acción
 */
@Composable
private fun HomeHeader(
    onAddOrderClick: () -> Unit
) {
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
        
        // Botón de agregar pedido
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    Color(0xFF007AFF),
                    CircleShape
                )
                .clickable { onAddOrderClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/**
 * Sección de tarjetas de resumen (Pedidos de hoy y Venta Total)
 */
@Composable
private fun SummaryCardsSection(
    uiState: com.orbit.ui.viewmodel.HomeUiState,
    isOrdersVisible: Boolean,
    isSalesVisible: Boolean,
    onOrdersVisibilityToggle: () -> Unit,
    onSalesVisibilityToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SummaryCard(
            title = "Pedidos de hoy",
            value = uiState.todayOrdersCount.toString(),
            icon = Icons.Default.LocalShipping,
            iconColor = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f),
            isAmountVisible = isOrdersVisible,
            onEyeClick = onOrdersVisibilityToggle
        )
        
        SummaryCard(
            title = "Venta Total",
            value = "$${String.format("%.2f", uiState.todaySales)}",
            icon = Icons.Default.MonetizationOn,
            iconColor = Color(0xFF9C27B0),
            modifier = Modifier.weight(1f),
            isAmountVisible = isSalesVisible,
            onEyeClick = onSalesVisibilityToggle
        )
    }
}

/**
 * Sección de botones de acción rápida
 */
@Composable
private fun QuickActionsSection(
    onOrdersClick: () -> Unit,
    onInventoryClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(
            title = "Pedidos",
            icon = Icons.Default.ShoppingCart,
            iconColor = Color(0xFF2196F3),
            onClick = onOrdersClick,
            modifier = Modifier.weight(1f)
        )
        
        QuickActionButton(
            title = "Inventario",
            icon = Icons.Default.Inventory,
            iconColor = Color(0xFFFF9800),
            onClick = onInventoryClick,
            modifier = Modifier.weight(1f)
        )
        
        QuickActionButton(
            title = "Mapa",
            icon = Icons.Default.LocationOn,
            iconColor = Color(0xFF4CAF50),
            onClick = { /* Mapa */ },
            modifier = Modifier.weight(1f)
        )
        
        QuickActionButton(
            title = "Reportes",
            icon = Icons.Default.BarChart,
            iconColor = Color(0xFF9C27B0),
            onClick = { /* Reporte */ },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Tarjeta contenedora del selector de fechas
 */
@Composable
private fun DateSelectorCard(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
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
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

/**
 * Header de la sección de pedidos recientes
 */
@Composable
private fun RecentOrdersHeader() {
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

/**
 * Indicador de carga
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF2196F3))
    }
}

/**
 * Tarjeta de resumen con icono, valor y botón de visibilidad
 */
@Composable
private fun SummaryCard(
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
            .height(90.dp)
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
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Fila superior con icono, valor y botón de ojo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono con fondo - primero
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            iconColor.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Valor (visible u oculto) - centrado
                Text(
                    text = if (isAmountVisible) value else "••••",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Botón de ojo (clickeable) - al final
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

            // Título centrado en la parte inferior
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

/**
 * Botón de acción rápida con icono y texto
 */
@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Título
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