package ru.shawarma.core.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.entities.*

class MainAuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource
): AuthRepository {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        authRemoteDataSource.login(userLoginRequest)


    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        authRemoteDataSource.refreshToken(tokensRequest)


    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        authRemoteDataSource.register(userRegisterRequest)

}