package com.orbit.domain.usecase.inventory

import com.orbit.data.repository.OrbitRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: OrbitRepository
) {
    suspend operator fun invoke(productId: Long): Result<Unit> {
        return try {
            val product = repository.getProductById(productId)
                ?: return Result.failure(IllegalArgumentException("Producto no encontrado"))
            
            // Verificar si tiene pedidos asociados
            val orderItems = repository.getOrderItemsByProduct(productId).first()
            if (orderItems.isNotEmpty()) {
                return Result.failure(
                    IllegalStateException(
                        "No se puede eliminar el producto porque tiene ${orderItems.size} pedidos asociados"
                    )
                )
            }
            
            repository.deleteProduct(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

