package ru.shawarma.order.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.core.data.utils.checkNotExpiresOrTryRefresh
import ru.shawarma.order.entities.Order
import ru.shawarma.order.mapOrderResponseToOrder
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private var authData: AuthData? = null

    private var getAuthDataJob = viewModelScope.launch {
        this@OrderViewModel.authData = tokenManager.getAuthData()
    }

    private val _orderState = MutableStateFlow<OrderUIState>(OrderUIState.Loading)

    val orderState = _orderState.asStateFlow()

    private val _orderId = MutableLiveData<Int>()
    val orderId: LiveData<Int> = _orderId

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _createdDate = MutableLiveData<String>()
    val createdDate: LiveData<String> = _createdDate

    private val _closedDate = MutableLiveData<String>()
    val closedDate: LiveData<String> = _closedDate

    private val _orderStatus = MutableLiveData<String>()
    val orderStatus: LiveData<String> = _orderStatus

    fun getOrder(orderId: Int){
        viewModelScope.launch {
            getAuthDataJob.join()
            if(!checkTokenValid()){
                _orderState.value = OrderUIState.TokenInvalidError
                return@launch
            }
            val token = "Bearer ${authData!!.accessToken}"
            //when(val result = orderRepository.getOrder(token, orderId)){
            when(val result = orderRepository.getOrders(token, 0,100)){
                is Result.Success<List<OrderResponse>> -> {
                    val orderResponse = result.data.find { it.id == orderId }
                    val order = mapOrderResponseToOrder(orderResponse!!)
                    _orderId.value = order.id
                    _totalPrice.value = order.totalPrice
                    _createdDate.value = order.createdDate.toString()
                    _closedDate.value = order.closeDate.toString()
                    _orderState.value = OrderUIState.Success(order)
                }
                is Result.Failure -> {
                    if(result.message == Errors.REFRESH_TOKEN_ERROR)
                        _orderState.value = OrderUIState.TokenInvalidError
                    else
                        _orderState.value = OrderUIState.Error(result.message)
                }
                is Result.NetworkFailure -> _orderState.value = OrderUIState.Error(Errors.NETWORK_ERROR)
            }
        }
    }
    fun startOrderStatusObserving(orderId: Int){
        viewModelScope.launch {
            getAuthDataJob.join()
            if(!checkTokenValid()){
                _orderState.value = OrderUIState.TokenInvalidError
                return@launch
            }
            val token = "Bearer ${authData!!.accessToken}"
            orderRepository.startOrdersStatusHub(token){orderResponse ->
                if(orderResponse.id == orderId){
                    _orderState.value = OrderUIState.Success(mapOrderResponseToOrder(orderResponse))
                }
            }
        }
    }
    fun setStatus(value: String){
        _orderStatus.value = value
    }

    private suspend fun checkTokenValid(): Boolean =
        if(checkNotExpiresOrTryRefresh(authData!!,authRepository, tokenManager)){
            authData = tokenManager.getAuthData()
            true
        }
        else false

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopOrdersStatusHub()
    }
}
sealed interface OrderUIState{
    object Loading: OrderUIState
    data class Success(val order: Order): OrderUIState
    data class Error(val message: String): OrderUIState
    object TokenInvalidError: OrderUIState
}