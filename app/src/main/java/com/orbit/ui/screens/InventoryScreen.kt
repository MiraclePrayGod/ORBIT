package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import com.orbit.ui.theme.HomeColors
import com.orbit.ui.theme.HomeDimens
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.Product
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.ui.viewmodel.InventoryViewModel
import com.orbit.ui.viewmodel.InventoryUiState
import com.orbit.ui.viewmodel.InventoryStats
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBackClick: () -> Unit = {},
    onNewVisualClick: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inventoryStats by viewModel.inventoryStats.collectAsState()
    val lowStockAlerts by viewModel.lowStockAlerts.collectAsState()
    var isVisualView by remember { mutableStateOf(false) }

    com.orbit.ui.components.ResponsiveContainer(
        modifier = Modifier.background(BackgroundPrimary)
    ) {
        when (val state = uiState) {
            is InventoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is InventoryUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Reintentar")
                    }
                }
            }
            
            is InventoryUiState.Success -> {
                InventoryContent(
                    state = state,
                    inventoryStats = inventoryStats,
                    lowStockAlerts = lowStockAlerts,
                    onBackClick = onBackClick,
                    onNewVisualClick = onNewVisualClick,
                    isVisualView = isVisualView,
                    onToggleView = { isVisualView = !isVisualView },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun InventoryContent(
    state: InventoryUiState.Success,
    inventoryStats: InventoryStats,
    lowStockAlerts: List<com.orbit.domain.usecase.inventory.StockAlert>,
    onBackClick: () -> Unit,
    onNewVisualClick: () -> Unit,
    isVisualView: Boolean,
    onToggleView: () -> Unit,
    viewModel: InventoryViewModel
) {
    Column {
        // Header
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
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = "Inventario",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            // Toggle entre vista lista y vista visual (más grande y visible)
            IconButton(
                onClick = onToggleView,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    if (isVisualView) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                    contentDescription = if (isVisualView) "Vista Lista" else "Vista Visual",
                    tint = if (isVisualView) PrimaryBlue else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Estadísticas rápidas
        if (lowStockAlerts.isNotEmpty() || inventoryStats.outOfStockCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (inventoryStats.outOfStockCount > 0) 
                        Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (inventoryStats.outOfStockCount > 0) 
                                "⚠️ Productos agotados: ${inventoryStats.outOfStockCount}" 
                            else "⚠️ Stock bajo: ${inventoryStats.lowStockCount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (inventoryStats.outOfStockCount > 0) 
                                Color(0xFFD32F2F) else Color(0xFFFF6F00)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Title and Count (solo en vista lista)
        if (!isVisualView) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "${state.filteredProducts.size} productos",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Search Bar (solo en vista lista)
        if (!isVisualView) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Category Filter Tabs (solo en vista lista)
        if (!isVisualView) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryTab(
                    text = "Todas",
                    isSelected = state.selectedCategory == ProductCategory.ALL,
                    onClick = { viewModel.setCategory(ProductCategory.ALL) },
                    modifier = Modifier.weight(1f)
                )
                
                CategoryTab(
                    text = "Espiritual",
                    isSelected = state.selectedCategory == ProductCategory.SPIRITUAL,
                    onClick = { viewModel.setCategory(ProductCategory.SPIRITUAL) },
                    modifier = Modifier.weight(1f)
                )
                
                CategoryTab(
                    text = "Otro",
                    isSelected = state.selectedCategory == ProductCategory.OTHER,
                    onClick = { viewModel.setCategory(ProductCategory.OTHER) },
                    modifier = Modifier.weight(1f)
                )
                
                CategoryTab(
                    text = "Psicología",
                    isSelected = state.selectedCategory == ProductCategory.PSYCHOLOGY,
                    onClick = { viewModel.setCategory(ProductCategory.PSYCHOLOGY) },
                    modifier = Modifier.weight(1f)
                )
                
                CategoryTab(
                    text = "Salud",
                    isSelected = state.selectedCategory == ProductCategory.HEALTH,
                    onClick = { viewModel.setCategory(ProductCategory.HEALTH) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Contenido según la vista seleccionada
        if (isVisualView) {
            // Vista Visual con tarjetas estilo HomeScreen
            VisualInventoryView(
                products = state.filteredProducts,
                onCategoryClick = { category ->
                    viewModel.setCategory(category)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Vista Lista tradicional
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (state.filteredProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.searchQuery.isNotEmpty()) 
                                    "No se encontraron productos" 
                                else "No hay productos",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    items(state.filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onAddQuantity = { viewModel.addProductQuantity(product.id, 1) },
                            onSubtractQuantity = { viewModel.subtractProductQuantity(product.id, 1) },
                            onDelete = { viewModel.deleteProduct(product.id) }
                        )
                    }
                }
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
    onAddQuantity: () -> Unit = {},
    onSubtractQuantity: () -> Unit = {},
    onDelete: () -> Unit = {},
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
                    IconButton(onClick = onSubtractQuantity) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Reducir",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(onClick = onAddQuantity) {
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


@Composable
private fun VisualInventoryView(
    products: List<Product>,
    onCategoryClick: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    // Grid de tarjetas estilo HomeScreen
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(listOf(
            ProductCategory.SPIRITUAL,
            ProductCategory.PSYCHOLOGY,
            ProductCategory.HEALTH,
            ProductCategory.OTHER
        )) { category ->
            val categoryProducts = products.filter { it.category == category }
            HomeStyleCategoryCard(
                category = category,
                categoryProducts = categoryProducts,
                onClick = { onCategoryClick(category) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HomeStyleCategoryCard(
    category: ProductCategory,
    categoryProducts: List<Product>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryCount = categoryProducts.size
    val availableCount = categoryProducts.count { it.status == InventoryStatus.AVAILABLE }
    val lowStockCount = categoryProducts.count { it.status == InventoryStatus.LOW_STOCK }
    val outOfStockCount = categoryProducts.count { it.status == InventoryStatus.OUT_OF_STOCK }
    
    val (categoryName, categoryColor, categoryIcon) = when (category) {
        ProductCategory.SPIRITUAL -> Triple("Espiritual", HomeColors.Purple, "🧘")
        ProductCategory.PSYCHOLOGY -> Triple("Psicología", HomeColors.Blue, "📚")
        ProductCategory.HEALTH -> Triple("Salud", HomeColors.Green, "💊")
        ProductCategory.OTHER -> Triple("Otro", HomeColors.Orange, "🔮")
        else -> Triple("Todas", HomeColors.TextPrimary, "📦")
    }
    
    Card(
        modifier = modifier
            .height(180.dp)
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(HomeDimens.SummaryCorner),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(HomeDimens.SummaryCorner),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeDimens.SummaryPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con icono y nombre
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icono con fondo estilo HomeScreen
                    Box(
                        modifier = Modifier
                            .size(HomeDimens.SummaryIconContainer)
                            .background(
                                categoryColor.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = categoryIcon,
                            fontSize = HomeDimens.SummaryInnerIcon.value.sp
                        )
                    }
                    
                    Column {
                        Text(
                            text = categoryName,
                            fontSize = HomeDimens.SummaryTitleSize,
                            fontWeight = FontWeight.W600,
                            color = HomeColors.TextPrimary,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            text = "Estante",
                            fontSize = 11.sp,
                            color = HomeColors.TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Contador de productos
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$categoryCount",
                        fontSize = HomeDimens.SummaryValueSize,
                        fontWeight = FontWeight.W600,
                        color = categoryColor
                    )
                }
            }
            
            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                val fillPercentage = ((availableCount.toFloat() / categoryCount.coerceAtLeast(1)) * 100).coerceIn(0f, 100f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fillPercentage / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(categoryColor)
                )
            }
            
            // Mini productos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(minOf(categoryCount, 6)) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index < availableCount) categoryColor.copy(alpha = 0.6f)
                                else if (index < availableCount + lowStockCount) HomeColors.Orange.copy(alpha = 0.4f)
                                else Color(0xFFE0E0E0)
                            )
                    )
                }
            }
            
            // Estadísticas estilo HomeScreen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (availableCount > 0) {
                    VisualStatBadge(
                        text = "$availableCount",
                        label = "Disponible",
                        color = HomeColors.Green,
                        icon = "✓"
                    )
                }
                if (lowStockCount > 0) {
                    VisualStatBadge(
                        text = "$lowStockCount",
                        label = "Bajo",
                        color = HomeColors.Orange,
                        icon = "⚠"
                    )
                }
                if (outOfStockCount > 0) {
                    VisualStatBadge(
                        text = "$outOfStockCount",
                        label = "Agotado",
                        color = HomeColors.Red,
                        icon = "✕"
                    )
                }
            }
        }
    }
}

@Composable
private fun VisualStatBadge(
    text: String,
    label: String,
    color: Color,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = color,
                letterSpacing = (-0.3).sp
            )
        }
        Text(
            text = label,
            fontSize = 9.sp,
            color = HomeColors.TextTertiary,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    InventoryScreen()
}
