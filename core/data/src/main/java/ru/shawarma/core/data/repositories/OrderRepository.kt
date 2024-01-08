package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.entities.FirebaseTokenResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.utils.Result

interface OrderRepository {

    suspend fun getOrders(offset: Int, count: Int): Result<List<OrderResponse>>

    suspend fun getOrder(id: Int): Result<OrderResponse>

    suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse>

    suspend fun startOrdersStatusHub(callback: (OrderResponse) -> (Unit))

    fun stopOrdersStatusHub()

}