package ru.shawarma.core.data.services

import retrofit2.http.Body
import retrofit2.http.POST
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest

interface MenuService {

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body tokensRequest: TokensRequest): AuthData
}