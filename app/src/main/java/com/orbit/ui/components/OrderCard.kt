package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Calcular dimensiones responsive
    val cardPadding = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 8.dp
            screenWidth < 400.dp -> 10.dp
            else -> 12.dp
        }
    }
    
    val (headerFontSize, bodyFontSize, smallFontSize) = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> Triple(13.sp, 12.sp, 11.sp) // header, body, small
            screenWidth < 400.dp -> Triple(14.sp, 13.sp, 12.sp)
            else -> Triple(16.sp, 14.sp, 13.sp)
        }
    }
    
    val spacingBetweenSections = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 4.dp
            screenWidth < 400.dp -> 6.dp
            else -> 8.dp
        }
    }
    
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacingBetweenSections)
        ) {
            // Top: Order number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Pedido #${order.id}",
                    fontSize = headerFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D1D1F),
                    letterSpacing = (-0.4).sp
                )
                
                StatusChip(status = order.status)
            }
            
            // Middle section: Order details on left, Amount and Payment on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left: Order details
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = "Cliente: ${client.name}",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF48484A),
                        letterSpacing = (-0.2).sp
                    )
                    
                    Text(
                        text = "Producto: Producto ${order.id}",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF48484A),
                        letterSpacing = (-0.2).sp
                    )
                    
                    Text(
                        text = "Cantidad: ${orderWithDetails.orderItems.size}",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF48484A),
                        letterSpacing = (-0.2).sp
                    )
                }
                
                // Right: Amount and Payment Method aligned with left content
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // Amount aligned with "Cliente:"
                    Text(
                        text = "$${String.format("%.2f", order.totalAmount)}",
                        fontSize = (headerFontSize.value + 5).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1D1F),
                        letterSpacing = (-0.5).sp
                    )
                    
                    // Payment method aligned with "Producto:"
                    Text(
                        text = getPaymentMethodText(order.paymentMethod),
                        fontSize = smallFontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E8E93),
                        letterSpacing = (-0.1).sp
                    )
                    
                    // Empty space to align with "Cantidad:"
                    Spacer(modifier = Modifier.height(bodyFontSize.value.dp + 1.dp))
                }
            }
            
            // Bottom: Date with calendar icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF8E8E93),
                    modifier = Modifier.size(if (screenWidth < 360.dp) 12.dp else 14.dp)
                )
                Spacer(modifier = Modifier.width(if (screenWidth < 360.dp) 4.dp else 6.dp))
                Text(
                    text = "Creado: ${formatOrderDate(order.createdAt)}",
                    fontSize = smallFontSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val (text, color) = when (status) {
        OrderStatus.IN_PROGRESS -> "En Proceso" to Color(0xFF007AFF)
        OrderStatus.PAID -> "Pagado" to Color(0xFF34C759)
        OrderStatus.PENDING -> "Pendiente" to Color(0xFFFF9500)
        OrderStatus.CANCELLED -> "Cancelado" to Color(0xFFFF3B30)
    }
    
    val chipFontSize = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 10.sp
            screenWidth < 400.dp -> 11.sp
            else -> 12.sp
        }
    }
    
    val chipPadding = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 6.dp to 3.dp
            screenWidth < 400.dp -> 7.dp to 3.dp
            else -> 8.dp to 4.dp
        }
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = chipPadding.first, vertical = chipPadding.second)
    ) {
        Text(
            text = text,
            fontSize = chipFontSize,
            fontWeight = FontWeight.Medium,
            color = color,
            letterSpacing = (-0.1).sp
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