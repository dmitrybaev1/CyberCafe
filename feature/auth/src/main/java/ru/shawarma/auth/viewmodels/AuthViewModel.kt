package ru.shawarma.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.auth.checkEmail
import ru.shawarma.auth.checkPassword
import ru.shawarma.auth.navigation.NavigationCommand
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navCommand = MutableLiveData<NavigationCommand>()
    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val _authState = MutableStateFlow<AuthUIState?>(null)
    val authState = _authState.asStateFlow()

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    fun goToRegister(){
        _navCommand.value = NavigationCommand.ToRegister
    }

    fun auth(){
        if(!checkPassword(password.value!!)){
            _isError.value = true
            _authState.value = AuthUIState.Error(Errors.PASSWORD_ERROR)
            return
        }
        if(!checkEmail(email.value!!)){
            _isError.value = true
            _authState.value = AuthUIState.Error(Errors.EMAIL_ERROR)
            return
        }
        _isLoading.value = true
        val userLoginRequest = UserLoginRequest(email.value!!,password.value!!)
        viewModelScope.launch {
            val result = authRepository.login(userLoginRequest)
            _isLoading.value = false
            when(result){
                is Result.Success<AuthData> -> {
                    val authData = result.data
                    tokenManager.update(authData)
                    _authState.value = AuthUIState.Success(authData)
                    _isError.value = false
                }
                is Result.Failure -> { _authState.value = AuthUIState.Error(result.message); _isError.value = true }
                is Result.NetworkFailure -> { _authState.value = AuthUIState.Error(Errors.NETWORK_ERROR); _isError.value = true }
            }
        }
    }

    fun setEmptyInputError(){
        _isError.value = true
        _authState.value = AuthUIState.Error(Errors.EMPTY_INPUT_ERROR)
    }

    fun setRefreshTokenError(message: String){
        _isError.value = true
        _authState.value = AuthUIState.Error(message)
    }
}

sealed interface AuthUIState{
    data class Success(val authData: AuthData): AuthUIState
    data class Error(val message: String): AuthUIState
}