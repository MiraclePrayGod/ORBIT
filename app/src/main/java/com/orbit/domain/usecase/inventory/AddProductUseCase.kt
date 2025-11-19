package com.orbit.domain.usecase.inventory

import com.orbit.data.entity.Product
import com.orbit.data.repository.OrbitRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: OrbitRepository
) {
    suspend operator fun invoke(product: Product): Result<Long> {
        return try {
            // Validaciones de negocio
            if (product.name.isBlank()) {
                return Result.failure(IllegalArgumentException("El nombre del producto es requerido"))
            }
            
            if (product.unitPrice <= 0) {
                return Result.failure(IllegalArgumentException("El precio debe ser mayor a 0"))
            }
            
            if (product.availableQuantity < 0) {
                return Result.failure(IllegalArgumentException("La cantidad disponible no puede ser negativa"))
            }
            
            if (product.totalQuantity < product.availableQuantity) {
                return Result.failure(IllegalArgumentException("La cantidad total no puede ser menor a la disponible"))
            }
            
            val productId = repository.insertProduct(product)
            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

