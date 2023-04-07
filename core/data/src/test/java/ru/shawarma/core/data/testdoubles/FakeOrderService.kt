package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.services.OrderService
import java.util.*

class FakeOrderService: OrderService {
    override suspend fun getOrders(token: String, offset: Int, count: Int): List<OrderResponse> =
        listOf(
            OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0),
            OrderResponse(1, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0),
            OrderResponse(2, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)
        )

    override suspend fun getOrder(token: String, id: Int): OrderResponse =
        OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)

    override suspend fun createOrder(token: String, request: CreateOrderRequest): OrderResponse =
        OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)
}