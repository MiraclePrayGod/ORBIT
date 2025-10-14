package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.ui.theme.*
import com.orbit.ui.viewmodel.AuthViewModel

private fun isInstitutionalEmail(email: String): Boolean {
    val institutionalDomains = listOf(
        ".edu",
        ".edu.pe",
        "upeu.edu.pe"
    )
    return institutionalDomains.any { domain ->
        email.endsWith(domain, ignoreCase = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }
    
    // Limpiar error cuando el usuario empiece a escribir
    LaunchedEffect(email, password) {
        if (uiState.error != null) {
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo o título
        Card(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "O",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Text(
            text = "Orbit App",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
        
        Text(
            text = "Gestión de Pedidos",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Institucional") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            supportingText = { 
                if (email.isNotEmpty() && !isInstitutionalEmail(email)) {
                    Text("Debe ser un correo institucional (.edu, .edu.pe, upeu.edu.pe)", color = Color.Red)
                }
            },
                    isError = email.isNotEmpty() && !isInstitutionalEmail(email)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón de login
                Button(
                    onClick = {
                        viewModel.signIn(email, password)
                    },
                    modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && 
                     email.isNotBlank() && 
                     password.isNotBlank() &&
                     isInstitutionalEmail(email),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Iniciar Sesión con Microsoft",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Error
                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Información adicional
        Text(
            text = "Solo correos institucionales (.edu, .edu.pe, upeu.edu.pe) son permitidos",
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    OrbitTheme {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}
