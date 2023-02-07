package ru.shawarma.auth

import ru.shawarma.core.data.entities.AuthData

interface AuthNavigation {
    fun navigateToMenu(authData: AuthData)
}