package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.entity.*
import com.orbit.data.repository.OrbitRepository
import com.orbit.data.model.InstallmentConfig
import com.orbit.data.model.OrderCreationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val repository: OrbitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewOrderUiState())
    val uiState: StateFlow<NewOrderUiState> = _uiState.asStateFlow()
    
    val clients: StateFlow<List<Client>> = repository.getAllClients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val products: StateFlow<List<Product>> = repository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    
    fun addProductToOrder(product: Product, quantity: Int) {
        // Validación básica de UI (cantidad > 0)
        if (quantity <= 0) {
            _uiState.value = _uiState.value.copy(
                error = "La cantidad debe ser mayor a 0"
            )
            return
        }
        
        val currentItems = _uiState.value.orderItems.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.first.id == product.id }
        
        val newQuantity = if (existingItemIndex >= 0) {
            currentItems[existingItemIndex].second + quantity
        } else {
            quantity
        }
        
        // Validar stock (validación de negocio)
        if (product.availableQuantity < newQuantity) {
            _uiState.value = _uiState.value.copy(
                error = "Stock insuficiente para ${product.name}. Disponible: ${product.availableQuantity}, Solicitado: $newQuantity"
            )
            return
        }
        
        // Actualizar items
        if (existingItemIndex >= 0) {
            currentItems[existingItemIndex] = product to newQuantity
        } else {
            currentItems.add(product to quantity)
        }
        
        _uiState.value = _uiState.value.copy(orderItems = currentItems, error = null)
        calculateTotal()
    }
    
    fun removeProductFromOrder(product: Product) {
        val currentItems = _uiState.value.orderItems.toMutableList()
        currentItems.removeAll { it.first.id == product.id }
        _uiState.value = _uiState.value.copy(orderItems = currentItems)
        calculateTotal()
    }
    
    fun updateProductQuantity(product: Product, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFromOrder(product)
            return
        }
        
        val currentItems = _uiState.value.orderItems.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.first.id == product.id }
        
        if (existingItemIndex >= 0) {
            currentItems[existingItemIndex] = product to newQuantity
            _uiState.value = _uiState.value.copy(orderItems = currentItems)
            calculateTotal()
        }
    }
    
    fun setPaymentMethod(paymentMethod: PaymentMethod) {
        _uiState.value = _uiState.value.copy(paymentMethod = paymentMethod)
    }
    
    fun setNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    fun setInstallmentConfig(config: InstallmentConfig?) {
        _uiState.value = _uiState.value.copy(installmentConfig = config)
    }
    
    private fun calculateTotal() {
        val total = _uiState.value.orderItems.sumOf { (product, quantity) ->
            product.unitPrice * quantity
        }
        _uiState.value = _uiState.value.copy(totalAmount = total)
    }
    
    fun createOrder() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(isLoading = true, error = null)
            
            if (currentState.selectedClient == null || currentState.orderItems.isEmpty()) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Cliente y productos son requeridos"
                )
                return@launch
            }
            
            val orderItems = currentState.orderItems.map { (product, quantity) ->
                product.id to quantity
            }
            
            val result = repository.createOrderWithItems(
                clientId = currentState.selectedClient!!.id,
                orderItems = orderItems,
                paymentMethod = currentState.paymentMethod,
                notes = currentState.notes
            )
            
            when (result) {
                is OrderCreationResult.Success -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isOrderCreated = true,
                        createdOrderId = result.orderId,
                        error = null
                    )
                }
                is OrderCreationResult.Error -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun createOrderWithClient(
        clientName: String,
        clientPhone: String,
        clientAddress: String,
        clientReference: String
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(isLoading = true, error = null)
            
            if (currentState.orderItems.isEmpty()) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Productos son requeridos"
                )
                return@launch
            }
            
            try {
                // Crear cliente primero
                val clientId = repository.createClient(
                    name = clientName,
                    phone = clientPhone,
                    address = clientAddress,
                    reference = clientReference.ifBlank { null }
                )
                
                // Crear pedido con el cliente
                val orderItems = currentState.orderItems.map { (product, quantity) ->
                    product.id to quantity
                }
                
                val result = repository.createOrderWithItems(
                    clientId = clientId,
                    orderItems = orderItems,
                    paymentMethod = currentState.paymentMethod,
                    notes = currentState.notes
                )
                
                when (result) {
                    is OrderCreationResult.Success -> {
                        // Si es pago por cuotas, crear las cuotas
                        if (currentState.paymentMethod == PaymentMethod.INSTALLMENTS && currentState.installmentConfig != null) {
                            repository.createInstallmentPlan(result.orderId, currentState.installmentConfig!!)
                        }
                        
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            isOrderCreated = true,
                            createdOrderId = result.orderId,
                            error = null
                        )
                    }
                    is OrderCreationResult.Error -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }
    
    fun clearOrder() {
        _uiState.value = NewOrderUiState()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class NewOrderUiState(
    val selectedClient: Client? = null,
    val orderItems: List<Pair<Product, Int>> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    val totalAmount: Double = 0.0,
    val installmentConfig: InstallmentConfig? = null,
    val isOrderCreated: Boolean = false,
    val createdOrderId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

