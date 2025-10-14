package com.orbit.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.orbit.data.entity.Product
import com.orbit.ui.components.AppleButton
import com.orbit.ui.components.AppleButtonVariant
import com.orbit.ui.components.AppleButtonSize

@Composable
fun ProductSelectionDialog(
    products: List<Product>,
    onProductSelected: (Product, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedQuantity by remember { mutableStateOf(1) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0xFF000000),
                    ambientColor = Color(0xFF000000)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Producto",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W700,
                        color = Color(0xFF1C1C1E),
                        letterSpacing = (-0.4).sp
                    )
                    
                    // Close button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2F2F7))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W400,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
                
                // Product List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(products) { product ->
                        AppleProductCard(
                            product = product,
                            isSelected = selectedProduct == product,
                            onClick = { selectedProduct = product }
                        )
                    }
                }
                
                // Quantity Selector
                if (selectedProduct != null) {
                    AppleQuantitySelector(
                        selectedProduct = selectedProduct!!,
                        selectedQuantity = selectedQuantity,
                        onQuantityChange = { selectedQuantity = it }
                    )
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppleButton(
                        text = "Cancelar",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        variant = AppleButtonVariant.SECONDARY,
                        size = AppleButtonSize.LARGE
                    )
                    
                    AppleButton(
                        text = "Agregar",
                        onClick = {
                            selectedProduct?.let { product ->
                                onProductSelected(product, selectedQuantity)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        variant = AppleButtonVariant.PRIMARY,
                        size = AppleButtonSize.LARGE,
                        enabled = selectedProduct != null && selectedQuantity > 0
                    )
                }
            }
        }
    }
}

@Composable
private fun AppleProductCard(
    product: Product,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                Color(0xFF007AFF).copy(alpha = 0.08f) 
            else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "$${String.format("%.2f", product.unitPrice)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stock indicator
                AppleStockIndicator(
                    availableQuantity = product.availableQuantity
                )
                
                // Selection indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF007AFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppleStockIndicator(
    availableQuantity: Int
) {
    val (text, color, backgroundColor) = when {
        availableQuantity <= 0 -> Triple(
            "Sin Stock",
            Color(0xFFFF3B30),
            Color(0xFFFF3B30).copy(alpha = 0.1f)
        )
        availableQuantity < 10 -> Triple(
            "Stock Bajo",
            Color(0xFFFF9500),
            Color(0xFFFF9500).copy(alpha = 0.1f)
        )
        else -> Triple(
            "Stock: $availableQuantity",
            Color(0xFF34C759),
            Color(0xFF34C759).copy(alpha = 0.1f)
        )
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            color = color,
            letterSpacing = (-0.1).sp
        )
    }
}

@Composable
private fun AppleQuantitySelector(
    selectedProduct: Product,
    selectedQuantity: Int,
    onQuantityChange: (Int) -> Unit
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cantidad",
                fontSize = 17.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1C1C1E),
                letterSpacing = (-0.2).sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (selectedQuantity > 1) Color(0xFF007AFF) else Color(0xFFE5E5EA)
                        )
                        .clickable { 
                            if (selectedQuantity > 1) {
                                onQuantityChange(selectedQuantity - 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (selectedQuantity > 1) Color.White else Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Quantity display
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color(0xFF000000),
                            ambientColor = Color(0xFF000000)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$selectedQuantity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF1C1C1E),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Increase button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (selectedQuantity < selectedProduct.availableQuantity) 
                                Color(0xFF007AFF) else Color(0xFFE5E5EA)
                        )
                        .clickable { 
                            if (selectedQuantity < selectedProduct.availableQuantity) {
                                onQuantityChange(selectedQuantity + 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = if (selectedQuantity < selectedProduct.availableQuantity) 
                            Color.White else Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Total calculation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
                Text(
                    text = "$${String.format("%.2f", selectedProduct.unitPrice * selectedQuantity)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}