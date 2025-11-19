package com.orbit.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.orbit.data.entity.Product
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.ui.theme.*
import com.orbit.ui.theme.HomeColors
import com.orbit.ui.theme.HomeDimens
import com.orbit.ui.viewmodel.InventoryViewModel
import com.orbit.ui.viewmodel.InventoryUiState
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryVisualScreen(
    onBackClick: () -> Unit = {},
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var zoomLevel by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    com.orbit.ui.components.ResponsiveContainer(
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF)
                )
            )
        )
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
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.Black
                                )
                            }
                            
                            Text(
                                text = "Vista Visual",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        
                        if (selectedCategory != null) {
                            IconButton(onClick = { selectedCategory = null }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                    
                    // Search Bar
                    if (selectedCategory == null) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                viewModel.setSearchQuery(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar productos...") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        searchQuery = ""
                                        viewModel.setSearchQuery("")
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Limpiar"
                                        )
                                    }
                                }
                            },
                            singleLine = true
                        )
                    }
                    
                    // Content
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectedCategory == null) {
                            // Vista de categorías (estantes)
                            CategoryShelvesView(
                                products = state.filteredProducts,
                                onCategoryClick = { category ->
                                    selectedCategory = category
                                    viewModel.setCategory(category)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTransformGestures { _, _, zoom, _ ->
                                            zoomLevel = (zoomLevel * zoom).coerceIn(0.5f, 2f)
                                        }
                                    }
                            )
                        } else {
                            // Vista de productos de una categoría
                            CategoryProductsView(
                                category = selectedCategory!!,
                                products = state.filteredProducts.filter { 
                                    it.category == selectedCategory || selectedCategory == ProductCategory.ALL
                                },
                                onProductClick = { product ->
                                    // Aquí puedes abrir un diálogo o navegar a detalles
                                },
                                onBackClick = { selectedCategory = null },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryShelvesView(
    products: List<Product>,
    onCategoryClick: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    // Fondo simple
    RoomBackground()
    
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
            HomeStyleShelfCard(
                category = category,
                categoryProducts = categoryProducts,
                onClick = { onCategoryClick(category) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoomBackground() {
    // Fondo simple y limpio
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    )
}

@Composable
private fun HomeStyleShelfCard(
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
                    StatBadgeHomeStyle(
                        text = "$availableCount",
                        label = "Disponible",
                        color = HomeColors.Green,
                        icon = "✓"
                    )
                }
                if (lowStockCount > 0) {
                    StatBadgeHomeStyle(
                        text = "$lowStockCount",
                        label = "Bajo",
                        color = HomeColors.Orange,
                        icon = "⚠"
                    )
                }
                if (outOfStockCount > 0) {
                    StatBadgeHomeStyle(
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
private fun StatBadgeHomeStyle(
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

@Composable
private fun CategoryProductsView(
    category: ProductCategory,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = when (category) {
        ProductCategory.SPIRITUAL -> "Espiritual"
        ProductCategory.PSYCHOLOGY -> "Psicología"
        ProductCategory.HEALTH -> "Salud"
        ProductCategory.OTHER -> "Otro"
        else -> "Todas"
    }
    
    Column(modifier = modifier) {
        // Header de categoría
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${products.size} productos",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
        
        // Grid de productos
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay productos en esta categoría",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(products) { product ->
                    ProductIconCard(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductIconCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (product.status) {
        InventoryStatus.AVAILABLE -> Color(0xFF4CAF50)
        InventoryStatus.LOW_STOCK -> Color(0xFFFF9800)
        InventoryStatus.OUT_OF_STOCK -> Color(0xFFF44336)
    }
    
    val statusText = when (product.status) {
        InventoryStatus.AVAILABLE -> "Disponible"
        InventoryStatus.LOW_STOCK -> "Bajo Stock"
        InventoryStatus.OUT_OF_STOCK -> "Agotado"
    }
    
    // Animación de entrada
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (product.status == InventoryStatus.LOW_STOCK) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Card(
        modifier = modifier
            .height(180.dp)
            .graphicsLayer {
                scaleX = if (product.status == InventoryStatus.LOW_STOCK) pulseScale else 1f
                scaleY = if (product.status == InventoryStatus.LOW_STOCK) pulseScale else 1f
            }
            .clickable(onClick = onClick)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = statusColor.copy(alpha = 0.3f),
                ambientColor = statusColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.05f),
                            Color.White
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header con nombre y estado
                Column {
                    Text(
                        text = product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Badge de estado
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(statusColor)
                            )
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = statusColor
                            )
                        }
                    }
                }
                
                // Indicador de cantidad mejorado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cantidad",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                statusColor.copy(alpha = 0.3f),
                                                statusColor.copy(alpha = 0.15f)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${product.availableQuantity}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                            
                            if (product.reservedQuantity > 0) {
                                Text(
                                    text = "+${product.reservedQuantity}",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    // Indicador visual de stock
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        // Barra de stock
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            val stockPercentage = (product.availableQuantity.toFloat() / product.totalQuantity.coerceAtLeast(1) * 100).coerceIn(0f, 100f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(stockPercentage / 100f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(statusColor)
                            )
                        }
                    }
                }
                
                // Footer con precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Precio unitario",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$${String.format("%.2f", product.unitPrice)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    
                    // Valor total del stock
                    if (product.availableQuantity > 0) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Valor total",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "$${String.format("%.2f", product.unitPrice * product.availableQuantity)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

