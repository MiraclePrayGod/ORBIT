package com.orbit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isRequired: Boolean = false,
    isEnabled: Boolean = true,
    errorMessage: String? = null,
    helperText: String? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    textAlign: TextAlign = TextAlign.Start
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Label con indicador de requerido
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = label,
                style = InputLabel,
                color = TextPrimary
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = InputLabel,
                    color = StatusError
                )
            }
        }
        
        // Campo de texto
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder?.let { 
                { 
                    Text(
                        text = it,
                        style = InputText,
                        color = TextTertiary
                    ) 
                } 
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (errorMessage != null) StatusError else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = {
                when {
                    isPassword -> {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    trailingIcon != null && onTrailingIconClick != null -> {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                tint = if (errorMessage != null) StatusError else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            enabled = isEnabled,
            readOnly = !isEnabled,
            maxLines = maxLines,
            singleLine = singleLine,
            textStyle = InputText.copy(
                textAlign = textAlign,
                color = if (isEnabled) TextPrimary else TextTertiary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = BackgroundTertiary,
                focusedBorderColor = if (errorMessage != null) InputError else InputBorderFocused,
                unfocusedBorderColor = if (errorMessage != null) InputError else InputBorder,
                disabledBorderColor = BorderMedium,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextTertiary,
                focusedPlaceholderColor = TextTertiary,
                unfocusedPlaceholderColor = TextTertiary,
                cursorColor = InputBorderFocused
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Mensaje de error o texto de ayuda
        if (errorMessage != null || helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage ?: helperText ?: "",
                style = InputHelper,
                color = if (errorMessage != null) StatusError else TextSecondary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun ProfessionalSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier,
    onClearClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { 
            Text(
                text = placeholder,
                style = InputText,
                color = TextTertiary
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (value.isNotEmpty() && onClearClick != null) {
            {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        singleLine = true,
        textStyle = InputText,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            focusedBorderColor = InputBorderFocused,
            unfocusedBorderColor = InputBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedPlaceholderColor = TextTertiary,
            unfocusedPlaceholderColor = TextTertiary,
            cursorColor = InputBorderFocused
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}
