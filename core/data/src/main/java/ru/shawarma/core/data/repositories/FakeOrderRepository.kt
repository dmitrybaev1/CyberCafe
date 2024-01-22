package ru.shawarma.core.data.repositories

import android.util.Log
import kotlinx.coroutines.delay
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.entities.OrderMenuItemResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import java.util.Date
import javax.inject.Inject

class FakeOrderRepository @Inject constructor(): OrderRepository {

    override suspend fun getOrders(offset: Int, count: Int): Result<List<OrderResponse>> {
        val orderList = arrayListOf<OrderResponse>()
        Log.d("TAG","invoking get orders, offset: $offset, count: $count")
        return if(offset>20) {
            delay(5000)
            Result.Failure("shit")
        }
        else{
            repeat(count){
                val time = System.currentTimeMillis()
                orderList.add(
                    OrderResponse(
                        id = time,
                        menuItems = listOf(OrderMenuItemResponse(
                            id = System.currentTimeMillis(),
                            name = "Shawarma",
                            price = 200,
                            imageUrl = null,
                            amount = 1
                        )),
                        clientId = "clientId",
                        createdDate = Date(System.currentTimeMillis()),
                        closeDate = Date(System.currentTimeMillis()+600),
                        status = OrderStatus.CLOSED,
                        totalPrice = 200
                    )
                )
            }
            Result.Success(orderList)
        }
    }

    override suspend fun getOrder(id: Long): Result<OrderResponse> {
        return Result.Success(
            OrderResponse(
                id = id,
                menuItems = listOf(OrderMenuItemResponse(
                    id = System.currentTimeMillis(),
                    name = "Shawarma",
                    price = 200,
                    imageUrl = null,
                    amount = 1
                )),
                clientId = "clientId",
                createdDate = Date(System.currentTimeMillis()),
                closeDate = Date(System.currentTimeMillis()+600),
                status = OrderStatus.CLOSED,
                totalPrice = 200
            )
        )
    }

    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> {
        val time = System.currentTimeMillis()
        return Result.Success(
            OrderResponse(
                id = time,
                menuItems = listOf(OrderMenuItemResponse(
                    id = System.currentTimeMillis(),
                    name = "Shawarma",
                    price = 200,
                    imageUrl = null,
                    amount = 1
                )),
                clientId = "clientId",
                createdDate = Date(System.currentTimeMillis()),
                closeDate = Date(System.currentTimeMillis()+600),
                status = OrderStatus.CLOSED,
                totalPrice = 200
            )
        )
    }

    override suspend fun startOrdersStatusHub(callback: (OrderResponse) -> Unit) {

    }

    override fun stopOrdersStatusHub() {

    }
}