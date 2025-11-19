package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.InventoryMovement
import com.orbit.data.entity.MovementType
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryMovementDao {
    
    @Query("SELECT * FROM inventory_movements WHERE productId = :productId ORDER BY createdAt DESC")
    fun getMovementsByProduct(productId: Long): Flow<List<InventoryMovement>>
    
    @Query("SELECT * FROM inventory_movements ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMovements(limit: Int = 50): Flow<List<InventoryMovement>>
    
    @Query("SELECT * FROM inventory_movements WHERE movementType = :type ORDER BY createdAt DESC")
    fun getMovementsByType(type: MovementType): Flow<List<InventoryMovement>>
    
    @Query("SELECT * FROM inventory_movements WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getMovementsByDateRange(startDate: Long, endDate: Long): Flow<List<InventoryMovement>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: InventoryMovement): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovements(movements: List<InventoryMovement>)
    
    @Delete
    suspend fun deleteMovement(movement: InventoryMovement)
    
    @Query("DELETE FROM inventory_movements WHERE productId = :productId")
    suspend fun deleteMovementsByProduct(productId: Long)
}

