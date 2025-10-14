package com.orbit.data.model

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errors: List<String>) : ValidationResult()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getErrorMessage(): String? {
        return if (this is Error) {
            errors.joinToString("\n")
        } else null
    }
}

sealed class OrderCreationResult {
    data class Success(val orderId: Long) : OrderCreationResult()
    data class Error(val message: String, val details: List<String> = emptyList()) : OrderCreationResult()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrderId(): Long? = if (this is Success) orderId else null
    fun getErrorMessage(): String? = if (this is Error) message else null
}

sealed class PaymentResult {
    data class Success(val paymentId: Long) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}
