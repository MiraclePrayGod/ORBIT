package com.orbit.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbit.data.entity.Product
import com.orbit.ui.components.AppleButton
import com.orbit.ui.components.AppleButtonVariant
import com.orbit.ui.components.AppleButtonSize
import com.orbit.ui.components.IconPosition
import com.orbit.ui.theme.*

@Composable
fun ProductsStep(
    orderItems: List<Pair<Product, Int>>,
    onAddProduct: () -> Unit,
    onUpdateQuantity: (Product, Int) -> Unit,
    onNavigateToPage: (Int) -> Unit,
    cameFromConfirm: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Productos",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        
        // Add Product Button
        AppleButton(
            text = "Agregar Producto",
            onClick = onAddProduct,
            modifier = Modifier.fillMaxWidth(),
            variant = AppleButtonVariant.SUCCESS,
            size = AppleButtonSize.LARGE,
            icon = Icons.Default.Add,
            iconPosition = IconPosition.START
        )
        
        // Products List
        if (orderItems.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orderItems) { (product, qty) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", product.unitPrice)} x $qty = $${String.format("%.2f", product.unitPrice * qty)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(product, qty - 1) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            StatusError.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Menos",
                                        tint = StatusError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Text(
                                    "$qty",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                
                                IconButton(
                                    onClick = { onUpdateQuantity(product, qty + 1) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            StatusSuccess.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Más",
                                        tint = StatusSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    if (cameFromConfirm) {
                        onNavigateToPage(3) // Regresar a confirmar
                    } else {
                        onNavigateToPage(0) // Ir a cliente
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ButtonPrimary
                )
            ) {
                Text(
                    text = if (cameFromConfirm) "Listo ✓" else "← Cliente",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Button(
                onClick = { 
                    if (cameFromConfirm) {
                        onNavigateToPage(3) // Regresar a confirmar
                    } else {
                        onNavigateToPage(2) // Ir a pago
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (orderItems.isNotEmpty()) 
                        ButtonPrimary else ButtonDisabled
                ),
                enabled = orderItems.isNotEmpty()
            ) {
                Text(
                    text = if (cameFromConfirm) "Listo ✓" else "Pago →",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextInverse
                )
            }
        }
    }
}
