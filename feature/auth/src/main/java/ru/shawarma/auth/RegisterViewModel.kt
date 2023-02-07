package ru.shawarma.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.shawarma.core.data.entities.UserRegisterRequest

class RegisterViewModel : ViewModel() {

    private val _navCommand = MutableLiveData<NavigationCommand>()
    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val _registerState = MutableStateFlow<RegisterUIState?>(null)
    val registerState = _registerState.asStateFlow()

    val email = MutableLiveData("")
    val name = MutableLiveData("")
    val password = MutableLiveData("")

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    fun register(){
        _isError.value = false
        _navCommand.value = NavigationCommand.ToValidateEmail
    }

    fun setEmptyInputError(){
        _isError.value = true
        _registerState.value = RegisterUIState.Error("empty input error")
    }
}

sealed interface RegisterUIState{
    data class Success(val user: UserRegisterRequest): RegisterUIState
    data class Error(val message: String): RegisterUIState
}