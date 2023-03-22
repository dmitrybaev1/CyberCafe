package ru.shawarma.core.data.datasources

import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.utils.Result

interface OrderRemoteDataSource {

    suspend fun getOrders(token: String, offset: Int, count: Int): Result<List<OrderResponse>>

    suspend fun getOrder(token: String, id: Int): Result<OrderResponse>

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse>

}