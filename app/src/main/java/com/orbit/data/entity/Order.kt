package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clientId"])]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val totalAmount: Double,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)

enum class OrderStatus {
    IN_PROGRESS,
    PAID,
    PENDING,
    CANCELLED
}

enum class PaymentMethod {
    CASH,
    INSTALLMENTS,
    CARD,
    TRANSFER
}

