package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.datasources.OrderRemoteDataSource
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.utils.Result
import java.util.*

class FakeOrderRemoteDataSource: OrderRemoteDataSource {
    override suspend fun getOrders(token: String, offset: Int, count: Int): Result<List<OrderResponse>> =
        Result.Success(listOf(
            OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0),
            OrderResponse(1, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0),
            OrderResponse(2, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)
        ))

    override suspend fun getOrder(token: String, id: Int): Result<OrderResponse> =
        Result.Success(
            OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)
        )

    override suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderResponse> =
        Result.Success(
            OrderResponse(0, listOf(),"", Date(), Date(), OrderStatus.IN_QUEUE,0)
        )

    override suspend fun startOrdersStatusHub(token: String, callback: (OrderResponse) -> Unit) {

    }

    override suspend fun refreshOrdersStatusHub(token: String, callback: (OrderResponse) -> Unit) {

    }

    override fun stopOrdersStatusHub() {

    }
}