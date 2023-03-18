package ru.shawarma.core.data.entities

data class CreateOrderRequest(
    val menuItems: List<OrderMenuItemRequest>
)
