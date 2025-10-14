package com.orbit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import com.orbit.data.model.InstallmentConfig
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Locale

// Helper functions for date handling with kotlinx.datetime (API 24 compatible)
private fun getCurrentDate(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

private fun addDaysToDate(date: LocalDate, days: Int): LocalDate {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(date.year, date.monthNumber - 1, date.dayOfMonth)
    calendar.add(java.util.Calendar.DAY_OF_MONTH, days)
    return LocalDate(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH) + 1, calendar.get(java.util.Calendar.DAY_OF_MONTH))
}

private fun formatDate(date: LocalDate): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = java.util.Calendar.getInstance()
    calendar.set(date.year, date.monthNumber - 1, date.dayOfMonth)
    return formatter.format(calendar.time)
}

private fun parseDate(dateString: String): LocalDate? {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatter.parse(dateString)
        if (date != null) {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            LocalDate(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH) + 1, calendar.get(java.util.Calendar.DAY_OF_MONTH))
        } else null
    } catch (e: Exception) {
        null
    }
}

@Composable
fun InstallmentConfigurator(
    totalAmount: Double,
    onConfigChange: (InstallmentConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var initialPayment by remember { mutableStateOf(0.0) }
    var numberOfInstallments by remember { mutableStateOf(3) }
    var paymentInterval by remember { mutableStateOf(30) }
    var startDate by remember { mutableStateOf(addDaysToDate(getCurrentDate(), 30)) }
    
    val installmentAmount = if (numberOfInstallments > 0) (totalAmount - initialPayment) / numberOfInstallments else 0.0
    
    // Actualizar configuración cuando cambien los valores
    LaunchedEffect(initialPayment, numberOfInstallments, paymentInterval, startDate) {
        onConfigChange(
            InstallmentConfig(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                numberOfInstallments = numberOfInstallments,
                installmentAmount = installmentAmount,
                paymentInterval = paymentInterval,
                startDate = java.time.LocalDate.of(startDate.year, startDate.monthNumber, startDate.dayOfMonth)
            )
        )
    }
    
    // REVOLUCIONARIO: Payment Flow Builder
    RevolutionaryPaymentFlow(
        totalAmount = totalAmount,
        initialPayment = initialPayment,
        numberOfInstallments = numberOfInstallments,
        installmentAmount = installmentAmount,
        startDate = startDate,
        onInitialPaymentChange = { initialPayment = it },
        onInstallmentsChange = { numberOfInstallments = it },
        onStartDateChange = { startDate = it },
        modifier = modifier
    )
}

@Composable
private fun RevolutionaryPaymentFlow(
    totalAmount: Double,
    initialPayment: Double,
    numberOfInstallments: Int,
    installmentAmount: Double,
    startDate: LocalDate,
    onInitialPaymentChange: (Double) -> Unit,
    onInstallmentsChange: (Int) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) } // 0: Initial, 1: Installments, 2: Summary
    
    // Estado compartido para las cuotas individuales - EMPEZAR VACÍO
    var installmentList by remember { 
        mutableStateOf(mutableListOf<InstallmentItem>())
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con progreso
        PaymentFlowHeader(
            currentStep = currentStep,
            totalSteps = 3,
            onStepClick = { currentStep = it }
        )
        
        // Contenido dinámico basado en el paso actual
        when (currentStep) {
            0 -> InitialPaymentStep(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                onInitialPaymentChange = onInitialPaymentChange,
                onNext = { currentStep = 1 }
            )
            1 -> InstallmentsStepWithList(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                installmentList = installmentList,
                onInstallmentListChange = { installmentList = it },
                onInstallmentsChange = onInstallmentsChange,
                onNext = { currentStep = 2 },
                onBack = { currentStep = 0 }
            )
            2 -> PaymentSummaryStepWithList(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                installmentList = installmentList,
                onBack = { currentStep = 1 },
                onEdit = { currentStep = it }
            )
        }
    }
}

