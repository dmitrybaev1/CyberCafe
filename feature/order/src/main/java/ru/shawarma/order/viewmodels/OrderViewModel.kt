package ru.shawarma.order.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.order.entities.Order
import ru.shawarma.order.mapOrderResponseToOrder
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<OrderUIState>(OrderUIState.Loading)

    val orderState = _orderState.asStateFlow()

    private val _isConnectedToInternet = MutableLiveData<Boolean>()

    val isConnectedToInternet: LiveData<Boolean> = _isConnectedToInternet

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
            when(val result = orderRepository.getOrder(orderId)){
                is Result.Success<OrderResponse> -> {
                    val order = mapOrderResponseToOrder(result.data)
                    _orderId.value = order.id
                    _totalPrice.value = order.totalPrice
                    _createdDate.value = order.createdDate.toString()
                    _closedDate.value = order.closeDate.toString()
                    _orderState.value = OrderUIState.Success(order)
                }
                is Result.Failure -> {
                    if(result.message == Errors.UNAUTHORIZED_ERROR || result.message == Errors.REFRESH_TOKEN_ERROR)
                        _orderState.value = OrderUIState.TokenInvalidError
                    else {
                        if(result.message == Errors.NO_INTERNET_ERROR)
                            _isConnectedToInternet.value = true
                        _orderState.value = OrderUIState.Error(result.message)
                    }
                }
                is Result.NetworkFailure -> _orderState.value = OrderUIState.Error(Errors.NETWORK_ERROR)
            }
        }
    }

    fun resetNoInternetState(){
        _isConnectedToInternet.value = false
    }

    fun startOrderStatusObserving(orderId: Int){
        viewModelScope.launch {
            orderRepository.startOrdersStatusHub{orderResponse ->
                if(orderResponse.id == orderId){
                    _orderState.value = OrderUIState.Success(mapOrderResponseToOrder(orderResponse))
                }
            }
        }
    }
    fun setStatus(value: String){
        _orderStatus.value = value
    }

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