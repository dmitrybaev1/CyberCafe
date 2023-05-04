package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.datasources.OrderRemoteDataSource
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

class MainOrderRepository @Inject constructor(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val authRepository: AuthRepository,
    private val internetManager: InternetManager
) : OrderRepository {

    override suspend fun getOrders(offset: Int, count: Int): Result<List<OrderResponse>> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return when (val result = authRepository.getActualAuthData()) {
            is Result.Success<AuthData> ->
                orderRemoteDataSource.getOrders("Bearer ${result.data.accessToken}", offset, count)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
    }

    override suspend fun getOrder(id: Int): Result<OrderResponse> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return when (val result = authRepository.getActualAuthData()) {
            is Result.Success<AuthData> ->
                orderRemoteDataSource.getOrder("Bearer ${result.data.accessToken}", id)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
    }

    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderResponse> {
        if (!internetManager.isOnline())
            return Result.Failure(Errors.NO_INTERNET_ERROR)
        return when (val result = authRepository.getActualAuthData()) {
            is Result.Success<AuthData> ->
                orderRemoteDataSource.createOrder("Bearer ${result.data.accessToken}", request)
            is Result.Failure -> result
            is Result.NetworkFailure -> result
        }
    }

    override suspend fun startOrdersStatusHub(callback: (OrderResponse) -> Unit) {
        if(internetManager.isOnline()) {
            val result = authRepository.getActualAuthData()
            if (result is Result.Success<AuthData>) {
                orderRemoteDataSource.startOrdersStatusHub(
                    "Bearer ${result.data.accessToken}",
                    callback
                )
            }
        }
    }

    override fun stopOrdersStatusHub() =
        orderRemoteDataSource.stopOrdersStatusHub()

}