@Composable
private fun PaymentFlowHeaderCompact() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Flow",
                fontSize = 20.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1D1D1F)
            )
            Text(
                text = "Configura tu plan de pagos",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93)
            )
        }
    }
}

@Composable
private fun InitialPaymentStepCompact(
    totalAmount: Double,
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF007AFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Pago Inicial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
            }
            
            OutlinedTextField(
                value = if (initialPayment == 0.0) "" else initialPayment.toInt().toString(),
                onValueChange = { value ->
                    val newValue = value.toDoubleOrNull() ?: 0.0
                    if (newValue >= 0 && newValue <= totalAmount) {
                        onInitialPaymentChange(newValue)
                    }
                },
                label = { Text("Monto inicial") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            
            Text(
                text = "Total disponible: $${totalAmount.toInt()}",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        }
    }
}

@Composable
private fun InstallmentsStepCompact(
    installmentList: MutableList<InstallmentItem>,
    onInstallmentListChange: (MutableList<InstallmentItem>) -> Unit,
    onInstallmentsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF34C759), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cuotas (${installmentList.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF1D1D1F)
                    )
                }
                
                // Botón agregar cuota
                IconButton(
                    onClick = {
                        val newList = installmentList.toMutableList()
                        newList.add(
                            InstallmentItem(
                                id = newList.size,
                                amount = 0.0,
                                date = getCurrentDate() // Solo para evitar null, pero se mostrará vacío
                            )
                        )
                        onInstallmentListChange(newList)
                        onInstallmentsChange(newList.size)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF2F2F7), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar cuota",
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Lista de cuotas compacta
            installmentList.forEachIndexed { index, installment ->
                InstallmentItemCardCompact(
                    installment = installment,
                    onAmountChange = { newAmount ->
                        val newList = installmentList.toMutableList()
                        newList[index] = installment.copy(amount = newAmount)
                        onInstallmentListChange(newList)
                    },
                    onDateChange = { newDate ->
                        val newList = installmentList.toMutableList()
                        newList[index] = installment.copy(date = newDate)
                        onInstallmentListChange(newList)
                    },
                    onRemove = {
                        if (installmentList.size > 1) {
                            val newList = installmentList.toMutableList()
                            newList.removeAt(index)
                            onInstallmentListChange(newList)
                            onInstallmentsChange(newList.size)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InstallmentItemCardCompact(
    installment: InstallmentItem,
    onAmountChange: (Double) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onRemove: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de cuota
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF34C759), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${installment.id + 1}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Campos de entrada
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Campo de monto
                OutlinedTextField(
                    value = if (installment.amount == 0.0) "" else installment.amount.toInt().toString(),
                    onValueChange = { value ->
                        val newValue = value.toDoubleOrNull() ?: 0.0
                        if (newValue >= 0) {
                            onAmountChange(newValue)
                        }
                    },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF34C759),
                        unfocusedBorderColor = Color(0xFFE5E5EA)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    singleLine = true
                )
                
                // Campo de fecha
                // Campo de fecha con icono externo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (installment.date == getCurrentDate()) "" else formatDate(installment.date),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Fecha") },
                        placeholder = { Text("Seleccionar fecha") },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF34C759),
                            unfocusedBorderColor = Color(0xFFE5E5EA),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Icono de calendario externo
                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF34C759), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Botón eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFFF3B30), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = installment.date,
            onDateSelected = { newDate ->
                onDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun PaymentSummaryStepCompact(
    totalAmount: Double,
    initialPayment: Double,
    installmentList: MutableList<InstallmentItem>,
    isBalanced: Boolean
) {
    val totalInstallments = installmentList.sumOf { it.amount }
    val totalCalculated = initialPayment + totalInstallments
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isBalanced) Color(0xFF34C759) else Color(0xFFFF9500),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isBalanced) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isBalanced) "¡Perfecto!" else "¡Atención!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
            }
            
            // Validación de totales
            if (!isBalanced) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "⚠️ Los montos no coinciden",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total del pedido: $${totalAmount.toInt()}",
                            fontSize = 12.sp,
                            color = Color(0xFF856404)
                        )
                        Text(
                            text = "Total configurado: $${totalCalculated.toInt()}",
                            fontSize = 12.sp,
                            color = Color(0xFF856404)
                        )
                        Text(
                            text = "Diferencia: $${(totalCalculated - totalAmount).toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF856404)
                        )
                    }
                }
            }
            
            // Resumen visual compacto
            PaymentSummaryVisualCompact(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                installmentList = installmentList,
                isBalanced = isBalanced
            )
        }
    }
}

