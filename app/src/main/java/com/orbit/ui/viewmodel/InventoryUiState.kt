package com.orbit.ui.viewmodel

import com.orbit.data.entity.Product
import com.orbit.domain.usecase.inventory.StockAlert

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(
        val products: List<Product>,
        val filteredProducts: List<Product>,
        val searchQuery: String = "",
        val selectedCategory: com.orbit.data.entity.ProductCategory = com.orbit.data.entity.ProductCategory.ALL,
        val lowStockAlerts: List<StockAlert> = emptyList(),
        val inventoryStats: InventoryStats = InventoryStats()
    ) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}

data class InventoryStats(
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val totalValue: Double = 0.0,
    val availableValue: Double = 0.0
)

