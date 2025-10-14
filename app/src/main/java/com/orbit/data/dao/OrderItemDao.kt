package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.OrderItem
import com.orbit.data.relation.OrderItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    
    @Transaction
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsWithProducts(orderId: Long): List<OrderItemWithProduct>
    
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Long): List<OrderItem>
    
    @Query("SELECT * FROM order_items WHERE productId = :productId")
    fun getOrderItemsByProduct(productId: Long): Flow<List<OrderItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
    
    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)
    
    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)
    
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: Long)
}
