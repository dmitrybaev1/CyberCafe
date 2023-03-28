package ru.shawarma.core.data.entities

data class MenuItemResponse(
    val id: Int,
    val name: String,
    val price: Int,
    val visible: Boolean
)
