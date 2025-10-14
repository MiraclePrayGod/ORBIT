package com.orbit.data.dao

import androidx.room.*
import com.orbit.data.entity.Product
import com.orbit.data.entity.ProductCategory
import com.orbit.data.entity.InventoryStatus
import com.orbit.data.relation.ProductWithOrderItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product?
    
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: ProductCategory): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE status = :status ORDER BY name ASC")
    fun getProductsByStatus(status: InventoryStatus): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE availableQuantity <= :threshold")
    fun getLowStockProducts(threshold: Int = 10): Flow<List<Product>>
    
    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductWithOrderItems(productId: Long): ProductWithOrderItems?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("UPDATE products SET availableQuantity = :newQuantity, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateProductQuantity(productId: Long, newQuantity: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE products SET reservedQuantity = :newReservedQuantity, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateReservedQuantity(productId: Long, newReservedQuantity: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE products SET status = :status, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateProductStatus(productId: Long, status: InventoryStatus, timestamp: Long = System.currentTimeMillis())
}

