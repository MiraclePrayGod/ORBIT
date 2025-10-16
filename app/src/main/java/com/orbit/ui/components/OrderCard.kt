package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.relation.OrderWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.orbit.ui.theme.HomeColors
import com.orbit.ui.theme.HomeStrings
import com.orbit.ui.theme.cupertinoCardShadow

@Composable
fun OrderCard(
    orderWithDetails: OrderWithDetails,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val order = orderWithDetails.order
    val client = orderWithDetails.client
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Calcular dimensiones responsive
    val cardPadding = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 10.dp
            screenWidth < 400.dp -> 12.dp
            else -> 14.dp
        }
    }
    
    val (headerFontSize, bodyFontSize, smallFontSize) = remember(screenWidth) {
        // Estilo Apple: título 17sp, cuerpo 14-15sp, secundario 12-13sp
        when {
            screenWidth < 360.dp -> Triple(16.sp, 14.sp, 12.sp)
            screenWidth < 400.dp -> Triple(17.sp, 15.sp, 12.sp)
            else -> Triple(17.sp, 15.sp, 13.sp)
        }
    }
    
    val spacingBetweenSections = remember(screenWidth) {
        when {
            screenWidth < 360.dp -> 4.dp
            screenWidth < 400.dp -> 6.dp
            else -> 8.dp
        }
    }
    
    // Unificar forma para sombra, clip y shape del Card
    val cardShape = RoundedCornerShape(22.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .cupertinoCardShadow(cornerRadius = 22.dp)
            .graphicsLayer {
                shadowElevation = 0f
                shape = cardShape
                clip = true
            }
            .border(
                width = 0.5.dp,
                color = Color(0x10000000),
                shape = cardShape
            )
            .clip(cardShape),
        shape = cardShape,
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
                    color = HomeColors.TextPrimary,
                    letterSpacing = (-0.5).sp
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
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3C),
                        letterSpacing = (-0.3).sp
                    )
                    
                    Text(
                        text = "Producto: Producto ${order.id}",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3C),
                        letterSpacing = (-0.3).sp
                    )
                    
                    Text(
                        text = "Cantidad: ${orderWithDetails.orderItems.size}",
                        fontSize = bodyFontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A3A3C),
                        letterSpacing = (-0.3).sp
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
                        fontSize = (headerFontSize.value + 7).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeColors.TextPrimary,
                        letterSpacing = (-0.6).sp
                    )
                    
                    // Payment method aligned with "Producto:"
                    Text(
                        text = getPaymentMethodText(order.paymentMethod),
                        fontSize = smallFontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeColors.TextTertiary,
                        letterSpacing = (-0.2).sp
                    )
                    
                    // Empty space to align with "Cantidad:"
                    Spacer(modifier = Modifier.height(bodyFontSize.value.dp + 1.dp))
                }
            }
            
            // Bottom: Date with calendar icon and map icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = HomeColors.TextTertiary,
                        modifier = Modifier.size(if (screenWidth < 360.dp) 12.dp else 14.dp)
                    )
                    Spacer(modifier = Modifier.width(if (screenWidth < 360.dp) 4.dp else 6.dp))
                    Text(
                        text = "${HomeStrings.Created} ${formatOrderDate(order.createdAt)}",
                        fontSize = smallFontSize,
                        fontWeight = FontWeight.Medium,
                        color = HomeColors.TextTertiary,
                        letterSpacing = (-0.2).sp
                    )
                }
                
                // Map icon
                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier.size(if (screenWidth < 360.dp) 28.dp else 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ver en mapa",
                        tint = HomeColors.Blue,
                        modifier = Modifier.size(if (screenWidth < 360.dp) 16.dp else 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val (text, color) = when (status) {
        OrderStatus.IN_PROGRESS -> "En Proceso" to HomeColors.Green
        OrderStatus.PAID -> "Pagado" to HomeColors.Blue
        OrderStatus.PENDING -> "Pendiente" to HomeColors.Orange
        OrderStatus.CANCELLED -> "Cancelado" to HomeColors.Red
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
            screenWidth < 360.dp -> 4.dp to 3.dp
            screenWidth < 400.dp -> 4.dp to 3.dp
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

private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.IN_PROGRESS -> HomeColors.Green
        OrderStatus.PAID -> HomeColors.Blue
        OrderStatus.PENDING -> HomeColors.Orange
        OrderStatus.CANCELLED -> HomeColors.Red
    }
}

private fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.IN_PROGRESS -> "En Proceso"
        OrderStatus.PAID -> "Pagado"
        OrderStatus.PENDING -> "Pendiente"
        OrderStatus.CANCELLED -> "Cancelado"
    }
}

@Composable
fun OrderCardCompact(
    orderWithDetails: OrderWithDetails,
    onClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val order = orderWithDetails.order
    val client = orderWithDetails.client
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 136.dp)
            .cupertinoCardShadow(cornerRadius = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Precio grande + Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$${String.format("%.2f", order.totalAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = 0.3.sp
                )
                
                // Status chip compacto
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = getStatusColor(order.status)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = getStatusText(order.status),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        letterSpacing = 0.2.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Número de pedido
            Text(
                text = "Pedido #${order.id}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1C1C1E),
                letterSpacing = 0.2.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Cliente (hasta 2 líneas)
            Text(
                text = client.name,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3A3A3C),
                letterSpacing = 0.1.sp,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Fecha de creación compacta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF8E8E93),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatOrderDate(order.createdAt),
                    fontSize = 10.5.sp,
                    color = Color(0xFF8E8E93)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Footer compacto alineado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items: ${orderWithDetails.orderItems.size}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3A3A3C)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = getPaymentMethodText(order.paymentMethod),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E8E93)
                    )
                    
                    // Map icon compacto
                    IconButton(
                        onClick = onMapClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ver en mapa",
                            tint = Color(0xFF0A84FF),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}