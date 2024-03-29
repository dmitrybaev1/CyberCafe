package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.exceptions.NoTokenException
import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.managers.MainInternetManager
import ru.shawarma.core.data.managers.TokenManager
import ru.shawarma.core.data.utils.*
import ru.shawarma.core.data.utils.checkExpires
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainAuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val internetManager: InternetManager
): AuthRepository {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData>{
        if(!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        val result = authRemoteDataSource.login(userLoginRequest)
        if(result is Result.Success<AuthData>)
            tokenManager.update(result.data)
        return result
    }

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return authRemoteDataSource.register(userRegisterRequest)
    }

    override suspend fun verifyGoogle(googleTokenRequest: GoogleTokenRequest): Result<AuthData> {
        if(!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        val result = authRemoteDataSource.verifyGoogle(googleTokenRequest)
        if(result is Result.Success<AuthData>)
            tokenManager.update(result.data)
        return result
    }

    override suspend fun getInfo(): Result<InfoResponse> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return when (val result = getActualAuthData()) {
            is Result.Success<AuthData> ->
                authRemoteDataSource.getInfo("Bearer ${result.data.accessToken}")
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
    }

    override suspend fun clearAuthData() {
        tokenManager.update(AuthData.empty())
    }

    override suspend fun getActualAuthData(): Result<AuthData>{
        val authData = tokenManager.getAuthData()
        if(authData.isEmpty())
            throw NoTokenException()
        return if(!checkExpires(authData.expiresIn))
            Result.Success(authData)
        else{
            val tokensRequest = TokensRequest(authData.refreshToken,authData.accessToken)
            return refreshToken(tokensRequest)
        }
    }

    private suspend fun refreshToken(tokensRequest: TokensRequest): Result<AuthData> {
        val result = authRemoteDataSource.refreshToken(tokensRequest)
        return if(result is Result.Success<AuthData>) {
            tokenManager.update(result.data)
            result
        } else
            Result.Failure(Errors.REFRESH_TOKEN_ERROR)
    }

    override suspend fun saveFirebaseToken(request: FirebaseTokenRequest): Result<FirebaseTokenResponse> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return when (val result = getActualAuthData()) {
            is Result.Success<AuthData> ->
                authRemoteDataSource.saveFirebaseToken("Bearer ${result.data.accessToken}", request)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
    }
}