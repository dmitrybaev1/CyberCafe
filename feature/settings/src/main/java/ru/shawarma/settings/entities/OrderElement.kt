package ru.shawarma.settings.entities

import ru.shawarma.core.data.entities.OrderStatus
import java.util.*

sealed interface OrderElement{
    data class OrderItem(
        val id: Int,
        val createdDate: Date,
        val status: OrderStatus
    ): OrderElement
    object Loading: OrderElement
    object Error: OrderElement
}