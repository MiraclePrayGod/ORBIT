package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "products",
    indices = [Index(value = ["name"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ProductCategory,
    val unitPrice: Double,
    val availableQuantity: Int = 0,
    val reservedQuantity: Int = 0,
    val totalQuantity: Int = 0,
    val status: InventoryStatus = InventoryStatus.AVAILABLE,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ProductCategory {
    ALL,
    SPIRITUAL,
    OTHER,
    PSYCHOLOGY,
    HEALTH
}

enum class InventoryStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    LOW_STOCK
}

