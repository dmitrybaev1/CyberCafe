package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.Result
import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest

class MainAuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource
): AuthRepository {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        authRemoteDataSource.login(userLoginRequest)

    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        authRemoteDataSource.refreshToken(tokensRequest)

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<Unit> =
        authRemoteDataSource.register(userRegisterRequest)

}