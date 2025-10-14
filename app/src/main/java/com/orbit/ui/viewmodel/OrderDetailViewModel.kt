package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.repository.OrbitRepository
import com.orbit.data.relation.OrderWithDetails
import com.orbit.data.model.PaymentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val repository: OrbitRepository
) : ViewModel() {
    
    private val _orderId = MutableStateFlow<Long?>(null)
    
    fun loadOrder(orderId: Long) {
        _orderId.value = orderId
    }
    
    private val _orderWithDetails = MutableStateFlow<OrderWithDetails?>(null)
    val orderWithDetails: StateFlow<OrderWithDetails?> = _orderWithDetails.asStateFlow()
    
    fun loadOrderDetails(orderId: Long) {
        viewModelScope.launch {
            try {
                _orderWithDetails.value = repository.getOrderWithDetails(orderId)
            } catch (e: Exception) {
                _orderWithDetails.value = null
            }
        }
    }
    
    suspend fun getOrderItemsWithProducts(orderId: Long): List<com.orbit.data.relation.OrderItemWithProduct> = 
        repository.getOrderItemsWithProducts(orderId)
    
    fun addPayment(
        orderId: Long, 
        amount: Double, 
        paymentMethod: com.orbit.data.entity.PaymentMethod, 
        notes: String? = null,
        reference: String? = null
    ) {
        viewModelScope.launch {
            val result = repository.addPaymentToOrder(orderId, amount, paymentMethod, notes, reference)
            when (result) {
                is PaymentResult.Success -> {
                    // Recargar detalles del pedido
                    loadOrderDetails(orderId)
                }
                is PaymentResult.Error -> {
                    // Manejar error - podrías emitir un evento de error aquí
                }
            }
        }
    }
    
    fun updateOrderStatus(orderId: Long, status: com.orbit.data.entity.OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }
}
