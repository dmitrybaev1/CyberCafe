package ru.shawarma.menu

import ru.shawarma.menu.entities.MenuItem

interface MenuController {

    fun addToCart(menuItem: MenuItem)

    fun removeFromCart(menuItem: MenuItem)

    fun goToMenuItemFragment(menuItem: MenuItem, count: Int)

    fun getMenuItemCount(menuItem: MenuItem): Int
}