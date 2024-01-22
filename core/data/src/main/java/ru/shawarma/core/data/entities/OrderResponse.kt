package ru.shawarma.core.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class OrderResponse(
    val id: Long,
    val menuItems: List<OrderMenuItemResponse>,
    val clientId: String,
    val createdDate: Date,
    val closeDate: Date,
    val status: OrderStatus,
    val totalPrice: Int
)
