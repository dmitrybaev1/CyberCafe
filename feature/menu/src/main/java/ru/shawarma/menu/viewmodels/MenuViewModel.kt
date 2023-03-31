package ru.shawarma.menu.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.core.data.utils.checkNotExpiresOrTryRefresh
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.NavigationCommand
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuElement
import ru.shawarma.menu.utlis.PlaceholderStringType
import ru.shawarma.menu.utlis.STANDARD_REQUEST_OFFSET
import ru.shawarma.menu.utlis.mapCartListToOrderMenuItemRequest
import ru.shawarma.menu.utlis.mapMenuItemResponseToMenuItem
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel(), MenuController {

    private var authData: AuthData? = null

    private var getAuthDataJob = viewModelScope.launch {
        this@MenuViewModel.authData = tokenManager.getAuthData()
    }

    private var menuOffset = - STANDARD_REQUEST_OFFSET

    private val menuCount = STANDARD_REQUEST_OFFSET

    private val menuList = arrayListOf<MenuElement>(MenuElement.Loading)

    private val _menuState = MutableStateFlow<MenuUIState>(
        MenuUIState.Success(listOf(MenuElement.Loading))
    )

    val menuState = _menuState.asStateFlow()

    private val _orderState = MutableStateFlow<OrderUIState?>(null)

    val orderState = _orderState.asStateFlow()

    private val _navCommand = MutableLiveData<NavigationCommand>()

    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val _orderWithDetailsText = MutableLiveData<CharSequence>()

    val orderWithDetailsText: LiveData<CharSequence> = _orderWithDetailsText

    private val _totalPriceText = MutableLiveData<CharSequence>()

    val totalPriceText: LiveData<CharSequence> = _totalPriceText

    private val _getPlaceholderString = MutableLiveData<Map<PlaceholderStringType,Array<Any>>>()

    val getPlaceholderString: LiveData<Map<PlaceholderStringType,Array<Any>>> = _getPlaceholderString

    private val _cartListLiveData = MutableLiveData<List<CartMenuItem>>(listOf())

    val cartListLiveData: LiveData<List<CartMenuItem>> = _cartListLiveData

    private val rawCartList = arrayListOf<MenuElement.MenuItem>()

    private val _chosenMenuItem = MutableLiveData<MenuElement.MenuItem>()

    val chosenMenuItem: LiveData<MenuElement.MenuItem> = _chosenMenuItem

    var totalCurrentCartPrice = 0

    private val _isOrderCreating = MutableLiveData(false)

    val isOrderCreating: LiveData<Boolean> = _isOrderCreating

    fun getMenu(loadNext: Boolean = true){
        viewModelScope.launch {
            getAuthDataJob.join()
            if(!checkTokenValid()){
                _menuState.value = MenuUIState.TokenInvalidError
                return@launch
            }
            if(loadNext)
                menuOffset += STANDARD_REQUEST_OFFSET
            val token = "Bearer ${authData!!.accessToken}"
            checkAndRemoveOldErrorAndLoading()
            when(val result = menuRepository.getMenu(token,menuOffset,menuCount)){
                is Result.Success<List<MenuItemResponse>> -> {
                    val items = mapMenuItemResponseToMenuItem(result.data)
                    menuList.add(MenuElement.Header("Шаверма"))
                    menuList.addAll(items)
                    if(items.isNotEmpty()) {
                        menuList.add(MenuElement.Loading)
                        copyAndSetMenuList(true)
                    }
                    else {
                        repeat(2){
                            // empty spacer for overlay button when scroll down
                            menuList.add(MenuElement.Header(""))
                        }
                        copyAndSetMenuList(true, isFullyLoaded = true)
                    }

                }
                is Result.Failure -> {
                    if(result.message == Errors.UNAUTHORIZED_ERROR)
                        _menuState.value = MenuUIState.TokenInvalidError
                    else {
                        if(menuList.lastOrNull() !is MenuElement.Error)
                            menuList.add(MenuElement.Error)
                        copyAndSetMenuList(false)
                    }
                }
                is Result.NetworkFailure -> {
                    if(menuList.lastOrNull() !is MenuElement.Error)
                        menuList.add(MenuElement.Error)
                    copyAndSetMenuList(false)
                }
            }
        }
    }
    private fun checkAndRemoveOldErrorAndLoading(){
        if(menuList.lastOrNull() is MenuElement.Error || menuList.lastOrNull() is MenuElement.Loading)
            menuList.removeLast()
    }
    override fun reloadMenu() {
        if(menuList.last() is MenuElement.Error)
            menuList.removeLast()
        menuList.add(MenuElement.Loading)
        copyAndSetMenuList(false)
        getMenu(loadNext = false)
    }

    override fun addToCart(menuItem: MenuElement.MenuItem) {
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

    override fun removeFromCart(menuItem: MenuElement.MenuItem) {
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

    override fun getMenuItemCount(menuItem: MenuElement.MenuItem): Int =
        rawCartList.filter { item -> item == menuItem }.size

    fun setFormattedString(type: PlaceholderStringType, formattedString: CharSequence){
        when(type) {
            PlaceholderStringType.ORDER_WITH_DETAILS -> _orderWithDetailsText.value = formattedString
            PlaceholderStringType.TOTAL_PRICE -> _totalPriceText.value = formattedString
        }
    }

    fun goToCart(){
        _navCommand.value = NavigationCommand.ToCartFragment
    }

    fun makeOrder(){
        Log.d("token",authData!!.accessToken)
        viewModelScope.launch {
            if(!checkTokenValid()){
                _orderState.value = OrderUIState.TokenInvalidError
                return@launch
            }
            _isOrderCreating.value = true
            val token = "Bearer ${authData!!.accessToken}"
            when(val result = orderRepository.createOrder(CreateOrderRequest(
                mapCartListToOrderMenuItemRequest(buildCartMenuItemList())
            ))){
                is Result.Success<OrderResponse> -> {
                    val id = result.data.id
                    clearCartMenuItemList()
                    _orderState.value = OrderUIState.Success(id)
                }
                is Result.Failure -> {
                    if(result.message == Errors.REFRESH_TOKEN_ERROR)
                        _orderState.value = OrderUIState.TokenInvalidError
                    else
                        _orderState.value = OrderUIState.Error(result.message)
                }
                is Result.NetworkFailure -> _orderState.value = OrderUIState.Error(Errors.NETWORK_ERROR)
            }
            _isOrderCreating.value = false
        }
    }

    fun resetOrderState(){
        _orderState.value = null
    }

    override fun goToMenuItemFragment(menuItem: MenuElement.MenuItem, count: Int){
        _chosenMenuItem.value = menuItem
        _navCommand.value = NavigationCommand.ToMenuItemFragment
    }

    private suspend fun checkTokenValid(): Boolean =
        if(checkNotExpiresOrTryRefresh(authData!!,authRepository, tokenManager)){
            authData = tokenManager.getAuthData()
            true
        }
        else false

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

    private fun copyAndSetMenuList(isSuccess: Boolean,isFullyLoaded: Boolean = false){
        val newList = arrayListOf<MenuElement>()
        newList.addAll(menuList)
        if(isSuccess)
            _menuState.value = MenuUIState.Success(newList,isFullyLoaded)
        else
            _menuState.value = MenuUIState.Error(newList)
    }
}
sealed interface MenuUIState{
    data class Success(val items: List<MenuElement>, val isFullyLoaded: Boolean = false): MenuUIState
    data class Error(val items: List<MenuElement>): MenuUIState
    object TokenInvalidError: MenuUIState
}
sealed interface OrderUIState{
    class Success(val orderId: Int): OrderUIState
    class Error(val message: String): OrderUIState
    object TokenInvalidError: OrderUIState
}
