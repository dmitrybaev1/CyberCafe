package ru.shawarma.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.auth.navigation.NavigationCommand
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navCommand = MutableLiveData<NavigationCommand>()
    val navCommand: LiveData<NavigationCommand> = _navCommand

    private val _authState = MutableStateFlow<AuthUIState?>(null)
    val authState = _authState.asStateFlow()

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    /*var authRepository: AuthRepository = MainAuthRepository(
        MainAuthRemoteDataSource(
            AppRetrofit.authService,
            Dispatchers.IO)
    )*/

    fun goToRegister(){
        _navCommand.value = NavigationCommand.ToRegister
    }

    fun auth(){
        val userLoginRequest = UserLoginRequest(email.value!!,password.value!!)
        viewModelScope.launch {
            when(val result = authRepository.login(userLoginRequest)){
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