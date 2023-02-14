package ru.shawarma.core.data.utils

import ru.shawarma.core.data.entities.AuthData

interface TokenManager {

    suspend fun update(authData: AuthData)

    suspend fun getAuthData(): AuthData

}