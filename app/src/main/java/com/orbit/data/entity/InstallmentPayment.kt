package com.orbit.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "installment_payments",
    foreignKeys = [
        ForeignKey(
            entity = Installment::class,
            parentColumns = ["id"],
            childColumns = ["installmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["installmentId"])]
)
data class InstallmentPayment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val installmentId: Long,
    val installmentNumber: Int, // 1, 2, 3, etc.
    val amount: Double,
    val dueDate: Long, // timestamp de la fecha de vencimiento
    val paidDate: Long? = null, // timestamp cuando se pagó (null si no se ha pagado)
    val status: InstallmentPaymentStatus = InstallmentPaymentStatus.PENDING,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class InstallmentPaymentStatus {
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED,
    PARTIALLY_PAID
}
