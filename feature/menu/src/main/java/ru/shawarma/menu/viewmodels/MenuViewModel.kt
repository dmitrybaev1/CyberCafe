package ru.shawarma.menu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.core.data.utils.checkExpiresAndTryRefresh
import ru.shawarma.menu.*
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel(), MenuController {

    private var authData: AuthData? = null

    private var menuOffset = - STANDARD_REQUEST_OFFSET

    private val menuCount = STANDARD_REQUEST_OFFSET

    private val menuList = arrayListOf<MenuElement>(MenuElement.Loading)

    private val _menuState = MutableStateFlow<MenuUIState>(
        MenuUIState.Success(menuList)
    )

    val menuState = _menuState.asStateFlow()

    private val _navCommand = MutableLiveData<NavigationCommand>()

    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val cartList = arrayListOf<MenuElement.MenuItem>()

    init {
        getMenu()
    }

    fun setToken(authData: AuthData){
        this.authData = authData
    }

    fun getMenu(loadNext: Boolean = true){
        viewModelScope.launch {
            if(!checkTokenExpires()){
                _menuState.value = MenuUIState.TokenInvalidError
                return@launch
            }
            if(loadNext)
                menuOffset += STANDARD_REQUEST_OFFSET
            val token = authData!!.accessToken
            if(menuList.last() is MenuElement.Error || menuList.last() is MenuElement.Loading)
                menuList.removeLast()
            when(val result = menuRepository.getMenu(token,menuOffset,menuCount)){
                is Result.Success<List<MenuItemResponse>> -> {
                    val items = mapMenuItemResponseToMenuItem(result.data)
                    menuList.add(MenuElement.Header("Шаверма"))
                    menuList.addAll(items)
                    if(items.isNotEmpty()) {
                        menuList.add(MenuElement.Loading)
                        _menuState.value = MenuUIState.Success(menuList)
                    }
                    else
                        _menuState.value = MenuUIState.Success(menuList,true)
                }
                is Result.Failure -> {
                    if(result.message == Errors.UNAUTHORIZED_ERROR)
                        _menuState.value = MenuUIState.TokenInvalidError
                    else {
                        menuList.add(MenuElement.Error)
                        _menuState.value = MenuUIState.Error(menuList)
                    }
                }
                is Result.NetworkFailure -> {
                    menuList.add(MenuElement.Error)
                    _menuState.value = MenuUIState.Error(menuList)
                }
            }
        }
    }

    override fun reloadMenu() {
        getMenu(loadNext = false)
    }

    override fun addToCart(menuItem: MenuElement.MenuItem) {
        TODO("Not yet implemented")
    }

    override fun removeFromCart(menuItem: MenuElement.MenuItem) {
        TODO("Not yet implemented")
    }

    fun goToCart(){
        _navCommand.value = NavigationCommand.ToCart
    }

    private suspend fun checkTokenExpires(): Boolean =
        if(checkExpiresAndTryRefresh(authData!!,authRepository, tokenManager)){
            authData = tokenManager.getAuthData()
            true
        }
        else false

}
sealed interface MenuUIState{
    data class Success(val items: List<MenuElement>,val isFullyLoaded: Boolean = false): MenuUIState
    data class Error(val items: List<MenuElement>): MenuUIState
    object TokenInvalidError: MenuUIState
}