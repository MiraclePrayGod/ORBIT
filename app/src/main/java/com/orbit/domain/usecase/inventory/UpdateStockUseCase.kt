package com.orbit.domain.usecase.inventory

import com.orbit.data.entity.InventoryStatus
import com.orbit.data.entity.MovementType
import com.orbit.data.repository.OrbitRepository
import javax.inject.Inject

class UpdateStockUseCase @Inject constructor(
    private val repository: OrbitRepository
) {
    suspend operator fun invoke(
        productId: Long,
        quantity: Int,
        movementType: MovementType,
        reason: String? = null
    ): Result<Unit> {
        return try {
            val product = repository.getProductById(productId)
                ?: return Result.failure(IllegalArgumentException("Producto no encontrado"))
            
            val previousQuantity = product.availableQuantity
            val newQuantity = when (movementType) {
                MovementType.STOCK_IN -> previousQuantity + quantity
                MovementType.STOCK_OUT -> previousQuantity - quantity
                MovementType.ADJUSTMENT -> quantity
                MovementType.RETURN -> previousQuantity + quantity
            }
            
            if (newQuantity < 0) {
                return Result.failure(IllegalArgumentException("No hay suficiente stock disponible"))
            }
            
            // Actualizar cantidad
            repository.updateProductQuantity(productId, newQuantity)
            
            // Actualizar estado según nueva cantidad
            val newStatus = when {
                newQuantity <= 0 -> InventoryStatus.OUT_OF_STOCK
                newQuantity <= 10 -> InventoryStatus.LOW_STOCK
                else -> InventoryStatus.AVAILABLE
            }
            repository.updateProductStatus(productId, newStatus)
            
            // Registrar movimiento
            repository.insertInventoryMovement(
                productId = productId,
                movementType = movementType,
                quantity = quantity,
                previousQuantity = previousQuantity,
                newQuantity = newQuantity,
                reason = reason
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

