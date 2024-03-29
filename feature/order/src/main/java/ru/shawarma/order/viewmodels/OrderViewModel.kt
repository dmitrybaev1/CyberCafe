package ru.shawarma.order.viewmodels

import android.util.Log
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
import ru.shawarma.core.ui.Event
import ru.shawarma.order.entities.Order
import ru.shawarma.order.mapOrderResponseToOrder
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<OrderUIState?>(null)

    val orderState = _orderState.asStateFlow()

    private val _isDisconnectedToInternet = MutableLiveData<Event<Boolean>>()

    val isDisconnectedToInternet: LiveData<Event<Boolean>> = _isDisconnectedToInternet

    private val _orderId = MutableLiveData<Long>()
    val orderId: LiveData<Long> = _orderId

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _createdDate = MutableLiveData<String>()
    val createdDate: LiveData<String> = _createdDate

    private val _closedDate = MutableLiveData<String>()
    val closedDate: LiveData<String> = _closedDate

    private val _orderStatus = MutableLiveData<String>()
    val orderStatus: LiveData<String> = _orderStatus

    var id: Long = -1 //avoiding duplicate requests when config changes
        set(value) {
            if(field != value) {
                field = value
                getOrder(field)
            }
        }

    fun getOrder(orderId: Long){
        viewModelScope.launch {
            _orderState.value = null
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
                            _isDisconnectedToInternet.value = Event(true)
                        _orderState.value = OrderUIState.Error(result.message)
                    }
                }
                is Result.NetworkFailure ->
                    _orderState.value = OrderUIState.Error(Errors.NETWORK_ERROR)
            }
        }
    }


    fun startOrderStatusObserving(orderId: Long){
        viewModelScope.launch {
            orderRepository.startOrdersStatusHub{orderResponse ->
                updateState(orderId,orderResponse)
            }
        }
    }

    private fun updateState(orderId: Long, orderResponse: OrderResponse){
        if(orderResponse.id == orderId){
            val order = mapOrderResponseToOrder(orderResponse)
            _orderId.postValue(order.id)
            _totalPrice.postValue(order.totalPrice)
            _createdDate.postValue(order.createdDate.toString())
            _closedDate.postValue(order.closeDate.toString())
            _orderState.value = OrderUIState.Success(order)
        }
    }

    fun stopOrdersStatusObserving(){
        orderRepository.stopOrdersStatusHub()
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
    data class Success(val order: Order): OrderUIState
    data class Error(val message: String): OrderUIState
    object TokenInvalidError: OrderUIState
}