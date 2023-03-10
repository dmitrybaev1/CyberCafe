package ru.shawarma.core.data.entities

data class OrderResponse(
    val id: Int,
    val menuItems: List<OrderMenuItemResponse>,
    val clientId: String,
    val createdDate: String,
    val closeDate: String,
    val status: String,
    val totalPrice: Double
)
