package ru.shawarma.core.data.datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderMenuItemResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.utils.Result
import java.util.Date
import javax.inject.Inject

class FakeOrderRemoteDataSource @Inject constructor() : OrderRemoteDataSource {

    override suspend fun getOrders(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<OrderResponse>> =
        withContext(Dispatchers.IO){
            val list = arrayListOf<OrderResponse>()
            repeat(count){
                list.add(
                    OrderResponse(
                    it+offset,
                    listOf(OrderMenuItemResponse(0,"Kebab",100,1)),
                    "clientId",
                    Date(),
                    Date(),
                    OrderStatus.CLOSED,
                    100
                    )
                )
            }
            delay(1000)
            Result.Success(list)
    }

    override suspend fun getOrder(token: String, id: Long): Result<OrderResponse> =
        withContext(Dispatchers.IO){
            delay(1000)
            Result.Success(
                OrderResponse(
                    0,
                    listOf(OrderMenuItemResponse(0,"Kebab",100,1)),
                    "clientId",
                    Date(),
                    Date(),
                    OrderStatus.CANCELED,
                    100
                )
            )
        }


    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> =
        withContext(Dispatchers.IO){
            delay(1000)
            Result.Success(
                OrderResponse(
                    0,
                    listOf(OrderMenuItemResponse(0,"Kebab",100,1)),
                    "clientId",
                    Date(),
                    Date(),
                    OrderStatus.CANCELED,
                    100
                )
            )
        }
}