@Composable
private fun PaymentSummaryVisualCompact(
    totalAmount: Double,
    initialPayment: Double,
    installmentList: MutableList<InstallmentItem>,
    isBalanced: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pago inicial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF007AFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("I", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.W600)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pago Inicial", fontSize = 14.sp, fontWeight = FontWeight.W600)
            }
            Text("$${initialPayment.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.W700, color = Color(0xFF007AFF))
        }
        
        // Cuotas
        installmentList.forEach { installment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF34C759), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${installment.id + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.W600)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cuota ${installment.id + 1}", fontSize = 14.sp, fontWeight = FontWeight.W600)
                }
                Text("$${installment.amount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.W700, color = Color(0xFF34C759))
            }
        }
        
        // Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isBalanced) Color(0xFFE8F5E8) else Color(0xFFFFF3CD),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = if (isBalanced) Color(0xFF1D1D1F) else Color(0xFF856404)
            )
            Text(
                "$${totalAmount.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.W800,
                color = if (isBalanced) Color(0xFF34C759) else Color(0xFF856404)
            )
        }
    }
}

@Composable
private fun PaymentFlowHeader(
    currentStep: Int,
    totalSteps: Int,
    onStepClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Flow",
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1D1D1F)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalSteps) { step ->
                    val isActive = step == currentStep
                    val isCompleted = step < currentStep
                    
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 12.dp else 8.dp)
                            .background(
                                when {
                                    isCompleted -> Color(0xFF34C759)
                                    isActive -> Color(0xFF007AFF)
                                    else -> Color(0xFF8E8E93)
                                },
                                CircleShape
                            )
                            .clickable { onStepClick(step) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Step labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Inicial",
                    fontSize = 12.sp,
                    color = if (currentStep == 0) Color(0xFF007AFF) else Color(0xFF8E8E93),
                    fontWeight = if (currentStep == 0) FontWeight.W600 else FontWeight.Normal
                )
                Text(
                    text = "Cuotas",
                    fontSize = 12.sp,
                    color = if (currentStep == 1) Color(0xFF007AFF) else Color(0xFF8E8E93),
                    fontWeight = if (currentStep == 1) FontWeight.W600 else FontWeight.Normal
                )
                Text(
                    text = "Resumen",
                    fontSize = 12.sp,
                    color = if (currentStep == 2) Color(0xFF007AFF) else Color(0xFF8E8E93),
                    fontWeight = if (currentStep == 2) FontWeight.W600 else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun InitialPaymentStep(
    totalAmount: Double,
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono grande
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF007AFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Pago Inicial",
                fontSize = 28.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1D1D1F)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "¿Cuánto quieres pagar ahora?",
                fontSize = 16.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Campo de entrada para monto inicial
            OutlinedTextField(
                value = if (initialPayment == 0.0) "" else initialPayment.toInt().toString(),
                onValueChange = { value ->
                    val newValue = value.toDoubleOrNull() ?: 0.0
                    if (newValue >= 0 && newValue <= totalAmount) {
                        onInitialPaymentChange(newValue)
                    }
                },
                label = { Text("Monto inicial") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA)
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total disponible
            Text(
                text = "Total disponible: $${totalAmount.toInt()}",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón siguiente
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Text(
                    text = "Continuar →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun InstallmentsStepWithList(
    totalAmount: Double,
    initialPayment: Double,
    installmentList: MutableList<InstallmentItem>,
    onInstallmentListChange: (MutableList<InstallmentItem>) -> Unit,
    onInstallmentsChange: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF34C759), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Cuotas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W700,
                            color = Color(0xFF1D1D1F)
                        )
                        Text(
                            text = "Agrega fechas y montos",
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
                
                // Botón agregar cuota
                IconButton(
                    onClick = {
                        val newList = installmentList.toMutableList().apply {
                            add(
                                InstallmentItem(
                                    id = size,
                                    amount = 0.0,
                                    date = getCurrentDate() // Solo para evitar null, pero se mostrará vacío
                                )
                            )
                        }
                        onInstallmentListChange(newList)
                        onInstallmentsChange(newList.size)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF2F2F7), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar cuota",
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de cuotas
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(installmentList.size) { index ->
                    val installment = installmentList[index]
                    InstallmentItemCard(
                        installment = installment,
                        onAmountChange = { newAmount ->
                            val newList = installmentList.toMutableList().apply {
                                set(index, installment.copy(amount = newAmount))
                            }
                            onInstallmentListChange(newList)
                        },
                        onDateChange = { newDate ->
                            val newList = installmentList.toMutableList().apply {
                                set(index, installment.copy(date = newDate))
                            }
                            onInstallmentListChange(newList)
                        },
                        onRemove = {
                            if (installmentList.size > 1) {
                                val newList = installmentList.toMutableList().apply {
                                    removeAt(index)
                                }
                                onInstallmentListChange(newList)
                                onInstallmentsChange(newList.size)
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Resumen de balance en tiempo real
            val totalInstallments = installmentList.sumOf { it.amount }
            val remaining = totalAmount - initialPayment - totalInstallments
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (remaining > 0) Color(0xFFE3F2FD) else Color(0xFFE8F5E8)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total del pedido",
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                        Text(
                            text = "$${totalAmount.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF1D1D1F)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pago inicial",
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                        Text(
                            text = "$${initialPayment.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF007AFF)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cuotas (${installmentList.size})",
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                        Text(
                            text = "$${totalInstallments.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF34C759)
                        )
                    }
                    
                    Divider(color = Color(0xFFE5E5EA), thickness = 1.dp)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (remaining > 0) "Falta por cubrir" else "Balance completo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            color = if (remaining > 0) Color(0xFF007AFF) else Color(0xFF34C759)
                        )
                        Text(
                            text = if (remaining > 0) "$${remaining.toInt()}" else "✓",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                            color = if (remaining > 0) Color(0xFF007AFF) else Color(0xFF34C759)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botones de navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007AFF)
                    )
                ) {
                    Text("← Atrás", fontSize = 16.sp, fontWeight = FontWeight.W600)
                }
                
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                ) {
                    Text("Continuar →", fontSize = 16.sp, fontWeight = FontWeight.W600, color = Color.White)
                }
            }
        }
    }
}

data class InstallmentItem(
    val id: Int,
    val amount: Double,
    val date: LocalDate
)

@Composable
private fun InstallmentItemCard(
    installment: InstallmentItem,
    onAmountChange: (Double) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onRemove: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número de cuota
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF34C759), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${installment.id + 1}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Campos de entrada
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo de monto libre
                OutlinedTextField(
                    value = if (installment.amount == 0.0) "" else installment.amount.toInt().toString(),
                    onValueChange = { value ->
                        val newValue = value.toDoubleOrNull() ?: 0.0
                        if (newValue >= 0) {
                            onAmountChange(newValue)
                        }
                    },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF34C759),
                        unfocusedBorderColor = Color(0xFFE5E5EA)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                
                // Campo de fecha con icono externo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (installment.date == getCurrentDate()) "" else formatDate(installment.date),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Fecha") },
                        placeholder = { Text("Seleccionar fecha") },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF34C759),
                            unfocusedBorderColor = Color(0xFFE5E5EA),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Icono de calendario externo
                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF34C759), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Botón eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFF3B30), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = installment.date,
            onDateSelected = { newDate ->
                onDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun PaymentSummaryStepWithList(
    totalAmount: Double,
    initialPayment: Double,
    installmentList: MutableList<InstallmentItem>,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    // Calcular total de cuotas
    val totalInstallments = installmentList.sumOf { it.amount }
    val totalCalculated = initialPayment + totalInstallments
    val isBalanced = kotlin.math.abs(totalCalculated - totalAmount) < 0.01
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono grande
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        if (isBalanced) Color(0xFF34C759) else Color(0xFFFF9500),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isBalanced) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isBalanced) "¡Perfecto!" else "¡Atención!",
                fontSize = 28.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFF1D1D1F)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isBalanced) "Tu plan de pagos está listo" else "Revisa los montos",
                fontSize = 16.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Resumen visual elegante
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pago inicial
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F7), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF007AFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("I", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.W600)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Pago Inicial", fontSize = 16.sp, fontWeight = FontWeight.W600, color = Color(0xFF1D1D1F))
                    }
                    Text("$${initialPayment.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.W700, color = Color(0xFF007AFF))
                }
                
                // Cuotas individuales
                installmentList.forEach { installment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF2F2F7), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF34C759), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${installment.id + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.W600)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Cuota ${installment.id + 1}", fontSize = 16.sp, fontWeight = FontWeight.W600, color = Color(0xFF1D1D1F))
                        }
                        Text("$${installment.amount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.W700, color = Color(0xFF34C759))
                    }
                }
                
                // Total
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isBalanced) Color(0xFFE8F5E8) else Color(0xFFFFF3CD),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        color = if (isBalanced) Color(0xFF1D1D1F) else Color(0xFF856404)
                    )
                    Text(
                        "$${totalAmount.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W800,
                        color = if (isBalanced) Color(0xFF34C759) else Color(0xFF856404)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Validación de totales (solo si no está balanceado)
            if (!isBalanced) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ Los montos no coinciden",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total del pedido: $${totalAmount.toInt()}",
                            fontSize = 14.sp,
                            color = Color(0xFF856404)
                        )
                        Text(
                            text = "Total configurado: $${totalCalculated.toInt()}",
                            fontSize = 14.sp,
                            color = Color(0xFF856404)
                        )
                        val difference = totalCalculated - totalAmount
                        Text(
                            text = if (difference > 0) {
                                "Te sobra: $${String.format("%.2f", difference)}"
                            } else {
                                "Te falta: $${String.format("%.2f", kotlin.math.abs(difference))}"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            color = if (difference > 0) Color(0xFF34C759) else Color(0xFFFF3B30)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón de regreso
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF007AFF)
                )
            ) {
                Text("← Editar Plan", fontSize = 16.sp, fontWeight = FontWeight.W600)
            }
        }
    }
}

