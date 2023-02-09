package ru.shawarma.core.data.testdoubles

import okhttp3.ResponseBody
import org.mockito.kotlin.mock
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.services.AuthService

class FakeAuthService : AuthService {
    override suspend fun register(userRegisterRequest: UserRegisterRequest): RegisteredUser =
        RegisteredUser("","","")

    override suspend fun refreshToken(tokensRequest: TokensRequest): AuthData =
        AuthData("", "", "", 0)

    override suspend fun login(userLoginRequest: UserLoginRequest): AuthData =
        AuthData("", "", "", 0)
}