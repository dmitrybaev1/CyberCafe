package ru.shawarma.order

data class OrderMenuItem (
    val id: Int,
    val name: String,
    val price: Int,
    val amount: Int
)