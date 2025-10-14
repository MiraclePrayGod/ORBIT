package com.orbit.data.model

import java.time.LocalDate

data class InstallmentConfig(
    val totalAmount: Double,
    val initialPayment: Double,
    val numberOfInstallments: Int,
    val installmentAmount: Double,
    val paymentInterval: Int, // días
    val startDate: LocalDate
)
