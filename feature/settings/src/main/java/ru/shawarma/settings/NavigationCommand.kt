package ru.shawarma.settings

sealed interface NavigationCommand{
    data class ToOrderModule(val orderId: Int): NavigationCommand
}