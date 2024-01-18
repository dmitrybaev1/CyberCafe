package ru.shawarma.menu.entities

import androidx.databinding.ObservableBoolean

sealed interface MenuElement{
    data class MenuItem(
        val id: Int,
        val name: String,
        val price: Int,
        val imageUrl: String?,
        val description: String?,
        val isPicked: ObservableBoolean = ObservableBoolean(false)
    ): MenuElement
    object Error: MenuElement
}