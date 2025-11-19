package com.orbit.domain.usecase.inventory

import com.orbit.data.entity.InventoryStatus
import com.orbit.data.repository.OrbitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class StockAlert(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val threshold: Int,
    val severity: AlertSeverity
)

enum class AlertSeverity {
    LOW,      // Stock bajo pero no crítico
    MEDIUM,   // Stock muy bajo
    HIGH,     // Stock crítico
    CRITICAL  // Sin stock
}

class CheckLowStockUseCase @Inject constructor(
    private val repository: OrbitRepository
) {
    operator fun invoke(threshold: Int = 10): Flow<List<StockAlert>> {
        return repository.getLowStockProducts(threshold)
            .map { products ->
                products.map { product ->
                    val severity = when {
                        product.availableQuantity == 0 -> AlertSeverity.CRITICAL
                        product.availableQuantity <= 3 -> AlertSeverity.HIGH
                        product.availableQuantity <= 5 -> AlertSeverity.MEDIUM
                        else -> AlertSeverity.LOW
                    }
                    
                    StockAlert(
                        productId = product.id,
                        productName = product.name,
                        currentStock = product.availableQuantity,
                        threshold = threshold,
                        severity = severity
                    )
                }
            }
    }
}

