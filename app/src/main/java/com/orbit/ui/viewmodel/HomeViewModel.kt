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
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            // Load today's orders count
            val todayOrdersCount = repository.getOrderCountFromDate(startOfDay)
            
            // Load today's total sales
            val todaySales = repository.getTotalSalesFromDate(startOfDay) ?: 0.0
            
            // Load recent orders
            repository.getAllOrdersWithDetails()
                .combine(repository.getOrdersFromDate(startOfDay)) { allOrders, todayOrders ->
                    val recentOrders = allOrders.take(3) // Show only 3 most recent
                    HomeUiState(
                        todayOrdersCount = todayOrdersCount,
                        todaySales = todaySales,
                        recentOrders = recentOrders,
                        isLoading = false
                    )
                }
                .collect { state ->
                    _uiState.value = state
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
    val recentOrders: List<com.orbit.data.relation.OrderWithDetails> = emptyList(),
    val isLoading: Boolean = true
)

