package ru.shawarma.core.data.datasources

import kotlinx.coroutines.CoroutineDispatcher
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.services.OrderService
import ru.shawarma.core.data.utils.safeServiceCall
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

class MainOrderRemoteDataSource @Inject constructor(
    private val orderService: OrderService,
    private val dispatcher: CoroutineDispatcher
) : OrderRemoteDataSource {
    override suspend fun getOrders(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<OrderResponse>> =
        safeServiceCall(dispatcher){
            orderService.getOrders(token, offset, count)
        }


    override suspend fun getOrder(token: String, id: Long): Result<OrderResponse> =
        safeServiceCall(dispatcher){
            orderService.getOrder(token, id)
        }

    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> =
        safeServiceCall(dispatcher){
            orderService.createOrder(request)
        }

}