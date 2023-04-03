package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.managers.TokenManager

class FakeTokenManager: TokenManager {
    override suspend fun update(authData: AuthData) {}

    override suspend fun getAuthData(): AuthData =
        AuthData("","","",0)
}