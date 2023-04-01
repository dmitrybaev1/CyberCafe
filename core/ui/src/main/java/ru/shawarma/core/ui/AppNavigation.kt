package ru.shawarma.core.ui

interface AppNavigation {
    fun navigateToMenu()
    fun navigateToAuth(errorMessage: String)
    fun navigateToSettings()
    fun navigateToOrder(orderId: Int)
}