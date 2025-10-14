package com.orbit.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.entity.Product
import com.orbit.ui.components.ProfessionalButton
import com.orbit.ui.components.ButtonVariant
import com.orbit.ui.components.ButtonSize
import com.orbit.ui.theme.*

@Composable
fun ConfirmStep(
    clientName: String,
    clientPhone: String,
    clientAddress: String,
    clientReference: String,
    orderItems: List<Pair<Product, Int>>,
    paymentMethod: PaymentMethod,
    notes: String,
    totalAmount: Double,
    onConfirm: () -> Unit,
    onNavigateToPage: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Confirmar Pedido",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        
        // Client Info - Clickable to edit
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToPage(0) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = ButtonPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cliente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Nombre: $clientName", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(text = "Teléfono: $clientPhone", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(text = "Dirección: $clientAddress", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                if (clientReference.isNotBlank()) {
                    Text(text = "Referencia: $clientReference", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
        
        // Products Summary - Clickable to edit
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToPage(1) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Productos (${orderItems.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                orderItems.forEach { (product, qty) ->
                    Text(
                        text = "• ${product.name} x $qty = $${String.format("%.2f", product.unitPrice * qty)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
        
        // Payment Summary - Clickable to edit
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToPage(2) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Pago",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Método: ${if (paymentMethod == PaymentMethod.CASH) "Efectivo" else "Tarjeta"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Total: $${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (notes.isNotBlank()) {
                    Text(
                        text = "Notas: $notes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onNavigateToPage(2) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ButtonPrimary
                )
            ) {
                Text(
                    text = "← Pago",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Create Order Button
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusSuccess
                )
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Crear Pedido",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
