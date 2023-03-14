package ru.shawarma.core.data.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
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
        @Path("id") id: Long
    ): OrderResponse

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderResponse


}