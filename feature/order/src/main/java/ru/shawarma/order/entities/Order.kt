package ru.shawarma.order.entities

import ru.shawarma.core.data.entities.OrderStatus
import java.util.*

data class Order (
    val id: Long,
    val menuItems: List<OrderMenuItem>,
    val createdDate: Date,
    val closeDate: Date,
    val status: OrderStatus,
    val totalPrice: Int
)