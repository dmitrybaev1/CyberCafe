package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainAuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
): AuthRepository {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        authRemoteDataSource.login(userLoginRequest)


    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        authRemoteDataSource.refreshToken(tokensRequest)


    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        authRemoteDataSource.register(userRegisterRequest)

}