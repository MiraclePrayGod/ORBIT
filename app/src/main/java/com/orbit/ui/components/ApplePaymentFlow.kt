package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.model.InstallmentConfig
import com.orbit.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
private fun getResponsiveValues(): ResponsiveValues {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    return when {
        screenWidth < 360 -> ResponsiveValues.SMALL
        screenWidth < 480 -> ResponsiveValues.MEDIUM
        else -> ResponsiveValues.LARGE
    }
}

private enum class ResponsiveValues {
    SMALL, MEDIUM, LARGE
}

private val ResponsiveValues.padding: Dp
    get() = when (this) {
        ResponsiveValues.SMALL -> 8.dp
        ResponsiveValues.MEDIUM -> 12.dp
        ResponsiveValues.LARGE -> 16.dp
    }

private val ResponsiveValues.spacing: Dp
    get() = when (this) {
        ResponsiveValues.SMALL -> 4.dp
        ResponsiveValues.MEDIUM -> 6.dp
        ResponsiveValues.LARGE -> 8.dp
    }

private val ResponsiveValues.stepSize: Dp
    get() = when (this) {
        ResponsiveValues.SMALL -> 20.dp
        ResponsiveValues.MEDIUM -> 24.dp
        ResponsiveValues.LARGE -> 28.dp
    }

private val ResponsiveValues.stepTextSize: TextUnit
    get() = when (this) {
        ResponsiveValues.SMALL -> 8.sp
        ResponsiveValues.MEDIUM -> 9.sp
        ResponsiveValues.LARGE -> 10.sp
    }

private val ResponsiveValues.titleTextSize: TextUnit
    get() = when (this) {
        ResponsiveValues.SMALL -> 12.sp
        ResponsiveValues.MEDIUM -> 14.sp
        ResponsiveValues.LARGE -> 16.sp
    }

private val ResponsiveValues.bodyTextSize: TextUnit
    get() = when (this) {
        ResponsiveValues.SMALL -> 10.sp
        ResponsiveValues.MEDIUM -> 12.sp
        ResponsiveValues.LARGE -> 14.sp
    }

private val ResponsiveValues.buttonSize: AppleButtonSize
    get() = when (this) {
        ResponsiveValues.SMALL -> AppleButtonSize.SMALL
        ResponsiveValues.MEDIUM -> AppleButtonSize.MEDIUM
        ResponsiveValues.LARGE -> AppleButtonSize.LARGE
    }

@Composable
fun ApplePaymentFlow(
    totalAmount: Double,
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    installments: List<PaymentInstallmentItem>,
    onInstallmentsChange: (List<PaymentInstallmentItem>) -> Unit,
    onConfigChange: (InstallmentConfig?) -> Unit,
    modifier: Modifier = Modifier
) {
    val responsive = getResponsiveValues()
    val pagerState = rememberPagerState(pageCount = { 3 })
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedInstallmentId by remember { mutableStateOf<Int?>(null) }
    var targetPage by remember { mutableStateOf<Int?>(null) }
    
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
    
    // Manejar navegación entre páginas
    LaunchedEffect(targetPage) {
        targetPage?.let { page ->
            pagerState.animateScrollToPage(page)
            targetPage = null
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(responsive.spacing)
    ) {
        // Header compacto
        ApplePaymentHeaderCompact(
            totalAmount = totalAmount,
            remainingAmount = remainingAmount,
            isValid = isValid,
            responsive = responsive
        )
        
        // Puntos de navegación compactos
        ApplePaymentStepsCompact(
            currentStep = pagerState.currentPage,
            onStepClick = { step ->
                targetPage = step
            },
            responsive = responsive
        )
        
        // Contenido del flujo compacto
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> AppleInitialPaymentStepCompact(
                    initialPayment = initialPayment,
                    onInitialPaymentChange = onInitialPaymentChange,
                    totalAmount = totalAmount,
                    onNext = { targetPage = 1 },
                    responsive = responsive
                )
                1 -> AppleInstallmentsStepCompact(
                    installments = installments,
                    onInstallmentsChange = onInstallmentsChange,
                    onDateClick = { id ->
                        selectedInstallmentId = id
                        showDatePicker = true
                    },
                    onNext = { targetPage = 2 },
                    onBack = { targetPage = 0 },
                    responsive = responsive
                )
                2 -> ApplePaymentSummaryStepCompact(
                    totalAmount = totalAmount,
                    initialPayment = initialPayment,
                    installments = installments,
                    onBack = { targetPage = 1 },
                    responsive = responsive
                )
            }
        }
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
private fun ApplePaymentStepsCompact(
    currentStep: Int,
    onStepClick: (Int) -> Unit,
    responsive: ResponsiveValues
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = responsive.padding, vertical = responsive.spacing),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Paso 1: Monto Inicial
        AppleStepIndicatorCompact(
            stepNumber = 1,
            title = "Inicial",
            isActive = currentStep == 0,
            isCompleted = currentStep > 0,
            onClick = { onStepClick(0) },
            responsive = responsive
        )
        
        // Línea conectora más corta
        AppleStepConnectorCompact(isCompleted = currentStep > 0)
        
        // Paso 2: Cuotas
        AppleStepIndicatorCompact(
            stepNumber = 2,
            title = "Cuotas",
            isActive = currentStep == 1,
            isCompleted = currentStep > 1,
            onClick = { onStepClick(1) },
            responsive = responsive
        )
        
        // Línea conectora más corta
        AppleStepConnectorCompact(isCompleted = currentStep > 1)
        
        // Paso 3: Resumen
        AppleStepIndicatorCompact(
            stepNumber = 3,
            title = "Resumen",
            isActive = currentStep == 2,
            isCompleted = false,
            onClick = { onStepClick(2) },
            responsive = responsive
        )
    }
}

