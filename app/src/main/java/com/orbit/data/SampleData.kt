package com.orbit.data

import com.orbit.data.entity.*

object SampleData {
    
    val sampleClients = listOf(
        Client(
            id = 1,
            name = "Ana Martínez",
            phone = "555-0101",
            address = "Av. Principal 123, Zona Centro",
            email = "ana.martinez@email.com"
        ),
        Client(
            id = 2,
            name = "Carlos López",
            phone = "555-0102",
            address = "Calle Secundaria 456, Zona Norte",
            email = "carlos.lopez@email.com"
        ),
        Client(
            id = 3,
            name = "Juan Pérez",
            phone = "555-0103",
            address = "Av. Comercial 789, Zona Sur",
            email = "juan.perez@email.com"
        ),
        Client(
            id = 4,
            name = "María García",
            phone = "555-0104",
            address = "Calle Residencial 321, Zona Este",
            email = "maria.garcia@email.com"
        ),
        Client(
            id = 5,
            name = "Pedro Rodríguez",
            phone = "555-0105",
            address = "Av. Industrial 654, Zona Oeste",
            email = "pedro.rodriguez@email.com"
        )
    )
    
    val sampleProducts = listOf(
        Product(
            id = 1,
            name = "Alimentación Consciente",
            category = ProductCategory.HEALTH,
            unitPrice = 22.99,
            availableQuantity = 15,
            reservedQuantity = 0,
            totalQuantity = 15,
            status = InventoryStatus.AVAILABLE,
            description = "Guía completa sobre alimentación saludable y consciente"
        ),
        Product(
            id = 2,
            name = "Conversaciones con Dios",
            category = ProductCategory.SPIRITUAL,
            unitPrice = 23.99,
            availableQuantity = 30,
            reservedQuantity = 0,
            totalQuantity = 30,
            status = InventoryStatus.AVAILABLE,
            description = "Serie de libros sobre espiritualidad y conexión divina"
        ),
        Product(
            id = 3,
            name = "El Alquimista",
            category = ProductCategory.SPIRITUAL,
            unitPrice = 16.99,
            availableQuantity = 25,
            reservedQuantity = 0,
            totalQuantity = 25,
            status = InventoryStatus.AVAILABLE,
            description = "Novela espiritual sobre el viaje personal y la búsqueda del tesoro"
        ),
        Product(
            id = 4,
            name = "Psicología del Éxito",
            category = ProductCategory.PSYCHOLOGY,
            unitPrice = 28.50,
            availableQuantity = 8,
            reservedQuantity = 0,
            totalQuantity = 8,
            status = InventoryStatus.LOW_STOCK,
            description = "Manual de psicología aplicada para el desarrollo personal"
        ),
        Product(
            id = 5,
            name = "Meditación y Mindfulness",
            category = ProductCategory.SPIRITUAL,
            unitPrice = 19.99,
            availableQuantity = 0,
            reservedQuantity = 0,
            totalQuantity = 0,
            status = InventoryStatus.OUT_OF_STOCK,
            description = "Guía práctica para la meditación y el mindfulness"
        ),
        Product(
            id = 6,
            name = "Nutrición Holística",
            category = ProductCategory.HEALTH,
            unitPrice = 32.00,
            availableQuantity = 12,
            reservedQuantity = 0,
            totalQuantity = 12,
            status = InventoryStatus.AVAILABLE,
            description = "Enfoque integral de la nutrición y el bienestar"
        ),
        Product(
            id = 7,
            name = "Desarrollo Personal",
            category = ProductCategory.PSYCHOLOGY,
            unitPrice = 24.99,
            availableQuantity = 20,
            reservedQuantity = 0,
            totalQuantity = 20,
            status = InventoryStatus.AVAILABLE,
            description = "Estrategias para el crecimiento y desarrollo personal"
        ),
        Product(
            id = 8,
            name = "Filosofía de Vida",
            category = ProductCategory.OTHER,
            unitPrice = 18.75,
            availableQuantity = 5,
            reservedQuantity = 0,
            totalQuantity = 5,
            status = InventoryStatus.LOW_STOCK,
            description = "Reflexiones sobre el sentido de la vida y la existencia"
        )
    )
    
