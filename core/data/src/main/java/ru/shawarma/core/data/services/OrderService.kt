package ru.shawarma.core.data.services

import retrofit2.http.*
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse

interface OrderService {

    @GET("orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): List<OrderResponse>

    @GET("orders/{id}")
    suspend fun getOrder(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): OrderResponse

    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): OrderResponse


}