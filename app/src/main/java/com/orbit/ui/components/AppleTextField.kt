package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isPassword: Boolean = false,
    isRequired: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    error: String? = null,
    helperText: String? = null,
    maxLines: Int = 1
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Label
        if (label != null) {
            Text(
                text = if (isRequired) "$label *" else label,
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFF1D1D1F),
                letterSpacing = (-0.1).sp
            )
        }
        
        // TextField Container - Ultra limpio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (error != null) Color(0xFFFFEBEE) else Color(0xFFF8F9FA)
                )
        ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = placeholder?.let {
                        {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                color = Color(0xFF8E8E93),
                                letterSpacing = (-0.1).sp
                            )
                        }
                    },
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            if (isPassword) {
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible }
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar",
                                        tint = Color(0xFF8E8E93),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            if (trailingIcon != null && onTrailingIconClick != null) {
                                IconButton(onClick = onTrailingIconClick) {
                                    Icon(
                                        imageVector = trailingIcon,
                                        contentDescription = null,
                                        tint = Color(0xFF8E8E93),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    },
                    visualTransformation = if (isPassword && !isPasswordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    enabled = enabled,
                    maxLines = maxLines,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (error != null) Color(0xFFFF3B30) else Color(0xFF007AFF),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color(0xFF1D1D1F),
                        unfocusedTextColor = Color(0xFF1D1D1F),
                        cursorColor = Color(0xFF007AFF),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        letterSpacing = (-0.1).sp
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
        
        // Helper text or error - Ultra limpio
        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = Color(0xFFFF3B30),
                letterSpacing = (-0.1).sp
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                fontSize = 11.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF8E8E93),
                letterSpacing = (-0.1).sp
            )
        }
    }

