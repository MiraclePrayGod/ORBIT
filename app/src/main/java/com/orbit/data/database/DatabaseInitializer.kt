package com.orbit.data.database

import com.orbit.data.SampleData
import com.orbit.data.repository.OrbitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val repository: OrbitRepository
) {
    
    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        try {
            // Check if database is already populated by trying to get a client
            val firstClient = repository.getClientById(1)
            if (firstClient != null) {
                // Database already has data, skip initialization
                return@withContext
            }
            
            // Insert sample data
            insertSampleData()
        } catch (e: Exception) {
            // If there's an error, try to insert sample data anyway
            try {
                insertSampleData()
            } catch (insertError: Exception) {
                // Log the error but don't crash the app
                android.util.Log.e("DatabaseInitializer", "Failed to initialize database", insertError)
            }
        }
    }
    
    private suspend fun insertSampleData() {
        android.util.Log.d("DatabaseInitializer", "Iniciando inserción de datos de muestra...")
        
        // Insert clients
        SampleData.sampleClients.forEach { client ->
            val clientId = repository.insertClient(client)
            android.util.Log.d("DatabaseInitializer", "Cliente insertado: ${client.name} con ID: $clientId")
        }
        
        // Insert products
        SampleData.sampleProducts.forEach { product ->
            val productId = repository.insertProduct(product)
            android.util.Log.d("DatabaseInitializer", "Producto insertado: ${product.name} con ID: $productId")
        }
        
        // Insert orders
        SampleData.getSampleOrders().forEach { order ->
            val orderId = repository.insertOrder(order)
            android.util.Log.d("DatabaseInitializer", "Pedido insertado: ID $orderId, Total: $${order.totalAmount}")
        }
        
        // Insert order items
        SampleData.getSampleOrderItems().forEach { orderItem ->
            val itemId = repository.insertOrderItem(orderItem)
            android.util.Log.d("DatabaseInitializer", "Item insertado: ID $itemId, Pedido: ${orderItem.orderId}")
        }
        
        // Insert payments
        SampleData.getSamplePayments().forEach { payment ->
            val paymentId = repository.insertPayment(payment)
            android.util.Log.d("DatabaseInitializer", "Pago insertado: ID $paymentId, Monto: $${payment.amount}")
        }
        
        android.util.Log.d("DatabaseInitializer", "Datos de muestra insertados correctamente")
    }
}

