package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.datasources.OrderRemoteDataSource
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

class MainOrderRepository @Inject constructor(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {

    override suspend fun getOrders(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<OrderResponse>> =
        orderRemoteDataSource.getOrders(token, offset, count)

    override suspend fun getOrder(token: String, id: Int): Result<OrderResponse> =
        orderRemoteDataSource.getOrder(token, id)

    override suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderResponse> =
        orderRemoteDataSource.createOrder(token, request)

    override fun startOrdersStatusHub(token: String, callback: (OrderResponse) -> Unit) =
        orderRemoteDataSource.startOrdersStatusHub(token, callback)

    override fun stopOrdersStatusHub() =
        orderRemoteDataSource.stopOrdersStatusHub()

}