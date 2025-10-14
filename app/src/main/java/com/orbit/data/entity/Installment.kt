package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "installments",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class Installment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val totalAmount: Double,
    val initialPayment: Double,
    val numberOfInstallments: Int,
    val installmentAmount: Double,
    val paymentInterval: Int, // días entre pagos
    val startDate: Long, // timestamp de la primera cuota
    val status: InstallmentStatus = InstallmentStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class InstallmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,
    OVERDUE
}
