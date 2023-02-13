package ru.shawarma.auth.navigation

import ru.shawarma.core.data.entities.AuthData

interface AuthNavigation {
    fun navigateToMenu(authData: AuthData)
}