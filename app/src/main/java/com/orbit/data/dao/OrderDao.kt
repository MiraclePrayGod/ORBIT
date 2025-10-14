package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.Order
import com.orbit.data.entity.OrderStatus
import com.orbit.data.relation.OrderWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    
    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersWithDetails(): Flow<List<OrderWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderWithDetails(orderId: Long): OrderWithDetails?
    
    @Query("SELECT * FROM orders WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getOrdersByClient(clientId: Long): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE createdAt >= :date ORDER BY createdAt DESC")
    fun getOrdersFromDate(date: Long): Flow<List<Order>>
    
    @Query("SELECT COUNT(*) FROM orders WHERE createdAt >= :date")
    suspend fun getOrderCountFromDate(date: Long): Int
    
    @Query("SELECT SUM(totalAmount) FROM orders WHERE createdAt >= :date AND status = 'PAID'")
    suspend fun getTotalSalesFromDate(date: Long): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Delete
    suspend fun deleteOrder(order: Order)
    
    @Query("UPDATE orders SET status = :status, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus, timestamp: Long = System.currentTimeMillis())
}

