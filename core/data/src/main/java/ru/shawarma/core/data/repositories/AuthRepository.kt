package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.exceptions.NoTokenException
import ru.shawarma.core.data.utils.Result

interface AuthRepository {

    suspend fun login(userLoginRequest: UserLoginRequest): Result<AuthData>

    suspend fun register(userRegisterRequest: UserRegisterRequest): Result<RegisteredUser>

    suspend fun getInfo(): Result<InfoResponse>

    suspend fun clearAuthData()

    @kotlin.jvm.Throws(NoTokenException::class)
    suspend fun getActualAuthData(): Result<AuthData>

    suspend fun saveFirebaseToken(request: FirebaseTokenRequest): Result<FirebaseTokenResponse>

}