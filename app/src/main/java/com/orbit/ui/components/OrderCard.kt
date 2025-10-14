package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.relation.OrderWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderCard(
    orderWithDetails: OrderWithDetails,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val order = orderWithDetails.order
    val client = orderWithDetails.client
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row with order number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Pedido #${order.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                
                StatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order details
            Text(
                text = "Cliente: ${client.name}",
                fontSize = 14.sp,
                color = Color(0xFF000000)
            )
            
            Text(
                text = "Producto: Producto ${order.id}",
                fontSize = 14.sp,
                color = Color(0xFF000000)
            )
            
            Text(
                text = "Cantidad: ${orderWithDetails.orderItems.size}",
                fontSize = 14.sp,
                color = Color(0xFF000000)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Bottom row with date and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Date with clock icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Creado: ${formatOrderDate(order.createdAt)}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                // Right: Price and payment method
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", order.totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )
                    Text(
                        text = getPaymentMethodText(order.paymentMethod),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (text, color) = when (status) {
        OrderStatus.IN_PROGRESS -> "En Proceso" to Color(0xFF2196F3)
        OrderStatus.PAID -> "Pagado" to Color(0xFF4CAF50)
        OrderStatus.PENDING -> "Pendiente" to Color(0xFFFF9800)
        OrderStatus.CANCELLED -> "Cancelado" to Color(0xFFF44336)
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

private fun formatOrderDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}

private fun getPaymentMethodText(paymentMethod: PaymentMethod): String {
    return when (paymentMethod) {
        PaymentMethod.CASH -> "Efectivo"
        PaymentMethod.INSTALLMENTS -> "Por Partes"
        PaymentMethod.CARD -> "Tarjeta"
        PaymentMethod.TRANSFER -> "Transferencia"
    }
}