@Composable
private fun PaymentSummaryVisual(
    totalAmount: Double,
    initialPayment: Double,
    numberOfInstallments: Int,
    installmentAmount: Double,
    isBalanced: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pago inicial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F7), RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF007AFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("I", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.W600)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Pago Inicial", fontSize = 16.sp, fontWeight = FontWeight.W600)
            }
            Text("$${String.format("%.0f", initialPayment)}", fontSize = 18.sp, fontWeight = FontWeight.W700, color = Color(0xFF007AFF))
        }
        
        // Cuotas
        repeat(numberOfInstallments) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F7), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF34C759), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.W600)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cuota ${index + 1}", fontSize = 16.sp, fontWeight = FontWeight.W600)
                }
                Text("$${String.format("%.0f", installmentAmount)}", fontSize = 18.sp, fontWeight = FontWeight.W700, color = Color(0xFF34C759))
            }
        }
        
        // Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isBalanced) Color(0xFFE8F5E8) else Color(0xFFFFF3CD),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total",
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                color = if (isBalanced) Color(0xFF1D1D1F) else Color(0xFF856404)
            )
            Text(
                "$${totalAmount.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                color = if (isBalanced) Color(0xFF34C759) else Color(0xFF856404)
            )
        }
    }
}

@Composable
private fun InitialPaymentCard(
    initialPayment: Double,
    totalAmount: Double,
    onInitialPaymentChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pago Inicial",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            OutlinedTextField(
                value = if (initialPayment == 0.0) "" else initialPayment.toInt().toString(),
                onValueChange = { value ->
                    val newValue = value.toDoubleOrNull() ?: 0.0
                    if (newValue >= 0 && newValue <= totalAmount * 0.8) {
                        onInitialPaymentChange(newValue)
                    }
                },
                label = { Text("Monto inicial") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Slider como alternativa
            Slider(
                value = (initialPayment / totalAmount).toFloat(),
                onValueChange = { 
                    onInitialPaymentChange(it * totalAmount)
                },
                valueRange = 0.1f..0.8f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF007AFF),
                    activeTrackColor = Color(0xFF007AFF)
                )
            )
        }
    }
}

