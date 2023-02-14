package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.utils.Result

class FakeAuthRemoteDataSource: AuthRemoteDataSource  {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        Result.Success(AuthData("", "", "", 0))

    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        Result.Success(AuthData("", "", "", 0))

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        Result.Failure("")

}