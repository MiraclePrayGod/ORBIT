package com.orbit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.data.entity.OrderStatus
import com.orbit.data.repository.OrbitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: OrbitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            val selectedDateValue = _selectedDate.value
            val startOfDay = selectedDateValue.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = selectedDateValue.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            // Load selected date's orders count
            val ordersCount = repository.getOrderCountFromDate(startOfDay)
            
            // Load selected date's total sales
            val sales = repository.getTotalSalesFromDate(startOfDay) ?: 0.0
            
            // Load pending orders count
            val pendingOrdersCount = repository.getPendingOrdersCount()
            
            // Load low stock products count
            val lowStockCount = repository.getLowStockProductsCount(threshold = 10)
            
            // Load recent orders filtered by selected date (only need the first 3)
            repository.getAllOrdersWithDetails()
                .collect { allOrders ->
                    val filteredOrders = allOrders.filter { order ->
                        order.order.createdAt >= startOfDay && order.order.createdAt < endOfDay
                    }
                    val recentOrders = filteredOrders.take(3) // Show only 3 most recent
                    _uiState.value = HomeUiState(
                        todayOrdersCount = ordersCount,
                        todaySales = sales,
                        pendingOrdersCount = pendingOrdersCount,
                        lowStockCount = lowStockCount,
                        recentOrders = recentOrders,
                        isLoading = false
                    )
                }
        }
    }
    
    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadHomeData()
    }
}

data class HomeUiState(
    val todayOrdersCount: Int = 0,
    val todaySales: Double = 0.0,
    val pendingOrdersCount: Int = 0,
    val lowStockCount: Int = 0,
    val recentOrders: List<com.orbit.data.relation.OrderWithDetails> = emptyList(),
    val isLoading: Boolean = true
)

