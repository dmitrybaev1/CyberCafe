package ru.shawarma.core.data.entities

data class MenuItemResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String?,
    val description: String?,
    val visible: Boolean
)