@Composable
private fun IndividualInstallmentsCard(
    numberOfInstallments: Int,
    installmentAmount: Double,
    startDate: LocalDate,
    paymentInterval: Int,
    onAddInstallment: () -> Unit,
    onRemoveInstallment: () -> Unit,
    onStartDateChange: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuotas ($numberOfInstallments)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
                
                Row {
                    IconButton(
                        onClick = onRemoveInstallment,
                        enabled = numberOfInstallments > 1
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Quitar cuota",
                            tint = if (numberOfInstallments > 1) Color(0xFF007AFF) else Color(0xFF8E8E93)
                        )
                    }
                    IconButton(onClick = onAddInstallment) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar cuota",
                            tint = Color(0xFF007AFF)
                        )
                    }
                }
            }
            
            // Lista de cuotas individuales
            repeat(numberOfInstallments) { index ->
                val paymentDate = addDaysToDate(startDate, (index + 1) * paymentInterval)
                IndividualInstallmentItem(
                    number = index + 1,
                    amount = installmentAmount,
                    date = paymentDate,
                    onDateChange = { newDate ->
                        // Aquí podrías implementar lógica para actualizar fechas individuales
                    }
                )
            }
        }
    }
}

@Composable
private fun IndividualInstallmentItem(
    number: Int,
    amount: Double,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de cuota
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF34C759), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Información de la cuota
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Cuota $number",
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            // Campo de fecha clickeable
            OutlinedTextField(
                value = formatDate(date),
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha de pago") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF34C759),
                    unfocusedBorderColor = Color(0xFFE5E5EA),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { showDatePicker = true }
                    )
                }
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Monto
        Text(
            text = "$${String.format("%.2f", amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = Color(0xFF34C759)
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = date,
            onDateSelected = { newDate ->
                onDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun InteractivePieChart(
    totalAmount: Double,
    initialPayment: Double,
    numberOfInstallments: Int,
    onInitialPaymentChange: (Double) -> Unit,
    onInstallmentsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val size = 150.dp
    val strokeWidth = 8.dp
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Distribución de Pagos",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val center = size.toPx() / 2
                                val angle = atan2(
                                    change.position.y - center,
                                    change.position.x - center
                                ) * 180 / PI
                                
                                val initialPaymentAngle = (initialPayment / totalAmount) * 360
                                val adjustedAngle = (angle + 90) % 360
                                
                                if (adjustedAngle in 0.0..initialPaymentAngle) {
                                    val newInitialPayment = (adjustedAngle / 360) * totalAmount
                                    onInitialPaymentChange(newInitialPayment.coerceIn(0.0, totalAmount * 0.8))
                                }
                            }
                        }
                ) {
                    drawPieChart(
                        totalAmount = totalAmount,
                        initialPayment = initialPayment,
                        numberOfInstallments = numberOfInstallments,
                        strokeWidth = strokeWidth.toPx()
                    )
                }
                
                // Información central
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$${String.format("%.0f", totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        color = Color(0xFF1D1D1F)
                    )
                    Text(
                        text = "Total",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Leyenda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    color = Color(0xFF007AFF),
                    label = "Inicial",
                    value = "$${String.format("%.0f", initialPayment)}"
                )
                LegendItem(
                    color = Color(0xFF34C759),
                    label = "Cuotas",
                    value = "$numberOfInstallments x $${String.format("%.0f", (totalAmount - initialPayment) / numberOfInstallments)}"
                )
            }
        }
    }
}

