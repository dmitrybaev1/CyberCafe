package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.entities.FirebaseTokenResponse
import ru.shawarma.core.data.entities.InfoResponse
import ru.shawarma.core.data.entities.RegisteredUser
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.exceptions.NoTokenException
import ru.shawarma.core.data.managers.TokenManager
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.checkExpires
import javax.inject.Inject

class FakeAuthRepository @Inject constructor(
    private val tokenManager: TokenManager
): AuthRepository {

    override suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData> {
        val result = Result.Success(
            AuthData(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                expiresIn = 1805769969084L,
                tokenType = "jwt"
            )
        )
        tokenManager.update(result.data)
        return result
    }

    override suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser> {
        return Result.Success(RegisteredUser("","",""))
    }

    override suspend fun getInfo(): Result<InfoResponse> {
        return Result.Success(InfoResponse("1","Name","email@example.com"))
    }

    override suspend fun clearAuthData() {
        tokenManager.update(AuthData.empty())
    }

    override suspend fun getActualAuthData(): Result<AuthData> {
        val authData = tokenManager.getAuthData()
        if(authData.isEmpty())
            throw NoTokenException()
        else
            return Result.Success(authData)
    }

    override suspend fun saveFirebaseToken(request: FirebaseTokenRequest): Result<FirebaseTokenResponse> {
        return Result.Success(FirebaseTokenResponse("clientId","firebaseToken"))
    }
}