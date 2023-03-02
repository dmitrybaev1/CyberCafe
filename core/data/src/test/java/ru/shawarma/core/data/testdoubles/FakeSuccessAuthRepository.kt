package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Result

class FakeSuccessAuthRepository: AuthRepository {
    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        Result.Success(AuthData("","","",0))

    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        Result.Success(AuthData("","","",0))

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        Result.Success(RegisteredUser("","",""))
}