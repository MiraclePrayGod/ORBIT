package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.model.InstallmentConfig
import com.orbit.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class PaymentInstallmentItem(
    val id: Int,
    val amount: Double,
    val dueDate: LocalDate
)

@Composable
fun InstallmentPaymentConfigWithState(
    totalAmount: Double,
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    installments: List<PaymentInstallmentItem>,
    onInstallmentsChange: (List<PaymentInstallmentItem>) -> Unit,
    onConfigChange: (InstallmentConfig?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedInstallmentId by remember { mutableStateOf<Int?>(null) }
    
    // Calcular totales
    val totalInstallments = installments.sumOf { it.amount }
    val totalPayment = initialPayment + totalInstallments
    val remainingAmount = totalAmount - totalPayment
    val isValid = totalPayment == totalAmount
    
    // Actualizar configuración cuando cambien los valores
    LaunchedEffect(initialPayment, installments) {
        if (isValid && installments.isNotEmpty()) {
            val config = InstallmentConfig(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                numberOfInstallments = installments.size,
                installmentAmount = installments.firstOrNull()?.amount ?: 0.0,
                paymentInterval = 30,
                startDate = installments.firstOrNull()?.dueDate ?: LocalDate.now()
            )
            onConfigChange(config)
        } else {
            onConfigChange(null)
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con total del libro y monto restante
        ApplePaymentHeader(
            totalAmount = totalAmount,
            remainingAmount = remainingAmount,
            isValid = isValid
        )
        
        // Pago inicial compacto
        AppleInitialPaymentCard(
            initialPayment = initialPayment,
            onInitialPaymentChange = onInitialPaymentChange,
            totalAmount = totalAmount
        )
        
        // Cuotas compactas
        AppleInstallmentsCard(
            installments = installments,
            onInstallmentsChange = onInstallmentsChange,
            onDateClick = { id ->
                selectedInstallmentId = id
                showDatePicker = true
            }
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker && selectedInstallmentId != null) {
        val installment = installments.find { it.id == selectedInstallmentId }
        DatePickerDialog(
            selectedDate = installment?.dueDate,
            onDateSelected = { date ->
                onInstallmentsChange(installments.map { 
                    if (it.id == selectedInstallmentId) it.copy(dueDate = date) else it 
                })
            },
            onDismiss = {
                showDatePicker = false
                selectedInstallmentId = null
            }
        )
    }
}

@Composable
private fun ApplePaymentHeader(
    totalAmount: Double,
    remainingAmount: Double,
    isValid: Boolean
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
        colors = CardDefaults.cardColors(
            containerColor = if (isValid) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total del Libro",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = (-0.2).sp
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (isValid) "Completado" else "Restante",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = if (isValid) Color(0xFF34C759) else Color(0xFFFF9500),
                    letterSpacing = (-0.1).sp
                )
                Text(
                    text = if (isValid) "✓" else "$${String.format("%.2f", remainingAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700,
                    color = if (isValid) Color(0xFF34C759) else Color(0xFFFF9500),
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}

@Composable
private fun AppleInitialPaymentCard(
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    totalAmount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pago Inicial",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1C1C1E),
                letterSpacing = (-0.2).sp
            )
            
            OutlinedTextField(
                value = if (initialPayment > 0) initialPayment.toString() else "",
                onValueChange = { value ->
                    onInitialPaymentChange(value.toDoubleOrNull() ?: 0.0)
                },
                label = { 
                    Text(
                        "Monto inicial",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFF8E8E93)
                    )
                },
                prefix = { 
                    Text(
                        "$",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1C1C1E)
                    )
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF1C1C1E)
                )
            )
        }
    }
}

@Composable
private fun AppleInstallmentsCard(
    installments: List<PaymentInstallmentItem>,
    onInstallmentsChange: (List<PaymentInstallmentItem>) -> Unit,
    onDateClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuotas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = (-0.2).sp
                )
                
                // Botón agregar compacto
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF))
                        .clickable {
                            val newId = (installments.maxOfOrNull { it.id } ?: 0) + 1
                            onInstallmentsChange(installments + PaymentInstallmentItem(
                                id = newId,
                                amount = 0.0,
                                dueDate = LocalDate.now().plusDays(30L * installments.size)
                            ))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar cuota",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }
            
            if (installments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF2F2F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Toca + para agregar cuotas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFF8E8E93),
                        letterSpacing = (-0.1).sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(installments) { installment ->
                        AppleInstallmentItem(
                            installment = installment,
                            onAmountChange = { newAmount ->
                                onInstallmentsChange(installments.map { 
                                    if (it.id == installment.id) it.copy(amount = newAmount) else it 
                                })
                            },
                            onDateChange = { newDate ->
                                onInstallmentsChange(installments.map { 
                                    if (it.id == installment.id) it.copy(dueDate = newDate) else it 
                                })
                            },
                            onDelete = {
                                onInstallmentsChange(installments.filter { it.id != installment.id })
                            },
                            onDateClick = { onDateClick(installment.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppleInstallmentItem(
    installment: PaymentInstallmentItem,
    onAmountChange: (Double) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onDelete: () -> Unit,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0xFF000000),
                ambientColor = Color(0xFF000000)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Monto compacto
            OutlinedTextField(
                value = if (installment.amount > 0) installment.amount.toString() else "",
                onValueChange = { value ->
                    onAmountChange(value.toDoubleOrNull() ?: 0.0)
                },
                label = { 
                    Text(
                        "Monto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFF8E8E93)
                    )
                },
                prefix = { 
                    Text(
                        "$",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1C1C1E)
                    )
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF1C1C1E)
                )
            )
            
            // Fecha compacta
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8F9FA))
                    .clickable { onDateClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = installment.dueDate.format(DateTimeFormatter.ofPattern("dd/MM")),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1C1C1E)
                    )
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF8E8E93)
                    )
                }
            }
            
            // Botón eliminar compacto
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30).copy(alpha = 0.1f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Eliminar cuota",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFF3B30)
                )
            }
        }
    }
}