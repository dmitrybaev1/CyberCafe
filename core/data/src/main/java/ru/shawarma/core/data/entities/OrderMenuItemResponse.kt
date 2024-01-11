package ru.shawarma.core.data.entities

data class OrderMenuItemResponse (
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val amount: Int
)