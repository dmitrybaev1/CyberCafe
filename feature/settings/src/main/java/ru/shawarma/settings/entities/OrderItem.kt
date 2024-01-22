package ru.shawarma.settings.entities

import ru.shawarma.core.data.entities.OrderStatus
import java.util.Date

data class OrderItem(
    val id: Long,
    val createdDate: Date,
    val status: OrderStatus
)
