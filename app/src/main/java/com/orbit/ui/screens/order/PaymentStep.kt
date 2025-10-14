package com.orbit.ui.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.model.InstallmentConfig
import com.orbit.ui.components.AppleButton
import com.orbit.ui.components.AppleButtonVariant
import com.orbit.ui.components.AppleButtonSize
import com.orbit.ui.components.AppleTextField
import com.orbit.ui.components.AppleSectionCard
import com.orbit.ui.components.ApplePaymentFlow
import com.orbit.ui.theme.*

@Composable
fun PaymentStep(
    paymentMethod: PaymentMethod,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    totalAmount: Double,
    installmentConfig: InstallmentConfig?,
    onInstallmentConfigChange: (InstallmentConfig?) -> Unit,
    initialPayment: Double,
    onInitialPaymentChange: (Double) -> Unit,
    installments: List<com.orbit.ui.components.PaymentInstallmentItem>,
    onInstallmentsChange: (List<com.orbit.ui.components.PaymentInstallmentItem>) -> Unit,
    onNavigateToPage: (Int) -> Unit,
    cameFromConfirm: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // Payment Method Selection
        AppleSectionCard(
            title = "Método de Pago",
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppleButton(
                    text = "Efectivo",
                    onClick = { onPaymentMethodChange(PaymentMethod.CASH) },
                    modifier = Modifier.weight(1f),
                    variant = if (paymentMethod == PaymentMethod.CASH) 
                        AppleButtonVariant.PRIMARY else AppleButtonVariant.SECONDARY,
                    size = AppleButtonSize.LARGE
                )
                
                AppleButton(
                    text = "Cuotas",
                    onClick = { onPaymentMethodChange(PaymentMethod.INSTALLMENTS) },
                    modifier = Modifier.weight(1f),
                    variant = if (paymentMethod == PaymentMethod.INSTALLMENTS) 
                        AppleButtonVariant.PRIMARY else AppleButtonVariant.SECONDARY,
                    size = AppleButtonSize.LARGE
                )
            }
        }
        
        // Configuración de pago por partes (para cuotas)
        if (paymentMethod == PaymentMethod.INSTALLMENTS) {
            ApplePaymentFlow(
                totalAmount = totalAmount,
                initialPayment = initialPayment,
                onInitialPaymentChange = onInitialPaymentChange,
                installments = installments,
                onInstallmentsChange = onInstallmentsChange,
                onConfigChange = onInstallmentConfigChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Notes
        AppleSectionCard(
            title = "Notas del Pedido",
            modifier = Modifier.fillMaxWidth()
        ) {
            AppleTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = "Notas del pedido",
                isRequired = false,
                placeholder = "Información adicional sobre el pedido",
                helperText = "Opcional: detalles especiales o instrucciones",
                maxLines = 3
            )
        }
        
        // Espacio adicional para el scroll
        Spacer(modifier = Modifier.height(20.dp))
        
        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppleButton(
                text = if (cameFromConfirm) "Listo ✓" else "← Productos",
                onClick = { 
                    if (cameFromConfirm) {
                        onNavigateToPage(3) // Regresar a confirmar
                    } else {
                        onNavigateToPage(1) // Ir a productos
                    }
                },
                modifier = Modifier.weight(1f),
                variant = AppleButtonVariant.SECONDARY,
                size = AppleButtonSize.LARGE
            )
            
            AppleButton(
                text = if (cameFromConfirm) "Listo ✓" else "Confirmar →",
                onClick = { 
                    if (cameFromConfirm) {
                        onNavigateToPage(3) // Regresar a confirmar
                    } else {
                        onNavigateToPage(3) // Ir a confirmar
                    }
                },
                modifier = Modifier.weight(1f),
                variant = AppleButtonVariant.PRIMARY,
                size = AppleButtonSize.LARGE
            )
        }
    }
}
