package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import com.orbit.ui.components.ProfessionalButton
import com.orbit.ui.components.ProfessionalCard
import com.orbit.ui.components.ButtonVariant
import com.orbit.ui.components.ButtonSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import com.orbit.ui.components.OrderCard
import com.orbit.ui.components.OrderCardCompact
import com.orbit.ui.viewmodel.OrdersViewModel
import com.orbit.ui.viewmodel.OrderFilter
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit = {},
    onOrderClick: (com.orbit.data.relation.OrderWithDetails) -> Unit = {},
    onMapClick: () -> Unit = {},
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val orders by viewModel.orders.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isGrid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .statusBarsPadding()
    ) {
        // Header minimal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
                
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = { isGrid = !isGrid }) {
                    Icon(
                        if (isGrid) Icons.Default.ViewList else Icons.Default.ViewModule,
                        contentDescription = "Cambiar vista",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = { /* Add */ }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Search Bar (condicional)
        if (showSearchBar) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por cliente o teléfono...", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = TextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
        
        // Filter Tabs en un solo contenedor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(22.dp))
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterTab(
                    text = "Todos",
                    isSelected = selectedFilter == OrderFilter.ALL,
                    onClick = { viewModel.setFilter(OrderFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
                FilterTab(
                    text = "En Proceso",
                    isSelected = selectedFilter == OrderFilter.IN_PROGRESS,
                    onClick = { viewModel.setFilter(OrderFilter.IN_PROGRESS) },
                    modifier = Modifier.weight(1f)
                )
                FilterTab(
                    text = "Pagados",
                    isSelected = selectedFilter == OrderFilter.PAID,
                    onClick = { viewModel.setFilter(OrderFilter.PAID) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Orders content (Lista o Grid)
        if (!isGrid) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { orderWithDetails ->
                    OrderCard(
                        orderWithDetails = orderWithDetails,
                        onClick = { onOrderClick(orderWithDetails) },
                        onMapClick = onMapClick
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { orderWithDetails ->
                    OrderCardCompact(
                        orderWithDetails = orderWithDetails,
                        onClick = { onOrderClick(orderWithDetails) },
                        onMapClick = onMapClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF0A84FF) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF3A3A3C)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}
