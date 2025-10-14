package com.orbit.data

// Legacy data classes - kept for backward compatibility
// New data classes are in com.orbit.data.entity package

data class LegacyInventoryItem(
    val id: Int,
    val name: String,
    val category: LegacyProductCategory,
    val availableQuantity: Int,
    val reservedQuantity: Int,
    val totalQuantity: Int,
    val status: LegacyInventoryStatus,
    val inProcessCount: Int,
    val lastUpdated: String
)

enum class LegacyProductCategory {
    ALL,
    SPIRITUAL,
    OTHER,
    PSYCHOLOGY,
    HEALTH
}

enum class LegacyInventoryStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    LOW_STOCK
}
