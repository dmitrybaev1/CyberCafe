package ru.shawarma.core.ui

import ru.shawarma.core.data.entities.AuthData

interface AppNavigation {
    fun navigateToMenu()
    fun navigateToAuth(errorMessage: String)
    fun navigateToSettings()
    fun navigateToOrder()
}