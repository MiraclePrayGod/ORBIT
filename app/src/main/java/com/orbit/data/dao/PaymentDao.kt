package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    
    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun getPaymentsByOrder(orderId: Long): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE orderId = :orderId")
    suspend fun getPaymentsByOrderSync(orderId: Long): List<Payment>
    
    @Query("SELECT SUM(amount) FROM payments WHERE orderId = :orderId")
    suspend fun getTotalPaidByOrder(orderId: Long): Double?
    
    @Query("SELECT COUNT(*) FROM payments WHERE orderId = :orderId")
    suspend fun getPaymentCountByOrder(orderId: Long): Int
    
    @Query("SELECT * FROM payments WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getPaymentsByDateRange(startDate: Long, endDate: Long): Flow<List<Payment>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long
    
    @Update
    suspend fun updatePayment(payment: Payment)
    
    @Delete
    suspend fun deletePayment(payment: Payment)
    
    @Query("DELETE FROM payments WHERE orderId = :orderId")
    suspend fun deletePaymentsByOrderId(orderId: Long)
}

