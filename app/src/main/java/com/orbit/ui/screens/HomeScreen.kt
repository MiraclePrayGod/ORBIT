package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.ui.components.DateSelectorCard
import com.orbit.ui.components.OrderCard
import com.orbit.ui.components.SummaryCard
import com.orbit.ui.components.QuickActionsSection
import com.orbit.ui.components.RecentOrdersHeader
import com.orbit.ui.components.LoadingIndicator
import com.orbit.ui.viewmodel.HomeViewModel
import com.orbit.ui.theme.HomeColors
import com.orbit.ui.theme.HomeStrings
import com.orbit.ui.theme.cupertinoCardShadow
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
    onMapClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isOrdersVisible by remember { mutableStateOf(true) }
    var isSalesVisible by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
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
        item { QuickActionsSection(onOrdersClick, onInventoryClick, onMapClick) }
        
        // Selector de fechas
        item { DateSelectorCard(selectedDate, onDateSelected = { selectedDate = it }) }
        
        // Título de pedidos recientes
        item { RecentOrdersHeader() }
        
        // Lista de pedidos recientes
        if (uiState.isLoading) {
            item { LoadingIndicator() }
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
            text = HomeStrings.AppTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.W600,
            color = HomeColors.TextPrimary,
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
                contentDescription = HomeStrings.Add,
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
            title = HomeStrings.OrdersToday,
            value = uiState.todayOrdersCount.toString(),
            icon = Icons.Default.DirectionsCar,
            iconColor = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f),
            isAmountVisible = isOrdersVisible,
            onEyeClick = onOrdersVisibilityToggle
        )

        SummaryCard(
            title = HomeStrings.TotalSales,
            value = "$${String.format("%.2f", uiState.todaySales)}",
            icon = Icons.Default.MonetizationOn,
            iconColor = Color(0xFF9C27B0),
            modifier = Modifier.weight(1f),
            isAmountVisible = isSalesVisible,
            onEyeClick = onSalesVisibilityToggle
        )
    }
}

// SummaryCard y QuickActionButton se movieron a com.orbit.ui.components

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}