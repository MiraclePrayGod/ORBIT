package com.orbit.data.repository

import com.orbit.data.dao.*
import com.orbit.data.entity.*
import com.orbit.data.relation.OrderWithDetails
import com.orbit.data.model.InstallmentConfig
import com.orbit.data.model.ValidationResult
import com.orbit.data.model.OrderCreationResult
import com.orbit.data.model.PaymentResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrbitRepository @Inject constructor(
    private val clientDao: ClientDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val paymentDao: PaymentDao,
    private val installmentDao: InstallmentDao
) {
    
    // Client operations
    fun getAllClients(): Flow<List<Client>> = clientDao.getAllClients()
    suspend fun getClientById(clientId: Long): Client? = clientDao.getClientById(clientId)
    suspend fun getClientByPhone(phone: String): Client? = clientDao.getClientByPhone(phone)
    fun searchClients(query: String): Flow<List<Client>> = clientDao.searchClients(query)
    suspend fun insertClient(client: Client): Long = clientDao.insertClient(client)
    suspend fun updateClient(client: Client) = clientDao.updateClient(client)
    suspend fun deleteClient(client: Client) = clientDao.deleteClient(client)
    
    suspend fun createClient(
        name: String,
        phone: String,
        address: String,
        reference: String? = null
    ): Long {
        val client = Client(
            id = 0, // Room will auto-generate
            name = name,
            phone = phone,
            address = address,
            reference = reference,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return clientDao.insertClient(client)
    }
    
    // Product operations
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    suspend fun getProductById(productId: Long): Product? = productDao.getProductById(productId)
    fun getProductsByCategory(category: ProductCategory): Flow<List<Product>> = productDao.getProductsByCategory(category)
    fun getProductsByStatus(status: InventoryStatus): Flow<List<Product>> = productDao.getProductsByStatus(status)
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    fun getLowStockProducts(threshold: Int = 10): Flow<List<Product>> = productDao.getLowStockProducts(threshold)
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun updateProductQuantity(productId: Long, newQuantity: Int) = 
        productDao.updateProductQuantity(productId, newQuantity)
    suspend fun updateReservedQuantity(productId: Long, newReservedQuantity: Int) = 
        productDao.updateReservedQuantity(productId, newReservedQuantity)
    suspend fun updateProductStatus(productId: Long, status: InventoryStatus) = 
        productDao.updateProductStatus(productId, status)
    
    // Order operations
    fun getAllOrdersWithDetails(): Flow<List<OrderWithDetails>> = orderDao.getAllOrdersWithDetails()
    suspend fun getOrderWithDetails(orderId: Long): OrderWithDetails? = orderDao.getOrderWithDetails(orderId)
    fun getOrdersByClient(clientId: Long): Flow<List<Order>> = orderDao.getOrdersByClient(clientId)
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> = orderDao.getOrdersByStatus(status)
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<Order>> = 
        orderDao.getOrdersByDateRange(startDate, endDate)
    fun getOrdersFromDate(date: Long): Flow<List<Order>> = orderDao.getOrdersFromDate(date)
    suspend fun getOrderCountFromDate(date: Long): Int = orderDao.getOrderCountFromDate(date)
    suspend fun getTotalSalesFromDate(date: Long): Double? = orderDao.getTotalSalesFromDate(date)
    suspend fun insertOrder(order: Order): Long = orderDao.insertOrder(order)
    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) = 
        orderDao.updateOrderStatus(orderId, status)
    
    // OrderItem operations
    suspend fun getOrderItemsWithProducts(orderId: Long): List<com.orbit.data.relation.OrderItemWithProduct> = 
        orderItemDao.getOrderItemsWithProducts(orderId)
    suspend fun getOrderItems(orderId: Long): List<OrderItem> = orderItemDao.getOrderItems(orderId)
    fun getOrderItemsByProduct(productId: Long): Flow<List<OrderItem>> = orderItemDao.getOrderItemsByProduct(productId)
    suspend fun insertOrderItem(orderItem: OrderItem): Long = orderItemDao.insertOrderItem(orderItem)
    suspend fun insertOrderItems(orderItems: List<OrderItem>) = orderItemDao.insertOrderItems(orderItems)
    suspend fun updateOrderItem(orderItem: OrderItem) = orderItemDao.updateOrderItem(orderItem)
    suspend fun deleteOrderItem(orderItem: OrderItem) = orderItemDao.deleteOrderItem(orderItem)
    suspend fun deleteOrderItemsByOrderId(orderId: Long) = orderItemDao.deleteOrderItemsByOrderId(orderId)
    
    // Payment operations
    fun getPaymentsByOrder(orderId: Long): Flow<List<Payment>> = paymentDao.getPaymentsByOrder(orderId)
    suspend fun getPaymentsByOrderSync(orderId: Long): List<Payment> = paymentDao.getPaymentsByOrderSync(orderId)
    suspend fun getTotalPaidByOrder(orderId: Long): Double? = paymentDao.getTotalPaidByOrder(orderId)
    suspend fun getPaymentCountByOrder(orderId: Long): Int = paymentDao.getPaymentCountByOrder(orderId)
    fun getPaymentsByDateRange(startDate: Long, endDate: Long): Flow<List<Payment>> = 
        paymentDao.getPaymentsByDateRange(startDate, endDate)
    suspend fun insertPayment(payment: Payment): Long = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = paymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: Payment) = paymentDao.deletePayment(payment)
    suspend fun deletePaymentsByOrderId(orderId: Long) = paymentDao.deletePaymentsByOrderId(orderId)
    
    // Business validation methods
    suspend fun validateStock(orderItems: List<Pair<Long, Int>>): ValidationResult {
        val errors = mutableListOf<String>()
        
        for ((productId, quantity) in orderItems) {
            val product = getProductById(productId)
            when {
                product == null -> errors.add("Producto con ID $productId no encontrado")
                product.availableQuantity < quantity -> {
                    errors.add("Stock insuficiente para ${product.name}. Disponible: ${product.availableQuantity}, Solicitado: $quantity")
                }
                quantity <= 0 -> errors.add("Cantidad inválida para ${product.name}: $quantity")
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
    
    suspend fun validateOrderData(
        clientId: Long,
        orderItems: List<Pair<Long, Int>>,
        paymentMethod: PaymentMethod
    ): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate client exists
        val client = getClientById(clientId)
        if (client == null) {
            errors.add("Cliente no encontrado")
        }
        
        // Validate order items
        if (orderItems.isEmpty()) {
            errors.add("El pedido debe tener al menos un producto")
        }
        
        // Validate stock
        val stockValidation = validateStock(orderItems)
        if (stockValidation is ValidationResult.Error) {
            errors.addAll(stockValidation.errors)
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    // Complex business operations
    suspend fun createOrderWithItems(
        clientId: Long,
        orderItems: List<Pair<Long, Int>>, // productId, quantity
        paymentMethod: PaymentMethod,
        notes: String? = null
    ): OrderCreationResult {
        return try {
            // Validate order data first
            val validation = validateOrderData(clientId, orderItems, paymentMethod)
            if (validation is ValidationResult.Error) {
                return OrderCreationResult.Error(
                    message = "Datos del pedido inválidos",
                    details = validation.errors
                )
            }
            
            // Calculate total amount
            var totalAmount = 0.0
            val orderItemsList = mutableListOf<OrderItem>()
            
            for ((productId, quantity) in orderItems) {
                val product = getProductById(productId) ?: continue
                val itemTotal = product.unitPrice * quantity
                totalAmount += itemTotal
                
                orderItemsList.add(
                    OrderItem(
                        orderId = 0, // Will be set after order creation
                        productId = productId,
                        quantity = quantity,
                        unitPrice = product.unitPrice,
                        totalPrice = itemTotal
                    )
                )
            }
            
            // Create order
            val order = Order(
                clientId = clientId,
                totalAmount = totalAmount,
                status = OrderStatus.IN_PROGRESS,
                paymentMethod = paymentMethod,
                notes = notes
            )
            
            val orderId = insertOrder(order)
            
            // Update order items with correct order ID
            val updatedOrderItems = orderItemsList.map { it.copy(orderId = orderId) }
            insertOrderItems(updatedOrderItems)
            
            // Update product quantities
            for ((productId, quantity) in orderItems) {
                val product = getProductById(productId) ?: continue
                val newAvailableQuantity = product.availableQuantity - quantity
                val newReservedQuantity = product.reservedQuantity + quantity
                
                updateProductQuantity(productId, newAvailableQuantity)
                updateReservedQuantity(productId, newReservedQuantity)
                
                // Update status if needed
                val newStatus = when {
                    newAvailableQuantity <= 0 -> InventoryStatus.OUT_OF_STOCK
                    newAvailableQuantity <= 10 -> InventoryStatus.LOW_STOCK
                    else -> InventoryStatus.AVAILABLE
                }
                updateProductStatus(productId, newStatus)
            }
            
            OrderCreationResult.Success(orderId)
        } catch (e: Exception) {
            OrderCreationResult.Error(
                message = "Error al crear el pedido: ${e.message}",
                details = listOf(e.stackTraceToString())
            )
        }
    }
    
    suspend fun addPaymentToOrder(
        orderId: Long, 
        amount: Double, 
        paymentMethod: PaymentMethod, 
        notes: String? = null,
        reference: String? = null
    ): PaymentResult {
        return try {
            // Validate payment
            if (amount <= 0) {
                return PaymentResult.Error("El monto debe ser mayor a 0")
            }
            
            val order = getOrderWithDetails(orderId)?.order
            if (order == null) {
                return PaymentResult.Error("Pedido no encontrado")
            }
            
            val totalPaid = getTotalPaidByOrder(orderId) ?: 0.0
            if (totalPaid + amount > order.totalAmount) {
                return PaymentResult.Error("El pago excede el monto total del pedido")
            }
            
            val payment = Payment(
                orderId = orderId,
                amount = amount,
                paymentMethod = paymentMethod,
                paymentType = PaymentType.REGULAR_PAYMENT,
                notes = notes,
                reference = reference,
                status = PaymentStatus.COMPLETED
            )
            
            val paymentId = insertPayment(payment)
            
            // Check if order is fully paid
            val newTotalPaid = totalPaid + amount
            if (newTotalPaid >= order.totalAmount) {
                updateOrderStatus(orderId, OrderStatus.PAID)
            }
            
            PaymentResult.Success(paymentId)
        } catch (e: Exception) {
            PaymentResult.Error("Error al procesar el pago: ${e.message}")
        }
    }
    
    // Database status methods for debugging
    suspend fun getDatabaseStatus(): String {
        val clientCount = getAllClients().let { flow ->
            var count = 0
            flow.collect { count = it.size }
            count
        }
        val productCount = getAllProducts().let { flow ->
            var count = 0
            flow.collect { count = it.size }
            count
        }
        val orderCount = getAllOrdersWithDetails().let { flow ->
            var count = 0
            flow.collect { count = it.size }
            count
        }
        
        return "Clientes: $clientCount, Productos: $productCount, Pedidos: $orderCount"
    }
    
    // Installment operations
    suspend fun createInstallmentPlan(orderId: Long, config: InstallmentConfig) {
        val installment = Installment(
            orderId = orderId,
            totalAmount = config.totalAmount,
            initialPayment = config.initialPayment,
            numberOfInstallments = config.numberOfInstallments,
            installmentAmount = config.installmentAmount,
            paymentInterval = config.paymentInterval,
            startDate = config.startDate.toEpochDay() * 24 * 60 * 60 * 1000 // Convert to timestamp
        )
        
        val installmentId = installmentDao.insertInstallment(installment)
        
        // Create individual payment records
        val payments = mutableListOf<InstallmentPayment>()
        
        // Add initial payment
        payments.add(
            InstallmentPayment(
                installmentId = installmentId,
                installmentNumber = 0,
                amount = config.initialPayment,
                dueDate = config.startDate.toEpochDay() * 24 * 60 * 60 * 1000,
                status = InstallmentPaymentStatus.PENDING
            )
        )
        
        // Add installment payments
        repeat(config.numberOfInstallments) { index ->
            val dueDate = config.startDate.plusDays((index + 1) * config.paymentInterval.toLong())
            payments.add(
                InstallmentPayment(
                    installmentId = installmentId,
                    installmentNumber = index + 1,
                    amount = config.installmentAmount,
                    dueDate = dueDate.toEpochDay() * 24 * 60 * 60 * 1000,
                    status = InstallmentPaymentStatus.PENDING
                )
            )
        }
        
        installmentDao.insertInstallmentPayments(payments)
    }
    
    suspend fun getInstallmentsByOrder(orderId: Long) = installmentDao.getInstallmentByOrderId(orderId)
    fun getInstallmentPayments(orderId: Long) = installmentDao.getPaymentsByOrderId(orderId)
}
