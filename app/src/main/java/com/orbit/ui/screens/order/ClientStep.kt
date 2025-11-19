package com.orbit.ui.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.components.AppleButton
import com.orbit.ui.components.AppleButtonVariant
import com.orbit.ui.components.AppleButtonSize
import com.orbit.ui.components.AppleTextField
import com.orbit.ui.components.AppleSectionCard
import com.orbit.ui.theme.*

@Composable
fun ClientStep(
    clientName: String,
    onClientNameChange: (String) -> Unit,
    clientPhone: String,
    onClientPhoneChange: (String) -> Unit,
    clientAddress: String,
    onClientAddressChange: (String) -> Unit,
    clientReference: String,
    onClientReferenceChange: (String) -> Unit,
    onNavigateToPage: (Int) -> Unit,
    cameFromConfirm: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Form Fields
        AppleSectionCard(
            title = "Datos del Cliente",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppleTextField(
                    value = clientName,
                    onValueChange = onClientNameChange,
                    label = "Nombre completo",
                    leadingIcon = Icons.Default.Person,
                    isRequired = true,
                    placeholder = "Ingresa el nombre completo del cliente"
                )
                
                AppleTextField(
                    value = clientPhone,
                    onValueChange = onClientPhoneChange,
                    label = "Teléfono",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    isRequired = true,
                    placeholder = "Número de teléfono"
                )
                
                AppleTextField(
                    value = clientAddress,
                    onValueChange = onClientAddressChange,
                    label = "Dirección",
                    leadingIcon = Icons.Default.LocationOn,
                    isRequired = true,
                    placeholder = "Dirección completa"
                )
                
                AppleTextField(
                    value = clientReference,
                    onValueChange = onClientReferenceChange,
                    label = "Referencia",
                    leadingIcon = Icons.AutoMirrored.Filled.Note,
                    isRequired = false,
                    placeholder = "Referencia adicional (opcional)",
                    helperText = "Información adicional sobre el cliente"
                )
            }
        }
        
        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppleButton(
                text = if (cameFromConfirm) "Listo ✓" else "Productos →",
                onClick = { 
                    if (cameFromConfirm) {
                        onNavigateToPage(3) // Regresar a confirmar
                    } else {
                        onNavigateToPage(1) // Ir a productos
                    }
                },
                modifier = Modifier.weight(1f),
                variant = if (clientName.isNotBlank() && clientPhone.isNotBlank() && clientAddress.isNotBlank()) 
                    AppleButtonVariant.PRIMARY else AppleButtonVariant.SECONDARY,
                size = AppleButtonSize.SMALL,
                enabled = clientName.isNotBlank() && clientPhone.isNotBlank() && clientAddress.isNotBlank()
            )
        }
    }
}
