package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.utils.Result

interface OrderRepository {

    suspend fun getOrders(token: String, offset: Int, count: Int): Result<List<OrderResponse>>

    suspend fun getOrder(token: String, id: Int): Result<OrderResponse>

    suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderResponse>

    fun startOrdersStatusHub(token: String ,callback: (OrderResponse) -> (Unit))

    fun stopOrdersStatusHub()

}