private fun DrawScope.drawPieChart(
    totalAmount: Double,
    initialPayment: Double,
    numberOfInstallments: Int,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = (minOf(size.width, size.height) - strokeWidth) / 2
    
    val initialPaymentAngle = (initialPayment / totalAmount) * 360
    val installmentAngle = ((totalAmount - initialPayment) / totalAmount) * 360
    
    // Dibujar pago inicial
    drawArc(
        color = Color(0xFF007AFF),
        startAngle = -90f,
        sweepAngle = initialPaymentAngle.toFloat(),
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth)
    )
    
    // Dibujar cuotas
    drawArc(
        color = Color(0xFF34C759),
        startAngle = -90f + initialPaymentAngle.toFloat(),
        sweepAngle = installmentAngle.toFloat(),
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth)
    )
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
        }
    }
}

@Composable
private fun ConfigurationControls(
    initialPayment: Double,
    numberOfInstallments: Int,
    paymentInterval: Int,
    startDate: LocalDate,
    totalAmount: Double,
    onInitialPaymentChange: (Double) -> Unit,
    onInstallmentsChange: (Int) -> Unit,
    onPaymentIntervalChange: (Int) -> Unit,
    onStartDateChange: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configuración",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            // Pago Inicial con Input Directo
            Column {
                Text(
                    text = "Pago Inicial",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if (initialPayment == 0.0) "" else initialPayment.toInt().toString(),
                    onValueChange = { value ->
                        val newValue = value.toDoubleOrNull() ?: 0.0
                        if (newValue >= 0 && newValue <= totalAmount * 0.8) {
                            onInitialPaymentChange(newValue)
                        }
                    },
                    label = { Text("Monto inicial") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color(0xFFE5E5EA)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                // Slider como alternativa
                Slider(
                    value = (initialPayment / totalAmount).toFloat(),
                    onValueChange = { 
                        onInitialPaymentChange(it * totalAmount)
                    },
                    valueRange = 0.1f..0.8f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF007AFF),
                        activeTrackColor = Color(0xFF007AFF)
                    )
                )
            }
            
            // Número de Cuotas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuotas: $numberOfInstallments",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
                Row {
                    IconButton(
                        onClick = { if (numberOfInstallments > 1) onInstallmentsChange(numberOfInstallments - 1) }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos")
                    }
                    IconButton(
                        onClick = { if (numberOfInstallments < 12) onInstallmentsChange(numberOfInstallments + 1) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más")
                    }
                }
            }
            
            // Intervalo de Pago
            Column {
                Text(
                    text = "Intervalo: $paymentInterval días",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1D1D1F)
                )
                Slider(
                    value = paymentInterval.toFloat(),
                    onValueChange = { onPaymentIntervalChange(it.toInt()) },
                    valueRange = 7f..90f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF34C759),
                        activeTrackColor = Color(0xFF34C759)
                    )
                )
            }
        }
    }
}

