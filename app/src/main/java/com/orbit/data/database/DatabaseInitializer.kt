package com.orbit.data.database

import com.orbit.data.SampleData
import com.orbit.data.repository.OrbitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
    
    /**
     * Agrega pedidos recientes adicionales a la base de datos
     * Útil para testing y ver la lógica de pedidos recientes
     */
    suspend fun addRecentSampleOrders() = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("DatabaseInitializer", "Agregando pedidos recientes adicionales...")
            
            // Obtener clientes y productos existentes
            val clientList = repository.getAllClients().first()
            val productList = repository.getAllProducts().first()
            
            if (clientList.isEmpty() || productList.isEmpty()) {
                android.util.Log.w("DatabaseInitializer", "No hay clientes o productos. Inicializando base de datos primero...")
                insertSampleData()
                return@withContext
            }
            
            val now = System.currentTimeMillis()
            val todayMorning = now - (2 * 60 * 60 * 1000) // Hace 2 horas
            val todayAfternoon = now - (60 * 60 * 1000) // Hace 1 hora
            val todayEvening = now - (30 * 60 * 1000) // Hace 30 minutos
            
            // Crear pedidos adicionales recientes
            val additionalOrders = listOf(
                com.orbit.data.entity.Order(
                    clientId = clientList[0].id,
                    totalAmount = 67.48,
                    status = com.orbit.data.entity.OrderStatus.PAID,
                    paymentMethod = com.orbit.data.entity.PaymentMethod.CARD,
                    createdAt = todayEvening,
                    notes = "Pedido reciente adicional, pago con tarjeta"
                ),
                com.orbit.data.entity.Order(
                    clientId = clientList[1 % clientList.size].id,
                    totalAmount = 45.98,
                    status = com.orbit.data.entity.OrderStatus.IN_PROGRESS,
                    paymentMethod = com.orbit.data.entity.PaymentMethod.CASH,
                    createdAt = todayAfternoon,
                    notes = "Cliente regular adicional, pago en efectivo"
                ),
                com.orbit.data.entity.Order(
                    clientId = clientList[2 % clientList.size].id,
                    totalAmount = 50.97,
                    status = com.orbit.data.entity.OrderStatus.IN_PROGRESS,
                    paymentMethod = com.orbit.data.entity.PaymentMethod.INSTALLMENTS,
                    createdAt = todayMorning,
                    notes = "Pedido múltiple adicional, pendiente de entrega"
                )
            )
            
            // Insertar pedidos y sus items
            additionalOrders.forEachIndexed { index, order ->
                val orderId = repository.insertOrder(order)
                android.util.Log.d("DatabaseInitializer", "Pedido adicional insertado: ID $orderId")
                
                // Agregar items al pedido
                val product1 = productList[index % productList.size]
                val product2 = productList[(index + 1) % productList.size]
                
                val orderItems = listOf(
                    com.orbit.data.entity.OrderItem(
                        orderId = orderId,
                        productId = product1.id,
                        quantity = 1,
                        unitPrice = product1.unitPrice,
                        totalPrice = product1.unitPrice
                    ),
                    com.orbit.data.entity.OrderItem(
                        orderId = orderId,
                        productId = product2.id,
                        quantity = 1,
                        unitPrice = product2.unitPrice,
                        totalPrice = product2.unitPrice
                    )
                )
                
                orderItems.forEach { item ->
                    repository.insertOrderItem(item)
                }
                
                // Agregar pago si está pagado
                if (order.status == com.orbit.data.entity.OrderStatus.PAID) {
                    repository.insertPayment(
                        com.orbit.data.entity.Payment(
                            orderId = orderId,
                            amount = order.totalAmount,
                            paymentMethod = order.paymentMethod,
                            createdAt = order.createdAt
                        )
                    )
                }
            }
            
            android.util.Log.d("DatabaseInitializer", "Pedidos recientes adicionales agregados correctamente")
        } catch (e: Exception) {
            android.util.Log.e("DatabaseInitializer", "Error al agregar pedidos recientes", e)
        }
    }
}

