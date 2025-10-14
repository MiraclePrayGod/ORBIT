package com.orbit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.data.entity.PaymentMethod
import com.orbit.data.entity.Product
import com.orbit.data.model.InstallmentConfig
import com.orbit.ui.components.OrderTabNavigation
import com.orbit.ui.components.PaymentInstallmentItem
import com.orbit.ui.screens.order.*
import com.orbit.ui.theme.*
import com.orbit.ui.viewmodel.NewOrderViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    onBackClick: () -> Unit = {},
    viewModel: NewOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    
    // Estados locales para el formulario
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }
    var clientReference by remember { mutableStateOf("") }
    var showProductDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var installmentConfig by remember { mutableStateOf<InstallmentConfig?>(null) }
    
    // Estados para el pago por partes
    var initialPayment by remember { mutableStateOf(0.0) }
    var installments by remember { mutableStateOf<List<com.orbit.ui.components.PaymentInstallmentItem>>(emptyList()) }
    
    // Estado para mostrar diálogo de éxito
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Estados para el diseño
    val pagerState = rememberPagerState(pageCount = { 4 })
    val steps = listOf("Cliente", "Productos", "Pago", "Confirmar")
    val coroutineScope = rememberCoroutineScope()
    
    // Estado para recordar si venimos del paso de confirmación
    var cameFromConfirm by remember { mutableStateOf(false) }
    
    // Función para navegar entre páginas
    fun navigateToPage(page: Int) {
        if (pagerState.currentPage == 3 && page < 3) {
            cameFromConfirm = true
        } else {
            cameFromConfirm = false
        }
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }
    
    // Función para agregar producto
    fun addProduct(product: Product, quantity: Int) {
        viewModel.addProductToOrder(product, quantity)
    }
    
    // Función para crear pedido
    fun createOrder() {
        // Actualizar el estado del ViewModel con los valores actuales
        viewModel.setPaymentMethod(paymentMethod)
        viewModel.setNotes(notes)
        
        // Si es pago por cuotas, crear el InstallmentConfig
        if (paymentMethod == PaymentMethod.INSTALLMENTS && installments.isNotEmpty()) {
            val config = InstallmentConfig(
                totalAmount = uiState.totalAmount,
                initialPayment = initialPayment,
                numberOfInstallments = installments.size,
                installmentAmount = installments.firstOrNull()?.amount ?: 0.0,
                paymentInterval = 30,
                startDate = installments.firstOrNull()?.dueDate ?: java.time.LocalDate.now()
            )
            viewModel.setInstallmentConfig(config)
        } else {
            viewModel.setInstallmentConfig(null)
        }
        
        viewModel.createOrderWithClient(
            clientName = clientName,
            clientPhone = clientPhone,
            clientAddress = clientAddress,
            clientReference = clientReference
        )
    }
    
    // Loading overlay
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = ButtonPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Procesando pedido...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
    
    // Main content
    com.orbit.ui.components.ResponsiveContainer {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Nuevo Pedido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfacePrimary
            )
        )
        
        // Tab Navigation
        OrderTabNavigation(
            steps = steps,
            pagerState = pagerState,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        // Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ClientStep(
                    clientName = clientName,
                    onClientNameChange = { clientName = it },
                    clientPhone = clientPhone,
                    onClientPhoneChange = { clientPhone = it },
                    clientAddress = clientAddress,
                    onClientAddressChange = { clientAddress = it },
                    clientReference = clientReference,
                    onClientReferenceChange = { clientReference = it },
                    onNavigateToPage = ::navigateToPage,
                    cameFromConfirm = cameFromConfirm
                )
                
                1 -> ProductsStep(
                    orderItems = uiState.orderItems,
                    onAddProduct = { showProductDialog = true },
                    onUpdateQuantity = { product, quantity ->
                        viewModel.updateProductQuantity(product, quantity)
                    },
                    onNavigateToPage = ::navigateToPage,
                    cameFromConfirm = cameFromConfirm
                )
                
                2 -> PaymentStep(
                    paymentMethod = paymentMethod,
                    onPaymentMethodChange = { paymentMethod = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    totalAmount = uiState.totalAmount,
                    installmentConfig = installmentConfig,
                    onInstallmentConfigChange = { installmentConfig = it },
                    initialPayment = initialPayment,
                    onInitialPaymentChange = { initialPayment = it },
                    installments = installments,
                    onInstallmentsChange = { installments = it },
                    onNavigateToPage = ::navigateToPage,
                    cameFromConfirm = cameFromConfirm
                )
                
                3 -> ConfirmStep(
                    clientName = clientName,
                    clientPhone = clientPhone,
                    clientAddress = clientAddress,
                    clientReference = clientReference,
                    orderItems = uiState.orderItems,
                    paymentMethod = paymentMethod,
                    notes = notes,
                    totalAmount = uiState.totalAmount,
                    onConfirm = ::createOrder,
                    onNavigateToPage = ::navigateToPage
                )
            }
        }
    }
    
    // Product Selection Dialog
    if (showProductDialog) {
        ProductSelectionDialog(
            products = products,
            onProductSelected = ::addProduct,
            onDismiss = { showProductDialog = false }
        )
    }
    
    // Success handling - Show success dialog and navigate back
    LaunchedEffect(uiState.isOrderCreated) {
        if (uiState.isOrderCreated) {
            showSuccessDialog = true
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar or dialog
            // You can implement error handling here
        }
    }
    
    // Show success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBackClick()
            },
            title = { Text("¡Pedido Creado!") },
            text = { Text("El pedido se ha creado exitosamente.") },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    onBackClick()
                }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Show error dialog
    val error = uiState.error
    if (error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

// Preview removido para evitar errores de KAPT en builds
