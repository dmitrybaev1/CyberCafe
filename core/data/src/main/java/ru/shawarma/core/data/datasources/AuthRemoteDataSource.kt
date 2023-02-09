package ru.shawarma.core.data.datasources

import okhttp3.ResponseBody
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.entities.*

interface AuthRemoteDataSource {

    suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData>

    suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData>

    suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser>

}