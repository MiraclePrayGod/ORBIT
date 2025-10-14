package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.orbit.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.entity.Order
import com.orbit.data.entity.OrderStatus
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.relation.OrderWithDetails
import com.orbit.ui.viewmodel.OrderDetailViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBackClick: () -> Unit = {},
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val orderWithDetails by viewModel.orderWithDetails.collectAsState()
    
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetails(orderId)
    }
    
    if (orderWithDetails == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }
    
    val order = orderWithDetails!!.order
    val client = orderWithDetails!!.client
    val orderItems = orderWithDetails!!.orderItems
    val payments = orderWithDetails!!.payments
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
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
            
            Text(
                text = "Información General",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = Color.Gray
                )
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Información General Card
            DetailCard(
                title = "Información General"
            ) {
                DetailRow("ID del Pedido", "#${order.id}")
                DetailRow("Cliente", client.name)
                DetailRow("Teléfono", client.phone)
                DetailRow("Dirección", client.address ?: "No especificada")
                DetailRow("Productos", "${orderItems.size} producto(s)")
                DetailRow("Fecha de Pedido", formatDate(order.createdAt))
            }
            
            // Información de Pago Card
            DetailCard(
                title = "Información de Pago"
            ) {
                val totalPaid = payments.sumOf { it.amount }
                val remaining = order.totalAmount - totalPaid
                
                DetailRow("Total", "$${String.format("%.2f", order.totalAmount)}")
                DetailRow("Total Pagado", "$${String.format("%.2f", totalPaid)}")
                DetailRow("Monto Restante", "$${String.format("%.2f", remaining)}")
                DetailRow("Tipo de Pago", getPaymentMethodText(order.paymentMethod))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado:",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    
                    StatusChip(status = order.status)
                }
            }
            
            // Cuotas del Pedido Card
            if (order.paymentMethod == PaymentMethod.INSTALLMENTS) {
                DetailCard(
                    title = "Cuotas del Pedido"
                ) {
                    val paidInstallments = payments.size
                val totalInstallments = payments.size // sin totalInstallments en Payment
                    val pendingInstallments = totalInstallments - paidInstallments
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cuotas Pagadas:",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "$paidInstallments de $totalInstallments",
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cuotas Pendientes:",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "$pendingInstallments de $totalInstallments",
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Alert Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0).copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Necesita actualizar cuotas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryPurple,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Ajusta montos/fechas o añade más cuotas para alcanzar el total.",
                            fontSize = 14.sp,
                            color = SecondaryPurple,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextButton(onClick = { /* Edit installments */ }) {
                            Text(
                                text = "Editar cuotas",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 16.sp,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (text, color) = when (status) {
        OrderStatus.IN_PROGRESS -> "EN_PROCESO" to Color(0xFF007AFF)
        OrderStatus.PAID -> "PAGADO" to Color(0xFF4CAF50)
        OrderStatus.PENDING -> "PENDIENTE" to Color(0xFFFF9800)
        OrderStatus.CANCELLED -> "CANCELADO" to Color(0xFFF44336)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun getPaymentMethodText(paymentMethod: PaymentMethod): String {
    return when (paymentMethod) {
        PaymentMethod.CASH -> "CONTADO"
        PaymentMethod.INSTALLMENTS -> "POR_PARTES"
        PaymentMethod.CARD -> "TARJETA"
        PaymentMethod.TRANSFER -> "TRANSFERENCIA"
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailScreenPreview() {
    // Preview simplified since OrderDetailScreen now uses ViewModel and database
    // In a real preview, you would need to provide a test orderId
    OrderDetailScreen(
        orderId = 1L
    )
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}
