package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.entity.OrderStatus
import com.orbit.data.repository.OrbitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrbitRepository
) : ViewModel() {
    
    private val _selectedFilter = MutableStateFlow(OrderFilter.ALL)
    val selectedFilter: StateFlow<OrderFilter> = _selectedFilter.asStateFlow()
    
    val orders: StateFlow<List<com.orbit.data.relation.OrderWithDetails>> = 
        repository.getAllOrdersWithDetails()
            .combine(_selectedFilter) { allOrders, filter ->
                when (filter) {
                    OrderFilter.ALL -> allOrders
                    OrderFilter.IN_PROGRESS -> allOrders.filter { it.order.status == OrderStatus.IN_PROGRESS }
                    OrderFilter.PAID -> allOrders.filter { it.order.status == OrderStatus.PAID }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    fun setFilter(filter: OrderFilter) {
        _selectedFilter.value = filter
    }
    
    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            val orderWithDetails = repository.getOrderWithDetails(orderId)
            orderWithDetails?.let { orderDetails ->
                // Delete related data first
                repository.deleteOrderItemsByOrderId(orderId)
                repository.deletePaymentsByOrderId(orderId)
                // Then delete the order
                repository.deleteOrder(orderDetails.order)
            }
        }
    }
}

enum class OrderFilter {
    ALL,
    IN_PROGRESS,
    PAID
}

