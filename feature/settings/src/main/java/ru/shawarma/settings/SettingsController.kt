package ru.shawarma.settings

interface SettingsController {
    fun goToOrder(id: Int)
    fun reloadOrders()
}