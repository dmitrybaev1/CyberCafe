package ru.shawarma.menu.entities

import androidx.databinding.ObservableBoolean

sealed interface MenuElement{
    data class Header(val title: String): MenuElement
    data class MenuItem(
        val id: Long,
        val name: String,
        val price: Int,
        val isPicked: ObservableBoolean = ObservableBoolean(false)
    ): MenuElement
    object Loading: MenuElement
    object Error: MenuElement
}