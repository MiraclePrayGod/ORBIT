package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_movements",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["createdAt"])
    ]
)
data class InventoryMovement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val movementType: MovementType,
    val quantity: Int,
    val previousQuantity: Int,
    val newQuantity: Int,
    val reason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null
)

enum class MovementType {
    STOCK_IN,      // Entrada de stock
    STOCK_OUT,     // Salida de stock (venta)
    ADJUSTMENT,    // Ajuste manual
    RETURN         // Devolución
}

