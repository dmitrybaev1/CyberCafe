package ru.shawarma.core.data.services

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import ru.shawarma.core.data.entities.*

interface AuthService {

    @POST("auth/register")
    suspend fun register(@Body userRegisterRequest: UserRegisterRequest): RegisteredUser

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body tokensRequest: TokensRequest): AuthData

    @POST("auth/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest): AuthData
}