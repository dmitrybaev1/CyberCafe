package ru.shawarma.core.ui

import ru.shawarma.core.data.entities.AuthData

interface AppNavigation {
    fun navigateToMenu(authData: AuthData)
    fun navigateToSettings()
    fun navigateToOrder()
}