@Composable
private fun AppleStepIndicatorCompact(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    responsive: ResponsiveValues
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Círculo del paso responsive
        Box(
            modifier = Modifier
                .size(responsive.stepSize)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Color(0xFF34C759)
                        isActive -> Color(0xFF007AFF)
                        else -> Color(0xFFF2F2F7)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(responsive.stepSize * 0.5f)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    fontSize = responsive.stepTextSize,
                    fontWeight = FontWeight.W600,
                    color = when {
                        isActive -> Color.White
                        else -> Color(0xFF8E8E93)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(responsive.spacing))
        
        // Título del paso responsive
        Text(
            text = title,
            fontSize = responsive.stepTextSize,
            fontWeight = FontWeight.W500,
            color = when {
                isActive -> Color(0xFF007AFF)
                isCompleted -> Color(0xFF34C759)
                else -> Color(0xFF8E8E93)
            },
            textAlign = TextAlign.Center,
            letterSpacing = (-0.1).sp
        )
    }
}

@Composable
private fun AppleStepConnectorCompact(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .width(16.dp)
            .height(1.dp)
            .background(
                if (isCompleted) Color(0xFF34C759) else Color(0xFFE5E5EA)
            )
    )
}

@Composable
private fun AppleInitialPaymentStepCompact(
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    totalAmount: Double,
    onNext: () -> Unit,
    responsive: ResponsiveValues
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(responsive.padding),
        verticalArrangement = Arrangement.spacedBy(responsive.spacing)
    ) {
        Text(
            text = "Monto Inicial",
            fontSize = responsive.titleTextSize,
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
                fontWeight = FontWeight.W600,
                color = Color(0xFF1C1C1E)
            )
        )
        
        AppleButton(
            text = "Continuar →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            variant = AppleButtonVariant.PRIMARY,
            size = responsive.buttonSize,
            enabled = initialPayment > 0
        )
    }
}

@Composable
private fun AppleInstallmentsStepCompact(
    installments: List<PaymentInstallmentItem>,
    onInstallmentsChange: (List<PaymentInstallmentItem>) -> Unit,
    onDateClick: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    responsive: ResponsiveValues
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(responsive.padding),
        verticalArrangement = Arrangement.spacedBy(responsive.spacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cuotas",
                fontSize = responsive.titleTextSize,
                fontWeight = FontWeight.W600,
                color = Color(0xFF1C1C1E),
                letterSpacing = (-0.2).sp
            )
            
            // Botón agregar responsive
            Box(
                modifier = Modifier
                    .size(responsive.stepSize + 4.dp)
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
                    modifier = Modifier.size(responsive.stepSize * 0.6f),
                    tint = Color.White
                )
            }
        }
        
        if (installments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(responsive.stepSize + 16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Toca + para agregar cuotas",
                    fontSize = responsive.bodyTextSize,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.1).sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(responsive.spacing),
                modifier = Modifier.heightIn(max = responsive.stepSize * 6)
            ) {
                items(installments) { installment ->
                    AppleInstallmentItemCompact(
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
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(responsive.spacing)
        ) {
            AppleButton(
                text = "← Atrás",
                onClick = onBack,
                modifier = Modifier.weight(1f),
                variant = AppleButtonVariant.SECONDARY,
                size = responsive.buttonSize
            )
            
            AppleButton(
                text = "Resumen →",
                onClick = onNext,
                modifier = Modifier.weight(1f),
                variant = AppleButtonVariant.PRIMARY,
                size = responsive.buttonSize,
                enabled = installments.isNotEmpty()
            )
        }
    }
}

