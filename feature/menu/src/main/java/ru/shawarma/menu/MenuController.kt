package ru.shawarma.menu

interface MenuController {

    fun reloadMenu()

    fun addToCart(menuItem: MenuElement.MenuItem)

    fun removeFromCart(menuItem: MenuElement.MenuItem)
}