    fun getSampleOrders(): List<Order> {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - (24 * 60 * 60 * 1000)
        val twoDaysAgo = now - (2 * 24 * 60 * 60 * 1000)
        val threeDaysAgo = now - (3 * 24 * 60 * 60 * 1000)
        
        return listOf(
            Order(
                id = 1,
                clientId = 1,
                totalAmount = 45.98,
                status = OrderStatus.PAID,
                paymentMethod = PaymentMethod.CASH,
                createdAt = twoDaysAgo,
                notes = "Cliente regular, pago en efectivo"
            ),
            Order(
                id = 2,
                clientId = 2,
                totalAmount = 23.99,
                status = OrderStatus.IN_PROGRESS,
                paymentMethod = PaymentMethod.INSTALLMENTS,
                createdAt = oneDayAgo,
                notes = "Pago por partes, primera cuota pagada"
            ),
            Order(
                id = 3,
                clientId = 3,
                totalAmount = 50.97,
                status = OrderStatus.IN_PROGRESS,
                paymentMethod = PaymentMethod.INSTALLMENTS,
                createdAt = oneDayAgo,
                notes = "Pedido múltiple, pendiente de entrega"
            ),
            Order(
                id = 4,
                clientId = 4,
                totalAmount = 67.48,
                status = OrderStatus.PENDING,
                paymentMethod = PaymentMethod.CARD,
                createdAt = now,
                notes = "Nuevo pedido, pendiente de confirmación"
            ),
            Order(
                id = 5,
                clientId = 5,
                totalAmount = 19.99,
                status = OrderStatus.CANCELLED,
                paymentMethod = PaymentMethod.CASH,
                createdAt = threeDaysAgo,
                notes = "Cancelado por cliente"
            )
        )
    }
    
    fun getSampleOrderItems(): List<OrderItem> {
        return listOf(
            // Order 1 items
            OrderItem(
                orderId = 1,
                productId = 1,
                quantity = 2,
                unitPrice = 22.99,
                totalPrice = 45.98
            ),
            // Order 2 items
            OrderItem(
                orderId = 2,
                productId = 2,
                quantity = 1,
                unitPrice = 23.99,
                totalPrice = 23.99
            ),
            // Order 3 items
            OrderItem(
                orderId = 3,
                productId = 3,
                quantity = 3,
                unitPrice = 16.99,
                totalPrice = 50.97
            ),
            // Order 4 items
            OrderItem(
                orderId = 4,
                productId = 4,
                quantity = 1,
                unitPrice = 28.50,
                totalPrice = 28.50
            ),
            OrderItem(
                orderId = 4,
                productId = 6,
                quantity = 1,
                unitPrice = 32.00,
                totalPrice = 32.00
            ),
            // Order 5 items (cancelled)
            OrderItem(
                orderId = 5,
                productId = 5,
                quantity = 1,
                unitPrice = 19.99,
                totalPrice = 19.99
            )
        )
    }
    
    fun getSamplePayments(): List<Payment> {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - (24 * 60 * 60 * 1000)
        val twoDaysAgo = now - (2 * 24 * 60 * 60 * 1000)
        
        return listOf(
            // Order 1 - Fully paid
            Payment(
                orderId = 1,
                amount = 45.98,
                paymentMethod = PaymentMethod.CASH,
                createdAt = twoDaysAgo
            ),
            // Order 2 - Partial payment (installments)
            Payment(
                orderId = 2,
                amount = 8.00,
                paymentMethod = PaymentMethod.INSTALLMENTS,
                paymentType = PaymentType.INSTALLMENT_PAYMENT,
                createdAt = oneDayAgo
            ),
            // Order 3 - Partial payment (installments)
            Payment(
                orderId = 3,
                amount = 15.00,
                paymentMethod = PaymentMethod.INSTALLMENTS,
                paymentType = PaymentType.INSTALLMENT_PAYMENT,
                createdAt = oneDayAgo
            )
        )
    }
}

