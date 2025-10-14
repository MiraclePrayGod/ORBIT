package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "payments",
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
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val paymentType: PaymentType = PaymentType.REGULAR_PAYMENT,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val reference: String? = null, // Referencia de transacción bancaria
    val status: PaymentStatus = PaymentStatus.COMPLETED
)

enum class PaymentType {
    REGULAR_PAYMENT,      // Pago normal (contado)
    INSTALLMENT_PAYMENT,  // Pago de cuota
    REFUND,              // Reembolso
    PARTIAL_PAYMENT      // Pago parcial
}

enum class PaymentStatus {
    PENDING,    // Pendiente
    COMPLETED,  // Completado
    FAILED,     // Fallido
    CANCELLED   // Cancelado
}

