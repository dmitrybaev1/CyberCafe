package ru.shawarma.core.data.datasources

import ru.shawarma.core.data.Result
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest

interface AuthRemoteDataSource {

    suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData>

    suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData>

    suspend fun register(userRegisterRequest: UserRegisterRequest): Result<Unit>

}