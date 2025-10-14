package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.Installment
import com.orbit.data.entity.InstallmentPayment
import com.orbit.data.entity.InstallmentStatus
import com.orbit.data.entity.InstallmentPaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {
    
    // Installment operations
    @Query("SELECT * FROM installments WHERE orderId = :orderId")
    suspend fun getInstallmentByOrderId(orderId: Long): Installment?
    
    @Query("SELECT * FROM installments WHERE status = :status")
    fun getInstallmentsByStatus(status: InstallmentStatus): Flow<List<Installment>>
    
    @Query("SELECT * FROM installments")
    fun getAllInstallments(): Flow<List<Installment>>
    
    @Insert
    suspend fun insertInstallment(installment: Installment): Long
    
    @Update
    suspend fun updateInstallment(installment: Installment)
    
    @Delete
    suspend fun deleteInstallment(installment: Installment)
    
    // InstallmentPayment operations
    @Query("SELECT * FROM installment_payments WHERE installmentId = :installmentId ORDER BY installmentNumber")
    fun getPaymentsByInstallmentId(installmentId: Long): Flow<List<InstallmentPayment>>
    
    @Query("SELECT * FROM installment_payments WHERE installmentId = :installmentId AND status = :status")
    fun getPaymentsByStatus(installmentId: Long, status: InstallmentPaymentStatus): Flow<List<InstallmentPayment>>
    
    @Query("SELECT * FROM installment_payments WHERE status = :status")
    fun getAllPaymentsByStatus(status: InstallmentPaymentStatus): Flow<List<InstallmentPayment>>
    
    @Insert
    suspend fun insertInstallmentPayment(payment: InstallmentPayment): Long
    
    @Insert
    suspend fun insertInstallmentPayments(payments: List<InstallmentPayment>)
    
    @Update
    suspend fun updateInstallmentPayment(payment: InstallmentPayment)
    
    @Delete
    suspend fun deleteInstallmentPayment(payment: InstallmentPayment)
    
    // Complex queries
    @Query("""
        SELECT ip.* FROM installment_payments ip
        INNER JOIN installments i ON ip.installmentId = i.id
        WHERE i.orderId = :orderId
        ORDER BY ip.installmentNumber
    """)
    fun getPaymentsByOrderId(orderId: Long): Flow<List<InstallmentPayment>>
    
    @Query("""
        SELECT COUNT(*) FROM installment_payments ip
        INNER JOIN installments i ON ip.installmentId = i.id
        WHERE i.orderId = :orderId AND ip.status = 'PAID'
    """)
    suspend fun getPaidPaymentsCount(orderId: Long): Int
    
    @Query("""
        SELECT SUM(ip.amount) FROM installment_payments ip
        INNER JOIN installments i ON ip.installmentId = i.id
        WHERE i.orderId = :orderId AND ip.status = 'PAID'
    """)
    suspend fun getTotalPaidAmount(orderId: Long): Double?
}
