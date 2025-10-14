package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import com.orbit.ui.viewmodel.OrdersViewModel
import com.orbit.ui.viewmodel.OrderFilter
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit = {},
    onOrderClick: (com.orbit.data.relation.OrderWithDetails) -> Unit = {},
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
            
            Row {
                IconButton(onClick = { /* Search */ }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSecondary
                    )
                }
                
                IconButton(onClick = { /* Delete */ }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = TextSecondary
                    )
                }
                
                IconButton(onClick = { /* Add */ }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = TextSecondary
                    )
                }
            }
        }
        
        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Orders List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(orders) { orderWithDetails ->
                    OrderCard(
                        orderWithDetails = orderWithDetails,
                        onClick = { onOrderClick(orderWithDetails) }
                    )
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
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ButtonPrimary else Color.Transparent
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
                fontWeight = FontWeight.Medium,
                color = if (isSelected) TextInverse else TextSecondary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}