@Composable
private fun InteractivePaymentTimeline(
    totalAmount: Double,
    initialPayment: Double,
    numberOfInstallments: Int,
    installmentAmount: Double,
    paymentInterval: Int,
    startDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit
) {
    // Estado para fechas individuales de cada cuota
    var paymentDates by remember { 
        mutableStateOf(
            mutableListOf<LocalDate>().apply {
                add(startDate) // Fecha inicial
                repeat(numberOfInstallments) { index ->
                    add(addDaysToDate(startDate, (index + 1) * paymentInterval))
                }
            }
        )
    }
    
    // Actualizar fechas cuando cambie la fecha inicial o el intervalo
    LaunchedEffect(startDate, paymentInterval, numberOfInstallments) {
        paymentDates = mutableListOf<LocalDate>().apply {
            add(startDate)
            repeat(numberOfInstallments) { index ->
                add(addDaysToDate(startDate, (index + 1) * paymentInterval))
            }
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Cronograma de Pagos",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pago inicial
            InteractivePaymentItem(
                number = 0,
                amount = initialPayment,
                date = paymentDates.getOrNull(0) ?: startDate,
                isInitial = true,
                onDateChange = { newDate ->
                    paymentDates = paymentDates.toMutableList().apply {
                        if (isNotEmpty()) set(0, newDate)
                    }
                    onStartDateChange(newDate)
                }
            )
            
            // Cuotas
            repeat(numberOfInstallments) { index ->
                val paymentDate = paymentDates.getOrNull(index + 1) ?: addDaysToDate(startDate, (index + 1) * paymentInterval)
                InteractivePaymentItem(
                    number = index + 1,
                    amount = installmentAmount,
                    date = paymentDate,
                    isInitial = false,
                    onDateChange = { newDate ->
                        paymentDates = paymentDates.toMutableList().apply {
                            if (size > index + 1) set(index + 1, newDate)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentMonth by remember { mutableStateOf(initialDate) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Seleccionar Fecha",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600
                )
                
                // Navegación de mes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { 
                            currentMonth = addDaysToDate(currentMonth, -30) // Aproximadamente un mes atrás
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Mes anterior",
                            tint = Color(0xFF007AFF)
                        )
                    }
                    
                    Text(
                        text = "${currentMonth.monthNumber}/${currentMonth.year}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1D1D1F),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    IconButton(
                        onClick = { 
                            currentMonth = addDaysToDate(currentMonth, 30) // Aproximadamente un mes adelante
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Mes siguiente",
                            tint = Color(0xFF007AFF)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Días de la semana
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF8E8E93),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Calendario del mes
                val firstDayOfMonth = kotlinx.datetime.LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
                val firstMonday = addDaysToDate(firstDayOfMonth, -(firstDayOfMonth.dayOfWeek.value - 1).toInt())
                
                // Generar días del calendario (6 semanas x 7 días = 42 días)
                val calendarDays = (0..41).map { dayOffset ->
                    addDaysToDate(firstMonday, dayOffset)
                }
                
                // Dividir en semanas
                val weeks = calendarDays.chunked(7)
                
                weeks.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        week.forEach { day ->
                            val isCurrentMonth = day.monthNumber == currentMonth.monthNumber
                            val isSelected = day == selectedDate
                            val isToday = day == getCurrentDate()
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .background(
                                        color = when {
                                            isSelected -> Color(0xFF007AFF)
                                            isToday -> Color(0xFFE3F2FD)
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { 
                                        if (isCurrentMonth) {
                                            selectedDate = day
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${day.dayOfMonth}",
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || isToday) FontWeight.W600 else FontWeight.Normal,
                                    color = when {
                                        isSelected -> Color.White
                                        isToday -> Color(0xFF007AFF)
                                        isCurrentMonth -> Color(0xFF1D1D1F)
                                        else -> Color(0xFFC7C7CC)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Fecha seleccionada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fecha Seleccionada",
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDate(selectedDate),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFF1D1D1F)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onDateSelected(selectedDate) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF8E8E93))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
private fun InteractivePaymentItem(
    number: Int,
    amount: Double,
    date: LocalDate,
    isInitial: Boolean,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número/Círculo
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isInitial) Color(0xFF007AFF) else Color(0xFF34C759),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isInitial) "I" else "$number",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Información
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isInitial) "Pago Inicial" else "Cuota $number",
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1D1D1F)
            )
            
            // Campo de fecha clickeable
            OutlinedTextField(
                value = formatDate(date),
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE5E5EA),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { showDatePicker = true }
                    )
                }
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Monto
        Text(
            text = "$${String.format("%.2f", amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            color = if (isInitial) Color(0xFF007AFF) else Color(0xFF34C759)
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = date,
            onDateSelected = { newDate ->
                onDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
