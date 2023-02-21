package ru.shawarma.menu.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.menu.MenuElement
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    private var authData: AuthData? = null

    private var offset = 0

    private val menuItems = arrayListOf<MenuElement>()

    private val _menuState = MutableStateFlow<MenuUIState>(
        MenuUIState.Success(arrayListOf(MenuElement.Loading))
    )

    val menuState = _menuState.asStateFlow()

    fun setToken(authData: AuthData){
        this.authData = authData
    }
}
sealed interface MenuUIState{
    data class Success(val items: List<MenuElement>): MenuUIState
    data class Error(val items: List<MenuElement>): MenuUIState
    object TokenInvalidError: MenuUIState
}