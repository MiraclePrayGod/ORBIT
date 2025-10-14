package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.relation.OrderWithDetails
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderCardWithDetails(
    orderWithDetails: OrderWithDetails,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
    onClick: () -> Unit = {}
) {
    val order = orderWithDetails.order
    val client = orderWithDetails.client
    val orderItems = orderWithDetails.orderItems
    
    Card(
        modifier = if (isVertical) {
            modifier
                .fillMaxWidth()
                .height(160.dp)
        } else {
            modifier
                .width(280.dp)
                .height(160.dp)
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            // Top row with order number and price/status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Order number on the left
                Text(
                    text = "Pedido #${order.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    StatusChip(status = order.status)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "$${String.format("%.2f", order.totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Text(
                        text = getPaymentMethodText(order.paymentMethod),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Order details
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = "Cliente: ${client.name}",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                
                // Show product summary
                Text(
                    text = "Productos: ${orderItems.size} item(s)",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(3.dp))
                    
                    Text(
                        text = "Creado: ${formatDate(order.createdAt)}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (text, color) = when (status) {
        OrderStatus.IN_PROGRESS -> "En Proceso" to OrderInProgress
        OrderStatus.PAID -> "Pagado" to OrderPaid
        OrderStatus.PENDING -> "Pendiente" to OrderPending
        OrderStatus.CANCELLED -> "Cancelado" to OrderCancelled
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

private fun getPaymentMethodText(paymentMethod: PaymentMethod): String {
    return when (paymentMethod) {
        PaymentMethod.CASH -> "Efectivo"
        PaymentMethod.INSTALLMENTS -> "Por Partes"
        PaymentMethod.CARD -> "Tarjeta"
        PaymentMethod.TRANSFER -> "Transferencia"
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun OrderCardWithDetailsPreview() {
    // This would need sample data for preview
    // OrderCardWithDetails(orderWithDetails = sampleOrderWithDetails, isVertical = true)
}
