package ru.shawarma.order.entities

data class OrderMenuItem (
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String?,
    val amount: Int
)