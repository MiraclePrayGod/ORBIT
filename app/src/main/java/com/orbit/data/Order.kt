package com.orbit.data

// Legacy data classes - kept for backward compatibility
// New data classes are in com.orbit.data.entity package

data class LegacyOrder(
    val id: Int,
    val client: String,
    val product: String,
    val quantity: Int,
    val price: Double,
    val status: LegacyOrderStatus,
    val paymentMethod: LegacyPaymentMethod,
    val createdAt: String,
    val phone: String = "555-0101",
    val address: String = "Av. Principal 123, Zona Centro",
    val unitPrice: Double = 0.0,
    val totalPaid: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val paidInstallments: Int = 0,
    val totalInstallments: Int = 0,
    val pendingInstallments: Int = 0
)

enum class LegacyOrderStatus {
    IN_PROGRESS,
    PAID,
    PENDING,
    CANCELLED
}

enum class LegacyPaymentMethod {
    CASH,
    INSTALLMENTS,
    CARD,
    TRANSFER
}
