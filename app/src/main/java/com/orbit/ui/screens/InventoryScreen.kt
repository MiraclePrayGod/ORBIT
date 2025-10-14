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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.Product
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.ui.viewmodel.InventoryViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBackClick: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val products by viewModel.products.collectAsState()

    com.orbit.ui.components.ResponsiveContainer(
        modifier = Modifier.background(BackgroundPrimary)
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
                        tint = Color.Black
                    )
                }
                
                Text(
                    text = "Inventario",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Row {
                IconButton(onClick = { /* Delete */ }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Gray
                    )
                }
                
                IconButton(onClick = { /* Stack */ }) {
                    Icon(
                        Icons.Default.StackedBarChart,
                        contentDescription = "Gestión",
                        tint = Color.Gray
                    )
                }
                
                IconButton(onClick = { /* Add */ }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = Color.Gray
                    )
                }
            }
        }
        
        // Title and Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inventario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "${products.size} productos",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryTab(
                text = "Todas",
                isSelected = selectedCategory == ProductCategory.ALL,
                onClick = { viewModel.setCategory(ProductCategory.ALL) },
                modifier = Modifier.weight(1f)
            )
            
            CategoryTab(
                text = "Espiritual",
                isSelected = selectedCategory == ProductCategory.SPIRITUAL,
                onClick = { viewModel.setCategory(ProductCategory.SPIRITUAL) },
                modifier = Modifier.weight(1f)
            )
            
            CategoryTab(
                text = "Otro",
                isSelected = selectedCategory == ProductCategory.OTHER,
                onClick = { viewModel.setCategory(ProductCategory.OTHER) },
                modifier = Modifier.weight(1f)
            )
            
            CategoryTab(
                text = "Psicología",
                isSelected = selectedCategory == ProductCategory.PSYCHOLOGY,
                onClick = { viewModel.setCategory(ProductCategory.PSYCHOLOGY) },
                modifier = Modifier.weight(1f)
            )
            
            CategoryTab(
                text = "Salu",
                isSelected = selectedCategory == ProductCategory.HEALTH,
                onClick = { viewModel.setCategory(ProductCategory.HEALTH) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Inventory Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product)
            }
        }
    }
}

@Composable
private fun CategoryTab(
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
            containerColor = if (isSelected) PrimaryBlue else Color.Transparent
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
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else TextSecondary
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "ID: ${product.id}",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                
                StatusChip(status = product.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quantity breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuantityColumn(
                    quantity = product.availableQuantity,
                    label = "Disponible",
                    isHighlighted = true
                )
                
                QuantityColumn(
                    quantity = product.reservedQuantity,
                    label = "Reservado",
                    isHighlighted = false
                )
                
                QuantityColumn(
                    quantity = product.totalQuantity,
                    label = "Total",
                    isHighlighted = false
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Process status and last updated
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Precio: $${String.format("%.2f", product.unitPrice)}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                
                Row {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(onClick = { /* Add */ }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: InventoryStatus) {
    val (text, color) = when (status) {
        InventoryStatus.AVAILABLE -> "Disponible" to PrimaryBlue
        InventoryStatus.OUT_OF_STOCK -> "Agotado" to StockOut
        InventoryStatus.LOW_STOCK -> "Poco Stock" to StockLow
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun QuantityColumn(
    quantity: Int,
    label: String,
    isHighlighted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) PrimaryBlue else TextPrimary
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}


@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    InventoryScreen()
}