@Composable
private fun ApplePaymentSummaryStepCompact(
    totalAmount: Double,
    initialPayment: Double,
    installments: List<PaymentInstallmentItem>,
    onBack: () -> Unit,
    responsive: ResponsiveValues
) {
    val totalInstallments = installments.sumOf { it.amount }
    val totalPayment = initialPayment + totalInstallments
    val isValid = totalPayment == totalAmount
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(responsive.padding),
        verticalArrangement = Arrangement.spacedBy(responsive.spacing)
    ) {
        Text(
            text = "Resumen de Pagos",
            fontSize = responsive.titleTextSize,
            fontWeight = FontWeight.W600,
            color = Color(0xFF1C1C1E),
            letterSpacing = (-0.2).sp
        )
        
        // Resumen compacto
        Column(
            verticalArrangement = Arrangement.spacedBy(responsive.spacing)
        ) {
            AppleSummaryRowCompact("Pago inicial", "$${String.format("%.2f", initialPayment)}", false, responsive)
            AppleSummaryRowCompact("Total cuotas", "$${String.format("%.2f", totalInstallments)}", false, responsive)
            
            HorizontalDivider(color = Color(0xFFE5E5EA))
            
            AppleSummaryRowCompact(
                "Total pagos", 
                "$${String.format("%.2f", totalPayment)}",
                isTotal = true,
                responsive = responsive
            )
            AppleSummaryRowCompact(
                "Total del libro", 
                "$${String.format("%.2f", totalAmount)}",
                isTotal = true,
                responsive = responsive
            )
        }
        
        // Estado de validación compacto
        if (isValid) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E8))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "¡Balanceado!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF34C759)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (totalPayment < totalAmount) 
                        "Faltan $${String.format("%.2f", totalAmount - totalPayment)}"
                    else 
                        "Sobran $${String.format("%.2f", totalPayment - totalAmount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        AppleButton(
            text = "← Atrás",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            variant = AppleButtonVariant.SECONDARY,
            size = responsive.buttonSize
        )
    }
}

@Composable
private fun AppleSummaryRowCompact(
    label: String,
    value: String,
    isTotal: Boolean = false,
    responsive: ResponsiveValues
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) responsive.titleTextSize else responsive.bodyTextSize,
            fontWeight = if (isTotal) FontWeight.W600 else FontWeight.W400,
            color = if (isTotal) Color(0xFF1C1C1E) else Color(0xFF8E8E93),
            letterSpacing = (-0.1).sp
        )
        Text(
            text = value,
            fontSize = if (isTotal) (responsive.titleTextSize.value + 2).sp else responsive.bodyTextSize,
            fontWeight = if (isTotal) FontWeight.W700 else FontWeight.W500,
            color = Color(0xFF1C1C1E),
            letterSpacing = (-0.2).sp
        )
    }
}

@Composable
private fun AppleInstallmentItemCompact(
    installment: PaymentInstallmentItem,
    onAmountChange: (Double) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onDelete: () -> Unit,
    onDateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF8E8E93)
                )
            },
            prefix = { 
                Text(
                    "$",
                    fontSize = 12.sp,
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
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF1C1C1E)
            )
        )
        
        // Fecha compacta
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF1C1C1E)
                )
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF8E8E93)
                )
            }
        }
        
        // Botón eliminar compacto
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF3B30).copy(alpha = 0.1f))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Eliminar cuota",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFFFF3B30)
            )
        }
    }
}

@Composable
private fun ApplePaymentHeaderCompact(
    totalAmount: Double,
    remainingAmount: Double,
    isValid: Boolean,
    responsive: ResponsiveValues
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = responsive.padding, vertical = responsive.spacing),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total: $${String.format("%.2f", totalAmount)}",
            fontSize = responsive.titleTextSize,
            fontWeight = FontWeight.W600,
            color = Color(0xFF1C1C1E),
            letterSpacing = (-0.2).sp
        )
        
        Text(
            text = if (isValid) "✓" else "Restante: $${String.format("%.2f", remainingAmount)}",
            fontSize = responsive.bodyTextSize,
            fontWeight = FontWeight.W500,
            color = if (isValid) Color(0xFF34C759) else Color(0xFFFF9500),
            letterSpacing = (-0.1).sp
        )
    }
}
