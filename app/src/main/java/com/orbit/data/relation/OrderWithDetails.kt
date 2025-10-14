package com.orbit.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.orbit.data.entity.*

data class OrderWithDetails(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val orderItems: List<OrderItem>,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val payments: List<Payment>
)

data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)

data class ClientWithOrders(
    @Embedded val client: Client,
    @Relation(
        parentColumn = "id",
        entityColumn = "clientId"
    )
    val orders: List<Order>
)

data class ProductWithOrderItems(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val orderItems: List<OrderItem>
)
