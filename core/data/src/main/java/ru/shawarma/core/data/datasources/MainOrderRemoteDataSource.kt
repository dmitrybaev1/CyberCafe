package ru.shawarma.core.data.datasources

import com.google.gson.GsonBuilder
import com.microsoft.signalr.GsonHubProtocol
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.entities.FirebaseTokenResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.services.OrderService
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.safeServiceCall
import javax.inject.Inject

class MainOrderRemoteDataSource @Inject constructor(
    private val orderService: OrderService,
    private val dispatcher: CoroutineDispatcher
) : OrderRemoteDataSource {

    private var hubConnection: HubConnection? = null

    override suspend fun getOrders(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<OrderResponse>> =
        safeServiceCall(dispatcher){
            orderService.getOrders(token, offset, count)
        }


    override suspend fun getOrder(token: String, id: Int): Result<OrderResponse> =
        safeServiceCall(dispatcher){
            orderService.getOrder(token, id)
        }

    override suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderResponse> =
        safeServiceCall(dispatcher){
            orderService.createOrder(token, request)
        }

    override suspend fun startOrdersStatusHub(token: String, callback: (OrderResponse) -> Unit) {
        withContext(dispatcher) {
            try {
                if (!isConnectedToHub()) {
                    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
                    hubConnection = HubConnectionBuilder
                        .create("http://192.168.0.106:5029/notifications/client/orders")
                        .withHubProtocol(GsonHubProtocol(gson))
                        .withAccessTokenProvider(Single.defer {
                            runBlocking {
                                Single.just(token.substringAfter("Bearer "))
                            }
                        }).build()
                    hubConnection?.on("Notify", { message ->
                        callback.invoke(message)
                    }, OrderResponse::class.java)
                    hubConnection?.start()?.blockingAwait()
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun isConnectedToHub(): Boolean =
        hubConnection?.connectionState == HubConnectionState.CONNECTED

    override fun stopOrdersStatusHub(){
        hubConnection?.stop()
    }

}