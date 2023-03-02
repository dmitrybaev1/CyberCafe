package ru.shawarma.core.data.services

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shawarma.core.data.entities.MenuItemResponse

interface MenuService {

    @GET("menu")
    suspend fun getMenu(
        @Header("Authorization") token: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): List<MenuItemResponse>

    @GET("menu/{id}")
    suspend fun getMenuItem(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): MenuItemResponse
}