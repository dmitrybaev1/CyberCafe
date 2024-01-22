package ru.shawarma.menu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.ui.CommonPagingSource
import ru.shawarma.core.ui.Event
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.NavigationCommand
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuItem
import ru.shawarma.menu.utlis.PlaceholderStringType
import ru.shawarma.menu.utlis.MENU_REQUEST_OFFSET
import ru.shawarma.menu.utlis.mapCartListToOrderMenuItemRequest
import ru.shawarma.menu.utlis.mapMenuItemResponseToMenuItem
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository
) : ViewModel(), MenuController {

    private val _orderState = MutableStateFlow<MakeOrderUIState?>(null)

    val orderState = _orderState.asStateFlow()

    private val _isDisconnectedToInternet = MutableLiveData<Event<Boolean>>()

    val isDisconnectedToInternet: LiveData<Event<Boolean>> = _isDisconnectedToInternet

    private val _navCommand = MutableLiveData<Event<NavigationCommand>>()

    val navCommand: LiveData<Event<NavigationCommand>> = _navCommand

    private val _orderWithDetailsText = MutableLiveData<CharSequence>()

    val orderWithDetailsText: LiveData<CharSequence> = _orderWithDetailsText

    private val _totalPriceText = MutableLiveData<CharSequence>()

    val totalPriceText: LiveData<CharSequence> = _totalPriceText

    private val _getPlaceholderString = MutableLiveData<Map<PlaceholderStringType,Array<Any>>>()

    val getPlaceholderString: LiveData<Map<PlaceholderStringType,Array<Any>>> = _getPlaceholderString

    private val _cartListLiveData = MutableLiveData<List<CartMenuItem>>(listOf())

    val cartListLiveData: LiveData<List<CartMenuItem>> = _cartListLiveData

    private val rawCartList = arrayListOf<MenuItem>()

    private val _chosenMenuItem = MutableLiveData<MenuItem>()

    val chosenMenuItem: LiveData<MenuItem> = _chosenMenuItem

    var totalCurrentCartPrice = 0

    private val _isOrderCreating = MutableLiveData(false)

    val isOrderCreating: LiveData<Boolean> = _isOrderCreating

    val menuFlow = Pager(
        config = PagingConfig(
            pageSize = MENU_REQUEST_OFFSET,
            initialLoadSize = MENU_REQUEST_OFFSET
        ),
        pagingSourceFactory = {
            CommonPagingSource<MenuItemResponse> { page, pageSize ->
                menuRepository.getMenu(page*pageSize, pageSize)
            }
        }).flow.map<PagingData<MenuItemResponse>,PagingData<MenuItem>>{ pagingData ->
            pagingData
                .filter {
                    menuItemResponse -> menuItemResponse.visible
                }
                .map {
                    menuItemResponse -> mapMenuItemResponseToMenuItem(menuItemResponse)
                }
            }.cachedIn(viewModelScope)


    override fun addToCart(menuItem: MenuItem) {
        rawCartList.add(menuItem)
        if(!menuItem.isPicked.get())
            menuItem.isPicked.set(true)
        _cartListLiveData.value = buildCartMenuItemList()
        val totalPrice = getTotalPrice()
        totalCurrentCartPrice = totalPrice
        _getPlaceholderString.value = mapOf(
            PlaceholderStringType.ORDER_WITH_DETAILS to arrayOf(totalPrice),
            PlaceholderStringType.TOTAL_PRICE to arrayOf(totalPrice)
        )
    }

    override fun removeFromCart(menuItem: MenuItem) {
        rawCartList.remove(menuItem)
        if(getMenuItemCount(menuItem) == 0)
            menuItem.isPicked.set(false)
        _cartListLiveData.value = buildCartMenuItemList()
        val totalPrice = getTotalPrice()
        totalCurrentCartPrice = totalPrice
        _getPlaceholderString.value = mapOf(
            PlaceholderStringType.ORDER_WITH_DETAILS to arrayOf(totalPrice),
            PlaceholderStringType.TOTAL_PRICE to arrayOf(totalPrice)
        )
    }

    override fun getMenuItemCount(menuItem: MenuItem): Int =
        rawCartList.filter { item -> item == menuItem }.size

    fun setFormattedString(type: PlaceholderStringType, formattedString: CharSequence){
        when(type) {
            PlaceholderStringType.ORDER_WITH_DETAILS -> _orderWithDetailsText.value = formattedString
            PlaceholderStringType.TOTAL_PRICE -> _totalPriceText.value = formattedString
        }
    }

    fun goToCart(){
        _navCommand.value = Event(NavigationCommand.ToCartFragment)
    }

    fun makeOrder(){
        viewModelScope.launch {
            _isOrderCreating.value = true
            when(val result = orderRepository.createOrder(CreateOrderRequest(
                mapCartListToOrderMenuItemRequest(buildCartMenuItemList())
            ))){
                is Result.Success<OrderResponse> -> {
                    val id = result.data.id
                    clearCartMenuItemList()
                    _orderState.value = MakeOrderUIState.Success(id)
                }
                is Result.Failure -> {
                    if(result.message == Errors.UNAUTHORIZED_ERROR || result.message == Errors.REFRESH_TOKEN_ERROR)
                        _orderState.value = MakeOrderUIState.TokenInvalidError
                    else {
                        if(result.message == Errors.NO_INTERNET_ERROR)
                            _isDisconnectedToInternet.value = Event(true)
                        _orderState.value = MakeOrderUIState.Error(result.message)
                    }
                }
                is Result.NetworkFailure ->
                    _orderState.value = MakeOrderUIState.Error(Errors.NETWORK_ERROR)

            }
            _isOrderCreating.value = false
        }
    }

    fun resetOrderState(){
        _orderState.value = null
    }

    override fun goToMenuItemFragment(menuItem: MenuItem, count: Int){
        _chosenMenuItem.value = menuItem
        _navCommand.value = Event(NavigationCommand.ToMenuItemFragment)
    }

    private fun getTotalPrice(): Int {
        var totalPrice = 0
        rawCartList.forEach { totalPrice += it.price }
        return totalPrice
    }
    private fun clearCartMenuItemList(){
        rawCartList.clear()
    }
    private fun buildCartMenuItemList(): List<CartMenuItem>{
        val cartList = arrayListOf<CartMenuItem>()
        val map = rawCartList.groupingBy { it }.eachCount()
        for(key in map.keys){
            val cartItem = CartMenuItem(key,map[key]!!)
            cartList.add(cartItem)
        }
        cartList.sortBy { it.menuItem.id }
        return cartList
    }
}

sealed interface MakeOrderUIState{
    class Success(val orderId: Long): MakeOrderUIState
    class Error(val message: String): MakeOrderUIState
    object TokenInvalidError: MakeOrderUIState
}
