package ru.shawarma.menu.entities

import androidx.databinding.ObservableBoolean

data class MenuItem(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String?,
    val description: String?,
    val isPicked: ObservableBoolean = ObservableBoolean(false)
)
