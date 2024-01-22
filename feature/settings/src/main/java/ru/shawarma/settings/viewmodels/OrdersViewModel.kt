package ru.shawarma.settings.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.ui.CommonPagingSource
import ru.shawarma.core.ui.Event
import ru.shawarma.settings.NavigationCommand
import ru.shawarma.settings.SettingsController
import ru.shawarma.settings.entities.OrderItem
import ru.shawarma.settings.utils.ORDERS_REQUEST_OFFSET
import ru.shawarma.settings.utils.mapOrderResponseToOrderItems
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel(), SettingsController {

    private val _isDisconnectedToInternet = MutableLiveData<Event<Boolean>>()

    val isDisconnectedToInternet: LiveData<Event<Boolean>> = _isDisconnectedToInternet

    private val _navCommand = MutableLiveData<Event<NavigationCommand>>()

    val navCommand: LiveData<Event<NavigationCommand>> = _navCommand

    val ordersFlow = Pager(
        config = PagingConfig(
            pageSize = ORDERS_REQUEST_OFFSET,
            initialLoadSize = ORDERS_REQUEST_OFFSET
        ),
        pagingSourceFactory = {
            CommonPagingSource { page, pageSize ->
                orderRepository.getOrders(page*pageSize, pageSize)
            }
        }).flow.map<PagingData<OrderResponse>, PagingData<OrderItem>>{ pagingData ->
            pagingData
                .map {
                    orderResponse -> mapOrderResponseToOrderItems(orderResponse)
                }
        }.cachedIn(viewModelScope)

    val isOrdersListEmpty = MutableLiveData(true)

    init {
        startOrdersStatusObserving()
    }

    private fun startOrdersStatusObserving(){
        viewModelScope.launch {
            orderRepository.startOrdersStatusHub{orderResponse ->
                updateState(orderResponse)
            }
        }
    }

    private fun updateState(orderResponse: OrderResponse){

    }

    override fun goToOrder(id: Long) {
        _navCommand.value = Event(NavigationCommand.ToOrderModule(id))
    }


    override fun onCleared() {
        super.onCleared()
        orderRepository.stopOrdersStatusHub()
    }
}
