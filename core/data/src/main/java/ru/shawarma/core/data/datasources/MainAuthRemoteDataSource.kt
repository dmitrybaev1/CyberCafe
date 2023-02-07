package ru.shawarma.core.data.datasources

import kotlinx.coroutines.CoroutineDispatcher
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.safeServiceCall
import ru.shawarma.core.data.services.AuthService

class MainAuthRemoteDataSource(
    private val authService: AuthService,
    private val dispatcher: CoroutineDispatcher): AuthRemoteDataSource {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        safeServiceCall(dispatcher){
            authService.login(userLoginRequest)
        }


    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        safeServiceCall(dispatcher){
            authService.refreshToken(tokensRequest)
        }


    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<Unit> =
        safeServiceCall(dispatcher){
            authService.register(userRegisterRequest)
        }

}