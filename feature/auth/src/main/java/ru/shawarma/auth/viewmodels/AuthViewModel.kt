package ru.shawarma.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.auth.NavigationCommand
import ru.shawarma.auth.checkEmail
import ru.shawarma.auth.checkPassword
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.ui.Event
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navCommand = MutableLiveData<Event<NavigationCommand>>()
    val navCommand: LiveData<Event<NavigationCommand>> = _navCommand

    private val _authState = MutableStateFlow<AuthUIState?>(null)
    val authState = _authState.asStateFlow()

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    fun goToRegister(){
        _navCommand.value = Event(NavigationCommand.ToRegisterFragment)
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
            when(val result = authRepository.login(userLoginRequest)){
                is Result.Success -> {
                    _authState.value = AuthUIState.Success
                    _isError.value = false
                }
                is Result.Failure -> {
                    _authState.value = AuthUIState.Error(result.message)
                    _isError.value = result.message != Errors.NO_INTERNET_ERROR

                }
                is Result.NetworkFailure -> { _authState.value = AuthUIState.Error(Errors.NETWORK_ERROR); _isError.value = true }
            }
            _isLoading.value = false
        }
    }

    fun resetState(){
        _authState.value = null
    }

    fun setEmptyInputError(){
        _isError.value = true
        _authState.value = AuthUIState.Error(Errors.EMPTY_INPUT_ERROR)
    }

    fun setRefreshTokenErrorAndClearData(message: String){
        if(message.isNotEmpty()) {
            _isError.value = true
            viewModelScope.launch {
                authRepository.clearAuthData()
            }
            _authState.value = AuthUIState.Error(message)
        }
    }

    fun saveFirebaseToken(request: FirebaseTokenRequest){
        viewModelScope.launch {
            when(val result = authRepository.saveFirebaseToken(request)){
                is Result.Success -> _authState.value = AuthUIState.FirebaseTokenSent(success = true)
                else -> _authState.value = AuthUIState.FirebaseTokenSent(success = false)
            }
        }
    }
}

sealed interface AuthUIState{
    object Success: AuthUIState
    class Error(val message: String): AuthUIState
    data class FirebaseTokenSent(val success: Boolean): AuthUIState
}