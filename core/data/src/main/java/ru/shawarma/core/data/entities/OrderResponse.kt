package ru.shawarma.core.data.entities

import java.util.*

data class OrderResponse(
    val id: Int,
    val menuItems: List<OrderMenuItemResponse>,
    val clientId: String,
    val createdDate: Date,
    val closeDate: Date,
    val status: OrderStatus,
    val totalPrice: Int
)
