package ru.shawarma.settings.viewmodels

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
import ru.shawarma.settings.NavigationCommand
import ru.shawarma.settings.SettingsController
import ru.shawarma.settings.entities.OrderElement
import ru.shawarma.settings.utils.STANDARD_REQUEST_OFFSET
import ru.shawarma.settings.utils.mapOrderResponseToOrderItem
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel(), SettingsController {

    private var ordersOffset = - STANDARD_REQUEST_OFFSET

    private val ordersCount = STANDARD_REQUEST_OFFSET

    private val _ordersState = MutableStateFlow<OrdersUIState>(
        OrdersUIState.Success(listOf(OrderElement.Loading))
    )

    val ordersState = _ordersState.asStateFlow()

    private val _isDisconnectedToInternet = MutableLiveData<Boolean>()

    val isDisconnectedToInternet: LiveData<Boolean> = _isDisconnectedToInternet

    private val _navCommand = MutableLiveData<NavigationCommand>()

    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val ordersList = arrayListOf<OrderElement>(OrderElement.Loading)

    init {
        getOrders()
        startOrdersStatusObserving()
    }

    fun getOrders(loadNext: Boolean = true){
        viewModelScope.launch {
            if(loadNext)
                ordersOffset += STANDARD_REQUEST_OFFSET
            checkAndRemoveOldErrorAndLoading()
            when(val result = orderRepository.getOrders(ordersOffset,ordersCount)){
                is Result.Success<List<OrderResponse>> -> {
                    if(result.data.isNotEmpty()) {
                        val items = mapOrderResponseToOrderItem(result.data)
                        ordersList.addAll(items)
                        ordersList.add(OrderElement.Loading)
                        copyAndSetOrdersList(true)
                    }
                    else
                        copyAndSetOrdersList(true, isFullyLoaded = true)
                }
                is Result.Failure -> {
                    if(result.message == Errors.UNAUTHORIZED_ERROR || result.message == Errors.REFRESH_TOKEN_ERROR)
                        _ordersState.value = OrdersUIState.TokenInvalidError
                    else {
                        _isDisconnectedToInternet.value = (result.message == Errors.NO_INTERNET_ERROR)
                        if(ordersList.lastOrNull() !is OrderElement.Error)
                            ordersList.add(OrderElement.Error)
                        copyAndSetOrdersList(false)
                    }
                }
                is Result.NetworkFailure -> {
                    if(ordersList.lastOrNull() !is OrderElement.Error)
                        ordersList.add(OrderElement.Error)
                    copyAndSetOrdersList(false)
                }
            }
        }
    }

    fun refreshOrders(){
        ordersList.clear()
        ordersOffset = - STANDARD_REQUEST_OFFSET
        getOrders()
        refreshOrdersStatusObserving()
    }

    fun resetNoInternetState(){
        _isDisconnectedToInternet.value = false
    }

    private fun startOrdersStatusObserving(){
        viewModelScope.launch {
            orderRepository.startOrdersStatusHub{orderResponse ->
                updateState(orderResponse)
            }
        }
    }

    private fun refreshOrdersStatusObserving(){
        viewModelScope.launch {
            orderRepository.refreshOrdersStatusHub{orderResponse ->
                updateState(orderResponse)
            }
        }
    }

    private fun updateState(orderResponse: OrderResponse){
        val newList = ordersList.map {
            if(it is OrderElement.OrderItem){
                if(it.id == orderResponse.id)
                    mapOrderResponseToOrderItem(listOf(orderResponse))[0]
                else
                    it
            }
            else
                it
        }
        _ordersState.value = OrdersUIState.Success(newList)
    }

    override fun goToOrder(id: Int) {
        _navCommand.value = NavigationCommand.ToOrderModule(id)
    }

    override fun reloadOrders() {
        if(ordersList.last() is OrderElement.Error)
            ordersList.removeLast()
        ordersList.add(OrderElement.Loading)
        copyAndSetOrdersList(false)
        getOrders(loadNext = false)
    }

    private fun checkAndRemoveOldErrorAndLoading(){
        if(ordersList.lastOrNull() is OrderElement.Error || ordersList.lastOrNull() is OrderElement.Loading)
            ordersList.removeLast()
    }

    private fun copyAndSetOrdersList(isSuccess: Boolean, isFullyLoaded: Boolean = false){
        val newList = arrayListOf<OrderElement>()
        newList.addAll(ordersList)
        if(isSuccess)
            _ordersState.value = OrdersUIState.Success(newList,isFullyLoaded)
        else
            _ordersState.value = OrdersUIState.Error(newList)
    }

    override fun onCleared() {
        super.onCleared()
        orderRepository.stopOrdersStatusHub()
    }
}
sealed interface OrdersUIState{
    class Success(val items: List<OrderElement>, val isFullyLoaded: Boolean = false): OrdersUIState
    data class Error(val items: List<OrderElement>): OrdersUIState
    object TokenInvalidError: OrdersUIState
}
