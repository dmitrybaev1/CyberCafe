package ru.shawarma.core.data.datasources

import kotlinx.coroutines.CoroutineDispatcher
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.services.AuthService
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.safeServiceCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainAuthRemoteDataSource @Inject constructor(
    private val authService: AuthService,
    private val dispatcher: CoroutineDispatcher
): AuthRemoteDataSource {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> =
        safeServiceCall(dispatcher){
            authService.login(userLoginRequest)
        }


    override suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> =
        safeServiceCall(dispatcher){
            authService.refreshToken(tokensRequest)
        }


    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> =
        safeServiceCall(dispatcher){
            authService.register(userRegisterRequest)
        }

    override suspend fun getInfo(token: String): Result<InfoResponse> =
        safeServiceCall(dispatcher){
            authService.getInfo(token)
        }

}