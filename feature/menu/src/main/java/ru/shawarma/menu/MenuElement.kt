package ru.shawarma.menu

sealed interface MenuElement{
    object Header: MenuElement
    data class MenuItem(val id: Long, val name: String, val price: Int): MenuElement
    object Loading: MenuElement
    object Error: MenuElement
}